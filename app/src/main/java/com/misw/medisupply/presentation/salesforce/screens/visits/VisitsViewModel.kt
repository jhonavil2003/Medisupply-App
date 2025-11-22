package com.misw.medisupply.presentation.salesforce.screens.visits

import androidx.lifecycle.ViewModel
import com.misw.medisupply.core.i18n.LocaleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for Visits Screen
 * Simple ViewModel for internationalization support
 */
@HiltViewModel
class VisitsViewModel @Inject constructor(
    val localeManager: LocaleManager
) : ViewModel()