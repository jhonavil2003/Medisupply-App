package com.misw.medisupply.presentation.salesforce.screens.orders.create.viewmodel

import androidx.lifecycle.ViewModel
import com.misw.medisupply.core.i18n.LocaleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel para CustomerListScreen que maneja la internacionalización
 */
@HiltViewModel
class CustomerListScreenViewModel @Inject constructor(
    val localeManager: LocaleManager
) : ViewModel()