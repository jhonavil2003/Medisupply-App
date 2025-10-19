package com.misw.medisupply.presentation.customermanagement.screens.shop.createorder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.customer.CustomerType
import com.misw.medisupply.domain.model.order.CartItem
import com.misw.medisupply.presentation.salesforce.screens.orders.products.ProductSelectionScreen
import com.misw.medisupply.presentation.salesforce.viewmodel.orders.ProductsViewModel

/**
 * Customer Product Selection Screen
 * Reutiliza ProductSelectionScreen pero para clientes (autogestión)
 */
@Composable
fun CustomerProductSelectionScreen(
    onNavigateBack: () -> Unit,
    onConfirmOrder: (Map<String, CartItem>) -> Unit,
    viewModel: ProductsViewModel = hiltViewModel()
) {
    // Customer estático por ahora (customer_id = 1)
    // En el futuro esto vendrá del login/sesión
    val staticCustomer = Customer(
        id = 1,
        documentType = com.misw.medisupply.domain.model.customer.DocumentType.NIT,
        documentNumber = "900123456-1",
        businessName = "Hospital General San José",
        tradeName = "Hospital San José",
        customerType = CustomerType.HOSPITAL,
        contactName = "Dr. Juan Pérez",
        contactEmail = "admin@hospitalsanjose.com",
        contactPhone = "+57 1 234 5678",
        address = "Calle 10 # 5-25",
        city = "Bogotá",
        department = "Cundinamarca",
        country = "Colombia",
        creditLimit = 10000000.0,
        creditDays = 30,
        isActive = true,
        createdAt = null,
        updatedAt = null
    )

    // Reutilizar la pantalla de selección de productos existente
    ProductSelectionScreen(
        customer = staticCustomer,
        onNavigateBack = onNavigateBack,
        onConfirmOrder = onConfirmOrder,
        viewModel = viewModel
    )
}