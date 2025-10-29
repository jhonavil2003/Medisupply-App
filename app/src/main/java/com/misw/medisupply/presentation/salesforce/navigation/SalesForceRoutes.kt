package com.misw.medisupply.presentation.salesforce.navigation

/**
 * Navigation routes for Sales Force role
 * Defines all available destinations for internal staff
 */
object SalesForceRoutes {
    const val HOME = "salesforce_home"
    const val VISITS = "salesforce_visits"
    const val ORDERS = "salesforce_orders"
    const val PERFORMANCE = "salesforce_performance"
    
    // Orders sub-navigation
    const val CUSTOMER_LIST = "salesforce_customer_list"
    const val CREATE_ORDER = "salesforce_create_order"
    const val MY_ORDERS = "salesforce_my_orders"
    const val PRODUCT_SELECTION = "salesforce_product_selection"
    const val ORDER_REVIEW = "salesforce_order_review"
    const val ORDER_DETAIL = "salesforce_order_detail" // Changed from comment to actual route
    
    // Edit order routes
    const val EDIT_ORDER_SELECT_PRODUCTS = "salesforce_edit_order_select_products"
    const val EDIT_ORDER_REVIEW = "salesforce_edit_order_review"
    
    // Future routes for detail screens
    const val VISIT_DETAIL = "salesforce_visit_detail"
    const val CUSTOMER_DETAIL = "salesforce_customer_detail"
}

/**
 * Helper object to build navigation routes with parameters
 */
object OrderRoute {
    /**
     * Build route for editing order - product selection step
     */
    fun editSelectProducts(orderId: String): String {
        return "${SalesForceRoutes.EDIT_ORDER_SELECT_PRODUCTS}/$orderId"
    }
    
    /**
     * Build route for editing order - review step
     */
    fun editReview(orderId: String): String {
        return "${SalesForceRoutes.EDIT_ORDER_REVIEW}/$orderId"
    }
    
    /**
     * Build route for order detail
     */
    fun detail(orderId: String): String {
        return "${SalesForceRoutes.ORDER_DETAIL}/$orderId"
    }
}
