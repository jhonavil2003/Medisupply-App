package com.misw.medisupply.presentation.salesforce.screens.home

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.HomeActivity
import com.misw.medisupply.MainActivity
import com.misw.medisupply.core.i18n.LocaleManager
import com.misw.medisupply.data.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for SalesForce Home Screen
 * Provides access to LocaleManager for language switching functionality
 */
@HiltViewModel
class SalesForceHomeViewModel @Inject constructor(
    application: Application,
    val localeManager: LocaleManager
) : AndroidViewModel(application) {

    fun logout() {
        viewModelScope.launch {
            try {
                AuthRepository.signOut()
                val context = getApplication<Application>()
                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                // Handle error silently for now
            }
        }
    }
}