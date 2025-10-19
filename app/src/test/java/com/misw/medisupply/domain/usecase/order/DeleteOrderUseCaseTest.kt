package com.misw.medisupply.domain.usecase.order

import com.google.common.truth.Truth.assertThat
import com.misw.medisupply.core.base.Resource
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
 * Unit tests for DeleteOrderUseCase
 */
class DeleteOrderUseCaseTest {

    private lateinit var repository: OrderRepository
    private lateinit var useCase: DeleteOrderUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = DeleteOrderUseCase(repository)
    }

    @Test
    fun `invoke calls repository with correct order ID`() = runTest {
        // Given
        coEvery { 
            repository.deleteOrder(1) 
        } returns flowOf(Resource.Success(Unit))

        // When
        useCase(1).toList()

        // Then
        coVerify { repository.deleteOrder(1) }
    }

    @Test
    fun `invoke returns success when deletion succeeds`() = runTest {
        // Given
        coEvery { 
            repository.deleteOrder(1) 
        } returns flowOf(
            Resource.Loading(),
            Resource.Success(Unit)
        )

        // When
        val results = useCase(1).toList()

        // Then
        assertThat(results).hasSize(2)
        assertThat(results[0]).isInstanceOf(Resource.Loading::class.java)
        assertThat(results[1]).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun `invoke propagates error when deletion fails due to status`() = runTest {
        // Given
        coEvery { 
            repository.deleteOrder(1) 
        } returns flowOf(
            Resource.Error("No se puede eliminar. Solo se pueden eliminar pedidos en estado Pendiente o Cancelado")
        )

        // When
        val results = useCase(1).toList()

        // Then
        assertThat(results).hasSize(1)
        assertThat(results[0]).isInstanceOf(Resource.Error::class.java)
        
        val errorResult = results[0] as Resource.Error
        assertThat(errorResult.message).contains("Pendiente o Cancelado")
    }

    @Test
    fun `invoke propagates error when deletion fails due to permissions`() = runTest {
        // Given
        coEvery { 
            repository.deleteOrder(1) 
        } returns flowOf(
            Resource.Error("No tiene permisos para eliminar este pedido")
        )

        // When
        val results = useCase(1).toList()

        // Then
        assertThat(results).hasSize(1)
        assertThat(results[0]).isInstanceOf(Resource.Error::class.java)
        
        val errorResult = results[0] as Resource.Error
        assertThat(errorResult.message).contains("No tiene permisos")
    }

    @Test
    fun `invoke propagates error when order not found`() = runTest {
        // Given
        coEvery { 
            repository.deleteOrder(999) 
        } returns flowOf(
            Resource.Error("Pedido no encontrado")
        )

        // When
        val results = useCase(999).toList()

        // Then
        assertThat(results).hasSize(1)
        assertThat(results[0]).isInstanceOf(Resource.Error::class.java)
        
        val errorResult = results[0] as Resource.Error
        assertThat(errorResult.message).contains("no encontrado")
    }

    @Test
    fun `invoke handles multiple order deletions correctly`() = runTest {
        // Given
        coEvery { repository.deleteOrder(1) } returns flowOf(Resource.Success(Unit))
        coEvery { repository.deleteOrder(2) } returns flowOf(Resource.Success(Unit))
        coEvery { repository.deleteOrder(3) } returns flowOf(Resource.Success(Unit))

        // When
        useCase(1).toList()
        useCase(2).toList()
        useCase(3).toList()

        // Then
        coVerify(exactly = 1) { repository.deleteOrder(1) }
        coVerify(exactly = 1) { repository.deleteOrder(2) }
        coVerify(exactly = 1) { repository.deleteOrder(3) }
    }
}
