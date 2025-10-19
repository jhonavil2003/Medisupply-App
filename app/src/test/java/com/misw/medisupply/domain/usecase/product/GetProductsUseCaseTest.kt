package com.misw.medisupply.domain.usecase.product

import app.cash.turbine.test
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.product.Pagination
import com.misw.medisupply.domain.model.product.Product
import com.misw.medisupply.domain.repository.product.ProductRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GetProductsUseCaseTest {

    private lateinit var repository: ProductRepository
    private lateinit var useCase: GetProductsUseCase

    private val testProducts = listOf(
        Product(
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
        ),
        Product(
            id = 2,
            sku = "MED-002",
            name = "Ibuprofeno",
            description = "Antiinflamatorio",
            category = "Medicamentos",
            subcategory = "Antiinflamatorios",
            unitPrice = 15.0f,
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
            barcode = "987654321",
            imageUrl = null,
            isActive = true,
            isDiscontinued = false,
            createdAt = "2024-01-01",
            updatedAt = "2024-01-01"
        )
    )

    private val testPagination = Pagination(
        page = 1,
        perPage = 20,
        totalPages = 1,
        totalItems = 2,
        hasNext = false,
        hasPrev = false
    )

    @Before
    fun setup() {
        repository = mock()
        useCase = GetProductsUseCase(repository)
    }

    @Test
    fun `invoke without filters returns success with products and pagination`() = runTest {
        whenever(repository.getProducts(
            anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(),
            anyOrNull(), any(), anyOrNull(), any(), any()
        )).thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))

        useCase.invoke().test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            val (products, pagination) = (result as Resource.Success).data!!
            assertEquals(2, products.size)
            assertEquals(1, pagination.page)
            assertEquals(2, pagination.totalItems)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with search filter returns filtered products`() = runTest {
        val filteredProducts = listOf(testProducts[0])
        whenever(repository.getProducts(
            search = "Aspirina",
            sku = null,
            category = null,
            subcategory = null,
            supplierId = null,
            isActive = true,
            requiresColdChain = null,
            page = 1,
            perPage = 20
        )).thenReturn(flowOf(Resource.Success(Pair(filteredProducts, testPagination.copy(totalItems = 1)))))

        useCase.invoke(search = "Aspirina").test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            val (products, _) = (result as Resource.Success).data!!
            assertEquals(1, products.size)
            assertEquals("Aspirina", products!![0].name)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with category filter returns products from that category`() = runTest {
        whenever(repository.getProducts(
            search = null,
            sku = null,
            category = "Medicamentos",
            subcategory = null,
            supplierId = null,
            isActive = true,
            requiresColdChain = null,
            page = 1,
            perPage = 20
        )).thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))

        useCase.invoke(category = "Medicamentos").test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            val (products, _) = (result as Resource.Success).data!!
            assertEquals(2, products.size)
            assertTrue(products.all { it.category == "Medicamentos" })
            awaitComplete()
        }
    }

    @Test
    fun `invoke with sku filter returns specific product`() = runTest {
        val filteredProducts = listOf(testProducts[0])
        whenever(repository.getProducts(
            search = null,
            sku = "MED-001",
            category = null,
            subcategory = null,
            supplierId = null,
            isActive = true,
            requiresColdChain = null,
            page = 1,
            perPage = 20
        )).thenReturn(flowOf(Resource.Success(Pair(filteredProducts, testPagination.copy(totalItems = 1)))))

        useCase.invoke(sku = "MED-001").test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            val (products, _) = (result as Resource.Success).data!!
            assertEquals(1, products.size)
            assertEquals("MED-001", products!![0].sku)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with pagination parameters returns correct page`() = runTest {
        whenever(repository.getProducts(
            search = null,
            sku = null,
            category = null,
            subcategory = null,
            supplierId = null,
            isActive = true,
            requiresColdChain = null,
            page = 2,
            perPage = 10
        )).thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination.copy(page = 2, perPage = 10)))))

        useCase.invoke(page = 2, perPage = 10).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            val (_, pagination) = (result as Resource.Success).data!!
            assertEquals(2, pagination.page)
            assertEquals(10, pagination.perPage)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with supplierId filter returns products from that supplier`() = runTest {
        whenever(repository.getProducts(
            search = null,
            sku = null,
            category = null,
            subcategory = null,
            supplierId = 1,
            isActive = true,
            requiresColdChain = null,
            page = 1,
            perPage = 20
        )).thenReturn(flowOf(Resource.Success(Pair(testProducts, testPagination))))

        useCase.invoke(supplierId = 1).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            val (products, _) = (result as Resource.Success).data!!
            assertTrue(products.all { it.supplierId == 1 })
            awaitComplete()
        }
    }

    @Test
    fun `invoke with requiresColdChain filter returns only cold chain products`() = runTest {
        whenever(repository.getProducts(
            search = null,
            sku = null,
            category = null,
            subcategory = null,
            supplierId = null,
            isActive = true,
            requiresColdChain = true,
            page = 1,
            perPage = 20
        )).thenReturn(flowOf(Resource.Success(Pair(emptyList(), testPagination.copy(totalItems = 0)))))

        useCase.invoke(requiresColdChain = true).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            val (products, _) = (result as Resource.Success).data!!
            assertTrue(products.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns empty list when no products found`() = runTest {
        whenever(repository.getProducts(
            anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(),
            anyOrNull(), any(), anyOrNull(), any(), any()
        )).thenReturn(flowOf(Resource.Success(Pair(emptyList(), testPagination.copy(totalItems = 0)))))

        useCase.invoke().test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            val (products, _) = (result as Resource.Success).data!!
            assertTrue(products.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        val errorMessage = "Network error"
        whenever(repository.getProducts(
            anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(),
            anyOrNull(), any(), anyOrNull(), any(), any()
        )).thenReturn(flowOf(Resource.Error(errorMessage)))

        useCase.invoke().test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Error)
            assertEquals(errorMessage, (result as Resource.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns loading state from repository`() = runTest {
        whenever(repository.getProducts(
            anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(),
            anyOrNull(), any(), anyOrNull(), any(), any()
        )).thenReturn(flowOf(Resource.Loading()))

        useCase.invoke().test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Loading)
            awaitComplete()
        }
    }
}
