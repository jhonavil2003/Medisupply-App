package com.misw.medisupply.core.utils

import com.misw.medisupply.core.config.ApiConfig

/**
 * Application-wide constants
 */
object Constants {
    
    // ============================================================================
    // API Configuration
    // ============================================================================
    
    /**
     * Get current API URLs from ApiConfig (based on build variant)
     * Switch between local/AWS by changing build variant:
     * - devDebug: Local development
     * - prodDebug/prodRelease: AWS production
     */
    
    // Base URL (defaults to Sales Service)
    val BASE_URL: String
        get() = ApiConfig.baseUrl
    
    // Sales Service (Orders & Customers)
    val SALES_SERVICE_URL: String
        get() = ApiConfig.salesServiceUrl
    
    // Catalog Service (Products)
    val CATALOG_SERVICE_URL: String
        get() = ApiConfig.catalogServiceUrl
    
    // Logistics Service (Inventory/Stock)
    val LOGISTICS_SERVICE_URL: String
        get() = ApiConfig.logisticsServiceUrl
    
    /**
     * API Timeout configurations (in seconds)
     */
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
    
    // ============================================================================
    // Database Configuration
    // ============================================================================
    
    /**
     * Local database name
     */
    const val DATABASE_NAME = "medisupply_db"
    
    /**
     * Database version
     */
    const val DATABASE_VERSION = 1
    
    // ============================================================================
    // Shared Preferences
    // ============================================================================
    
    /**
     * Shared preferences file name
     */
    const val PREFERENCES_NAME = "medisupply_prefs"
    
    // ============================================================================
    // API Endpoints
    // ============================================================================
    
    object Endpoints {
        const val CUSTOMERS = "customers"
        const val CUSTOMER_BY_ID = "customers/{id}"
        const val ORDERS = "orders"
        const val PRODUCTS = "products"
        const val VISITS = "visits"
    }
    
    // ============================================================================
    // Error Messages
    // ============================================================================
    
    object ErrorMessages {
        const val NETWORK_ERROR = "Error de conexión. Verifica tu internet."
        const val SERVER_ERROR = "Error en el servidor. Intenta más tarde."
        const val NOT_FOUND = "Recurso no encontrado."
        const val UNAUTHORIZED = "No autorizado. Inicia sesión nuevamente."
        const val TIMEOUT = "Tiempo de espera agotado."
        const val UNKNOWN_ERROR = "Error inesperado. Intenta nuevamente."
        const val VALIDATION_ERROR = "Por favor verifica los datos ingresados."
        const val NO_DATA = "No hay datos disponibles."
    }
    
    // ============================================================================
    // Customer Types
    // ============================================================================
    
    object CustomerTypes {
        const val HOSPITAL = "hospital"
        const val CLINICA = "clinica"
        const val FARMACIA = "farmacia"
        const val DISTRIBUIDOR = "distribuidor"
        const val IPS = "ips"
        const val EPS = "eps"
    }
    
    // ============================================================================
    // Date Formats
    // ============================================================================
    
    object DateFormats {
        const val ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        const val DATE_DISPLAY = "dd/MM/yyyy"
        const val DATE_TIME_DISPLAY = "dd/MM/yyyy HH:mm"
    }
    
    // ============================================================================
    // Pagination
    // ============================================================================
    
    const val PAGE_SIZE = 20
    const val INITIAL_PAGE = 1
    
    // ============================================================================
    // Cache Duration
    // ============================================================================
    
    const val CACHE_DURATION_MINUTES = 5L
}
