package com.misw.medisupply.presentation.login.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.misw.medisupply.R
import com.misw.medisupply.core.i18n.LocaleManager
import com.misw.medisupply.presentation.components.localizedStringResource
import com.misw.medisupply.presentation.login.viewmodel.LoginViewModel
import com.misw.medisupply.presentation.login.viewmodel.NextStep
import com.misw.medisupply.presentation.navigation.MainRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    vm: LoginViewModel = viewModel(),
    navController: NavHostController
) {
    Log.d("LoginScreen", "Recomposing")
    val state by vm.state.collectAsState()
    val (username, setUsername) = rememberSaveable { mutableStateOf("") }
    val (password, setPassword) = rememberSaveable { mutableStateOf("") }
    val (challenge, setChallenge) = rememberSaveable { mutableStateOf("") }
    // Get LocaleManager from a ViewModel for navigation
    val context = LocalContext.current
    val localeManager = remember { LocaleManager(context) }

    Log.d("LoginScreen", "State: $state")
    Log.d("LoginScreen", "Username: $username, Password: $password, Challenge: $challenge")


    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.message) {
        val message = state.message
        if (message != null) {
            Log.d("LoginScreen", "State: $state")
            Log.d("LoginScreen", "Showing snackbar: $message")
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Iniciar sesión", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = username,
                onValueChange = { newUsername ->
                    Log.d("LoginScreen", "Username changed to: $newUsername")
                    setUsername(newUsername)
                },
                label = { Text("Usuario") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { newPassword ->
                    Log.d("LoginScreen", "Password changed")
                    setPassword(newPassword)
                },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        Log.d("LoginScreen", "Keyboard 'Done' clicked")
                        vm.signIn(username.trim(), password)
                    }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    Log.d("LoginScreen", "Login button clicked")
                    vm.signIn(username.trim(), password)
                },
                enabled = username.isNotBlank() && password.isNotBlank()
            ) {
                Text("Entrar")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${localizedStringResource(R.string.register_new_customer, localeManager)} ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(
                    onClick = {
                        navController.navigate(MainRoutes.CUSTOMER_REGISTRATION) {
                            launchSingleTop = true
                        }
                    }
                ) {
                    Text(
                        text = localizedStringResource(R.string.register_button, localeManager),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            when (val step = state.nextStep) {
                is NextStep.Mfa -> {
                    Log.d("LoginScreen", "Next step: MFA")
                    Spacer(Modifier.height(16.dp))
                    Text("Ingresa el código MFA" + (step.delivery?.let { " enviado a $it" } ?: ""))
                    OutlinedTextField(
                        value = challenge,
                        onValueChange = { newChallenge ->
                            Log.d("LoginScreen", "Challenge changed to: $newChallenge")
                            setChallenge(newChallenge)
                        },
                        label = { Text("Código") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            Log.d("LoginScreen", "Confirm MFA button clicked")
                            vm.confirm(challenge.trim())
                        },
                        enabled = challenge.isNotBlank()
                    ) {
                        Text("Confirmar MFA")
                    }
                }

                is NextStep.NewPassword -> {
                    Log.d("LoginScreen", "Next step: New Password")
                    Spacer(Modifier.height(16.dp))
                    Text("Debes establecer una nueva contraseña")
                    OutlinedTextField(
                        value = challenge,
                        onValueChange = { newChallenge ->
                            Log.d("LoginScreen", "New Password challenge changed")
                            setChallenge(newChallenge)
                        },
                        label = { Text("Nueva contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            Log.d("LoginScreen", "Update password button clicked")
                            vm.confirm(challenge)
                        },
                        enabled = challenge.length >= 8
                    ) {
                        Text("Actualizar contraseña")
                    }
                }

                is NextStep.ResetPassword -> {
                    Log.d("LoginScreen", "Next step: Reset Password")
                    Spacer(Modifier.height(16.dp))
                    Text("La cuenta requiere restablecer contraseña (usa Forgot Password).")
                }

                is NextStep.Unconfirmed -> {
                    Log.d("LoginScreen", "Next step: Unconfirmed user")
                    Spacer(Modifier.height(16.dp))
                    Text("Usuario no confirmado. Verifica tu correo en la web y vuelve a intentar.")
                }

                null -> {
                    Log.d("LoginScreen", "Next step: null")
                }
            }

            if (state.loading) {
                Log.d("LoginScreen", "Showing loading indicator")
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}