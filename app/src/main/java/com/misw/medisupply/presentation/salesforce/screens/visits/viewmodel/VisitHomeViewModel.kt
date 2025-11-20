package com.misw.medisupply.presentation.salesforce.screens.visits.viewmodel

import androidx.lifecycle.ViewModel
import com.misw.medisupply.core.i18n.LocaleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for Visit Home Screen
 * Provides access to LocaleManager for language switching functionality
 */
@HiltViewModel
class VisitHomeViewModel @Inject constructor(
    val localeManager: LocaleManager
) : ViewModel()