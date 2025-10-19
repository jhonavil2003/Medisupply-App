package com.misw.medisupply.domain.usecase.order

import com.google.common.truth.Truth.assertThat
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.repository.order.OrderRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for GetOrdersUseCase
 */
class GetOrdersUseCaseTest {

    private lateinit var repository: OrderRepository
    private lateinit var useCase: GetOrdersUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetOrdersUseCase(repository)
    }

    @Test
    fun `invoke calls repository with correct parameters`() = runTest {
        // Given
        val mockOrders = emptyList<Order>()
        coEvery { 
            repository.getOrders(any(), any(), any()) 
        } returns flowOf(Resource.Success(mockOrders))

        // When
        useCase(
            sellerId = "SELLER-001",
            customerId = 123,
            status = "pending"
        ).toList()

        // Then
        coVerify { 
            repository.getOrders("SELLER-001", 123, "pending") 
        }
    }

    @Test
    fun `invoke returns repository flow unchanged`() = runTest {
        // Given
        val mockOrders = emptyList<Order>()
        val repositoryFlow = flowOf(
            Resource.Loading(),
            Resource.Success(mockOrders)
        )
        coEvery { 
            repository.getOrders(any(), any(), any()) 
        } returns repositoryFlow

        // When
        val results = useCase("SELLER-001", null, null).toList()

        // Then
        assertThat(results).hasSize(2)
        assertThat(results[0]).isInstanceOf(Resource.Loading::class.java)
        assertThat(results[1]).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun `invoke propagates errors from repository`() = runTest {
        // Given
        coEvery { 
            repository.getOrders(any(), any(), any()) 
        } returns flowOf(Resource.Error("Test error"))

        // When
        val results = useCase("SELLER-001", null, null).toList()

        // Then
        assertThat(results).hasSize(1)
        assertThat(results[0]).isInstanceOf(Resource.Error::class.java)
        val errorResult = results[0] as Resource.Error
        assertThat(errorResult.message).isEqualTo("Test error")
    }
}
