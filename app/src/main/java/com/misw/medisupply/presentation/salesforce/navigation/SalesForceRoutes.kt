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
    
    // Future routes for detail screens
    const val ORDER_DETAIL = "salesforce_order_detail"
    const val VISIT_DETAIL = "salesforce_visit_detail"
    const val CUSTOMER_DETAIL = "salesforce_customer_detail"
}
