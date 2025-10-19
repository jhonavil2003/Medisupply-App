package com.misw.medisupply.presentation.customermanagement.navigation

/**
 * Navigation routes for Customer Management role
 * Defines all available destinations for self-service clients
 */
object CustomerManagementRoutes {
    const val HOME = "customer_home"
    const val SHOP = "customer_shop"
    const val ORDERS = "customer_orders"
    const val ACCOUNT = "customer_account"
    
    // Order creation flow routes
    const val CREATE_ORDER_PRODUCTS = "customer_create_order_products"
    const val CREATE_ORDER_REVIEW = "customer_create_order_review"
    
    // Future routes for detail screens
    const val PRODUCT_DETAIL = "customer_product_detail"
    const val ORDER_DETAIL = "customer_order_detail"
    const val ORDER_TRACKING = "customer_order_tracking"
}
