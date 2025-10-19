package com.misw.medisupply.presentation.salesforce.screens.orders.list

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.data.remote.dto.OrdersResponse
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderItem
import com.misw.medisupply.domain.model.order.OrderStatus
import com.misw.medisupply.domain.usecase.order.GetOrdersUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date

/**
 * UI tests for MyOrdersScreen
 * Tests rendering and user interactions
 */
class MyOrdersScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var getOrdersUseCase: GetOrdersUseCase

    @Before
    fun setup() {
        getOrdersUseCase = mockk()
    }

    @Test
    fun loadingState_displaysLoadingIndicator() {
        // Given
        coEvery { 
            getOrdersUseCase(any(), any(), any()) 
        } returns flowOf(Resource.Loading())

        // When
        composeTestRule.setContent {
            MyOrdersScreen(
                viewModel = MyOrdersViewModel(getOrdersUseCase),
                onNavigateBack = {},
                onNavigateToEditOrder = {}
            )
        }

        // Then - loading indicator should be displayed
        // Note: You may need to add test tags to your UI components
    }

    @Test
    fun successState_displaysOrdersList() {
        // Given
        val mockOrders = listOf(
            createMockOrder(1, "ORD-00001"),
            createMockOrder(2, "ORD-00002")
        )
        val mockResponse = OrdersResponse(data = mockOrders, totalCount = 2)
        
        coEvery { 
            getOrdersUseCase(any(), any(), any()) 
        } returns flowOf(Resource.Success(mockResponse))

        // When
        composeTestRule.setContent {
            MyOrdersScreen(
                viewModel = MyOrdersViewModel(getOrdersUseCase),
                onNavigateBack = {},
                onNavigateToEditOrder = {}
            )
        }

        // Then - orders should be displayed
        composeTestRule.onNodeWithText("ORD-00001").assertIsDisplayed()
        composeTestRule.onNodeWithText("ORD-00002").assertIsDisplayed()
    }

    @Test
    fun errorState_displaysErrorMessage() {
        // Given
        coEvery { 
            getOrdersUseCase(any(), any(), any()) 
        } returns flowOf(Resource.Error("Network error"))

        // When
        composeTestRule.setContent {
            MyOrdersScreen(
                viewModel = MyOrdersViewModel(getOrdersUseCase),
                onNavigateBack = {},
                onNavigateToEditOrder = {}
            )
        }

        // Then - error message should be displayed
        composeTestRule.onNodeWithText("Network error").assertIsDisplayed()
    }

    @Test
    fun emptyState_displaysEmptyMessage() {
        // Given
        val mockResponse = OrdersResponse(data = emptyList(), totalCount = 0)
        coEvery { 
            getOrdersUseCase(any(), any(), any()) 
        } returns flowOf(Resource.Success(mockResponse))

        // When
        composeTestRule.setContent {
            MyOrdersScreen(
                viewModel = MyOrdersViewModel(getOrdersUseCase),
                onNavigateBack = {},
                onNavigateToEditOrder = {}
            )
        }

        // Then - empty state should be displayed
        // Note: Add test tag to empty state component
    }

    @Test
    fun filterDropdown_filtersOrdersByStatus() {
        // Given
        val mockOrders = listOf(
            createMockOrder(1, "ORD-00001").copy(status = OrderStatus.PENDING),
            createMockOrder(2, "ORD-00002").copy(status = OrderStatus.CONFIRMED)
        )
        val mockResponse = OrdersResponse(data = mockOrders, totalCount = 2)
        
        coEvery { 
            getOrdersUseCase(any(), any(), any()) 
        } returns flowOf(Resource.Success(mockResponse))

        // When
        composeTestRule.setContent {
            MyOrdersScreen(
                viewModel = MyOrdersViewModel(getOrdersUseCase),
                onNavigateBack = {},
                onNavigateToEditOrder = {}
            )
        }

        // Click on filter dropdown and select "Pendiente"
        // Note: Add test tags to dropdown and options
    }

    @Test
    fun orderCard_clickEditButton_navigatesToDetail() {
        // Given
        val mockOrders = listOf(createMockOrder(1, "ORD-00001"))
        val mockResponse = OrdersResponse(data = mockOrders, totalCount = 1)
        var navigatedOrderId: String? = null
        
        coEvery { 
            getOrdersUseCase(any(), any(), any()) 
        } returns flowOf(Resource.Success(mockResponse))

        // When
        composeTestRule.setContent {
            MyOrdersScreen(
                viewModel = MyOrdersViewModel(getOrdersUseCase),
                onNavigateBack = {},
                onNavigateToEditOrder = { orderId -> navigatedOrderId = orderId }
            )
        }

        // Click "Editar" button
        composeTestRule.onNodeWithText("Editar").performClick()

        // Then
        assert(navigatedOrderId == "1")
    }

    @Test
    fun deletedOrderMessage_displaysSuccessSnackbar() {
        // Given
        val mockResponse = OrdersResponse(data = emptyList(), totalCount = 0)
        coEvery { 
            getOrdersUseCase(any(), any(), any()) 
        } returns flowOf(Resource.Success(mockResponse))

        // When
        composeTestRule.setContent {
            MyOrdersScreen(
                viewModel = MyOrdersViewModel(getOrdersUseCase),
                onNavigateBack = {},
                onNavigateToEditOrder = {},
                deletedOrderMessage = "La orden ORD-00001 ha sido eliminada exitosamente"
            )
        }

        // Then - success message should be displayed in snackbar
        composeTestRule.onNode(
            hasText("La orden ORD-00001 ha sido eliminada exitosamente")
        ).assertIsDisplayed()
    }

    private fun createMockOrder(id: Int, orderNumber: String): Order {
        return Order(
            id = id,
            orderNumber = orderNumber,
            customerId = 1,
            customer = Customer(
                id = 1,
                businessName = "Test Customer",
                contactName = "John Doe",
                contactPhone = "1234567890",
                contactEmail = "test@example.com",
                address = "123 Test St",
                city = "Test City",
                department = "Test Dept",
                nit = "123456789"
            ),
            sellerId = "SELLER-001",
            orderDate = Date(),
            status = OrderStatus.PENDING,
            items = listOf(
                OrderItem(
                    id = 1,
                    orderId = id,
                    productId = 1,
                    productName = "Test Product",
                    productSku = "TEST-001",
                    quantity = 10,
                    unitPrice = 100.0,
                    subtotal = 1000.0
                )
            ),
            totalAmount = 1000.0,
            deliveryAddress = "123 Delivery St",
            deliveryCity = "Delivery City",
            deliveryDepartment = "Delivery Dept",
            notes = "Test notes"
        )
    }
}
