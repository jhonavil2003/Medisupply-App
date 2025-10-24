package com.misw.medisupply.domain.usecase.order

import app.cash.turbine.test
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.customer.CustomerType
import com.misw.medisupply.domain.model.customer.DocumentType
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderItem
import com.misw.medisupply.domain.model.order.OrderStatus
import com.misw.medisupply.domain.model.order.PaymentMethod
import com.misw.medisupply.domain.model.order.PaymentTerms
import com.misw.medisupply.domain.repository.order.OrderItemRequest
import com.misw.medisupply.domain.repository.order.OrderRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class UpdateOrderUseCaseTest {

    private lateinit var repository: OrderRepository
    private lateinit var useCase: UpdateOrderUseCase

    private val testCustomer = Customer(
        id = 1,
        customerType = CustomerType.HOSPITAL,
        businessName = "Hospital Central",
        tradeName = "HC",
        documentType = DocumentType.NIT,
        documentNumber = "900123456",
        contactName = "Juan Pérez",
        contactEmail = "contacto@hospital.com",
        contactPhone = "3001234567",
        address = "Calle 123",
        city = "Bogotá",
        department = "Cundinamarca",
        country = "Colombia",
        creditLimit = 10000000.0,
        creditDays = 30,
        isActive = true,
        createdAt = null,
        updatedAt = null
    )

    private val testOrderItems = listOf(
        OrderItem(
            id = 1,
            orderId = 1,
            productSku = "MED-001",
            productName = "Jeringa 10ml",
            quantity = 150,
            unitPrice = 350.0,
            discountPercentage = 0.0,
            discountAmount = 0.0,
            taxPercentage = 19.0,
            taxAmount = 9975.0,
            subtotal = 52500.0,
            total = 62475.0,
            distributionCenterCode = "DC-BOG",
            stockConfirmed = true,
            stockConfirmationDate = null,
            createdAt = null
        )
    )

    private val testOrder = Order(
        id = 1,
        orderNumber = "ORD-20251023-0001",
        customerId = 1,
        sellerId = "SELLER-001",
        sellerName = "Vendedor Demo",
        orderDate = null,
        deliveryDate = null,
        status = OrderStatus.CONFIRMED,
        subtotal = 52500.0,
        discountAmount = 0.0,
        taxAmount = 9975.0,
        totalAmount = 62475.0,
        paymentTerms = PaymentTerms.CREDIT_30,
        paymentMethod = PaymentMethod.TRANSFER,
        deliveryAddress = "Calle 123 #45-67",
        deliveryCity = "Bogotá",
        deliveryDepartment = "Cundinamarca",
        preferredDistributionCenter = "DC-BOG",
        notes = null,
        createdAt = null,
        updatedAt = null,
        customer = testCustomer,
        items = testOrderItems
    )

    @Before
    fun setup() {
        repository = mock()
        useCase = UpdateOrderUseCase(repository)
    }

    @Test
    fun `invoke with valid parameters returns success with updated order`() = runTest {
        // Given
        val orderId = 1
        val customerId = 1
        val items = listOf(
            OrderItemRequest(
                productSku = "MED-001",
                productName = "Jeringa 10ml",
                quantity = 150,
                unitPrice = 350.0,
                discountPercentage = 0.0,
                taxPercentage = 19.0
            )
        )

        whenever(
            repository.updateOrder(
                orderId = eq(orderId),
                customerId = eq(customerId),
                items = eq(items),
                paymentTerms = eq(PaymentTerms.CREDIT_30),
                paymentMethod = eq(PaymentMethod.TRANSFER),
                deliveryAddress = eq("Calle 123 #45-67"),
                deliveryCity = eq("Bogotá"),
                deliveryDepartment = eq("Cundinamarca"),
                deliveryDate = eq(null),
                preferredDistributionCenter = eq("DC-BOG"),
                notes = eq(null)
            )
        ).thenReturn(flowOf(Resource.Loading(), Resource.Success(testOrder)))
        useCase(
            orderId = orderId,
            customerId = customerId,
            items = items,
            paymentTerms = PaymentTerms.CREDIT_30,
            paymentMethod = PaymentMethod.TRANSFER,
            deliveryAddress = "Calle 123 #45-67",
            deliveryCity = "Bogotá",
            deliveryDepartment = "Cundinamarca",
            deliveryDate = null,
            preferredDistributionCenter = "DC-BOG",
            notes = null
        ).test {
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)
            val result = awaitItem()
            assertTrue(result is Resource.Success)
            assertEquals(testOrder, result.data)
            assertEquals("ORD-20251023-0001", result.data?.orderNumber)
            assertEquals(OrderStatus.CONFIRMED, result.data?.status)
        }
    }

    @Test
    fun `invoke with unitPrice ensures it is sent to repository`() = runTest {
        // Given
        val orderId = 1
        val items = listOf(
            OrderItemRequest(
                productSku = "MED-001",
                productName = "Jeringa 10ml",
                quantity = 150,
                unitPrice = 350.0, // REQUIRED field
                discountPercentage = 0.0,
                taxPercentage = 19.0
            )
        )

        whenever(
            repository.updateOrder(
                orderId = any(),
                customerId = any(),
                items = any(),
                paymentTerms = any(),
                paymentMethod = anyOrNull(),
                deliveryAddress = anyOrNull(),
                deliveryCity = anyOrNull(),
                deliveryDepartment = anyOrNull(),
                deliveryDate = anyOrNull(),
                preferredDistributionCenter = anyOrNull(),
                notes = anyOrNull()
            )
        ).thenReturn(flowOf(Resource.Loading(), Resource.Success(testOrder)))
        useCase(
            orderId = orderId,
            customerId = 1,
            items = items,
            paymentTerms = PaymentTerms.CREDIT_30,
            paymentMethod = null,
            deliveryAddress = null,
            deliveryCity = null,
            deliveryDepartment = null,
            deliveryDate = null,
            preferredDistributionCenter = null,
            notes = null
        ).test {
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)
            val result = awaitItem()
            assertTrue(result is Resource.Success)
            assertEquals(350.0, result.data?.items?.get(0)?.unitPrice)
        }

        // Then - verify unitPrice was passed
        verify(repository).updateOrder(
            orderId = eq(orderId),
            customerId = any(),
            items = eq(items), // Items with unitPrice
            paymentTerms = any(),
            paymentMethod = anyOrNull(),
            deliveryAddress = anyOrNull(),
            deliveryCity = anyOrNull(),
            deliveryDepartment = anyOrNull(),
            deliveryDate = anyOrNull(),
            preferredDistributionCenter = anyOrNull(),
            notes = anyOrNull()
        )
    }

    @Test
    fun `invoke with productName ensures it is sent to repository`() = runTest {
        // Given
        val orderId = 1
        val items = listOf(
            OrderItemRequest(
                productSku = "MED-001",
                productName = "Jeringa 10ml", // Should be sent
                quantity = 150,
                unitPrice = 350.0,
                discountPercentage = 0.0,
                taxPercentage = 19.0
            )
        )

        whenever(
            repository.updateOrder(
                orderId = any(),
                customerId = any(),
                items = any(),
                paymentTerms = any(),
                paymentMethod = anyOrNull(),
                deliveryAddress = anyOrNull(),
                deliveryCity = anyOrNull(),
                deliveryDepartment = anyOrNull(),
                deliveryDate = anyOrNull(),
                preferredDistributionCenter = anyOrNull(),
                notes = anyOrNull()
            )
        ).thenReturn(flowOf(Resource.Loading(), Resource.Error("Validación fallida: Solo se pueden actualizar pedidos en estado Pendiente")))
        useCase(
            orderId = orderId,
            customerId = 1,
            items = items,
            paymentTerms = PaymentTerms.CREDIT_30,
            paymentMethod = null,
            deliveryAddress = null,
            deliveryCity = null,
            deliveryDepartment = null,
            deliveryDate = null,
            preferredDistributionCenter = null,
            notes = null
        ).test {
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)
            val result = awaitItem()
            assertTrue(result is Resource.Error)
            assertTrue(result.message?.contains("Pendiente") == true)
        }

        // Then - verify productName was passed
        verify(repository).updateOrder(
            orderId = eq(orderId),
            customerId = any(),
            items = eq(items), // Items with productName
            paymentTerms = any(),
            paymentMethod = anyOrNull(),
            deliveryAddress = anyOrNull(),
            deliveryCity = anyOrNull(),
            deliveryDepartment = anyOrNull(),
            deliveryDate = anyOrNull(),
            preferredDistributionCenter = anyOrNull(),
            notes = anyOrNull()
        )
    }

    @Test
    fun `invoke returns error when order is not PENDING`() = runTest {
        // Given
        val orderId = 1
        val items = listOf(
            OrderItemRequest(
                productSku = "MED-001",
                productName = "Jeringa 10ml",
                quantity = 150,
                unitPrice = 350.0,
                discountPercentage = 0.0,
                taxPercentage = 19.0
            )
        )

        whenever(
            repository.updateOrder(
                orderId = any(),
                customerId = any(),
                items = any(),
                paymentTerms = any(),
                paymentMethod = anyOrNull(),
                deliveryAddress = anyOrNull(),
                deliveryCity = anyOrNull(),
                deliveryDepartment = anyOrNull(),
                deliveryDate = anyOrNull(),
                preferredDistributionCenter = anyOrNull(),
                notes = anyOrNull()
            )
        ).thenReturn(flowOf(Resource.Loading(), Resource.Error("Validación fallida: Solo se pueden actualizar pedidos en estado Pendiente")))

        // When & Then
        useCase(
            orderId = orderId,
            customerId = 1,
            items = items,
            paymentTerms = PaymentTerms.CREDIT_30,
            paymentMethod = null,
            deliveryAddress = null,
            deliveryCity = null,
            deliveryDepartment = null,
            deliveryDate = null,
            preferredDistributionCenter = null,
            notes = null
        ).test {
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)
            val result = awaitItem()
            assertTrue(result is Resource.Error)
            assertTrue(result.message?.contains("Pendiente") == true)
        }
    }

    @Test
    fun `invoke returns error 404 when order not found`() = runTest {
        // Given
        val orderId = 999
        val items = listOf(
            OrderItemRequest(
                productSku = "MED-001",
                productName = "Jeringa 10ml",
                quantity = 150,
                unitPrice = 350.0,
                discountPercentage = 0.0,
                taxPercentage = 19.0
            )
        )

        whenever(
            repository.updateOrder(
                orderId = any(),
                customerId = any(),
                items = any(),
                paymentTerms = any(),
                paymentMethod = anyOrNull(),
                deliveryAddress = anyOrNull(),
                deliveryCity = anyOrNull(),
                deliveryDepartment = anyOrNull(),
                deliveryDate = anyOrNull(),
                preferredDistributionCenter = anyOrNull(),
                notes = anyOrNull()
            )
        ).thenReturn(flowOf(Resource.Loading(), Resource.Error("Pedido no encontrado")))

        // When & Then
        useCase(
            orderId = orderId,
            customerId = 1,
            items = items,
            paymentTerms = PaymentTerms.CREDIT_30,
            paymentMethod = null,
            deliveryAddress = null,
            deliveryCity = null,
            deliveryDepartment = null,
            deliveryDate = null,
            preferredDistributionCenter = null,
            notes = null
        ).test {
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)
            val result = awaitItem()
            assertTrue(result is Resource.Error)
            assertTrue(result.message?.contains("encontrado") == true)
        }
    }

    @Test
    fun `invoke returns error when insufficient stock`() = runTest {
        // Given
        val orderId = 1
        val items = listOf(
            OrderItemRequest(
                productSku = "MED-001",
                productName = "Jeringa 10ml",
                quantity = 10000,
                unitPrice = 350.0,
                discountPercentage = 0.0,
                taxPercentage = 19.0
            )
        )

        whenever(
            repository.updateOrder(
                orderId = any(),
                customerId = any(),
                items = any(),
                paymentTerms = any(),
                paymentMethod = anyOrNull(),
                deliveryAddress = anyOrNull(),
                deliveryCity = anyOrNull(),
                deliveryDepartment = anyOrNull(),
                deliveryDate = anyOrNull(),
                preferredDistributionCenter = anyOrNull(),
                notes = anyOrNull()
            )
        ).thenReturn(flowOf(Resource.Loading(), Resource.Error("Stock insuficiente")))

        // When & Then
        useCase(
            orderId = orderId,
            customerId = 1,
            items = items,
            paymentTerms = PaymentTerms.CREDIT_30,
            paymentMethod = null,
            deliveryAddress = null,
            deliveryCity = null,
            deliveryDepartment = null,
            deliveryDate = null,
            preferredDistributionCenter = null,
            notes = null
        ).test {
            val result = awaitItem()
            assertTrue(result is Resource.Error)
            assertTrue(result.message?.contains("Stock") == true)
            // End test
        }
    }

        useCase(
            orderId = orderId,
            customerId = 1,
            items = items,
            paymentTerms = PaymentTerms.CREDIT_30,
            paymentMethod = null,
            deliveryAddress = null,
            deliveryCity = null,
            deliveryDepartment = null,
            deliveryDate = null,
            preferredDistributionCenter = null,
            notes = null
        ).test {
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)
            val result = awaitItem()
            assertTrue(result is Resource.Error)
            assertTrue(result.message?.contains("Stock insuficiente") == true)
        }
                discountPercentage = 0.0,
                taxPercentage = 19.0
            )
        )

        whenever(
            repository.updateOrder(
                orderId = any(),
                customerId = any(),
                items = any(),
                paymentTerms = any(),
                paymentMethod = anyOrNull(),
                deliveryAddress = anyOrNull(),
                deliveryCity = anyOrNull(),
                deliveryDepartment = anyOrNull(),
                deliveryDate = anyOrNull(),
                preferredDistributionCenter = anyOrNull(),
                notes = anyOrNull()
            )
        ).thenReturn(flowOf(Resource.Success(testOrder)))

        // When & Then
        useCase(
            orderId = orderId,
            customerId = 1,
            items = items,
            paymentTerms = PaymentTerms.CREDIT_30,
            paymentMethod = null,
            deliveryAddress = null,
            deliveryCity = null,
            deliveryDepartment = null,
            deliveryDate = null,
            preferredDistributionCenter = null,
            notes = null
        ).test {
            val result = awaitItem()
            assertTrue(result is Resource.Success)
            // End test
        }

        verify(repository).updateOrder(
            orderId = eq(orderId),
            customerId = any(),
            items = eq(items),
            paymentTerms = any(),
            paymentMethod = anyOrNull(),
            deliveryAddress = anyOrNull(),
            deliveryCity = anyOrNull(),
            deliveryDepartment = anyOrNull(),
            deliveryDate = anyOrNull(),
            preferredDistributionCenter = anyOrNull(),
            notes = anyOrNull()
        )
    }

    @Test
    fun `invoke returns loading state from repository`() = runTest {
        // Given
        val orderId = 1
        val items = listOf(
            OrderItemRequest(
                productSku = "MED-001",
                productName = "Jeringa 10ml",
                quantity = 150,
                unitPrice = 350.0,
                discountPercentage = 0.0,
                taxPercentage = 19.0
            )
        )

        whenever(
            repository.updateOrder(
                orderId = any(),
                customerId = any(),
                items = any(),
                paymentTerms = any(),
                paymentMethod = anyOrNull(),
                deliveryAddress = anyOrNull(),
                deliveryCity = anyOrNull(),
                deliveryDepartment = anyOrNull(),
                deliveryDate = anyOrNull(),
                preferredDistributionCenter = anyOrNull(),
                notes = anyOrNull()
            )
        ).thenReturn(
            flowOf(
                Resource.Loading(),
                Resource.Success(testOrder)
            )
        )

        // When & Then
        useCase(
            orderId = orderId,
            customerId = 1,
            items = items,
            paymentTerms = PaymentTerms.CREDIT_30,
            paymentMethod = null,
            deliveryAddress = null,
            deliveryCity = null,
            deliveryDepartment = null,
            deliveryDate = null,
            preferredDistributionCenter = null,
            notes = null
        ).test {
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)

            val success = awaitItem()
            assertTrue(success is Resource.Success)

            // End test
        }
    }
}
