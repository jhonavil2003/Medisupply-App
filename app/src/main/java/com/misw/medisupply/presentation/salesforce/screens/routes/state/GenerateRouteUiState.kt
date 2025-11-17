package com.misw.medisupply.presentation.salesforce.screens.routes.state

import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.route.OptimizationStrategy
import com.misw.medisupply.domain.model.route.Route
import java.time.LocalDate
import java.time.LocalTime

/**
 * Estado de UI para la pantalla de generación de rutas
 */
data class GenerateRouteUiState(
    val isLoading: Boolean = false,
    val isGenerating: Boolean = false,
    val error: String? = null,
    
    // Clientes disponibles
    val customers: List<Customer> = emptyList(),
    val isLoadingCustomers: Boolean = false,
    val customerError: String? = null,
    
    // Selección de clientes
    val selectedCustomerIds: Set<Int> = emptySet(),
    val searchQuery: String = "",
    
    // Configuración de ruta
    val selectedDate: LocalDate = LocalDate.now().plusDays(1),
    val optimizationStrategy: OptimizationStrategy = OptimizationStrategy.MINIMIZE_DISTANCE,
    val workHoursStart: LocalTime = LocalTime.of(8, 0),
    val workHoursEnd: LocalTime = LocalTime.of(18, 0),
    val serviceTimeMinutes: Int = 30,
    
    // Ubicación de inicio (opcional)
    val useCustomStartLocation: Boolean = false,
    val startLocationName: String = "",
    val startLocationLatitude: String = "",
    val startLocationLongitude: String = "",
    
    // Resultado
    val generatedRoute: Route? = null,
    val computationTime: Double? = null,
    val warnings: List<String> = emptyList(),
    
    // Validación
    val isFormValid: Boolean = false,
    val validationError: String? = null
) {
    /**
     * Clientes filtrados por búsqueda
     */
    val filteredCustomers: List<Customer>
        get() = if (searchQuery.isBlank()) {
            customers ?: emptyList()
        } else {
            customers?.filter { customer ->
                customer.businessName.contains(searchQuery, ignoreCase = true) ||
                customer.tradeName?.contains(searchQuery, ignoreCase = true) == true ||
                customer.documentNumber.contains(searchQuery, ignoreCase = true)
            } ?: emptyList()
        }
    
    /**
     * Clientes seleccionados
     */
    val selectedCustomers: List<Customer>
        get() = customers.filter { it.id in selectedCustomerIds }
    
    /**
     * Número de clientes seleccionados
     */
    val selectedCount: Int
        get() = selectedCustomerIds.size
    
    /**
     * Indica si se puede generar la ruta
     */
    val canGenerate: Boolean
        get() = selectedCustomerIds.isNotEmpty() && 
                selectedCustomerIds.size <= 20 &&
                !isGenerating &&
                !isLoading
}
