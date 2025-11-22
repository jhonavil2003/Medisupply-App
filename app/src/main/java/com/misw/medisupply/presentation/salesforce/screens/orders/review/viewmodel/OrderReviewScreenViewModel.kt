package com.misw.medisupply.presentation.salesforce.screens.orders.review.viewmodel

import androidx.lifecycle.ViewModel
import com.misw.medisupply.core.i18n.LocaleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel para OrderReviewScreen que maneja la internacionalización
 */
@HiltViewModel
class OrderReviewScreenViewModel @Inject constructor(
    val localeManager: LocaleManager
) : ViewModel()