package com.misw.medisupply.domain.usecase.stock

import app.cash.turbine.test
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.stock.DistributionCenter
import com.misw.medisupply.domain.model.stock.MultipleStockLevels
import com.misw.medisupply.domain.model.stock.StockLevel
import com.misw.medisupply.domain.repository.stock.StockRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GetMultipleProductsStockUseCaseTest {

    private lateinit var repository: StockRepository
    private lateinit var useCase: GetMultipleProductsStockUseCase

    private val testDistributionCenter1 = DistributionCenter(
        id = 1,
        code = "DC-001",
        name = "Centro Bogotá",
        city = "Bogotá",
        quantityAvailable = 100,
        quantityReserved = 10,
        quantityInTransit = 5,
        isLowStock = false,
        isOutOfStock = false
    )

    private val testDistributionCenter2 = DistributionCenter(
        id = 2,
        code = "DC-002",
        name = "Centro Medellín",
        city = "Medellín",
        quantityAvailable = 50,
        quantityReserved = 5,
        quantityInTransit = 10,
        isLowStock = false,
        isOutOfStock = false
    )

    private val testStockLevels = listOf(
        StockLevel(
            productSku = "MED-001",
            totalAvailable = 100,
            totalReserved = 10,
            totalInTransit = 5,
            distributionCenters = listOf(testDistributionCenter1)
        ),
        StockLevel(
            productSku = "MED-002",
            totalAvailable = 50,
            totalReserved = 5,
            totalInTransit = 10,
            distributionCenters = listOf(testDistributionCenter2)
        )
    )

    private val testMultipleStockLevels = MultipleStockLevels(
        products = testStockLevels,
        totalProducts = 2
    )

    @Before
    fun setup() {
        repository = mock()
        useCase = GetMultipleProductsStockUseCase(repository)
    }

    @Test
    fun `invoke with valid skus returns success with multiple stock levels`() = runTest {
        val skus = listOf("MED-001", "MED-002")
        whenever(repository.getMultipleProductsStock(skus, null, null, true, false))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))

        useCase.invoke(skus).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            assertEquals(2, (result as Resource.Success).data!!.products.size)
            assertEquals(2, result.data!!.totalProducts)
            assertEquals("MED-001", result.data!!.products[0].productSku)
            assertEquals("MED-002", result.data!!.products[1].productSku)
            awaitComplete()
        }

        verify(repository).getMultipleProductsStock(skus, null, null, true, false)
    }

    @Test
    fun `invoke with single sku returns stock for that product`() = runTest {
        val skus = listOf("MED-001")
        val singleStockLevel = testMultipleStockLevels.copy(
            products = listOf(testStockLevels[0]),
            totalProducts = 1
        )
        whenever(repository.getMultipleProductsStock(skus, null, null, true, false))
            .thenReturn(flowOf(Resource.Success(singleStockLevel)))

        useCase.invoke(skus).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            assertEquals(1, (result as Resource.Success).data!!.products.size)
            assertEquals("MED-001", result.data!!.products[0].productSku)
            awaitComplete()
        }
    }
    
    @Test
    fun `invoke with onlyAvailable true returns only available products`() = runTest {
        val skus = listOf("MED-001", "MED-002")
        whenever(repository.getMultipleProductsStock(skus, null, true, true, false))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))

        useCase.invoke(skus, onlyAvailable = true).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            assertTrue((result as Resource.Success).data!!.products.all { 
                it.totalAvailable > 0 
            })
            awaitComplete()
        }

        verify(repository).getMultipleProductsStock(skus, null, true, true, false)
    }

    @Test
    fun `invoke with includeReserved false excludes reserved quantities`() = runTest {
        val skus = listOf("MED-001", "MED-002")
        whenever(repository.getMultipleProductsStock(skus, null, null, false, false))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))

        useCase.invoke(skus, includeReserved = false).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            awaitComplete()
        }

        verify(repository).getMultipleProductsStock(skus, null, null, false, false)
    }

    @Test
    fun `invoke with includeInTransit true includes in-transit quantities`() = runTest {
        val skus = listOf("MED-001", "MED-002")
        whenever(repository.getMultipleProductsStock(skus, null, null, true, true))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))

        useCase.invoke(skus, includeInTransit = true).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            awaitComplete()
        }

        verify(repository).getMultipleProductsStock(skus, null, null, true, true)
    }

    @Test
    fun `invoke with all parameters passes them correctly to repository`() = runTest {
        val skus = listOf("MED-001", "MED-002")
        whenever(repository.getMultipleProductsStock(skus, 1, true, false, true))
            .thenReturn(flowOf(Resource.Success(testMultipleStockLevels)))

        useCase.invoke(
            productSkus = skus,
            distributionCenterId = 1,
            onlyAvailable = true,
            includeReserved = false,
            includeInTransit = true
        ).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            awaitComplete()
        }

        verify(repository).getMultipleProductsStock(skus, 1, true, false, true)
    }

    @Test
    fun `invoke with empty sku list returns empty result`() = runTest {
        val emptyResult = testMultipleStockLevels.copy(products = emptyList(), totalProducts = 0)
        whenever(repository.getMultipleProductsStock(emptyList(), null, null, true, false))
            .thenReturn(flowOf(Resource.Success(emptyResult)))

        useCase.invoke(emptyList()).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            assertTrue((result as Resource.Success).data!!.products.isEmpty())
            assertEquals(0, result.data!!.totalProducts)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with non-existent skus returns error`() = runTest {
        val skus = listOf("INVALID-SKU-1", "INVALID-SKU-2")
        val errorMessage = "Products not found"
        whenever(repository.getMultipleProductsStock(skus, null, null, true, false))
            .thenReturn(flowOf(Resource.Error(errorMessage)))

        useCase.invoke(skus).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Error)
            assertEquals(errorMessage, (result as Resource.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        val skus = listOf("MED-001", "MED-002")
        val errorMessage = "Network error"
        whenever(repository.getMultipleProductsStock(skus, null, null, true, false))
            .thenReturn(flowOf(Resource.Error(errorMessage)))

        useCase.invoke(skus).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Error)
            assertEquals(errorMessage, (result as Resource.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns loading state from repository`() = runTest {
        val skus = listOf("MED-001", "MED-002")
        whenever(repository.getMultipleProductsStock(skus, null, null, true, false))
            .thenReturn(flowOf(Resource.Loading()))

        useCase.invoke(skus).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Loading)
            awaitComplete()
        }
    }
}
