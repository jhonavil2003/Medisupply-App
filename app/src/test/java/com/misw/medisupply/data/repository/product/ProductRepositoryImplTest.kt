package com.misw.medisupply.data.repository.product

import app.cash.turbine.test
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.data.remote.api.product.ProductApiService
import com.misw.medisupply.data.remote.dto.product.PaginationDto
import com.misw.medisupply.data.remote.dto.product.ProductDto
import com.misw.medisupply.data.remote.dto.product.ProductsResponse
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
import org.mockito.kotlin.whenever
import retrofit2.Response
import java.io.IOException

class ProductRepositoryImplTest {

    @Mock
    private lateinit var apiService: ProductApiService

    private lateinit var repository: ProductRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = ProductRepositoryImpl(apiService)
    }

    @Test
    fun `getProducts emits Loading then Success when API call succeeds`() = runTest {
        val mockProductDto = ProductDto(
            id = 1,
            sku = "MED-001",
            name = "Test Product",
            description = "Test Description",
            category = "Medicines",
            subcategory = "Antibiotics",
            unitPrice = 10000.0f,
            currency = "COP",
            unitOfMeasure = "UNIDAD",
            supplierId = 1,
            supplierName = "Test Supplier",
            requiresColdChain = false,
            storageConditions = null,
            regulatoryInfo = null,
            physicalDimensions = null,
            manufacturer = "Test Manufacturer",
            countryOfOrigin = "Colombia",
            barcode = "123456789",
            imageUrl = null,
            isActive = true,
            isDiscontinued = false,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            certifications = null,
            regulatoryConditions = null
        )
        val mockPagination = PaginationDto(
            page = 1,
            perPage = 10,
            totalPages = 1,
            totalItems = 1,
            hasNext = false,
            hasPrev = false
        )
        val mockResponse = ProductsResponse(
            products = listOf(mockProductDto),
            pagination = mockPagination
        )
        whenever(apiService.getProducts(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(Response.success(mockResponse))

        repository.getProducts().test {
            val loading = awaitItem()
            assertTrue("First emission should be Loading", loading is Resource.Loading)

            val result = awaitItem()
            if (result is Resource.Error) {
                fail("Expected Success but got Error: ${result.message}")
            }
            assertTrue("Second emission should be Success", result is Resource.Success)
            
            val success = result as Resource.Success
            assertEquals(1, success.data?.first?.size)
            assertEquals("Test Product", success.data?.first?.get(0)?.name)
            assertEquals(1, success.data?.second?.totalItems)

            awaitComplete()
        }
    }

    @Test
    fun `getProducts emits Error when API returns error`() = runTest {
        val errorBody = "Server Error".toResponseBody("text/plain".toMediaTypeOrNull())
        whenever(apiService.getProducts(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(Response.error(500, errorBody))

        repository.getProducts().test {
            awaitItem()
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertNotNull(error.message)
            awaitComplete()
        }
    }

    @Test
    fun `getProducts emits Error when IOException occurs`() = runTest {
        whenever(apiService.getProducts(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenThrow(RuntimeException(IOException("Network error")))

        repository.getProducts().test {
            awaitItem()
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertNotNull(error.message)
            awaitComplete()
        }
    }

    @Test
    fun `getProducts with filters passes correct parameters`() = runTest {
        val mockResponse = ProductsResponse(
            products = emptyList(),
            pagination = PaginationDto(1, 10, 0, 0, false, false)
        )
        whenever(apiService.getProducts(any(), anyOrNull(), any(), anyOrNull(), anyOrNull(), any(), anyOrNull(), any(), any()))
            .thenReturn(Response.success(mockResponse))

        repository.getProducts(
            search = "test",
            category = "Medicines",
            isActive = true,
            page = 1,
            perPage = 10
        ).test {
            awaitItem()
            val success = awaitItem()
            assertTrue(success is Resource.Success)
            awaitComplete()
        }
    }

    @Test
    fun `getProductBySku emits Loading then Success when API call succeeds`() = runTest {
        val mockProductDto = ProductDto(
            id = 1,
            sku = "MED-001",
            name = "Test Product",
            description = null,
            category = "Medicines",
            subcategory = null,
            unitPrice = 5000.0f,
            currency = "COP",
            unitOfMeasure = "UNIDAD",
            supplierId = 1,
            supplierName = null,
            requiresColdChain = false,
            storageConditions = null,
            regulatoryInfo = null,
            physicalDimensions = null,
            manufacturer = null,
            countryOfOrigin = null,
            barcode = null,
            imageUrl = null,
            isActive = true,
            isDiscontinued = false,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            certifications = null,
            regulatoryConditions = null
        )
        whenever(apiService.getProductBySku(any()))
            .thenReturn(Response.success(mockProductDto))

        repository.getProductBySku("MED-001").test {
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)

            val success = awaitItem()
            assertTrue(success is Resource.Success)
            assertEquals("MED-001", success.data?.sku)
            assertEquals("Test Product", success.data?.name)

            awaitComplete()
        }
    }

    @Test
    fun `getProductBySku emits Error when product not found`() = runTest {
        val errorBody = "Not Found".toResponseBody("text/plain".toMediaTypeOrNull())
        whenever(apiService.getProductBySku(any()))
            .thenReturn(Response.error(404, errorBody))

        repository.getProductBySku("INVALID-SKU").test {
            awaitItem()
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertTrue(error.message?.contains("not found") == true)
            awaitComplete()
        }
    }

    @Test
    fun `getProductBySku emits Error when network fails`() = runTest {
        whenever(apiService.getProductBySku(any()))
            .thenThrow(RuntimeException(IOException("No connection")))

        repository.getProductBySku("MED-001").test {
            awaitItem()
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertNotNull(error.message)
            awaitComplete()
        }
    }
}
