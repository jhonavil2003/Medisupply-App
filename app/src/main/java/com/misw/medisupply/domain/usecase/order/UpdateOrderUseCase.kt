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
 * Use case for updating an existing order
 */
class UpdateOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    operator fun invoke(
        orderId: Int,
        customerId: Int,
        items: List<OrderItemRequest>,
        paymentTerms: PaymentTerms = PaymentTerms.CASH,
        paymentMethod: PaymentMethod? = null,
        deliveryAddress: String? = null,
        deliveryCity: String? = null,
        deliveryDepartment: String? = null,
        deliveryDate: String? = null,
        preferredDistributionCenter: String? = null,
        notes: String? = null
    ): Flow<Resource<Order>> {
        return orderRepository.updateOrder(
            orderId = orderId,
            customerId = customerId,
            items = items,
            paymentTerms = paymentTerms,
            paymentMethod = paymentMethod,
            deliveryAddress = deliveryAddress,
            deliveryCity = deliveryCity,
            deliveryDepartment = deliveryDepartment,
            deliveryDate = deliveryDate,
            preferredDistributionCenter = preferredDistributionCenter,
            notes = notes
        )
    }
}
