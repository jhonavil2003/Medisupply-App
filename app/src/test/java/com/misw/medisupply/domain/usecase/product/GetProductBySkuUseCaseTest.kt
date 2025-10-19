package com.misw.medisupply.domain.usecase.product

import app.cash.turbine.test
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.product.Product
import com.misw.medisupply.domain.repository.product.ProductRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GetProductBySkuUseCaseTest {

    private lateinit var repository: ProductRepository
    private lateinit var useCase: GetProductBySkuUseCase

    private val testProduct = Product(
        id = 1,
        sku = "MED-001",
        name = "Aspirina",
        description = "Analgésico",
        category = "Medicamentos",
        subcategory = "Analgésicos",
        unitPrice = 10.5f,
        currency = "USD",
        unitOfMeasure = "Caja",
        supplierId = 1,
        supplierName = "Supplier A",
        requiresColdChain = false,
        storageConditions = null,
        regulatoryInfo = null,
        physicalDimensions = null,
        manufacturer = "Pharma Inc",
        countryOfOrigin = "USA",
        barcode = "123456789",
        imageUrl = null,
        isActive = true,
        isDiscontinued = false,
        createdAt = "2024-01-01",
        updatedAt = "2024-01-01"
    )

    @Before
    fun setup() {
        repository = mock()
        useCase = GetProductBySkuUseCase(repository)
    }

    @Test
    fun `invoke with valid sku returns success with product`() = runTest {
        whenever(repository.getProductBySku("MED-001"))
            .thenReturn(flowOf(Resource.Success(testProduct)))

        useCase.invoke("MED-001").test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            assertEquals("MED-001", (result as Resource.Success).data!!.sku)
            assertEquals("Aspirina", result.data!!.name)
            assertEquals(10.5f, result.data!!.unitPrice)
            awaitComplete()
        }

        verify(repository).getProductBySku("MED-001")
    }

    @Test
    fun `invoke with different sku calls repository with correct parameter`() = runTest {
        val sku = "MED-999"
        whenever(repository.getProductBySku(sku))
            .thenReturn(flowOf(Resource.Success(testProduct.copy(sku = sku))))

        useCase.invoke(sku).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            assertEquals(sku, (result as Resource.Success).data!!.sku)
            awaitComplete()
        }

        verify(repository).getProductBySku(sku)
    }

    @Test
    fun `invoke with non-existent sku returns error`() = runTest {
        val errorMessage = "Product not found"
        whenever(repository.getProductBySku("INVALID-SKU"))
            .thenReturn(flowOf(Resource.Error(errorMessage)))

        useCase.invoke("INVALID-SKU").test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Error)
            assertEquals(errorMessage, (result as Resource.Error).message)
            awaitComplete()
        }

        verify(repository).getProductBySku("INVALID-SKU")
    }

    @Test
    fun `invoke with empty sku returns error`() = runTest {
        val errorMessage = "SKU cannot be empty"
        whenever(repository.getProductBySku(""))
            .thenReturn(flowOf(Resource.Error(errorMessage)))

        useCase.invoke("").test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Error)
            awaitComplete()
        }

        verify(repository).getProductBySku("")
    }

    @Test
    fun `invoke returns loading state from repository`() = runTest {
        whenever(repository.getProductBySku("MED-001"))
            .thenReturn(flowOf(Resource.Loading()))

        useCase.invoke("MED-001").test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Loading)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        val errorMessage = "Network error"
        whenever(repository.getProductBySku("MED-001"))
            .thenReturn(flowOf(Resource.Error(errorMessage)))

        useCase.invoke("MED-001").test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Error)
            assertEquals(errorMessage, (result as Resource.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with sku containing special characters calls repository correctly`() = runTest {
        val specialSku = "MED-001-#@"
        whenever(repository.getProductBySku(specialSku))
            .thenReturn(flowOf(Resource.Success(testProduct.copy(sku = specialSku))))

        useCase.invoke(specialSku).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            awaitComplete()
        }

        verify(repository).getProductBySku(specialSku)
    }
}
