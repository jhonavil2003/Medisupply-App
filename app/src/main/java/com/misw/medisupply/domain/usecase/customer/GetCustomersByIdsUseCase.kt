package com.misw.medisupply.domain.usecase.customer

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.repository.customer.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Use case para obtener múltiples clientes por sus IDs
 */
class GetCustomersByIdsUseCase @Inject constructor(
    private val repository: CustomerRepository
) {
    /**
     * Obtiene una lista de clientes por sus IDs
     * @param customerIds Lista de IDs de clientes
     * @return Flow con mapa de ID -> Customer
     */
    operator fun invoke(customerIds: List<Int>): Flow<Resource<Map<Int, Customer>>> {
        if (customerIds.isEmpty()) {
            return flowOf(Resource.Success(emptyMap()))
        }
        
        // Obtener todos los clientes en paralelo
        val customerFlows = customerIds.distinct().map { customerId ->
            repository.getCustomerById(customerId)
        }
        
        // Combinar todos los resultados
        return combine(customerFlows) { results ->
            val successfulCustomers = mutableMapOf<Int, Customer>()
            val errors = mutableListOf<String>()
            
            results.forEach { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { customer ->
                            successfulCustomers[customer.id] = customer
                        }
                    }
                    is Resource.Error -> {
                        errors.add(resource.message ?: "Error desconocido")
                    }
                    is Resource.Loading -> {
                        // Ignorar estados de carga intermedios
                    }
                }
            }
            
            // Si hay errores pero también datos exitosos, devolver los datos
            if (successfulCustomers.isNotEmpty()) {
                Resource.Success(successfulCustomers)
            } else if (errors.isNotEmpty()) {
                Resource.Error(errors.joinToString(", "))
            } else {
                Resource.Loading()
            }
        }
    }
}
