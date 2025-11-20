package com.misw.medisupply.presentation.salesforce.screens.customers.detail.viewmodel

import androidx.lifecycle.ViewModel
import com.misw.medisupply.core.i18n.LocaleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CustomerDetailScreenViewModel @Inject constructor(
    val localeManager: LocaleManager
) : ViewModel()