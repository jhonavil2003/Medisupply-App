package com.misw.medisupply.presentation.salesforce.screens.home

import androidx.lifecycle.ViewModel
import com.misw.medisupply.core.i18n.LocaleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for SalesForce Home Screen
 * Provides access to LocaleManager for language switching functionality
 */
@HiltViewModel
class SalesForceHomeViewModel @Inject constructor(
    val localeManager: LocaleManager
) : ViewModel()