package com.misw.medisupply.core.utils

import android.util.Patterns
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Extension functions for common operations
 */

// ============================================================================
// String Extensions
// ============================================================================

/**
 * Check if string is a valid email
 */
fun String.isValidEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * Check if string is a valid phone number
 */
fun String.isValidPhone(): Boolean {
    return this.matches(Regex("^[+]?[0-9]{10,13}\$"))
}

/**
 * Capitalize first letter of each word
 */
fun String.capitalizeWords(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) 
            else it.toString() 
        }
    }
}

/**
 * Remove extra whitespaces
 */
fun String.trimSpaces(): String {
    return this.trim().replace(Regex("\\s+"), " ")
}

// ============================================================================
// Number Extensions
// ============================================================================

/**
 * Format number as currency (Colombian format: dot for thousands, comma for decimals)
 */
fun Double.toCurrency(): String {
    return FormatUtils.formatCurrency(this)
}

/**
 * Format number with thousands separator (Colombian format with dot)
 */
fun Int.toFormattedString(): String {
    val formatter = DecimalFormat("#,###")
    val symbols = DecimalFormatSymbols().apply {
        groupingSeparator = '.'
    }
    formatter.decimalFormatSymbols = symbols
    return formatter.format(this)
}

/**
 * Format number with decimal places
 */
fun Double.toDecimalFormat(decimals: Int = 2): String {
    val pattern = "#,##0.${"0".repeat(decimals)}"
    val formatter = DecimalFormat(pattern)
    return formatter.format(this)
}

// ============================================================================
// Date Extensions
// ============================================================================

/**
 * Format Date to display string
 */
fun Date.toDisplayFormat(): String {
    val formatter = SimpleDateFormat(Constants.DateFormats.DATE_DISPLAY, Locale.getDefault())
    return formatter.format(this)
}

/**
 * Format Date to display string with time
 */
fun Date.toDisplayFormatWithTime(): String {
    val formatter = SimpleDateFormat(Constants.DateFormats.DATE_TIME_DISPLAY, Locale.getDefault())
    return formatter.format(this)
}

/**
 * Parse ISO 8601 string to Date
 */
fun String.parseIso8601(): Date? {
    return try {
        val formatter = SimpleDateFormat(Constants.DateFormats.ISO_8601, Locale.getDefault())
        formatter.parse(this)
    } catch (e: Exception) {
        null
    }
}

// ============================================================================
// Collection Extensions
// ============================================================================

/**
 * Safe get element at index
 */
fun <T> List<T>.getOrNull(index: Int): T? {
    return if (index in indices) this[index] else null
}

/**
 * Check if list is not null and not empty
 */
fun <T> List<T>?.isNotNullOrEmpty(): Boolean {
    return this != null && this.isNotEmpty()
}
