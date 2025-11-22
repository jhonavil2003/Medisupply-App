package com.misw.medisupply.presentation.roleselection

import androidx.lifecycle.ViewModel
import com.misw.medisupply.core.i18n.LocaleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for Role Selection Screen
 * Handles locale management for the screen
 */
@HiltViewModel
class RoleSelectionViewModel @Inject constructor(
    val localeManager: LocaleManager
) : ViewModel()