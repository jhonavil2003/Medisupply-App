package com.misw.medisupply.data.repository.product

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.data.remote.api.product.ProductApiService
import com.misw.medisupply.domain.model.product.Pagination
import com.misw.medisupply.domain.model.product.Product
import com.misw.medisupply.domain.repository.product.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Product Repository Implementation
 * Handles product data operations with catalog service
 */
class ProductRepositoryImpl @Inject constructor(
    private val apiService: ProductApiService
) : ProductRepository {
    
    override fun getProducts(
        search: String?,
        sku: String?,
        category: String?,
        subcategory: String?,
        supplierId: Int?,
        isActive: Boolean?,
        requiresColdChain: Boolean?,
        page: Int?,
        perPage: Int?
    ): Flow<Resource<Pair<List<Product>, Pagination>>> = flow {
        try {
            emit(Resource.Loading())
            
            val response = apiService.getProducts(
                search = search,
                sku = sku,
                category = category,
                subcategory = subcategory,
                supplierId = supplierId,
                isActive = isActive,
                requiresColdChain = requiresColdChain,
                page = page,
                perPage = perPage
            )
            
            if (response.isSuccessful) {
                response.body()?.let { productsResponse ->
                    val products = productsResponse.products.map { it.toDomain() }
                    val pagination = productsResponse.getPaginationDomain()
                    emit(Resource.Success(Pair(products, pagination)))
                } ?: emit(Resource.Error("Response body is null"))
            } else {
                emit(Resource.Error("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("HTTP Error: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Network Error: Check your internet connection"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected Error: ${e.message}"))
        }
    }
    
    override fun getProductBySku(sku: String): Flow<Resource<Product>> = flow {
        try {
            emit(Resource.Loading())
            
            val response = apiService.getProductBySku(sku)
            
            if (response.isSuccessful) {
                response.body()?.let { productDto ->
                    emit(Resource.Success(productDto.toDomain()))
                } ?: emit(Resource.Error("Product not found"))
            } else {
                when (response.code()) {
                    404 -> emit(Resource.Error("Product with SKU '$sku' not found"))
                    else -> emit(Resource.Error("Error ${response.code()}: ${response.message()}"))
                }
            }
        } catch (e: HttpException) {
            emit(Resource.Error("HTTP Error: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Network Error: Check your internet connection"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected Error: ${e.message}"))
        }
    }
}
