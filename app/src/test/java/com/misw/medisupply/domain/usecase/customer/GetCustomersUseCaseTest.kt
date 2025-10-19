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

class GetCustomersUseCaseTest {

    private lateinit var repository: CustomerRepository
    private lateinit var useCase: GetCustomersUseCase

    private val testCustomers = listOf(
        Customer(
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
        ),
        Customer(
            id = 2,
            documentType = DocumentType.NIT,
            documentNumber = "987654321",
            businessName = "Clínica XYZ",
            tradeName = "Clínica XYZ",
            customerType = CustomerType.CLINICA,
            contactName = "Jane Smith",
            contactEmail = "jane@clinica.com",
            contactPhone = "987654321",
            address = "Carrera 45",
            city = "Medellín",
            department = "Antioquia",
            country = "Colombia",
            creditLimit = 15000.0,
            creditDays = 45,
            isActive = true,
            createdAt = Date(),
            updatedAt = Date()
        )
    )

    @Before
    fun setup() {
        repository = mock()
        useCase = GetCustomersUseCase(repository)
    }

    @Test
    fun `invoke without filters returns success with customers list`() = runTest {
        whenever(repository.getCustomers(null, null, null))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        useCase.invoke().test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            assertEquals(2, (result as Resource.Success).data!!.size)
            assertEquals("Hospital ABC", result.data!![0].businessName)
            assertEquals("Clínica XYZ", result.data!![1].businessName)
            awaitComplete()
        }

        verify(repository).getCustomers(null, null, null)
    }

    @Test
    fun `invoke with customerType filter returns filtered customers`() = runTest {
        val filteredCustomers = listOf(testCustomers[0])
        whenever(repository.getCustomers("HOSPITAL", null, null))
            .thenReturn(flowOf(Resource.Success(filteredCustomers)))

        useCase.invoke(customerType = "HOSPITAL").test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            assertEquals(1, (result as Resource.Success).data!!.size)
            assertEquals(CustomerType.HOSPITAL, result.data!![0].customerType)
            awaitComplete()
        }

        verify(repository).getCustomers("HOSPITAL", null, null)
    }

    @Test
    fun `invoke with city filter returns customers from that city`() = runTest {
        val filteredCustomers = listOf(testCustomers[0])
        whenever(repository.getCustomers(null, "Bogotá", null))
            .thenReturn(flowOf(Resource.Success(filteredCustomers)))

        useCase.invoke(city = "Bogotá").test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            assertEquals(1, (result as Resource.Success).data!!.size)
            assertEquals("Bogotá", result.data!![0].city)
            awaitComplete()
        }

        verify(repository).getCustomers(null, "Bogotá", null)
    }

    @Test
    fun `invoke with isActive filter returns only active customers`() = runTest {
        whenever(repository.getCustomers(null, null, true))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        useCase.invoke(isActive = true).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            assertEquals(2, (result as Resource.Success).data!!.size)
            assertTrue(result.data!!.all { it.isActive })
            awaitComplete()
        }

        verify(repository).getCustomers(null, null, true)
    }

    @Test
    fun `invoke with all filters returns correctly filtered customers`() = runTest {
        val filteredCustomers = listOf(testCustomers[0])
        whenever(repository.getCustomers("HOSPITAL", "Bogotá", true))
            .thenReturn(flowOf(Resource.Success(filteredCustomers)))

        useCase.invoke(
            customerType = "HOSPITAL",
            city = "Bogotá",
            isActive = true
        ).test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            assertEquals(1, (result as Resource.Success).data!!.size)
            awaitComplete()
        }

        verify(repository).getCustomers("HOSPITAL", "Bogotá", true)
    }

    @Test
    fun `invoke returns empty list when no customers found`() = runTest {
        whenever(repository.getCustomers(null, null, null))
            .thenReturn(flowOf(Resource.Success(emptyList())))

        useCase.invoke().test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Success)
            assertTrue((result as Resource.Success).data!!.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        val errorMessage = "Network error"
        whenever(repository.getCustomers(null, null, null))
            .thenReturn(flowOf(Resource.Error(errorMessage)))

        useCase.invoke().test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Error)
            assertEquals(errorMessage, (result as Resource.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns loading state from repository`() = runTest {
        whenever(repository.getCustomers(null, null, null))
            .thenReturn(flowOf(Resource.Loading()))

        useCase.invoke().test {
            val result = awaitItem()
            
            assertTrue(result is Resource.Loading)
            awaitComplete()
        }
    }
}
