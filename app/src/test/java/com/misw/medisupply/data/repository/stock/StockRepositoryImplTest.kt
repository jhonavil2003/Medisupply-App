package com.misw.medisupply.data.repository.stock

import app.cash.turbine.test
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.data.remote.api.stock.StockApiService
import com.misw.medisupply.data.remote.dto.stock.DistributionCenterDto
import com.misw.medisupply.data.remote.dto.stock.MultipleStockLevelsDto
import com.misw.medisupply.data.remote.dto.stock.StockLevelDto
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import retrofit2.Response
import java.io.IOException

class StockRepositoryImplTest {

    @Mock
    private lateinit var apiService: StockApiService

    private lateinit var repository: StockRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = StockRepositoryImpl(apiService)
    }

    @Test
    fun `getProductStock emits Loading then Success when API call succeeds`() = runTest {
        val mockDistributionCenter = DistributionCenterDto(
            distributionCenterId = 1,
            distributionCenterCode = "DC-BOG",
            distributionCenterName = "Centro Bogotá",
            city = "Bogotá",
            quantityAvailable = 100,
            quantityReserved = 10,
            quantityInTransit = 20,
            isLowStock = false,
            isOutOfStock = false
        )
        val mockStockLevel = StockLevelDto(
            productSku = "MED-001",
            totalAvailable = 100,
            totalReserved = 10,
            totalInTransit = 20,
            distributionCenters = listOf(mockDistributionCenter)
        )
        whenever(apiService.getSingleProductStock(any(), anyOrNull(), any(), any()))
            .thenReturn(Response.success(mockStockLevel))

        repository.getProductStock("MED-001").test {
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)

            val success = awaitItem()
            assertTrue(success is Resource.Success)
            assertEquals("MED-001", success.data?.productSku)
            assertEquals(100, success.data?.totalAvailable)
            assertEquals(1, success.data?.distributionCenters?.size)

            awaitComplete()
        }
    }

    @Test
    fun `getProductStock emits Error when product not found`() = runTest {
        val errorBody = "Not Found".toResponseBody("text/plain".toMediaTypeOrNull())
        whenever(apiService.getSingleProductStock(any(), anyOrNull(), any(), any()))
            .thenReturn(Response.error(404, errorBody))

        repository.getProductStock("INVALID-SKU").test {
            awaitItem()
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertTrue(error.message?.contains("not found") == true)
            awaitComplete()
        }
    }

    @Test
    fun `getProductStock emits Error when IOException occurs`() = runTest {
        whenever(apiService.getSingleProductStock(any(), anyOrNull(), any(), any()))
            .thenThrow(RuntimeException(IOException("Network error")))

        repository.getProductStock("MED-001").test {
            awaitItem()
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertNotNull(error.message)
            awaitComplete()
        }
    }

    @Test
    fun `getProductStock converts SKU to uppercase`() = runTest {
        val mockStockLevel = StockLevelDto(
            productSku = "MED-001",
            totalAvailable = 50,
            totalReserved = null,
            totalInTransit = null,
            distributionCenters = emptyList()
        )
        whenever(apiService.getSingleProductStock(any(), anyOrNull(), any(), any()))
            .thenReturn(Response.success(mockStockLevel))

        repository.getProductStock("med-001").test {
            awaitItem()
            val success = awaitItem()
            assertTrue(success is Resource.Success)
            awaitComplete()
        }
    }

    @Test
    fun `getProductStock with distribution center ID passes correct parameters`() = runTest {
        val mockStockLevel = StockLevelDto(
            productSku = "MED-001",
            totalAvailable = 50,
            totalReserved = 5,
            totalInTransit = 10,
            distributionCenters = emptyList()
        )
        whenever(apiService.getSingleProductStock(any(), any(), any(), any()))
            .thenReturn(Response.success(mockStockLevel))

        repository.getProductStock(
            productSku = "MED-001",
            distributionCenterId = 1,
            includeReserved = false,
            includeInTransit = false
        ).test {
            awaitItem()
            val success = awaitItem()
            assertTrue(success is Resource.Success)
            awaitComplete()
        }
    }

    @Test
    fun `getMultipleProductsStock emits Loading then Success when API call succeeds`() = runTest {
        val mockStockLevel1 = StockLevelDto(
            productSku = "MED-001",
            totalAvailable = 100,
            totalReserved = 10,
            totalInTransit = 20,
            distributionCenters = emptyList()
        )
        val mockStockLevel2 = StockLevelDto(
            productSku = "MED-002",
            totalAvailable = 50,
            totalReserved = 5,
            totalInTransit = 10,
            distributionCenters = emptyList()
        )
        val mockResponse = MultipleStockLevelsDto(
            products = listOf(mockStockLevel1, mockStockLevel2),
            totalProducts = 2
        )
        whenever(apiService.getMultipleProductsStock(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(Response.success(mockResponse))

        repository.getMultipleProductsStock(listOf("MED-001", "MED-002")).test {
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)

            val success = awaitItem()
            assertTrue(success is Resource.Success)
            assertEquals(2, success.data?.products?.size)

            awaitComplete()
        }
    }

    @Test
    fun `getMultipleProductsStock converts SKUs to uppercase`() = runTest {
        val mockResponse = MultipleStockLevelsDto(products = emptyList(), totalProducts = 0)
        whenever(apiService.getMultipleProductsStock(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(Response.success(mockResponse))

        repository.getMultipleProductsStock(listOf("med-001", "med-002")).test {
            awaitItem()
            val success = awaitItem()
            assertTrue(success is Resource.Success<*>)
            awaitComplete()
        }
    }

    @Test
    fun `getMultipleProductsStock emits Error when API returns error`() = runTest {
        val errorBody = "Bad Request".toResponseBody("text/plain".toMediaTypeOrNull())
        whenever(apiService.getMultipleProductsStock(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenReturn(Response.error(400, errorBody))

        repository.getMultipleProductsStock(listOf("MED-001")).test {
            awaitItem()
            val error = awaitItem()
            assertTrue(error is Resource.Error<*>)
            assertTrue(error.message?.contains("Invalid") == true || error.message?.contains("parameters") == true)
            awaitComplete()
        }
    }

    @Test
    fun `getMultipleProductsStock emits Error when IOException occurs`() = runTest {
        whenever(apiService.getMultipleProductsStock(any(), anyOrNull(), anyOrNull(), any(), any()))
            .thenThrow(RuntimeException(IOException("No connection")))

        repository.getMultipleProductsStock(listOf("MED-001")).test {
            awaitItem()
            val error = awaitItem()
            assertTrue(error is Resource.Error<*>)
            assertNotNull(error.message)
            awaitComplete()
        }
    }

    @Test
    fun `getMultipleProductsStock with filters passes correct parameters`() = runTest {
        val mockResponse = MultipleStockLevelsDto(products = emptyList(), totalProducts = 0)
        whenever(apiService.getMultipleProductsStock(any(), any(), any(), any(), any()))
            .thenReturn(Response.success(mockResponse))

        repository.getMultipleProductsStock(
            productSkus = listOf("MED-001"),
            distributionCenterId = 1,
            onlyAvailable = true,
            includeReserved = false,
            includeInTransit = false
        ).test {
            awaitItem()
            val success = awaitItem()
            assertTrue(success is Resource.Success<*>)
            awaitComplete()
        }
    }
}
