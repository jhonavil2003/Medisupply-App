package com.misw.medisupply.domain.usecase.order

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.PaymentMethod
import com.misw.medisupply.domain.model.order.PaymentTerms
import com.misw.medisupply.domain.repository.order.OrderItemRequest
import com.misw.medisupply.domain.repository.order.OrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Create Order Use Case
 * Business logic for creating a new order
 */
class CreateOrderUseCase @Inject constructor(
    private val repository: OrderRepository
) {
    /**
     * Invoke use case to create an order
     * 
     * @param customerId ID of the customer placing the order
     * @param sellerId ID of the seller
     * @param sellerName Name of the seller
     * @param items List of order items
     * @param paymentTerms Payment terms for the order
     * @param paymentMethod Payment method (optional)
     * @param deliveryAddress Delivery address (optional)
     * @param deliveryCity Delivery city (optional)
     * @param deliveryDepartment Delivery department (optional)
     * @param preferredDistributionCenter Preferred distribution center code (optional)
     * @param notes Additional notes (optional)
     * @return Flow with Resource containing created Order
     */
    operator fun invoke(
        customerId: Int,
        sellerId: String,
        sellerName: String? = null,
        items: List<OrderItemRequest>,
        paymentTerms: PaymentTerms = PaymentTerms.CASH,
        paymentMethod: PaymentMethod? = null,
        deliveryAddress: String? = null,
        deliveryCity: String? = null,
        deliveryDepartment: String? = null,
        preferredDistributionCenter: String? = null,
        notes: String? = null
    ): Flow<Resource<Order>> {
        return repository.createOrder(
            customerId = customerId,
            sellerId = sellerId,
            sellerName = sellerName,
            items = items,
            paymentTerms = paymentTerms,
            paymentMethod = paymentMethod,
            deliveryAddress = deliveryAddress,
            deliveryCity = deliveryCity,
            deliveryDepartment = deliveryDepartment,
            preferredDistributionCenter = preferredDistributionCenter,
            notes = notes
        )
    }
}
