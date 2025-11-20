package com.misw.medisupply.presentation.salesforce.screens.routes.viewmodel

import androidx.lifecycle.ViewModel
import com.misw.medisupply.core.i18n.LocaleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel para RouteListScreen que maneja la internacionalización
 */
@HiltViewModel
class RouteListScreenViewModel @Inject constructor(
    val localeManager: LocaleManager
) : ViewModel()