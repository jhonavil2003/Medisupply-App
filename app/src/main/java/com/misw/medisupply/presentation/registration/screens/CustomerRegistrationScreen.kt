package com.misw.medisupply.presentation.registration.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.misw.medisupply.R
import com.misw.medisupply.presentation.components.CompactLanguageToggle
import com.misw.medisupply.presentation.components.localizedStringResource
import com.misw.medisupply.presentation.registration.viewmodel.CustomerRegistrationViewModel

/**
 * Customer Registration Screen
 * Allows new customers to self-register in the system
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerRegistrationScreen(
    onBackClick: () -> Unit,
    onRegistrationComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CustomerRegistrationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Handle registration completion
    LaunchedEffect(uiState.isRegistrationComplete) {
        if (uiState.isRegistrationComplete) {
            onRegistrationComplete()
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Language toggle button positioned at top-right
        CompactLanguageToggle(
            localeManager = viewModel.localeManager,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            text = localizedStringResource(R.string.registration_title, viewModel.localeManager),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = localizedStringResource(R.string.registration_back_description, viewModel.localeManager)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Header
                Text(
                    text = localizedStringResource(R.string.registration_app_name, viewModel.localeManager),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Basic Data Section
                SectionTitle(localizedStringResource(R.string.registration_basic_data, viewModel.localeManager))
                
                // Business Name
                CustomTextField(
                    value = uiState.businessName,
                    onValueChange = viewModel::updateBusinessName,
                    label = localizedStringResource(R.string.registration_business_name, viewModel.localeManager),
                    error = uiState.businessNameError,
                    enabled = !uiState.isLoading
                )            // Document Number Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // NIT/RUC Field
                Box(modifier = Modifier.weight(1f)) {
                    CustomTextField(
                        value = uiState.documentNumber,
                        onValueChange = viewModel::updateDocumentNumber,
                        label = localizedStringResource(R.string.registration_document_number, viewModel.localeManager),
                        error = uiState.documentNumberError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        trailingIcon = {
                            when {
                                uiState.isValidatingDocument -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                }
                                uiState.isDocumentValidated && uiState.documentNumberError == null -> {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = localizedStringResource(R.string.registration_valid, viewModel.localeManager),
                                        tint = Color(0xFF4CAF50)
                                    )
                                }
                                uiState.documentNumberError != null -> {
                                    Icon(
                                        imageVector = Icons.Default.Error,
                                        contentDescription = localizedStringResource(R.string.registration_error, viewModel.localeManager),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        },
                        enabled = !uiState.isLoading
                    )
                }
                
                // Internal Code Field (Optional)
                Box(modifier = Modifier.weight(1f)) {
                    CustomTextField(
                        value = uiState.internalCode,
                        onValueChange = viewModel::updateInternalCode,
                        label = localizedStringResource(R.string.registration_internal_code, viewModel.localeManager),
                        enabled = !uiState.isLoading
                    )
                }
            }
            
            // Address Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                CustomTextField(
                    value = uiState.address,
                    onValueChange = viewModel::updateAddress,
                    label = localizedStringResource(R.string.registration_address, viewModel.localeManager),
                    error = uiState.addressError,
                    enabled = !uiState.isLoading,
                    modifier = Modifier.weight(1f)
                )
                
                CustomTextField(
                    value = uiState.contactPhone,
                    onValueChange = viewModel::updateContactPhone,
                    label = localizedStringResource(R.string.registration_phone, viewModel.localeManager),
                    error = uiState.contactPhoneError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    enabled = !uiState.isLoading,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Cell and Email Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                CustomTextField(
                    value = uiState.department,
                    onValueChange = viewModel::updateDepartment,
                    label = localizedStringResource(R.string.registration_cell, viewModel.localeManager),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    enabled = !uiState.isLoading,
                    modifier = Modifier.weight(1f)
                )
                
                CustomTextField(
                    value = uiState.contactEmail,
                    onValueChange = viewModel::updateContactEmail,
                    label = localizedStringResource(R.string.registration_email, viewModel.localeManager),
                    error = uiState.contactEmailError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    enabled = !uiState.isLoading,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Access Data Section
            SectionTitle(localizedStringResource(R.string.registration_access_data, viewModel.localeManager))
            
            // Username and Password Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                CustomTextField(
                    value = uiState.username,
                    onValueChange = viewModel::updateUsername,
                    label = localizedStringResource(R.string.registration_username, viewModel.localeManager),
                    enabled = !uiState.isLoading,
                    modifier = Modifier.weight(1f)
                )
                
                CustomTextField(
                    value = uiState.password,
                    onValueChange = viewModel::updatePassword,
                    label = localizedStringResource(R.string.registration_password, viewModel.localeManager),
                    enabled = !uiState.isLoading,
                    isPassword = true,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Error Message
            uiState.generalError?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Cancel Button
                OutlinedButton(
                    onClick = onBackClick,
                    enabled = !uiState.isLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(localizedStringResource(R.string.registration_cancel, viewModel.localeManager))
                }
                
                // Register Button
                Button(
                    onClick = {
                        viewModel.clearGeneralError()
                        viewModel.registerCustomer()
                    },
                    enabled = uiState.isFormValid && !uiState.isLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(localizedStringResource(R.string.registration_save, viewModel.localeManager))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
}

/**
 * Section title component
 */
@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

/**
 * Custom text field component
 */
@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    isPassword: Boolean = false,
    viewModel: CustomerRegistrationViewModel? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            isError = error != null,
            enabled = enabled,
            keyboardOptions = if (isPassword) {
                keyboardOptions.copy(keyboardType = KeyboardType.Password)
            } else {
                keyboardOptions
            },
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                }
            } else {
                trailingIcon
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                // Container colors (background)
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                
                // Border colors
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                errorBorderColor = MaterialTheme.colorScheme.error,
                
                // Text colors
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                errorTextColor = MaterialTheme.colorScheme.onSurface,
                
                // Label colors
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                errorLabelColor = MaterialTheme.colorScheme.error,
                
                // Cursor color
                cursorColor = MaterialTheme.colorScheme.primary,
                errorCursorColor = MaterialTheme.colorScheme.error
            )
        )
        
        // Error message
        error?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}