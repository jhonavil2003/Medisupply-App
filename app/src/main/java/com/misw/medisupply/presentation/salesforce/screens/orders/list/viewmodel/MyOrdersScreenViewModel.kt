package com.misw.medisupply.presentation.salesforce.screens.orders.list.viewmodel

import androidx.lifecycle.ViewModel
import com.misw.medisupply.core.i18n.LocaleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyOrdersScreenViewModel @Inject constructor(
    val localeManager: LocaleManager
) : ViewModel()