package com.misw.medisupply.core.config

import com.misw.medisupply.BuildConfig

/**
 * API Configuration Manager
 * Centralizes API URLs and environment configuration
 * Uses BuildConfig values from Gradle build variants
 */
object ApiConfig {
    
    /**
     * Current environment name
     * Values: "LOCAL" or "AWS"
     */
    val environment: String
        get() = BuildConfig.ENVIRONMENT
    
    /**
     * Check if running in local development mode
     */
    val isLocalEnvironment: Boolean
        get() = environment == "LOCAL"
    
    /**
     * Check if running in AWS production mode
     */
    val isAwsEnvironment: Boolean
        get() = environment == "AWS"
    
    /**
     * Sales Service URL (Orders & Customers)
     * - LOCAL: http://10.0.2.2:8000/ (Android emulator localhost)
     * - AWS: http://lb-sales-service-570996197.us-east-1.elb.amazonaws.com/
     */
    val salesServiceUrl: String
        get() = BuildConfig.SALES_SERVICE_URL
    
    /**
     * Catalog Service URL (Products)
     * - LOCAL: http://10.0.2.2:8001/
     * - AWS: http://lb-catalog-service-11171664.us-east-1.elb.amazonaws.com/
     */
    val catalogServiceUrl: String
        get() = BuildConfig.CATALOG_SERVICE_URL
    
    /**
     * Logistics Service URL (Inventory/Stock)
     * - LOCAL: http://10.0.2.2:8002/
     * - AWS: http://lb-logistics-service-1435144637.us-east-1.elb.amazonaws.com/
     */
    val logisticsServiceUrl: String
        get() = BuildConfig.LOGISTICS_SERVICE_URL
    
    /**
     * Get base URL (defaults to sales service)
     */
    val baseUrl: String
        get() = salesServiceUrl
    
    /**
     * Get environment display name
     */
    fun getEnvironmentDisplayName(): String {
        return when (environment) {
            "LOCAL" -> "🏠 Desarrollo Local"
            "AWS" -> "☁️ AWS Producción"
            else -> "❓ Desconocido"
        }
    }
    
    /**
     * Print current configuration (for debugging)
     */
    fun printConfig() {
        println("═══════════════════════════════════════")
        println("📡 API Configuration")
        println("═══════════════════════════════════════")
        println("Environment: $environment")
        println("Sales Service: $salesServiceUrl")
        println("Catalog Service: $catalogServiceUrl")
        println("Logistics Service: $logisticsServiceUrl")
        println("═══════════════════════════════════════")
    }
}
