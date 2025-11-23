package com.misw.medisupply.core.utils.auth

import com.amplifyframework.core.Amplify
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private suspend fun fetchSessionSuspend(): AWSCognitoAuthSession =
    suspendCancellableCoroutine { cont ->
        Amplify.Auth.fetchAuthSession(
            { cont.resume(it as AWSCognitoAuthSession) },
            { cont.resumeWithException(it) }
        )
    }

/** true = usuario logueado (y si puede, tokens ya refrescados) */
suspend fun isUserSignedIn(): Boolean = try {
    fetchSessionSuspend().isSignedIn
} catch (_: Exception) { false }

/** opcional: exige tokens y te los devuelve (o null si no hay) */
suspend fun currentAccessToken(): String? = try {
    fetchSessionSuspend().userPoolTokensResult.value?.accessToken
} catch (_: Exception) { null }