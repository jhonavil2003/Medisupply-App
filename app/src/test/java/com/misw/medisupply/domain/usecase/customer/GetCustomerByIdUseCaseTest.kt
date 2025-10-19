package com.misw.medisupply.domain.usecase.customer

import app.cash.turbine.test
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.customer.CustomerType
import com.misw.medisupply.domain.model.customer.DocumentType
import com.misw.medisupply.domain.repository.customer.CustomerRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Date

class GetCustomerByIdUseCaseTest {

    private lateinit var repository: CustomerRepository
    private lateinit var useCase: GetCustomerByIdUseCase

    private val testCustomer = Customer(
        id = 1,
        documentType = DocumentType.NIT,
        documentNumber = "123456789",
        businessName = "Hospital ABC",
        tradeName = "Hospital ABC",
        customerType = CustomerType.HOSPITAL,
        contactName = "John Doe",
        contactEmail = "john@hospital.com",
        contactPhone = "123456789",
        address = "Calle 123",
        city = "Bogotá",
        department = "Cundinamarca",
        country = "Colombia",
        creditLimit = 10000.0,
        creditDays = 30,
        isActive = true,
        createdAt = Date(),
        updatedAt = Date()
    )

    @Before
    fun setup() {
        repository = mock()
        useCase = GetCustomerByIdUseCase(repository)
    }

    @Test
    fun `invoke with valid id returns success with customer`() = runTest {
        whenever(repository.getCustomerById(1))
            .thenReturn(flowOf(Resource.Success(testCustomer)))

        useCase.invoke(1).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            assertEquals(1, (result as Resource.Success).data!!.id)
            assertEquals("Hospital ABC", result.data!!.businessName)
            assertEquals("123456789", result.data!!.documentNumber)
            awaitComplete()
        }

        verify(repository).getCustomerById(1)
    }

    @Test
    fun `invoke with different id calls repository with correct parameter`() = runTest {
        val customerId = 42
        whenever(repository.getCustomerById(customerId))
            .thenReturn(flowOf(Resource.Success(testCustomer.copy(id = customerId))))

        useCase.invoke(customerId).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            assertEquals(customerId, (result as Resource.Success).data!!.id)
            awaitComplete()
        }

        verify(repository).getCustomerById(customerId)
    }

    @Test
    fun `invoke with non-existent id returns error`() = runTest {
        val errorMessage = "Customer not found"
        whenever(repository.getCustomerById(999))
            .thenReturn(flowOf(Resource.Error(errorMessage)))

        useCase.invoke(999).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Error)
            assertEquals(errorMessage, (result as Resource.Error).message)
            awaitComplete()
        }

        verify(repository).getCustomerById(999)
    }

    @Test
    fun `invoke returns loading state from repository`() = runTest {
        whenever(repository.getCustomerById(1))
            .thenReturn(flowOf(Resource.Loading()))

        useCase.invoke(1).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Loading)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        val errorMessage = "Network error"
        whenever(repository.getCustomerById(1))
            .thenReturn(flowOf(Resource.Error(errorMessage)))

        useCase.invoke(1).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Error)
            assertEquals(errorMessage, (result as Resource.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with negative id calls repository correctly`() = runTest {
        val errorMessage = "Invalid customer ID"
        whenever(repository.getCustomerById(-1))
            .thenReturn(flowOf(Resource.Error(errorMessage)))

        useCase.invoke(-1).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Error)
            awaitComplete()
        }

        verify(repository).getCustomerById(-1)
    }
}
