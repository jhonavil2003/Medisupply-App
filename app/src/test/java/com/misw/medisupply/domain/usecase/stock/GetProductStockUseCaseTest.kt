package com.misw.medisupply.domain.usecase.stock

import app.cash.turbine.test
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.stock.DistributionCenter
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

class GetProductStockUseCaseTest {

    private lateinit var repository: StockRepository
    private lateinit var useCase: GetProductStockUseCase

    private val testDistributionCenter = DistributionCenter(
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

    private val testStockLevel = StockLevel(
        productSku = "MED-001",
        totalAvailable = 100,
        totalReserved = 10,
        totalInTransit = 5,
        distributionCenters = listOf(testDistributionCenter)
    )

    @Before
    fun setup() {
        repository = mock()
        useCase = GetProductStockUseCase(repository)
    }

    @Test
    fun `invoke with valid sku returns success with stock level`() = runTest {
        whenever(repository.getProductStock("MED-001", null, true, false))
            .thenReturn(flowOf(Resource.Success(testStockLevel)))

        useCase.invoke("MED-001").test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            assertEquals("MED-001", (result as Resource.Success).data!!.productSku)
            assertEquals(100, result.data!!.totalAvailable)
            assertEquals(testDistributionCenter.name, result.data!!.distributionCenters[0].name)
            awaitComplete()
        }

        verify(repository).getProductStock("MED-001", null, true, false)
    }

    @Test
    fun `invoke with distribution center filter returns stock for that center`() = runTest {
        whenever(repository.getProductStock("MED-001", 1, true, false))
            .thenReturn(flowOf(Resource.Success(testStockLevel)))

        useCase.invoke("MED-001", distributionCenterId = 1).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            assertEquals(1, (result as Resource.Success).data!!.distributionCenters[0].id)
            assertEquals("DC-001", result.data!!.distributionCenters[0].code)
            awaitComplete()
        }

        verify(repository).getProductStock("MED-001", 1, true, false)
    }

    @Test
    fun `invoke with includeReserved false excludes reserved quantities`() = runTest {
        whenever(repository.getProductStock("MED-001", null, false, false))
            .thenReturn(flowOf(Resource.Success(testStockLevel.copy(totalReserved = 0))))

        useCase.invoke("MED-001", includeReserved = false).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            assertEquals(0, (result as Resource.Success).data!!.totalReserved)
            awaitComplete()
        }

        verify(repository).getProductStock("MED-001", null, false, false)
    }

    @Test
    fun `invoke with includeInTransit true includes in-transit quantities`() = runTest {
        whenever(repository.getProductStock("MED-001", null, true, true))
            .thenReturn(flowOf(Resource.Success(testStockLevel)))

        useCase.invoke("MED-001", includeInTransit = true).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            assertEquals(5, (result as Resource.Success).data!!.totalInTransit)
            awaitComplete()
        }

        verify(repository).getProductStock("MED-001", null, true, true)
    }

    @Test
    fun `invoke with all parameters passes them correctly to repository`() = runTest {
        whenever(repository.getProductStock("MED-001", 1, false, true))
            .thenReturn(flowOf(Resource.Success(testStockLevel)))

        useCase.invoke(
            productSku = "MED-001",
            distributionCenterId = 1,
            includeReserved = false,
            includeInTransit = true
        ).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            awaitComplete()
        }

        verify(repository).getProductStock("MED-001", 1, false, true)
    }

    @Test
    fun `invoke with non-existent sku returns error`() = runTest {
        val errorMessage = "Product not found"
        whenever(repository.getProductStock("INVALID-SKU", null, true, false))
            .thenReturn(flowOf(Resource.Error(errorMessage)))

        useCase.invoke("INVALID-SKU").test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Error)
            assertEquals(errorMessage, (result as Resource.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        val errorMessage = "Network error"
        whenever(repository.getProductStock("MED-001", null, true, false))
            .thenReturn(flowOf(Resource.Error(errorMessage)))

        useCase.invoke("MED-001").test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Error)
            assertEquals(errorMessage, (result as Resource.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns loading state from repository`() = runTest {
        whenever(repository.getProductStock("MED-001", null, true, false))
            .thenReturn(flowOf(Resource.Loading()))

        useCase.invoke("MED-001").test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Loading)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with zero available quantity returns success`() = runTest {
        val zeroStockLevel = testStockLevel.copy(totalAvailable = 0)
        whenever(repository.getProductStock("MED-001", null, true, false))
            .thenReturn(flowOf(Resource.Success(zeroStockLevel)))

        useCase.invoke("MED-001").test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            assertEquals(0, (result as Resource.Success).data!!.totalAvailable)
            awaitComplete()
        }
    }
}
