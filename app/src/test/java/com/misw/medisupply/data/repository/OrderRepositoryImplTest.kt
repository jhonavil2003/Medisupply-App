package com.misw.medisupply.data.repository

import com.google.common.truth.Truth.assertThat
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.data.remote.api.order.OrderApiService
import com.misw.medisupply.data.remote.dto.customer.CustomerDto
import com.misw.medisupply.data.remote.dto.order.OrderDto
import com.misw.medisupply.data.remote.dto.order.OrderItemDto
import com.misw.medisupply.data.remote.dto.order.OrdersResponse
import com.misw.medisupply.data.repository.order.OrderRepositoryImpl
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.customer.CustomerType
import com.misw.medisupply.domain.model.customer.DocumentType
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderItem
import com.misw.medisupply.domain.model.order.OrderStatus
import com.misw.medisupply.domain.model.order.PaymentMethod
import com.misw.medisupply.domain.model.order.PaymentTerms
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.util.Date

/**
 * Unit tests for OrderRepositoryImpl
 * Tests API calls and error handling without requiring backend
 */
class OrderRepositoryImplTest {

    private lateinit var apiService: OrderApiService
    private lateinit var repository: OrderRepositoryImpl

    @Before
    fun setup() {
        apiService = mockk()
        repository = OrderRepositoryImpl(apiService)
    }

    // ============ GET ORDERS TESTS ============

    @Test
    fun `getOrders emits Loading then Success when API call succeeds`() = runTest {
        // Given
        val mockOrderDtos = listOf(createMockOrderDto(1), createMockOrderDto(2))
        val mockResponse = OrdersResponse(
            orders = mockOrderDtos,
            total = 2
        )
        coEvery { 
            apiService.getOrders(any(), any(), any()) 
        } returns Response.success(mockResponse)

        // When
        val results = repository.getOrders(
            sellerId = "SELLER-001",
            customerId = null,
            status = null
        ).toList()

        // Then
        assertThat(results).hasSize(2)
        assertThat(results[0]).isInstanceOf(Resource.Loading::class.java)
        assertThat(results[1]).isInstanceOf(Resource.Success::class.java)
        
        val successResult = results[1] as Resource.Success<List<Order>>
        assertThat(successResult.data).hasSize(2)
        
        coVerify { apiService.getOrders("SELLER-001", null, null) }
    }

    @Test
    fun `getOrders emits Error when API call fails`() = runTest {
        // Given
        coEvery { 
            apiService.getOrders(any(), any(), any()) 
        } returns Response.error(500, "Server Error".toResponseBody())

        // When
        val results = repository.getOrders("SELLER-001", null, null).toList()

        // Then
        assertThat(results).hasSize(2)
        assertThat(results[0]).isInstanceOf(Resource.Loading::class.java)
        assertThat(results[1]).isInstanceOf(Resource.Error::class.java)
        
        val errorResult = results[1] as Resource.Error<List<Order>>
        assertThat(errorResult.message).contains("Error")
    }

    @Test
    fun `getOrders with filters passes correct parameters to API`() = runTest {
        // Given
        val mockResponse = OrdersResponse(orders = emptyList(), total = 0)
        coEvery { 
            apiService.getOrders(any(), any(), any()) 
        } returns Response.success(mockResponse)

        // When
        repository.getOrders(
            sellerId = "SELLER-001",
            customerId = 123,
            status = "pending"
        ).toList()

        // Then
        coVerify { 
            apiService.getOrders("SELLER-001", 123, "pending") 
        }
    }

    // ============ GET ORDER BY ID TESTS ============

    @Test
    fun `getOrderById emits Loading then Success when API call succeeds`() = runTest {
        // Given
        val mockOrderDto = createMockOrderDto(1)
        coEvery { 
            apiService.getOrderById(1) 
        } returns Response.success(mockOrderDto)

        // When
        val results = repository.getOrderById(1).toList()

        // Then
        assertThat(results).hasSize(2)
        assertThat(results[0]).isInstanceOf(Resource.Loading::class.java)
        assertThat(results[1]).isInstanceOf(Resource.Success::class.java)
        
        val successResult = results[1] as Resource.Success<Order>
        assertThat(successResult.data?.id).isEqualTo(1)
        
        coVerify { apiService.getOrderById(1) }
    }

    @Test
    fun `getOrderById emits Error with 404 message when order not found`() = runTest {
        // Given
        coEvery { 
            apiService.getOrderById(999) 
        } returns Response.error(404, "Not Found".toResponseBody())

        // When
        val results = repository.getOrderById(999).toList()

        // Then
        assertThat(results).hasSize(2)
        assertThat(results[1]).isInstanceOf(Resource.Error::class.java)
        
        val errorResult = results[1] as Resource.Error
        assertThat(errorResult.message).contains("no encontrado")
    }

    @Test
    fun `getOrderById emits Error when network exception occurs`() = runTest {
        // Given
        coEvery { 
            apiService.getOrderById(any()) 
        } throws Exception("Network error")

        // When
        val results = repository.getOrderById(1).toList()

        // Then
        assertThat(results).hasSize(2)
        assertThat(results[1]).isInstanceOf(Resource.Error::class.java)
        
        val errorResult = results[1] as Resource.Error
        assertThat(errorResult.message).contains("Network error")
    }

    // ============ DELETE ORDER TESTS ============

    @Test
    fun `deleteOrder emits Loading then Success when API call succeeds`() = runTest {
        // Given
        coEvery { 
            apiService.deleteOrder(1) 
        } returns Response.success(Unit)

        // When
        val results = repository.deleteOrder(1).toList()

        // Then
        assertThat(results).hasSize(2)
        assertThat(results[0]).isInstanceOf(Resource.Loading::class.java)
        assertThat(results[1]).isInstanceOf(Resource.Success::class.java)
        
        coVerify { apiService.deleteOrder(1) }
    }

    @Test
    fun `deleteOrder emits Error with 400 when order status invalid`() = runTest {
        // Given
        coEvery { 
            apiService.deleteOrder(1) 
        } returns Response.error(400, "Bad Request".toResponseBody())

        // When
        val results = repository.deleteOrder(1).toList()

        // Then
        assertThat(results).hasSize(2)
        assertThat(results[1]).isInstanceOf(Resource.Error::class.java)
        
        val errorResult = results[1] as Resource.Error
        assertThat(errorResult.message).contains("Pendiente o Cancelado")
    }

    @Test
    fun `deleteOrder emits Error with 403 when user lacks permission`() = runTest {
        // Given
        coEvery { 
            apiService.deleteOrder(1) 
        } returns Response.error(403, "Forbidden".toResponseBody())

        // When
        val results = repository.deleteOrder(1).toList()

        // Then
        assertThat(results).hasSize(2)
        assertThat(results[1]).isInstanceOf(Resource.Error::class.java)
        
        val errorResult = results[1] as Resource.Error
        assertThat(errorResult.message).contains("No tiene permisos")
    }

    @Test
    fun `deleteOrder emits Error with 404 when order not found`() = runTest {
        // Given
        coEvery { 
            apiService.deleteOrder(999) 
        } returns Response.error(404, "Not Found".toResponseBody())

        // When
        val results = repository.deleteOrder(999).toList()

        // Then
        assertThat(results).hasSize(2)
        assertThat(results[1]).isInstanceOf(Resource.Error::class.java)
        
        val errorResult = results[1] as Resource.Error
        assertThat(errorResult.message).contains("no encontrado")
    }

    @Test
    fun `deleteOrder emits Error with 500 when server error occurs`() = runTest {
        // Given
        coEvery { 
            apiService.deleteOrder(1) 
        } returns Response.error(500, "Server Error".toResponseBody())

        // When
        val results = repository.deleteOrder(1).toList()

        // Then
        assertThat(results).hasSize(2)
        assertThat(results[1]).isInstanceOf(Resource.Error::class.java)
        
        val errorResult = results[1] as Resource.Error
        assertThat(errorResult.message).contains("Error en el servidor")
    }

    // ============ HELPER METHODS ============

    private fun createMockOrder(id: Int): Order {
        return Order(
            id = id,
            orderNumber = "ORD-${String.format("%05d", id)}",
            customerId = 1,
            customer = Customer(
                id = 1,
                documentType = DocumentType.NIT,
                documentNumber = "123456789",
                businessName = "Test Customer $id",
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

    private fun createMockOrderDto(id: Int): OrderDto {
        return OrderDto(
            id = id,
            orderNumber = "ORD-${String.format("%05d", id)}",
            customerId = 1,
            sellerId = "SELLER-001",
            sellerName = "Test Seller",
            orderDate = "2024-01-01T00:00:00.000Z",
            status = "pending",
            subtotal = 1000.0,
            discountAmount = 0.0,
            taxAmount = 190.0,
            totalAmount = 1190.0,
            paymentTerms = "credito_30",
            paymentMethod = "transferencia",
            deliveryAddress = "123 Delivery St",
            deliveryCity = "Delivery City",
            deliveryDepartment = "Delivery Dept",
            preferredDistributionCenter = "DC-001",
            notes = "Test notes",
            createdAt = "2024-01-01T00:00:00.000Z",
            updatedAt = "2024-01-01T00:00:00.000Z",
            customer = CustomerDto(
                id = 1,
                documentType = "NIT",
                documentNumber = "123456789",
                businessName = "Test Customer",
                tradeName = "Test Trade Name",
                customerType = "hospital",
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
                createdAt = "2024-01-01T00:00:00.000Z",
                updatedAt = "2024-01-01T00:00:00.000Z"
            ),
            items = listOf(
                OrderItemDto(
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
                    stockConfirmationDate = "2024-01-01T00:00:00.000Z",
                    createdAt = "2024-01-01T00:00:00.000Z"
                )
            )
        )
    }
}
