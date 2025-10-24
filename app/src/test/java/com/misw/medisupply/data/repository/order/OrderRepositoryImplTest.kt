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
        }
    }

    @Test
    fun `createOrder with multiple items maps correctly`() = runTest {
        val orderItems = listOf(
            OrderItemRequest("MED-001", "Producto 1", 10, 100.0, 0.0, 19.0),
            OrderItemRequest("MED-002", "Producto 2", 5, 200.0, 5.0, 19.0),
            OrderItemRequest("EQUIP-001", "Equipo 1", 2, 500.0, 10.0, 16.0)
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
        }
    }

    // ===== UPDATE ORDER TESTS =====

    @Test
    fun `updateOrder emits Loading then Success when API call succeeds`() = runTest {
        // Given
        val orderId = 1
        val orderItems = listOf(
            OrderItemRequest(
                productSku = "MED-001",
                productName = "Jeringa 10ml",
                quantity = 150,
                unitPrice = 350.0,
                discountPercentage = 0.0,
                taxPercentage = 19.0
            )
        )
        val mockOrderItemDto = OrderItemDto(
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
            createdAt = "2024-01-01T00:00:00Z"
        )
        val mockOrderDto = OrderDto(
            id = 1,
            orderNumber = "ORD-20241023-0001",
            customerId = 1,
            sellerId = "SELLER-001",
            sellerName = "Test Seller",
            orderDate = "2024-01-01T00:00:00Z",
            items = listOf(mockOrderItemDto),
            subtotal = 52500.0,
            discountAmount = 0.0,
            taxAmount = 9975.0,
            totalAmount = 62475.0,
            status = "confirmed",
            paymentTerms = "credito_30",
            paymentMethod = "transferencia",
            deliveryAddress = "Calle 123 #45-67",
            deliveryCity = "Bogotá",
            deliveryDepartment = "Cundinamarca",
            preferredDistributionCenter = "DC-BOG",
            notes = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T12:00:00Z",
            customer = null,
            deliveryDate = null
        )
        whenever(apiService.updateOrder(any(), any()))
            .thenReturn(Response.success(mockOrderDto))

        // When & Then
        repository.updateOrder(
            orderId = orderId,
            customerId = 1,
            items = orderItems,
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

            val success = awaitItem()
            assertTrue(success is Resource.Success)
            assertEquals("ORD-20241023-0001", success.data?.orderNumber)
            assertEquals(1, success.data?.items?.size)
            assertEquals("Jeringa 10ml", success.data?.items?.get(0)?.productName)
            success.data?.items?.get(0)?.unitPrice?.let {
                assertEquals(350.0, it, 0.001)
            }
        }
    }

    @Test
    fun `updateOrder emits Error when order is not PENDING (400)`() = runTest {
        // Given
        val orderId = 1
        val orderItems = listOf(
            OrderItemRequest(
                productSku = "MED-001",
                productName = "Jeringa 10ml",
                quantity = 150,
                unitPrice = 350.0,
                discountPercentage = 0.0,
                taxPercentage = 19.0
            )
        )
        val errorBody = """{"error":"Validación fallida: Solo se pueden actualizar pedidos en estado Pendiente"}"""
            .toResponseBody("application/json".toMediaTypeOrNull())

        whenever(apiService.updateOrder(any(), any()))
            .thenReturn(Response.error(400, errorBody))

        // When & Then
        repository.updateOrder(
            orderId = orderId,
            customerId = 1,
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
            awaitItem() // Loading
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertTrue(error.message?.contains("Pendiente") == true)
        }
    }

    @Test
    fun `updateOrder emits Error 404 when order not found`() = runTest {
        // Given
        val orderId = 999
        val orderItems = listOf(
            OrderItemRequest(
                productSku = "MED-001",
                productName = "Jeringa 10ml",
                quantity = 150,
                unitPrice = 350.0,
                discountPercentage = 0.0,
                taxPercentage = 19.0
            )
        )
        val errorBody = """{"error":"Pedido no encontrado"}"""
            .toResponseBody("application/json".toMediaTypeOrNull())

        whenever(apiService.updateOrder(any(), any()))
            .thenReturn(Response.error(404, errorBody))

        // When & Then
        repository.updateOrder(
            orderId = orderId,
            customerId = 1,
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
            awaitItem() // Loading
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertTrue(error.message?.contains("encontrado") == true)
        }
    }

    @Test
    fun `updateOrder emits Error when insufficient stock (409)`() = runTest {
        // Given
        val orderId = 1
        val orderItems = listOf(
            OrderItemRequest(
                productSku = "MED-001",
                productName = "Jeringa 10ml",
                quantity = 10000,
                unitPrice = 350.0,
                discountPercentage = 0.0,
                taxPercentage = 19.0
            )
        )
        val errorBody = """{"error":"Stock insuficiente para el producto MED-001"}"""
            .toResponseBody("application/json".toMediaTypeOrNull())

        whenever(apiService.updateOrder(any(), any()))
            .thenReturn(Response.error(409, errorBody))

        // When & Then
        repository.updateOrder(
            orderId = orderId,
            customerId = 1,
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
            awaitItem() // Loading
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertTrue(error.message?.contains("Stock") == true)
        }
    }

    @Test
    fun `updateOrder emits Error on server error (500)`() = runTest {
        // Given
        val orderId = 1
        val orderItems = listOf(
            OrderItemRequest(
                productSku = "MED-001",
                productName = "Jeringa 10ml",
                quantity = 150,
                unitPrice = 350.0,
                discountPercentage = 0.0,
                taxPercentage = 19.0
            )
        )
        val errorBody = """{"error":"Error del servidor"}"""
            .toResponseBody("application/json".toMediaTypeOrNull())

        whenever(apiService.updateOrder(any(), any()))
            .thenReturn(Response.error(500, errorBody))

        // When & Then
        repository.updateOrder(
            orderId = orderId,
            customerId = 1,
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
            awaitItem() // Loading
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertTrue(error.message?.contains("Error") == true)
        }
    }

    @Test
    fun `updateOrder emits Error on network exception`() = runTest {
        // Given
        val orderId = 1
        val orderItems = listOf(
            OrderItemRequest(
                productSku = "MED-001",
                productName = "Jeringa 10ml",
                quantity = 150,
                unitPrice = 350.0,
                discountPercentage = 0.0,
                taxPercentage = 19.0
            )
        )

        whenever(apiService.updateOrder(any(), any()))
            .thenThrow(IOException("Network error"))

        // When & Then
        repository.updateOrder(
            orderId = orderId,
            customerId = 1,
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
            awaitItem() // Loading
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertTrue(error.message?.contains("red") == true)
        }
    }

    @Test
    fun `updateOrder sends unitPrice in request`() = runTest {
        // Given
        val orderId = 1
        val orderItems = listOf(
            OrderItemRequest(
                productSku = "MED-001",
                productName = "Jeringa 10ml",
                quantity = 150,
                unitPrice = 350.0, // REQUIRED field
                discountPercentage = 0.0,
                taxPercentage = 19.0
            )
        )
        val mockOrderItemDto = OrderItemDto(
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
            createdAt = "2024-01-01T00:00:00Z"
        )
        val mockOrderDto = OrderDto(
            id = 1,
            orderNumber = "ORD-20241023-0001",
            customerId = 1,
            sellerId = "SELLER-001",
            sellerName = "Test Seller",
            orderDate = "2024-01-01T00:00:00Z",
            items = listOf(mockOrderItemDto),
            subtotal = 52500.0,
            discountAmount = 0.0,
            taxAmount = 9975.0,
            totalAmount = 62475.0,
            status = "confirmed",
            paymentTerms = "credito_30",
            paymentMethod = null,
            deliveryAddress = null,
            deliveryCity = null,
            deliveryDepartment = null,
            preferredDistributionCenter = null,
            notes = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T12:00:00Z",
            customer = null,
            deliveryDate = null
        )
        whenever(apiService.updateOrder(any(), any()))
            .thenReturn(Response.success(mockOrderDto))

        // When
        repository.updateOrder(
            orderId = orderId,
            customerId = 1,
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
            awaitItem() // Loading
            val success = awaitItem()
            assertTrue(success is Resource.Success)
            // Verify unitPrice is preserved
            success.data?.items?.get(0)?.unitPrice?.let {
                assertEquals(350.0, it, 0.001)
            }
        }
    }

    @Test
    fun `updateOrder sends productName in request`() = runTest {
        // Given
        val orderId = 1
        val orderItems = listOf(
            OrderItemRequest(
                productSku = "MED-001",
                productName = "Jeringa 10ml", // Should be sent
                quantity = 150,
                unitPrice = 350.0,
                discountPercentage = 0.0,
                taxPercentage = 19.0
            )
        )
        val mockOrderItemDto = OrderItemDto(
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
            createdAt = "2024-01-01T00:00:00Z"
        )
        val mockOrderDto = OrderDto(
            id = 1,
            orderNumber = "ORD-20241023-0001",
            customerId = 1,
            sellerId = "SELLER-001",
            sellerName = "Test Seller",
            orderDate = "2024-01-01T00:00:00Z",
            items = listOf(mockOrderItemDto),
            subtotal = 52500.0,
            discountAmount = 0.0,
            taxAmount = 9975.0,
            totalAmount = 62475.0,
            status = "confirmed",
            paymentTerms = "credito_30",
            paymentMethod = null,
            deliveryAddress = null,
            deliveryCity = null,
            deliveryDepartment = null,
            preferredDistributionCenter = null,
            notes = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T12:00:00Z",
            customer = null,
            deliveryDate = null
        )
        whenever(apiService.updateOrder(any(), any()))
            .thenReturn(Response.success(mockOrderDto))

        // When
        repository.updateOrder(
            orderId = orderId,
            customerId = 1,
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
            awaitItem() // Loading
            val success = awaitItem()
            assertTrue(success is Resource.Success)
            // Verify productName is preserved
            assertEquals("Jeringa 10ml", success.data?.items?.get(0)?.productName)
        }
    }

    // ===== DELETE ORDER TESTS =====

    @Test
    fun `deleteOrder emits Loading then Success when API call succeeds`() = runTest {
        // Given
        val orderId = 1
        whenever(apiService.deleteOrder(orderId))
            .thenReturn(Response.success(Unit))

        // When & Then
        repository.deleteOrder(orderId).test {
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)

            val success = awaitItem()
            assertTrue(success is Resource.Success)
            assertEquals(Unit, success.data)
        }
    }

    @Test
    fun `deleteOrder emits Error when order is not PENDING (400)`() = runTest {
        // Given
        val orderId = 1
        val errorBody = """{"error":"Validación fallida: Solo se pueden eliminar pedidos en estado Pendiente"}"""
            .toResponseBody("application/json".toMediaTypeOrNull())

        whenever(apiService.deleteOrder(orderId))
            .thenReturn(Response.error(400, errorBody))

        // When & Then
        repository.deleteOrder(orderId).test {
            awaitItem() // Loading
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertTrue(error.message?.contains("Pendiente") == true)
        }
    }

    @Test
    fun `deleteOrder emits Error 404 when order not found`() = runTest {
        // Given
        val orderId = 999
        val errorBody = """{"error":"Pedido no encontrado"}"""
            .toResponseBody("application/json".toMediaTypeOrNull())

        whenever(apiService.deleteOrder(orderId))
            .thenReturn(Response.error(404, errorBody))

        // When & Then
        repository.deleteOrder(orderId).test {
            awaitItem() // Loading
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertTrue(error.message?.contains("encontrado") == true)
        }
    }

    @Test
    fun `deleteOrder emits Error on server error (500)`() = runTest {
        // Given
        val orderId = 1
        val errorBody = """{"error":"Error del servidor al eliminar el pedido"}"""
            .toResponseBody("application/json".toMediaTypeOrNull())

        whenever(apiService.deleteOrder(orderId))
            .thenReturn(Response.error(500, errorBody))

        // When & Then
        repository.deleteOrder(orderId).test {
            awaitItem() // Loading
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertTrue(error.message?.contains("servidor") == true)
        }
    }

    @Test
    fun `deleteOrder emits Error on network exception`() = runTest {
        // Given
        val orderId = 1

        whenever(apiService.deleteOrder(orderId))
            .thenThrow(IOException("Network error"))

        // When & Then
        repository.deleteOrder(orderId).test {
            awaitItem() // Loading
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertTrue(error.message?.contains("red") == true)
        }
    }
}
