package com.misw.medisupply.data.repository.auth

import aws.sdk.kotlin.services.cognitoidentityprovider.model.SignUpResponse
import com.amplifyframework.core.Amplify
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.auth.result.AuthSignUpResult
import com.amplifyframework.auth.result.step.AuthSignInStep
import com.amplifyframework.auth.result.step.AuthSignUpStep

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

sealed class SignUpOutcome {
    object Success : SignUpOutcome()
    data class ConfirmationRequired(val destination: String?) : SignUpOutcome()
    data class Error(val message: String) : SignUpOutcome()
}

private suspend fun signInSuspend(username: String, password: String): AuthSignInResult =
    suspendCancellableCoroutine { cont ->
        Amplify.Auth.signIn(
            username, password,
            { cont.resume(it) },
            { cont.resumeWithException(it) }
        )
    }

private suspend fun signUpSuspend(
    username: String, password: String, email: String, phoneNumber: String,
    businessName: String, tradeName: String?, documentNumber: String,
    documentType: String, contactName: String?, latitude: Double?, longitude: Double?,
    address: String, neighborhood: String?, city: String?, customerType: String,
    department: String?, country: String, salespersonId: Int?
): AuthSignUpResult = suspendCancellableCoroutine { cont ->
    val attributes = mutableListOf<AuthUserAttribute>().apply {
        add(AuthUserAttribute(AuthUserAttributeKey.email(), email))
        add(AuthUserAttribute(AuthUserAttributeKey.phoneNumber(), phoneNumber))
        // Custom attributes: deben existir en el User Pool (role, first_name, last_name, territory)
        add(AuthUserAttribute(AuthUserAttributeKey.custom("role"), "CUSTOMER"))
        add(AuthUserAttribute(AuthUserAttributeKey.custom("business_name"), businessName))
        add(AuthUserAttribute(AuthUserAttributeKey.custom("trade_name"), tradeName))
        add(AuthUserAttribute(AuthUserAttributeKey.custom("document_number"), documentNumber))
        add(AuthUserAttribute(AuthUserAttributeKey.custom("document_type"), documentType))
        add(AuthUserAttribute(AuthUserAttributeKey.custom("contact_name"), contactName))
        add(AuthUserAttribute(AuthUserAttributeKey.custom("contact_email"), email))
        add(AuthUserAttribute(AuthUserAttributeKey.custom("contact_phone"), phoneNumber))
        add(AuthUserAttribute(AuthUserAttributeKey.custom("address"), address))
        add(AuthUserAttribute(AuthUserAttributeKey.custom("neighborhood"), neighborhood))
        add(AuthUserAttribute(AuthUserAttributeKey.custom("city"), city))
        add(AuthUserAttribute(AuthUserAttributeKey.custom("department"), department))
        add(AuthUserAttribute(AuthUserAttributeKey.custom("country"), country))
        add(AuthUserAttribute(AuthUserAttributeKey.custom("latitude"), latitude.toString()))
        add(AuthUserAttribute(AuthUserAttributeKey.custom("longitude"), longitude.toString()))
        add(AuthUserAttribute(AuthUserAttributeKey.custom("customer_type"), customerType))
        add(
            AuthUserAttribute(
                AuthUserAttributeKey.custom("salesperson_id"),
                salespersonId.toString()
            )
        )
    }

    val options = AuthSignUpOptions.builder()
        .userAttributes(attributes)
        .build()

    Amplify.Auth.signUp(
        username,
        password,
        options,
        { result -> cont.resume(result) },
        { error -> cont.resumeWithException(error) }
    )
}

private suspend fun confirmSignInSuspend(challengeResponse: String): AuthSignInResult =
    suspendCancellableCoroutine { cont ->
        Amplify.Auth.confirmSignIn(
            challengeResponse,
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
                    AuthSignInStep.CONFIRM_SIGN_IN_WITH_SMS_MFA_CODE -> SignInOutcome.MfaRequired(
                        res.nextStep.codeDeliveryDetails?.destination
                    )

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

    suspend fun signUp(
        username: String,
        password: String,
        email: String,
        phoneNumber: String,
        businessName:
        String, tradeName: String?,
        documentNumber: String,
        documentType: String,
        contactName: String?, latitude: Double?, longitude: Double?,
        address: String, neighborhood: String?, city: String?, customerType: String,
        department: String?, country: String, salespersonId: Int?
    ): SignUpOutcome {
        return try {
            val res = signUpSuspend(
                username = username,
                password = password,
                email = email,
                phoneNumber = phoneNumber,
                businessName = businessName,
                tradeName = tradeName,
                documentNumber = documentNumber,
                documentType = documentType,
                contactName = contactName,
                latitude = latitude,
                longitude = longitude,
                address = address,
                neighborhood = neighborhood,
                city = city,
                customerType = customerType,
                department = department,
                country = country,
                salespersonId = salespersonId
            )

            if (res.isSignUpComplete) {
                // Algunos flujos auto-confirman al usuario
                SignUpOutcome.Success
            } else {
                when (res.nextStep.signUpStep) {
                    AuthSignUpStep.CONFIRM_SIGN_UP_STEP -> {
                        val destination = res.nextStep.codeDeliveryDetails?.destination
                        SignUpOutcome.ConfirmationRequired(destination)
                    }
                    else -> {
                        SignUpOutcome.Error("Paso adicional requerido: ${res.nextStep.signUpStep}")
                    }
                }
            }
        } catch (e: AuthException) {
            val msg = e.localizedMessage ?: e.toString()
            SignUpOutcome.Error(msg)
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
    } catch (_: Exception) {
        null
    }


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