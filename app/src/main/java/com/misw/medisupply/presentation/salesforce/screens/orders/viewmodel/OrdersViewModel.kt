package com.misw.medisupply.presentation.salesforce.screens.orders.viewmodel

import androidx.lifecycle.ViewModel
import com.misw.medisupply.core.i18n.LocaleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for Orders Screen
 * Provides access to LocaleManager for language switching functionality
 */
@HiltViewModel
class OrdersViewModel @Inject constructor(
    val localeManager: LocaleManager
) : ViewModel()