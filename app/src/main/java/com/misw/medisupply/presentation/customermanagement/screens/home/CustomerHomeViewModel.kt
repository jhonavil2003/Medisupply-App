package com.misw.medisupply.presentation.customermanagement.screens.home

import androidx.lifecycle.ViewModel
import com.misw.medisupply.core.i18n.LocaleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for Customer Home Screen
 * Handles locale management for the screen
 */
@HiltViewModel
class CustomerHomeViewModel @Inject constructor(
    val localeManager: LocaleManager
) : ViewModel()