package com.misw.medisupply.domain.usecase.order

import app.cash.turbine.test
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.repository.order.OrderRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class DeleteOrderUseCaseTest {

    private lateinit var repository: OrderRepository
    private lateinit var useCase: DeleteOrderUseCase

    @Before
    fun setup() {
        repository = mock()
        useCase = DeleteOrderUseCase(repository)
    }

    @Test
    fun `invoke with valid orderId returns success`() = runTest {
        // Given
        val orderId = 1
        whenever(repository.deleteOrder(orderId))
            .thenReturn(flowOf(Resource.Success(Unit)))

        // When & Then
        useCase(orderId).test {
            val result = awaitItem()
            assertTrue(result is Resource.Success)
            assertEquals(Unit, result.data)
            cancelAndIgnoreRemainingEvents()
        }

        verify(repository).deleteOrder(eq(orderId))
    }

    @Test
    fun `invoke returns error when order is not PENDING`() = runTest {
        // Given
        val orderId = 1
        whenever(repository.deleteOrder(orderId))
            .thenReturn(flowOf(Resource.Error("Validación fallida: Solo se pueden eliminar pedidos en estado Pendiente")))

        // When & Then
        useCase(orderId).test {
            val result = awaitItem()
            assertTrue(result is Resource.Error)
            assertTrue(result.message?.contains("Pendiente") == true)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `invoke returns error 404 when order not found`() = runTest {
        // Given
        val orderId = 999
        whenever(repository.deleteOrder(orderId))
            .thenReturn(flowOf(Resource.Error("Pedido no encontrado")))

        // When & Then
        useCase(orderId).test {
            val result = awaitItem()
            assertTrue(result is Resource.Error)
            assertTrue(result.message?.contains("encontrado") == true)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `invoke returns error 500 on server error`() = runTest {
        // Given
        val orderId = 1
        whenever(repository.deleteOrder(orderId))
            .thenReturn(flowOf(Resource.Error("Error del servidor al eliminar el pedido")))

        // When & Then
        useCase(orderId).test {
            val result = awaitItem()
            assertTrue(result is Resource.Error)
            assertTrue(result.message?.contains("servidor") == true)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `invoke handles network exception`() = runTest {
        // Given
        val orderId = 1
        whenever(repository.deleteOrder(orderId))
            .thenReturn(flowOf(Resource.Error("Error de red al eliminar pedido")))

        // When & Then
        useCase(orderId).test {
            val result = awaitItem()
            assertTrue(result is Resource.Error)
            assertTrue(result.message?.contains("red") == true)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `invoke returns loading state from repository`() = runTest {
        // Given
        val orderId = 1
        whenever(repository.deleteOrder(orderId))
            .thenReturn(
                flowOf(
                    Resource.Loading(),
                    Resource.Success(Unit)
                )
            )

        // When & Then
        useCase(orderId).test {
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)

            val success = awaitItem()
            assertTrue(success is Resource.Success)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `invoke calls repository exactly once`() = runTest {
        // Given
        val orderId = 1
        whenever(repository.deleteOrder(orderId))
            .thenReturn(flowOf(Resource.Success(Unit)))

        // When
        useCase(orderId).test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        // Then
        verify(repository).deleteOrder(eq(orderId))
    }

    @Test
    fun `invoke with different orderIds calls repository with correct ids`() = runTest {
        // Given
        val orderId1 = 1
        val orderId2 = 2

        whenever(repository.deleteOrder(any()))
            .thenReturn(flowOf(Resource.Success(Unit)))

        // When
        useCase(orderId1).test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        useCase(orderId2).test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        // Then
        verify(repository).deleteOrder(eq(orderId1))
        verify(repository).deleteOrder(eq(orderId2))
    }
}
