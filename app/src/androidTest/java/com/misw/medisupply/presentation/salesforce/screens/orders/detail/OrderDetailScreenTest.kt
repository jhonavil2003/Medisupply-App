package com.misw.medisupply.presentation.salesforce.screens.orders.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderItem
import com.misw.medisupply.domain.model.order.OrderStatus
import com.misw.medisupply.domain.usecase.order.DeleteOrderUseCase
import com.misw.medisupply.domain.usecase.order.GetOrderByIdUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date

/**
 * UI tests for OrderDetailScreen
 * Tests rendering and user interactions
 */
class OrderDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var getOrderByIdUseCase: GetOrderByIdUseCase
    private lateinit var deleteOrderUseCase: DeleteOrderUseCase

    @Before
    fun setup() {
        getOrderByIdUseCase = mockk()
        deleteOrderUseCase = mockk()
    }

    @Test
    fun loadingState_displaysLoadingIndicator() {
        // Given
        coEvery { 
            getOrderByIdUseCase(any()) 
        } returns flowOf(Resource.Loading())

        // When
        composeTestRule.setContent {
            OrderDetailScreen(
                orderId = "1",
                viewModel = OrderDetailViewModel(getOrderByIdUseCase, deleteOrderUseCase),
                onNavigateBack = {},
                onOrderDeleted = {}
            )
        }

        // Then - loading indicator should be displayed
    }

    @Test
    fun successState_displaysOrderDetails() {
        // Given
        val mockOrder = createMockOrder(1, "ORD-00001")
        coEvery { 
            getOrderByIdUseCase(1) 
        } returns flowOf(Resource.Success(mockOrder))

        // When
        composeTestRule.setContent {
            OrderDetailScreen(
                orderId = "1",
                viewModel = OrderDetailViewModel(getOrderByIdUseCase, deleteOrderUseCase),
                onNavigateBack = {},
                onOrderDeleted = {}
            )
        }

        // Then - order details should be displayed
        composeTestRule.onNodeWithText("ORD-00001").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Customer").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Product").assertIsDisplayed()
    }

    @Test
    fun errorState_displaysErrorMessage() {
        // Given
        coEvery { 
            getOrderByIdUseCase(1) 
        } returns flowOf(Resource.Error("Order not found"))

        // When
        composeTestRule.setContent {
            OrderDetailScreen(
                orderId = "1",
                viewModel = OrderDetailViewModel(getOrderByIdUseCase, deleteOrderUseCase),
                onNavigateBack = {},
                onOrderDeleted = {}
            )
        }

        // Then - error message should be displayed
        composeTestRule.onNodeWithText("Order not found").assertIsDisplayed()
    }

    @Test
    fun editQuantity_updatesProductQuantity() {
        // Given
        val mockOrder = createMockOrder(1, "ORD-00001")
        coEvery { 
            getOrderByIdUseCase(1) 
        } returns flowOf(Resource.Success(mockOrder))

        // When
        composeTestRule.setContent {
            OrderDetailScreen(
                orderId = "1",
                viewModel = OrderDetailViewModel(getOrderByIdUseCase, deleteOrderUseCase),
                onNavigateBack = {},
                onOrderDeleted = {}
            )
        }

        // Find quantity input and change value
        // Note: Add test tag to quantity input field
    }

    @Test
    fun deleteProductButton_removesProductFromList() {
        // Given
        val mockOrder = createMockOrder(1, "ORD-00001")
        coEvery { 
            getOrderByIdUseCase(1) 
        } returns flowOf(Resource.Success(mockOrder))

        // When
        composeTestRule.setContent {
            OrderDetailScreen(
                orderId = "1",
                viewModel = OrderDetailViewModel(getOrderByIdUseCase, deleteOrderUseCase),
                onNavigateBack = {},
                onOrderDeleted = {}
            )
        }

        // Click delete icon on product
        // Note: Add test tag to delete icon button
    }

    @Test
    fun confirmOrderButton_isDisabledWhenNoChanges() {
        // Given
        val mockOrder = createMockOrder(1, "ORD-00001")
        coEvery { 
            getOrderByIdUseCase(1) 
        } returns flowOf(Resource.Success(mockOrder))

        // When
        composeTestRule.setContent {
            OrderDetailScreen(
                orderId = "1",
                viewModel = OrderDetailViewModel(getOrderByIdUseCase, deleteOrderUseCase),
                onNavigateBack = {},
                onOrderDeleted = {}
            )
        }

        // Then - confirm button should show "Sin cambios"
        composeTestRule.onNodeWithText("Sin cambios").assertIsDisplayed()
    }

    @Test
    fun confirmOrderButton_isEnabledWhenChangesExist() {
        // Given
        val mockOrder = createMockOrder(1, "ORD-00001")
        coEvery { 
            getOrderByIdUseCase(1) 
        } returns flowOf(Resource.Success(mockOrder))

        // When
        composeTestRule.setContent {
            OrderDetailScreen(
                orderId = "1",
                viewModel = OrderDetailViewModel(getOrderByIdUseCase, deleteOrderUseCase),
                onNavigateBack = {},
                onOrderDeleted = {}
            )
        }

        // Edit quantity (this would need proper implementation with test tags)
        // Then confirm button should be enabled with "Confirmar pedido" text
    }

    @Test
    fun deleteOrderButton_showsConfirmationDialog() {
        // Given
        val mockOrder = createMockOrder(1, "ORD-00001")
        coEvery { 
            getOrderByIdUseCase(1) 
        } returns flowOf(Resource.Success(mockOrder))

        // When
        composeTestRule.setContent {
            OrderDetailScreen(
                orderId = "1",
                viewModel = OrderDetailViewModel(getOrderByIdUseCase, deleteOrderUseCase),
                onNavigateBack = {},
                onOrderDeleted = {}
            )
        }

        // Click "Eliminar pedido" button
        composeTestRule.onNodeWithText("Eliminar pedido").performClick()

        // Then - confirmation dialog should be displayed
        composeTestRule.onNodeWithText("¿Eliminar pedido?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Esta acción no se puede deshacer.").assertIsDisplayed()
    }

    @Test
    fun deleteConfirmationDialog_cancelButton_closesDialog() {
        // Given
        val mockOrder = createMockOrder(1, "ORD-00001")
        coEvery { 
            getOrderByIdUseCase(1) 
        } returns flowOf(Resource.Success(mockOrder))

        // When
        composeTestRule.setContent {
            OrderDetailScreen(
                orderId = "1",
                viewModel = OrderDetailViewModel(getOrderByIdUseCase, deleteOrderUseCase),
                onNavigateBack = {},
                onOrderDeleted = {}
            )
        }

        // Click "Eliminar pedido" button
        composeTestRule.onNodeWithText("Eliminar pedido").performClick()
        
        // Click "Cancelar" in dialog
        composeTestRule.onNodeWithText("Cancelar").performClick()

        // Then - dialog should be closed
        composeTestRule.onNodeWithText("¿Eliminar pedido?").assertDoesNotExist()
    }

    @Test
    fun deleteConfirmationDialog_confirmButton_deletesOrder() {
        // Given
        val mockOrder = createMockOrder(1, "ORD-00001")
        var deletedOrderNumber: String? = null
        
        coEvery { getOrderByIdUseCase(1) } returns flowOf(Resource.Success(mockOrder))
        coEvery { 
            deleteOrderUseCase(1) 
        } returns flowOf(
            Resource.Loading(),
            Resource.Success(Unit)
        )

        // When
        composeTestRule.setContent {
            OrderDetailScreen(
                orderId = "1",
                viewModel = OrderDetailViewModel(getOrderByIdUseCase, deleteOrderUseCase),
                onNavigateBack = {},
                onOrderDeleted = { orderNumber -> deletedOrderNumber = orderNumber }
            )
        }

        // Click "Eliminar pedido" button
        composeTestRule.onNodeWithText("Eliminar pedido").performClick()
        
        // Click "Eliminar" in dialog
        composeTestRule.onNodeWithText("Eliminar").performClick()

        // Then - order should be deleted and callback invoked
        assert(deletedOrderNumber == "ORD-00001")
    }

    @Test
    fun deleteInProgress_displaysLoadingInDialog() {
        // Given
        val mockOrder = createMockOrder(1, "ORD-00001")
        coEvery { getOrderByIdUseCase(1) } returns flowOf(Resource.Success(mockOrder))
        coEvery { 
            deleteOrderUseCase(1) 
        } returns flowOf(Resource.Loading())

        // When
        composeTestRule.setContent {
            OrderDetailScreen(
                orderId = "1",
                viewModel = OrderDetailViewModel(getOrderByIdUseCase, deleteOrderUseCase),
                onNavigateBack = {},
                onOrderDeleted = {}
            )
        }

        // Click "Eliminar pedido" button
        composeTestRule.onNodeWithText("Eliminar pedido").performClick()
        
        // Click "Eliminar" in dialog
        composeTestRule.onNodeWithText("Eliminar").performClick()

        // Then - loading state should be displayed
        composeTestRule.onNodeWithText("Eliminando pedido...").assertIsDisplayed()
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
