package com.misw.medisupply.data.repository.order

import app.cash.turbine.test
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.data.remote.api.order.OrderApiService
import com.misw.medisupply.data.remote.dto.order.CreateOrderRequest
import com.misw.medisupply.data.remote.dto.order.OrderDto
import com.misw.medisupply.data.remote.dto.order.OrderItemDto
import com.misw.medisupply.domain.model.order.PaymentMethod
import com.misw.medisupply.domain.model.order.PaymentTerms
import com.misw.medisupply.domain.repository.order.OrderItemRequest
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import retrofit2.Response
import java.io.IOException

class OrderRepositoryImplTest {

    @Mock
    private lateinit var apiService: OrderApiService

    private lateinit var repository: OrderRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = OrderRepositoryImpl(apiService)
    }


    @Test
    fun `createOrder emits Loading then Success when API call succeeds`() = runTest {
        val orderItems = listOf(
            OrderItemRequest(
                productSku = "MED-001",
                quantity = 10,
                discountPercentage = 0.0,
                taxPercentage = 19.0
            )
        )
        val mockOrderItemDto = OrderItemDto(
            id = 1,
            orderId = 1,
            productSku = "MED-001",
            productName = "Test Product",
            quantity = 10,
            unitPrice = 10000.0,
            discountPercentage = 0.0,
            discountAmount = 0.0,
            taxPercentage = 19.0,
            taxAmount = 19000.0,
            subtotal = 100000.0,
            total = 119000.0,
            distributionCenterCode = "DC-BOG",
            stockConfirmed = true,
            stockConfirmationDate = null,
            createdAt = "2024-01-01T00:00:00Z"
        )
        val mockOrderDto = OrderDto(
            id = 1,
            orderNumber = "ORD-001",
            customerId = 1,
            sellerId = "SELLER-001",
            sellerName = "Test Seller",
            orderDate = "2024-01-01T00:00:00Z",
            items = listOf(mockOrderItemDto),
            subtotal = 100000.0,
            discountAmount = 0.0,
            taxAmount = 19000.0,
            totalAmount = 119000.0,
            status = "PENDING",
            paymentTerms = "credito_30",
            paymentMethod = "transferencia",
            deliveryAddress = "Test Address",
            deliveryCity = "Bogotá",
            deliveryDepartment = "Cundinamarca",
            preferredDistributionCenter = "DC-BOG",
            notes = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            customer = null,
            deliveryDate = null
        )
        whenever(apiService.createOrder(any<CreateOrderRequest>()))
            .thenReturn(Response.success(mockOrderDto))

        repository.createOrder(
            customerId = 1,
            sellerId = "SELLER-001",
            sellerName = "Test Seller",
            items = orderItems,
            paymentTerms = PaymentTerms.CREDIT_30,
            paymentMethod = PaymentMethod.TRANSFER,
            deliveryAddress = "Test Address",
            deliveryCity = "Bogotá",
            deliveryDepartment = "Cundinamarca",
            deliveryDate = null,
            preferredDistributionCenter = "DC-BOG",
            notes = null
        ).test {
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)

            val success = awaitItem()
            assertTrue(success is Resource.Success)
            assertEquals("ORD-001", success.data?.orderNumber)
            assertEquals(1, success.data?.items?.size)
            success.data?.totalAmount?.let { assertEquals(119000.0, it, 0.01) }

            awaitComplete()
        }
    }

    @Test
    fun `createOrder emits Error when validation fails`() = runTest {
        val orderItems = listOf(
            OrderItemRequest(
                productSku = "MED-001",
                quantity = 10,
                discountPercentage = 0.0,
                taxPercentage = 19.0
            )
        )
        val errorBody = "Validation failed".toResponseBody("text/plain".toMediaTypeOrNull())
        whenever(apiService.createOrder(any<CreateOrderRequest>()))
            .thenReturn(Response.error(400, errorBody))

        repository.createOrder(
            customerId = 1,
            sellerId = "SELLER-001",
            sellerName = null,
            items = orderItems,
            paymentTerms = PaymentTerms.CREDIT_30,
            paymentMethod = null,
            deliveryAddress = null,
            deliveryCity = null,
            deliveryDepartment = null,
            deliveryDate = null,
            preferredDistributionCenter = null,
            notes = null
        ).test {
            awaitItem()
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertTrue(error.message?.contains("Validación") == true || error.message?.contains("400") == true)
            awaitComplete()
        }
    }

    @Test
    fun `createOrder emits Error when insufficient stock`() = runTest {
        val orderItems = listOf(
            OrderItemRequest(
                productSku = "MED-001",
                quantity = 1000,
                discountPercentage = 0.0,
                taxPercentage = 19.0
            )
        )
        val errorBody = "Insufficient stock".toResponseBody("text/plain".toMediaTypeOrNull())
        whenever(apiService.createOrder(any<CreateOrderRequest>()))
            .thenReturn(Response.error(409, errorBody))

        repository.createOrder(
            customerId = 1,
            sellerId = "SELLER-001",
            sellerName = null,
            items = orderItems,
            paymentTerms = PaymentTerms.CREDIT_30,
            paymentMethod = null,
            deliveryAddress = null,
            deliveryCity = null,
            deliveryDepartment = null,
            deliveryDate = null,
            preferredDistributionCenter = null,
            notes = null
        ).test {
            awaitItem()
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertTrue(error.message?.contains("Stock") == true || error.message?.contains("409") == true)
            awaitComplete()
        }
    }

    @Test
    fun `createOrder emits Error when customer not found`() = runTest {
        val orderItems = listOf(
            OrderItemRequest(
                productSku = "MED-001",
                quantity = 10,
                discountPercentage = 0.0,
                taxPercentage = 19.0
            )
        )
        val errorBody = "Customer not found".toResponseBody("text/plain".toMediaTypeOrNull())
        whenever(apiService.createOrder(any<CreateOrderRequest>()))
            .thenReturn(Response.error(404, errorBody))

        repository.createOrder(
            customerId = 999,
            sellerId = "SELLER-001",
            sellerName = null,
            items = orderItems,
            paymentTerms = PaymentTerms.CREDIT_30,
            paymentMethod = null,
            deliveryAddress = null,
            deliveryCity = null,
            deliveryDepartment = null,
            deliveryDate = null,
            preferredDistributionCenter = null,
            notes = null
        ).test {
            awaitItem()
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertTrue(error.message?.contains("encontrado") == true || error.message?.contains("404") == true)
            awaitComplete()
        }
    }

    @Test
    fun `createOrder emits Error when IOException occurs`() = runTest {
        val orderItems = listOf(
            OrderItemRequest(
                productSku = "MED-001",
                quantity = 10,
                discountPercentage = 0.0,
                taxPercentage = 19.0
            )
        )
        whenever(apiService.createOrder(any<CreateOrderRequest>()))
            .thenThrow(RuntimeException(IOException("Network error")))

        repository.createOrder(
            customerId = 1,
            sellerId = "SELLER-001",
            sellerName = null,
            items = orderItems,
            paymentTerms = PaymentTerms.CREDIT_30,
            paymentMethod = null,
            deliveryAddress = null,
            deliveryCity = null,
            deliveryDepartment = null,
            deliveryDate = null,
            preferredDistributionCenter = null,
            notes = null
        ).test {
            awaitItem()
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertNotNull(error.message)
            awaitComplete()
        }
    }

    @Test
    fun `createOrder with multiple items maps correctly`() = runTest {
        val orderItems = listOf(
            OrderItemRequest("MED-001", 10, 0.0, 19.0),
            OrderItemRequest("MED-002", 5, 5.0, 19.0),
            OrderItemRequest("EQUIP-001", 2, 10.0, 16.0)
        )
        val mockOrderDto = OrderDto(
            id = 1,
            orderNumber = "ORD-001",
            customerId = 1,
            sellerId = "SELLER-001",
            sellerName = null,
            orderDate = "2024-01-01T00:00:00Z",
            items = emptyList(),
            subtotal = 150000.0,
            discountAmount = 5000.0,
            taxAmount = 28000.0,
            totalAmount = 173000.0,
            status = "PENDING",
            paymentTerms = "credito_30",
            paymentMethod = null,
            deliveryAddress = null,
            deliveryCity = null,
            deliveryDepartment = null,
            preferredDistributionCenter = null,
            notes = "Test order",
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            customer = null,
            deliveryDate = null
        )
        whenever(apiService.createOrder(any<CreateOrderRequest>()))
            .thenReturn(Response.success(mockOrderDto))

        repository.createOrder(
            customerId = 1,
            sellerId = "SELLER-001",
            sellerName = null,
            items = orderItems,
            paymentTerms = PaymentTerms.CREDIT_30,
            paymentMethod = null,
            deliveryAddress = null,
            deliveryCity = null,
            deliveryDepartment = null,
            deliveryDate = null,
            preferredDistributionCenter = null,
            notes = "Test order"
        ).test {
            awaitItem()
            val success = awaitItem()
            assertTrue(success is Resource.Success)
            assertEquals("ORD-001", success.data?.orderNumber)
            awaitComplete()
        }
    }
}
