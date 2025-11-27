package com.misw.medisupply.data.repository.auth

import com.amplifyframework.core.Amplify
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.step.AuthSignInStep

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

sealed class SignInOutcome {
    object Success : SignInOutcome()
    data class MfaRequired(val delivery: String?) : SignInOutcome()
    object NewPasswordRequired : SignInOutcome()
    object ResetPasswordRequired : SignInOutcome()
    object UnconfirmedUser : SignInOutcome()
    data class Error(val message: String) : SignInOutcome()
}

private suspend fun signInSuspend(username: String, password: String): AuthSignInResult =
    suspendCancellableCoroutine { cont ->
        Amplify.Auth.signIn(username, password,
            { cont.resume(it) },
            { cont.resumeWithException(it) }
        )
    }

private suspend fun confirmSignInSuspend(challengeResponse: String): AuthSignInResult =
    suspendCancellableCoroutine { cont ->
        Amplify.Auth.confirmSignIn(challengeResponse,
            { cont.resume(it) },
            { cont.resumeWithException(it) }
        )
    }

private suspend fun fetchSessionSuspend(): AWSCognitoAuthSession =
    suspendCancellableCoroutine { cont ->
        Amplify.Auth.fetchAuthSession(
            { result -> cont.resume(result as AWSCognitoAuthSession) },
            { cont.resumeWithException(it) }
        )
    }

object AuthRepository {
    suspend fun signIn(username: String, password: String): SignInOutcome {
        return try {
            val res = signInSuspend(username, password)
            if (res.isSignedIn) {
                SignInOutcome.Success
            } else {
                when (res.nextStep.signInStep) {
                    AuthSignInStep.CONFIRM_SIGN_IN_WITH_SMS_MFA_CODE -> SignInOutcome.MfaRequired(res.nextStep.codeDeliveryDetails?.destination)
                    AuthSignInStep.CONFIRM_SIGN_IN_WITH_TOTP_CODE -> SignInOutcome.MfaRequired("Authenticator app (TOTP)")
                    AuthSignInStep.CONFIRM_SIGN_IN_WITH_NEW_PASSWORD -> SignInOutcome.NewPasswordRequired
                    AuthSignInStep.RESET_PASSWORD -> SignInOutcome.ResetPasswordRequired
                    else -> SignInOutcome.Error("Paso adicional requerido: ${res.nextStep.signInStep}")
                }
            }
        } catch (e: AuthException) {
            val msg = e.localizedMessage ?: e.toString()
            if (msg.contains("User is not confirmed", true)) SignInOutcome.UnconfirmedUser
            else SignInOutcome.Error(msg)
        }
    }

    suspend fun confirmNextStep(response: String): SignInOutcome {
        return try {
            val res = confirmSignInSuspend(response)
            if (res.isSignedIn) SignInOutcome.Success
            else when (res.nextStep.signInStep) {
                AuthSignInStep.CONFIRM_SIGN_IN_WITH_SMS_MFA_CODE,
                AuthSignInStep.CONFIRM_SIGN_IN_WITH_TOTP_CODE -> SignInOutcome.MfaRequired(null)
                AuthSignInStep.CONFIRM_SIGN_IN_WITH_NEW_PASSWORD -> SignInOutcome.NewPasswordRequired
                else -> SignInOutcome.Error("Paso pendiente: ${res.nextStep.signInStep}")
            }
        } catch (e: AuthException) {
            SignInOutcome.Error(e.localizedMessage ?: e.toString())
        }
    }

    suspend fun getIdToken(): String? {
        val s = fetchSessionSuspend()
        return s.userPoolTokensResult.value?.idToken
    }

    suspend fun getAccessToken(): String? {
        val s = fetchSessionSuspend()
        return s.userPoolTokensResult.value?.accessToken
    }

    suspend fun fetchUserAttributesSuspend(): List<AuthUserAttribute> =
        suspendCancellableCoroutine { cont ->
            Amplify.Auth.fetchUserAttributes(
                { cont.resume(it) },
                { cont.resumeWithException(it) }
            )
        }

    suspend fun getRoleFromUserAttributes(): String? = try {
        val attrs = fetchUserAttributesSuspend()
        attrs.firstOrNull { it.key.keyString == "custom:role" }?.value
    } catch (_: Exception) { null }


    suspend fun getUserDbId(): String? {
        return try {
            val attrs = fetchUserAttributesSuspend()
            attrs.firstOrNull { it.key.keyString == "custom:db_user_id" }?.value
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getUserId(): String? {
        return try {
            val attrs = fetchUserAttributesSuspend()
            attrs.firstOrNull { it.key.keyString == "sub" }?.value
        } catch (_: Exception) {
            null
        }
    }
}