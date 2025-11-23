package com.misw.medisupply.presentation.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.data.repository.auth.AuthRepository
import com.misw.medisupply.data.repository.auth.SignInOutcome
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class NextStep {
    data class Mfa(val delivery: String?) : NextStep()
    object NewPassword : NextStep()
    object ResetPassword : NextStep()
    object Unconfirmed : NextStep()
}

data class LoginState(
    val loading: Boolean = false,
    val message: String? = null,
    val nextStep: NextStep? = null,
    val loginSuccess: Boolean = false
)

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun signIn(username: String, password: String) = viewModelScope.launch {
        _state.value = LoginState(loading = true)
        when (val res = AuthRepository.signIn(username, password)) {
            is SignInOutcome.Success -> _state.value = LoginState(loginSuccess = true)
            is SignInOutcome.MfaRequired -> _state.value = LoginState(nextStep = NextStep.Mfa(res.delivery))
            is SignInOutcome.NewPasswordRequired -> _state.value = LoginState(nextStep = NextStep.NewPassword)
            is SignInOutcome.ResetPasswordRequired -> _state.value = LoginState(nextStep = NextStep.ResetPassword)
            is SignInOutcome.UnconfirmedUser -> _state.value = LoginState(nextStep = NextStep.Unconfirmed)
            is SignInOutcome.Error -> _state.value = LoginState(message = res.message)
        }
    }

    fun confirm(response: String) = viewModelScope.launch {
        _state.value = LoginState(loading = true)
        when (val res = AuthRepository.confirmNextStep(response)) {
            is SignInOutcome.Success -> _state.value = LoginState(loginSuccess = true)
            is SignInOutcome.Error -> _state.value = LoginState(message = res.message)
            else -> _state.value = LoginState(message = "Unhandled confirmation result: $res")
        }
    }
}
