package com.misw.medisupply.domain.usecase.order

import app.cash.turbine.test
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.order.Order
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
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Date

class CreateOrderUseCaseTest {

    private lateinit var repository: OrderRepository
    private lateinit var useCase: CreateOrderUseCase

    private val testOrderItems = listOf(
        OrderItemRequest(
            productSku = "MED-001",
            quantity = 10,
            discountPercentage = 0.0,
            taxPercentage = 19.0
        ),
        OrderItemRequest(
            productSku = "MED-002",
            quantity = 5,
            discountPercentage = 0.0,
            taxPercentage = 19.0
        )
    )

    private val testOrder = Order(
        id = 1,
        orderNumber = "ORD-2024-001",
        customerId = 1,
        sellerId = "seller123",
        sellerName = "John Seller",
        orderDate = Date(),
        status = OrderStatus.PENDING,
        subtotal = 1000.0,
        discountAmount = 100.0,
        taxAmount = 50.0,
        totalAmount = 950.0,
        paymentTerms = PaymentTerms.CASH,
        paymentMethod = PaymentMethod.CASH,
        deliveryAddress = "Calle 123",
        deliveryCity = "Bogotá",
        deliveryDepartment = "Cundinamarca",
        preferredDistributionCenter = "DC-001",
        notes = "Test order",
        createdAt = Date(),
        updatedAt = Date(),
        customer = null,
        items = emptyList()
    )

    @Before
    fun setup() {
        repository = mock()
        useCase = CreateOrderUseCase(repository)
    }

    @Test
    fun `invoke with valid parameters returns success with created order`() = runTest {
        whenever(repository.createOrder(
            customerId = 1,
            sellerId = "seller123",
            sellerName = "John Seller",
            items = testOrderItems,
            paymentTerms = PaymentTerms.CASH,
            paymentMethod = PaymentMethod.CASH,
            deliveryAddress = "Calle 123",
            deliveryCity = "Bogotá",
            deliveryDepartment = "Cundinamarca",
            deliveryDate = null,
            preferredDistributionCenter = "DC-001",
            notes = "Test order"
        )).thenReturn(flowOf(Resource.Success(testOrder)))

        useCase.invoke(
            customerId = 1,
            sellerId = "seller123",
            sellerName = "John Seller",
            items = testOrderItems,
            paymentTerms = PaymentTerms.CASH,
            paymentMethod = PaymentMethod.CASH,
            deliveryAddress = "Calle 123",
            deliveryCity = "Bogotá",
            deliveryDepartment = "Cundinamarca",
            deliveryDate = null,
            preferredDistributionCenter = "DC-001",
            notes = "Test order"
        ).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            assertEquals("ORD-2024-001", (result as Resource.Success).data!!.orderNumber)
            assertEquals(1, result.data!!.customerId)
            assertEquals("seller123", result.data!!.sellerId)
            assertEquals(OrderStatus.PENDING, result.data!!.status)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with minimal parameters uses defaults correctly`() = runTest {
        whenever(repository.createOrder(
            customerId = any(),
            sellerId = any(),
            sellerName = anyOrNull(),
            items = any(),
            paymentTerms = any(),
            paymentMethod = anyOrNull(),
            deliveryAddress = anyOrNull(),
            deliveryCity = anyOrNull(),
            deliveryDepartment = anyOrNull(),
            deliveryDate = anyOrNull(),
            preferredDistributionCenter = anyOrNull(),
            notes = anyOrNull()
        )).thenReturn(flowOf(Resource.Success(testOrder)))

        useCase.invoke(
            customerId = 1,
            sellerId = "seller123",
            items = testOrderItems
        ).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            awaitComplete()
        }

        verify(repository).createOrder(
            customerId = 1,
            sellerId = "seller123",
            sellerName = null,
            items = testOrderItems,
            paymentTerms = PaymentTerms.CASH,
            paymentMethod = null,
            deliveryAddress = null,
            deliveryCity = null,
            deliveryDepartment = null,
            deliveryDate = null,
            preferredDistributionCenter = null,
            notes = null
        )
    }

    @Test
    fun `invoke with credit payment terms creates order correctly`() = runTest {
        val creditOrder = testOrder.copy(paymentTerms = PaymentTerms.CREDIT_30)
        whenever(repository.createOrder(
            customerId = any(),
            sellerId = any(),
            sellerName = anyOrNull(),
            items = any(),
            paymentTerms = any(),
            paymentMethod = anyOrNull(),
            deliveryAddress = anyOrNull(),
            deliveryCity = anyOrNull(),
            deliveryDepartment = anyOrNull(),
            deliveryDate = anyOrNull(),
            preferredDistributionCenter = anyOrNull(),
            notes = anyOrNull()
        )).thenReturn(flowOf(Resource.Success(creditOrder)))

        useCase.invoke(
            customerId = 1,
            sellerId = "seller123",
            items = testOrderItems,
            paymentTerms = PaymentTerms.CREDIT_30
        ).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            assertEquals(PaymentTerms.CREDIT_30, (result as Resource.Success).data!!.paymentTerms)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with transfer payment method creates order correctly`() = runTest {
        val transferOrder = testOrder.copy(paymentMethod = PaymentMethod.TRANSFER)
        whenever(repository.createOrder(
            customerId = any(),
            sellerId = any(),
            sellerName = anyOrNull(),
            items = any(),
            paymentTerms = any(),
            paymentMethod = any(),
            deliveryAddress = anyOrNull(),
            deliveryCity = anyOrNull(),
            deliveryDepartment = anyOrNull(),
            deliveryDate = anyOrNull(),
            preferredDistributionCenter = anyOrNull(),
            notes = anyOrNull()
        )).thenReturn(flowOf(Resource.Success(transferOrder)))

        useCase.invoke(
            customerId = 1,
            sellerId = "seller123",
            items = testOrderItems,
            paymentMethod = PaymentMethod.TRANSFER
        ).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            assertEquals(PaymentMethod.TRANSFER, (result as Resource.Success).data!!.paymentMethod)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with single item creates order correctly`() = runTest {
        val singleItem = listOf(testOrderItems[0])
        whenever(repository.createOrder(
            customerId = any(),
            sellerId = any(),
            sellerName = anyOrNull(),
            items = any(),
            paymentTerms = any(),
            paymentMethod = anyOrNull(),
            deliveryAddress = anyOrNull(),
            deliveryCity = anyOrNull(),
            deliveryDepartment = anyOrNull(),
            deliveryDate = anyOrNull(),
            preferredDistributionCenter = anyOrNull(),
            notes = anyOrNull()
        )).thenReturn(flowOf(Resource.Success(testOrder)))

        useCase.invoke(
            customerId = 1,
            sellerId = "seller123",
            items = singleItem
        ).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with multiple items creates order correctly`() = runTest {
        val multipleItems = testOrderItems + OrderItemRequest(
            productSku = "MED-003",
            quantity = 15,
            discountPercentage = 0.0,
            taxPercentage = 19.0
        )
        whenever(repository.createOrder(
            customerId = any(),
            sellerId = any(),
            sellerName = anyOrNull(),
            items = any(),
            paymentTerms = any(),
            paymentMethod = anyOrNull(),
            deliveryAddress = anyOrNull(),
            deliveryCity = anyOrNull(),
            deliveryDepartment = anyOrNull(),
            deliveryDate = anyOrNull(),
            preferredDistributionCenter = anyOrNull(),
            notes = anyOrNull()
        )).thenReturn(flowOf(Resource.Success(testOrder)))

        useCase.invoke(
            customerId = 1,
            sellerId = "seller123",
            items = multipleItems
        ).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            awaitComplete()
        }

        verify(repository).createOrder(
            customerId = 1,
            sellerId = "seller123",
            sellerName = null,
            items = multipleItems,
            paymentTerms = PaymentTerms.CASH,
            paymentMethod = null,
            deliveryAddress = null,
            deliveryCity = null,
            deliveryDepartment = null,
            deliveryDate = null,
            preferredDistributionCenter = null,
            notes = null
        )
    }

    @Test
    fun `invoke with delivery information creates order correctly`() = runTest {
        whenever(repository.createOrder(
            customerId = any(),
            sellerId = any(),
            sellerName = anyOrNull(),
            items = any(),
            paymentTerms = any(),
            paymentMethod = anyOrNull(),
            deliveryAddress = any(),
            deliveryCity = any(),
            deliveryDepartment = any(),
            deliveryDate = anyOrNull(),
            preferredDistributionCenter = anyOrNull(),
            notes = anyOrNull()
        )).thenReturn(flowOf(Resource.Success(testOrder)))

        useCase.invoke(
            customerId = 1,
            sellerId = "seller123",
            items = testOrderItems,
            deliveryAddress = "Calle 123",
            deliveryCity = "Bogotá",
            deliveryDepartment = "Cundinamarca"
        ).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            assertEquals("Calle 123", (result as Resource.Success).data!!.deliveryAddress)
            assertEquals("Bogotá", result.data!!.deliveryCity)
            assertEquals("Cundinamarca", result.data!!.deliveryDepartment)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with preferred distribution center creates order correctly`() = runTest {
        whenever(repository.createOrder(
            customerId = any(),
            sellerId = any(),
            sellerName = anyOrNull(),
            items = any(),
            paymentTerms = any(),
            paymentMethod = anyOrNull(),
            deliveryAddress = anyOrNull(),
            deliveryCity = anyOrNull(),
            deliveryDepartment = anyOrNull(),
            deliveryDate = anyOrNull(),
            preferredDistributionCenter = any(),
            notes = anyOrNull()
        )).thenReturn(flowOf(Resource.Success(testOrder)))

        useCase.invoke(
            customerId = 1,
            sellerId = "seller123",
            items = testOrderItems,
            preferredDistributionCenter = "DC-001"
        ).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            assertEquals("DC-001", (result as Resource.Success).data!!.preferredDistributionCenter)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        val errorMessage = "Failed to create order"
        whenever(repository.createOrder(
            customerId = any(),
            sellerId = any(),
            sellerName = anyOrNull(),
            items = any(),
            paymentTerms = any(),
            paymentMethod = anyOrNull(),
            deliveryAddress = anyOrNull(),
            deliveryCity = anyOrNull(),
            deliveryDepartment = anyOrNull(),
            deliveryDate = anyOrNull(),
            preferredDistributionCenter = anyOrNull(),
            notes = anyOrNull()
        )).thenReturn(flowOf(Resource.Error(errorMessage)))

        useCase.invoke(
            customerId = 1,
            sellerId = "seller123",
            items = testOrderItems
        ).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Error)
            assertEquals(errorMessage, (result as Resource.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns loading state from repository`() = runTest {
        whenever(repository.createOrder(
            customerId = any(),
            sellerId = any(),
            sellerName = anyOrNull(),
            items = any(),
            paymentTerms = any(),
            paymentMethod = anyOrNull(),
            deliveryAddress = anyOrNull(),
            deliveryCity = anyOrNull(),
            deliveryDepartment = anyOrNull(),
            deliveryDate = anyOrNull(),
            preferredDistributionCenter = anyOrNull(),
            notes = anyOrNull()
        )).thenReturn(flowOf(Resource.Loading()))

        useCase.invoke(
            customerId = 1,
            sellerId = "seller123",
            items = testOrderItems
        ).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Loading)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with notes creates order correctly`() = runTest {
        val notesText = "Special handling required"
        whenever(repository.createOrder(
            customerId = any(),
            sellerId = any(),
            sellerName = anyOrNull(),
            items = any(),
            paymentTerms = any(),
            paymentMethod = anyOrNull(),
            deliveryAddress = anyOrNull(),
            deliveryCity = anyOrNull(),
            deliveryDepartment = anyOrNull(),
            deliveryDate = anyOrNull(),
            preferredDistributionCenter = anyOrNull(),
            notes = any()
        )).thenReturn(flowOf(Resource.Success(testOrder.copy(notes = notesText))))

        useCase.invoke(
            customerId = 1,
            sellerId = "seller123",
            items = testOrderItems,
            notes = notesText
        ).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            assertEquals(notesText, (result as Resource.Success).data!!.notes)
            awaitComplete()
        }
    }
}
