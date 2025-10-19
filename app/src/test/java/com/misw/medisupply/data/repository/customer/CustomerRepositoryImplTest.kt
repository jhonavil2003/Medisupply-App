package com.misw.medisupply.data.repository.customer

import app.cash.turbine.test
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.data.remote.api.customer.CustomerApiService
import com.misw.medisupply.data.remote.dto.customer.CustomerDto
import com.misw.medisupply.data.remote.dto.customer.CustomersResponse
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

class CustomerRepositoryImplTest {

    @Mock
    private lateinit var apiService: CustomerApiService

    private lateinit var repository: CustomerRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = CustomerRepositoryImpl(apiService)
    }

    @Test
    fun `getCustomers emits Loading then Success when API call succeeds`() = runTest {
        val mockCustomerDto = CustomerDto(
            id = 1,
            documentType = "NIT",
            documentNumber = "900123456-7",
            businessName = "Hospital Test",
            tradeName = "HT",
            customerType = "HOSPITAL",
            contactName = "Test Contact",
            contactEmail = "test@hospital.com",
            contactPhone = "3001234567",
            address = "Address 123",
            city = "Bogotá",
            department = "Cundinamarca",
            country = "Colombia",
            creditLimit = 10000000.0,
            creditDays = 30,
            isActive = true,
            createdAt = null,
            updatedAt = null
        )
        val mockResponse = CustomersResponse(customers = listOf(mockCustomerDto), total = 1)
        whenever(apiService.getCustomers(anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(Response.success(mockResponse))

        repository.getCustomers().test {
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)

            val success = awaitItem()
            assertTrue(success is Resource.Success)
            assertEquals(1, success.data?.size)
            assertEquals("Hospital Test", success.data?.get(0)?.businessName)

            awaitComplete()
        }
    }

    @Test
    fun `getCustomers emits Loading then Error when API returns error`() = runTest {
        val errorBody = "Not Found".toResponseBody("text/plain".toMediaTypeOrNull())
        whenever(apiService.getCustomers(anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(Response.error(404, errorBody))

        repository.getCustomers().test {
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)

            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertNotNull(error.message)

            awaitComplete()
        }
    }

    @Test
    fun `getCustomers emits Error when IOException occurs`() = runTest {
        whenever(apiService.getCustomers(anyOrNull(), anyOrNull(), anyOrNull()))
            .thenThrow(RuntimeException(IOException("Network error")))

        repository.getCustomers().test {
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)

            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertNotNull(error.message)

            awaitComplete()
        }
    }

    @Test
    fun `getCustomers with filters passes correct parameters`() = runTest {
        val mockResponse = CustomersResponse(customers = emptyList(), total = 0)
        whenever(apiService.getCustomers(any(), any(), any()))
            .thenReturn(Response.success(mockResponse))

        repository.getCustomers(
            customerType = "HOSPITAL",
            city = "Bogotá",
            isActive = true
        ).test {
            awaitItem()
            val success = awaitItem()
            assertTrue(success is Resource.Success)
            awaitComplete()
        }
    }

    @Test
    fun `getCustomerById emits Loading then Success when API call succeeds`() = runTest {
        val mockCustomerDto = CustomerDto(
            id = 1,
            documentType = "NIT",
            documentNumber = "900123456-7",
            businessName = "Hospital Test",
            tradeName = null,
            customerType = "HOSPITAL",
            contactName = null,
            contactEmail = null,
            contactPhone = null,
            address = null,
            city = null,
            department = null,
            country = "Colombia",
            creditLimit = 10000000.0,
            creditDays = 30,
            isActive = true,
            createdAt = null,
            updatedAt = null
        )
        whenever(apiService.getCustomerById(any()))
            .thenReturn(Response.success(mockCustomerDto))

        repository.getCustomerById(1).test {
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)

            val success = awaitItem()
            assertTrue(success is Resource.Success)
            assertEquals(1, success.data?.id)
            assertEquals("Hospital Test", success.data?.businessName)

            awaitComplete()
        }
    }

    @Test
    fun `getCustomerById emits Error when customer not found`() = runTest {
        val errorBody = "Not Found".toResponseBody("text/plain".toMediaTypeOrNull())
        whenever(apiService.getCustomerById(any()))
            .thenReturn(Response.error(404, errorBody))

        repository.getCustomerById(999).test {
            awaitItem()
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `getCustomerById emits Error when network fails`() = runTest {
        whenever(apiService.getCustomerById(any()))
            .thenThrow(RuntimeException(IOException("No internet")))

        repository.getCustomerById(1).test {
            awaitItem()
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertNotNull(error.message)
            awaitComplete()
        }
    }
}
