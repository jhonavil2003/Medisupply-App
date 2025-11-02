package com.misw.medisupply.presentation.salesforce.viewmodel.orders

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.data.remote.websocket.InventoryWebSocketClient
import com.misw.medisupply.data.remote.websocket.WebSocketState
import com.misw.medisupply.domain.model.order.CartItem
import com.misw.medisupply.domain.model.product.Pagination
import com.misw.medisupply.domain.model.product.Product
import com.misw.medisupply.domain.model.stock.DistributionCenter
import com.misw.medisupply.domain.model.stock.MultipleStockLevels
import com.misw.medisupply.domain.model.stock.StockLevel
import com.misw.medisupply.domain.usecase.product.GetProductsUseCase
import com.misw.medisupply.domain.usecase.stock.GetMultipleProductsStockUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.isNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for ProductsViewModel
 * Tests product loading, stock management, filtering, pagination and cart operations
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProductsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    
    private lateinit var getProductsUseCase: GetProductsUseCase
    private lateinit var getMultipleProductsStockUseCase: GetMultipleProductsStockUseCase
    private lateinit var webSocketClient: InventoryWebSocketClient
    private lateinit var viewModel: ProductsViewModel

    private val testProducts = listOf(
        Product(
            id = 1,
            sku = "PROD001",
            name = "Product 1",
            description = "Description 1",
            category = "Medicamentos",
            subcategory = null,
            unitPrice = 100.0f,
            currency = "COP",
            unitOfMeasure = "UND",
            supplierId = 1,
            supplierName = "Supplier 1",
            requiresColdChain = false,
            storageConditions = null,
            regulatoryInfo = null,
            physicalDimensions = null,
            manufacturer = "Manufacturer 1",
            countryOfOrigin = "Colombia",
            barcode = "123456789",
            imageUrl = null,
            isActive = true,
            isDiscontinued = false,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        ),
        Product(
            id = 2,
            sku = "PROD002",
            name = "Product 2",
            description = "Description 2",
            category = "Insumos",
            subcategory = null,
            unitPrice = 50.0f,
            currency = "COP",
            unitOfMeasure = "UND",
            supplierId = 1,
            supplierName = "Supplier 1",
            requiresColdChain = true,
            storageConditions = null,
            regulatoryInfo = null,
            physicalDimensions = null,
            manufacturer = "Manufacturer 2",
            countryOfOrigin = "Colombia",
            barcode = "987654321",
            imageUrl = null,
            isActive = true,
            isDiscontinued = false,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        ),
        Product(
            id = 3,
            sku = "PROD003",
            name = "Product 3",
            description = "Description 3",
            category = "Medicamentos",
            subcategory = null,
            unitPrice = 75.0f,
            currency = "COP",
            unitOfMeasure = "UND",
            supplierId = 1,
            supplierName = "Supplier 1",
            requiresColdChain = false,
            storageConditions = null,
            regulatoryInfo = null,
            physicalDimensions = null,
            manufacturer = "Manufacturer 3",
            countryOfOrigin = "Colombia",
            barcode = "456789123",
            imageUrl = null,
            isActive = true,
            isDiscontinued = false,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        )
    )

    private val testPagination = Pagination(
        page = 1,
        perPage = 20,
        totalPages = 1,
        totalItems = 3,
        hasNext = false,
        hasPrev = false
    )

    private val testStockLevels = listOf(
        StockLevel(
            productSku = "PROD001",
            totalAvailable = 100,
            totalReserved = 10,
            totalInTransit = 5,
            distributionCenters = listOf(
                DistributionCenter(
                    id = 1,
                    code = "CD001",
                    name = "Centro 1",
                    city = "Bogotá",
                    quantityAvailable = 100,
                    quantityReserved = 10,
                    quantityInTransit = 5,
                    isLowStock = false,
                    isOutOfStock = false
                )
            )
        ),
        StockLevel(
            productSku = "PROD002",
            totalAvailable = 50,
            totalReserved = 5,
            totalInTransit = 0,
            distributionCenters = listOf(
                DistributionCenter(
                    id = 1,
                    code = "CD001",
                    name = "Centro 1",
                    city = "Bogotá",
                    quantityAvailable = 50,
                    quantityReserved = 5,
                    quantityInTransit = 0,
                    isLowStock = true,
                    isOutOfStock = false
                )
            )
        ),
        StockLevel(
            productSku = "PROD003",
            totalAvailable = 0,
            totalReserved = 0,
            totalInTransit = 10,
            distributionCenters = emptyList()
        )
    )

    private val testMultipleStockLevels = MultipleStockLevels(
        products = testStockLevels,
        totalProducts = 3
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getProductsUseCase = mock()
        getMultipleProductsStockUseCase = mock()
        webSocketClient = mock()
        
        // Mock WebSocket client behavior
        whenever(webSocketClient.connectionState).thenReturn(MutableStateFlow(WebSocketState.DISCONNECTED))
        whenever(webSocketClient.stockEvents).thenReturn(MutableStateFlow(null))
        whenever(webSocketClient.isConnected()).thenReturn(false)
        
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Loading()))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Loading()))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads products automatically`() = runTest {
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))

        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)

        verify(getProductsUseCase).invoke(isNull(), isNull(), isNull(), isNull(), isNull(), eq(true), isNull(), eq(1), eq(20))
    }

    @Test
    fun `loading products shows loading state`() = runTest {
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Loading()))

        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isLoading)
            assertNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loading products successfully updates state`() = runTest {
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))

        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(3, state.products.size)
            assertEquals(testProducts, state.products)
            assertEquals(testPagination, state.pagination)
            assertNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loading products with error updates error state`() = runTest {
        val errorMessage = "Error al cargar productos"
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Error(errorMessage)))

        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(errorMessage, state.error)
            assertTrue(state.products.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSearchQueryChange updates search query and reloads products`() = runTest {
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))
        
        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)

        viewModel.onSearchQueryChange("Product 1")

        viewModel.state.test {
            val state = awaitItem()
            assertEquals("Product 1", state.searchQuery)
            assertEquals(1, state.currentPage) // Reset to page 1
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSearchQueryChange with blank query sets null`() = runTest {
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))
        
        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)
        viewModel.onSearchQueryChange("test")

        viewModel.onSearchQueryChange("")

        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.searchQuery)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onCategoryFilterChange updates category and reloads products`() = runTest {
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))
        
        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)

        viewModel.onCategoryFilterChange("Medicamentos")

        viewModel.state.test {
            val state = awaitItem()
            assertEquals("Medicamentos", state.selectedCategory)
            assertEquals(1, state.currentPage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearFilters clears all filters and resets page`() = runTest {
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))
        
        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)
        viewModel.onSearchQueryChange("test")
        viewModel.onCategoryFilterChange("Medicamentos")

        viewModel.clearFilters()

        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.searchQuery)
            assertNull(state.selectedCategory)
            assertEquals(1, state.currentPage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadNextPage loads next page when hasNext is true`() = runTest {
        val paginationWithNext = testPagination.copy(hasNext = true, page = 1)
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, paginationWithNext))))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))
        
        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)

        viewModel.loadNextPage()

        verify(getProductsUseCase).invoke(isNull(), isNull(), isNull(), isNull(), isNull(), eq(true), isNull(), eq(2), eq(20))
    }

    @Test
    fun `loadNextPage does nothing when hasNext is false`() = runTest {
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))
        
        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)

        viewModel.loadNextPage()

        verify(getProductsUseCase).invoke(isNull(), isNull(), isNull(), isNull(), isNull(), eq(true), isNull(), eq(1), eq(20))
    }

    @Test
    fun `loadPreviousPage loads previous page when hasPrev is true`() = runTest {
        val paginationWithPrev = testPagination.copy(hasPrev = true, page = 2)
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, paginationWithPrev))))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))
        
        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)

        viewModel.loadPreviousPage()

        verify(getProductsUseCase, times(2)).invoke(isNull(), isNull(), isNull(), isNull(), isNull(), eq(true), isNull(), any(), eq(20))
    }

    @Test
    fun `refresh forces reload of products and stock`() = runTest {
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))
        
        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)

        viewModel.refresh()

        verify(getProductsUseCase, times(2)).invoke(isNull(), isNull(), isNull(), isNull(), isNull(), eq(true), isNull(), eq(1), eq(20))
    }

    @Test
    fun `addToCart adds new product to cart`() = runTest {
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))
        
        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)
        val product = testProducts[0]

        viewModel.addToCart(product)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(1, state.cartItems.size)
            assertTrue(state.cartItems.containsKey(product.sku.uppercase()))
            assertEquals(1, state.cartItems[product.sku.uppercase()]?.quantity)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addToCart increases quantity when product already in cart`() = runTest {
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))
        
        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)
        val product = testProducts[0]

        viewModel.addToCart(product)
        viewModel.addToCart(product)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(1, state.cartItems.size)
            assertEquals(2, state.cartItems[product.sku.uppercase()]?.quantity)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addToCart respects stock limit`() = runTest {
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))
        
        val limitedStockLevels = MultipleStockLevels(
            products = listOf(testStockLevels[0].copy(totalAvailable = 2)),
            totalProducts = 1
        )
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Success(limitedStockLevels)))
        
        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)
        val product = testProducts[0]

        viewModel.addToCart(product)
        viewModel.addToCart(product)
        viewModel.addToCart(product)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(2, state.cartItems[product.sku.uppercase()]?.quantity)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `removeFromCart decreases quantity`() = runTest {
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))
        
        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)
        val product = testProducts[0]
        viewModel.addToCart(product)
        viewModel.addToCart(product)

        viewModel.removeFromCart(product.sku)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(1, state.cartItems[product.sku.uppercase()]?.quantity)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `removeFromCart removes product when quantity is 1`() = runTest {
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))
        
        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)
        val product = testProducts[0]
        viewModel.addToCart(product)

        viewModel.removeFromCart(product.sku)

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.cartItems.containsKey(product.sku.uppercase()))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateCartQuantity updates quantity correctly`() = runTest {
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))
        
        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)
        val product = testProducts[0]
        viewModel.addToCart(product)

        viewModel.updateCartQuantity(product.sku, 5)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(5, state.cartItems[product.sku.uppercase()]?.quantity)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateCartQuantity with 0 removes product`() = runTest {
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))
        
        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)
        val product = testProducts[0]
        viewModel.addToCart(product)

        viewModel.updateCartQuantity(product.sku, 0)
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.cartItems.containsKey(product.sku.uppercase()))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `removeProductFromCart removes product completely`() = runTest {
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))
        
        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)
        val product = testProducts[0]
        viewModel.addToCart(product)
        viewModel.addToCart(product)
        viewModel.removeProductFromCart(product.sku)

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.cartItems.containsKey(product.sku.uppercase()))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearCart removes all items from cart`() = runTest {
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))
        
        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)
        viewModel.addToCart(testProducts[0])
        viewModel.addToCart(testProducts[1])

        viewModel.clearCart()

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.cartItems.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getCartQuantity returns correct quantity`() = runTest {
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))
        
        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)
        val product = testProducts[0]
        viewModel.addToCart(product)
        viewModel.addToCart(product)

        val quantity = viewModel.getCartQuantity(product.sku)

        assertEquals(2, quantity)
    }

    @Test
    fun `getCartItemsCount returns total items in cart`() = runTest {
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))
        
        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)
        viewModel.addToCart(testProducts[0])
        viewModel.addToCart(testProducts[0])
        viewModel.addToCart(testProducts[1])

        val count = viewModel.getCartItemsCount()

        assertEquals(3, count) // 2 + 1
    }

    @Test
    fun `getCartTotal calculates total price correctly`() = runTest {
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))
        
        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)
        viewModel.addToCart(testProducts[0]) // 100.0
        viewModel.addToCart(testProducts[0]) // 100.0
        viewModel.addToCart(testProducts[1]) // 50.0

        val total = viewModel.getCartTotal()

        assertEquals(250.0f, total, 0.01f)
    }

    @Test
    fun `hasItemsInCart returns true when cart has items`() = runTest {
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))
        
        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)
        viewModel.addToCart(testProducts[0])

        val hasItems = viewModel.hasItemsInCart()

        assertTrue(hasItems)
    }

    @Test
    fun `hasItemsInCart returns false when cart is empty`() = runTest {
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))
        
        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)

        val hasItems = viewModel.hasItemsInCart()

        assertFalse(hasItems)
    }

    @Test
    fun `getStockForProduct returns correct stock level`() = runTest {
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))
        
        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)

        val stock = viewModel.getStockForProduct("PROD001")

        assertNotNull(stock)
        assertEquals(100, stock?.totalAvailable)
    }

    @Test
    fun `retryStockLoading reloads stock for current products`() = runTest {
        whenever(getProductsUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))
        whenever(getMultipleProductsStockUseCase.invoke(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(flowOf(Resource.Error("Stock error")))
        
        viewModel = ProductsViewModel(getProductsUseCase, getMultipleProductsStockUseCase, webSocketClient)

        viewModel.retryStockLoading()

        verify(getMultipleProductsStockUseCase, times(2)).invoke(any(), anyOrNull(), anyOrNull(), any(), any())
    }
}

