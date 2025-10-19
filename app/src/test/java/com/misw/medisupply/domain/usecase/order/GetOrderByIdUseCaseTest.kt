package com.misw.medisupply.domain.usecase.order

import com.google.common.truth.Truth.assertThat
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.customer.CustomerType
import com.misw.medisupply.domain.model.customer.DocumentType
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderItem
import com.misw.medisupply.domain.model.order.OrderStatus
import com.misw.medisupply.domain.model.order.PaymentMethod
import com.misw.medisupply.domain.model.order.PaymentTerms
import com.misw.medisupply.domain.repository.order.OrderRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.Date

/**
 * Unit tests for GetOrderByIdUseCase
 */
class GetOrderByIdUseCaseTest {

    private lateinit var repository: OrderRepository
    private lateinit var useCase: GetOrderByIdUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetOrderByIdUseCase(repository)
    }

    @Test
    fun `invoke calls repository with correct order ID`() = runTest {
        // Given
        val mockOrder = createMockOrder(1)
        coEvery { 
            repository.getOrderById(1) 
        } returns flowOf(Resource.Success(mockOrder))

        // When
        useCase(1).toList()

        // Then
        coVerify { repository.getOrderById(1) }
    }

    @Test
    fun `invoke returns order data from repository`() = runTest {
        // Given
        val mockOrder = createMockOrder(1)
        coEvery { 
            repository.getOrderById(1) 
        } returns flowOf(Resource.Success(mockOrder))

        // When
        val results = useCase(1).toList()

        // Then
        assertThat(results).hasSize(1)
        assertThat(results[0]).isInstanceOf(Resource.Success::class.java)
        
        val successResult = results[0] as Resource.Success
        assertThat(successResult.data?.id).isEqualTo(1)
        assertThat(successResult.data?.orderNumber).isEqualTo("ORD-00001")
    }

    @Test
    fun `invoke propagates loading state from repository`() = runTest {
        // Given
        val mockOrder = createMockOrder(1)
        coEvery { 
            repository.getOrderById(1) 
        } returns flowOf(
            Resource.Loading(),
            Resource.Success(mockOrder)
        )

        // When
        val results = useCase(1).toList()

        // Then
        assertThat(results).hasSize(2)
        assertThat(results[0]).isInstanceOf(Resource.Loading::class.java)
        assertThat(results[1]).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun `invoke propagates error when order not found`() = runTest {
        // Given
        coEvery { 
            repository.getOrderById(999) 
        } returns flowOf(Resource.Error("Pedido no encontrado"))

        // When
        val results = useCase(999).toList()

        // Then
        assertThat(results).hasSize(1)
        assertThat(results[0]).isInstanceOf(Resource.Error::class.java)
        
        val errorResult = results[0] as Resource.Error
        assertThat(errorResult.message).contains("no encontrado")
    }

    private fun createMockOrder(id: Int): Order {
        return Order(
            id = id,
            orderNumber = "ORD-${String.format("%05d", id)}",
            customerId = 1,
            customer = Customer(
                id = 1,
                documentType = DocumentType.NIT,
                documentNumber = "123456789",
                businessName = "Test Customer",
                tradeName = "Test Trade Name",
                customerType = CustomerType.HOSPITAL,
                contactName = "John Doe",
                contactEmail = "test@example.com",
                contactPhone = "1234567890",
                address = "123 Test St",
                city = "Test City",
                department = "Test Dept",
                country = "Colombia",
                creditLimit = 10000.0,
                creditDays = 30,
                isActive = true,
                createdAt = Date(),
                updatedAt = Date()
            ),
            sellerId = "SELLER-001",
            sellerName = "Test Seller",
            orderDate = Date(),
            status = OrderStatus.PENDING,
            subtotal = 1000.0,
            discountAmount = 0.0,
            taxAmount = 190.0,
            totalAmount = 1190.0,
            paymentTerms = PaymentTerms.CREDIT_30,
            paymentMethod = PaymentMethod.TRANSFER,
            deliveryAddress = "123 Delivery St",
            deliveryCity = "Delivery City",
            deliveryDepartment = "Delivery Dept",
            preferredDistributionCenter = "DC-001",
            notes = "Test notes",
            createdAt = Date(),
            updatedAt = Date(),
            items = listOf(
                OrderItem(
                    id = 1,
                    orderId = id,
                    productSku = "TEST-001",
                    productName = "Test Product",
                    quantity = 10,
                    unitPrice = 100.0,
                    discountPercentage = 0.0,
                    discountAmount = 0.0,
                    taxPercentage = 19.0,
                    taxAmount = 190.0,
                    subtotal = 1000.0,
                    total = 1190.0,
                    distributionCenterCode = "DC-001",
                    stockConfirmed = true,
                    stockConfirmationDate = Date(),
                    createdAt = Date()
                )
            )
        )
    }
}
