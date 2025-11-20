package com.misw.medisupply.core.i18n

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages application locale and language switching
 * Provides reactive state for language changes and persistence
 */
@Singleton
class LocaleManager @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val PREF_NAME = "locale_preferences"
        private const val KEY_LANGUAGE = "selected_language"
        const val LANG_SPANISH = "es"
        const val LANG_ENGLISH = "en"
    }

    private val preferences: SharedPreferences = 
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private val _currentLanguage = MutableStateFlow(getSavedLanguage())
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    // Observable state for Compose
    var currentLanguageState by mutableStateOf(getSavedLanguage())
        private set

    init {
        // Apply saved language on init
        updateLocale(_currentLanguage.value)
    }

    /**
     * Get saved language from SharedPreferences
     * Defaults to Spanish if not set
     */
    private fun getSavedLanguage(): String {
        return preferences.getString(KEY_LANGUAGE, LANG_SPANISH) ?: LANG_SPANISH
    }

    /**
     * Switch to specified language
     */
    fun switchLanguage(language: String) {
        if (language != _currentLanguage.value) {
            _currentLanguage.value = language
            currentLanguageState = language
            saveLanguage(language)
            updateLocale(language)
        }
    }

    /**
     * Toggle between Spanish and English
     */
    fun toggleLanguage() {
        val newLanguage = when (_currentLanguage.value) {
            LANG_SPANISH -> LANG_ENGLISH
            LANG_ENGLISH -> LANG_SPANISH
            else -> LANG_ENGLISH
        }
        switchLanguage(newLanguage)
    }

    /**
     * Check if current language is Spanish
     */
    fun isSpanish(): Boolean = _currentLanguage.value == LANG_SPANISH

    /**
     * Check if current language is English
     */
    fun isEnglish(): Boolean = _currentLanguage.value == LANG_ENGLISH

    /**
     * Get current language display name
     */
    fun getCurrentLanguageDisplayName(): String {
        return when (_currentLanguage.value) {
            LANG_SPANISH -> "Español"
            LANG_ENGLISH -> "English"
            else -> "Español"
        }
    }

    /**
     * Save language preference to SharedPreferences
     */
    private fun saveLanguage(language: String) {
        preferences.edit()
            .putString(KEY_LANGUAGE, language)
            .apply()
    }

    /**
     * Update application locale
     * Note: This updates the application context locale
     * For full locale change, activity recreation might be needed
     */
    private fun updateLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        
        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
    }

    /**
     * Create localized context for specific language
     * Useful for getting localized strings without changing app locale
     */
    fun createLocalizedContext(language: String): Context {
        val locale = Locale(language)
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }

    /**
     * Get available languages
     */
    fun getAvailableLanguages(): List<LanguageOption> {
        return listOf(
            LanguageOption(LANG_SPANISH, "Español", "🇪🇸"),
            LanguageOption(LANG_ENGLISH, "English", "🇺🇸")
        )
    }

    /**
     * Get a localized string for the current language
     * This method creates a localized context and retrieves the string
     */
    fun getLocalizedString(stringId: Int): String {
        val localizedContext = createLocalizedContext(_currentLanguage.value)
        return localizedContext.getString(stringId)
    }
}

/**
 * Data class representing a language option
 */
data class LanguageOption(
    val code: String,
    val displayName: String,
    val flag: String
)