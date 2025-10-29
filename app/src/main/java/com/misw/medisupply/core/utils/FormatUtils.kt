package com.misw.medisupply.core.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Utility object for formatting numbers and currency
 * Uses Colombian format: dot (.) for thousands separator, comma (,) for decimal separator
 */
object FormatUtils {
    
    /**
     * Format currency with Colombian format
     * Dot as thousands separator, comma as decimal separator
     * Example: 1234567.89 -> "$ 1.234.567,89"
     * Example: 84000 -> "$ 84.000"
     */
    fun formatCurrency(amount: Double, decimals: Int = 2): String {
        val symbols = DecimalFormatSymbols().apply {
            decimalSeparator = ','
            groupingSeparator = '.'
        }
        
        val pattern = if (decimals > 0) {
            "#,##0.${"0".repeat(decimals)}"
        } else {
            "#,##0"
        }
        
        val formatter = DecimalFormat(pattern, symbols)
        return "$ ${formatter.format(amount)}"
    }
    
    /**
     * Format currency from Float
     */
    fun formatCurrency(amount: Float, decimals: Int = 2): String {
        return formatCurrency(amount.toDouble(), decimals)
    }
    
    /**
     * Format number with Colombian format
     * Dot as thousands separator, comma as decimal separator
     * Example: 1234.56 -> "1.234,56"
     */
    fun formatNumber(number: Double, decimals: Int = 2): String {
        val symbols = DecimalFormatSymbols().apply {
            decimalSeparator = ','
            groupingSeparator = '.'
        }
        
        val pattern = if (decimals > 0) {
            "#,##0.${"0".repeat(decimals)}"
        } else {
            "#,##0"
        }
        
        val formatter = DecimalFormat(pattern, symbols)
        return formatter.format(number)
    }
}
