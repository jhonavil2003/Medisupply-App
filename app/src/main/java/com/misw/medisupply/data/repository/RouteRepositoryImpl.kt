package com.misw.medisupply.data.repository

import com.misw.medisupply.data.network.api.RouteApiService
import com.misw.medisupply.data.network.dto.route.*
import com.misw.medisupply.data.network.mapper.*
import com.misw.medisupply.domain.model.route.*
import com.misw.medisupply.domain.repository.RouteRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouteRepositoryImpl @Inject constructor(
    private val routeApiService: RouteApiService
) : RouteRepository {
    
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    override suspend fun generateRoute(
        salespersonId: Int,
        salespersonName: String,
        employeeId: String,
        customerIds: List<Int>,
        plannedDate: LocalDate,
        optimizationStrategy: OptimizationStrategy,
        startLocation: Location?,
        endLocation: Location?,
        workHours: WorkHours?,
        serviceTimePerVisitMinutes: Int
    ): Result<RouteGenerationResult> {
        return try {
            val request = GenerateRouteRequest(
                salespersonId = salespersonId,
                salespersonName = salespersonName,
                salespersonEmployeeId = employeeId,
                customerIds = customerIds,
                plannedDate = plannedDate.format(dateFormatter),
                optimizationStrategy = optimizationStrategy.apiValue,
                startLocation = startLocation?.toDto(),
                endLocation = endLocation?.toDto(),
                workHours = workHours?.toDto(),
                serviceTimePerVisitMinutes = serviceTimePerVisitMinutes
            )
            
            val response = routeApiService.generateRoute(request)
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    try {
                        val route = responseBody.route.toRoute()
                        val computationTime = responseBody.computationTimeSeconds
                        val warnings = responseBody.warnings
                        Result.success(RouteGenerationResult(
                            route = route,
                            computationTime = computationTime,
                            warnings = warnings
                        ))
                    } catch (e: Exception) {
                        // Log parsing error for debugging
                        android.util.Log.e("RouteRepository", "Error parsing route response", e)
                        Result.failure(Exception("Error parsing response: ${e.message}"))
                    }
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody?.isNotEmpty() == true) {
                    try {
                        // Intenta extraer el mensaje de error del JSON
                        val errorJson = com.google.gson.JsonParser.parseString(errorBody).asJsonObject
                        errorJson.get("error")?.asString ?: "Error ${response.code()}: ${response.message()}"
                    } catch (e: Exception) {
                        "Error ${response.code()}: ${response.message()}"
                    }
                } else {
                    "Error ${response.code()}: ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getRoute(routeId: Int): Result<Route> {
        return try {
            val response = routeApiService.getRoute(routeId)
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val route = responseBody.toRoute()
                    Result.success(route)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSalespersonRoutes(
        salespersonId: Int,
        date: LocalDate?,
        status: RouteStatus?,
        page: Int,
        perPage: Int
    ): Result<List<Route>> {
        return try {
            val response = routeApiService.getSalespersonRoutes(
                salespersonId = salespersonId,
                date = date?.format(dateFormatter),
                status = status?.apiValue,
                page = page,
                perPage = perPage
            )
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val routes = responseBody.routes.map { it.toRoute() }
                    Result.success(routes)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun confirmRoute(routeId: Int): Result<Route> {
        return try {
            val response = routeApiService.confirmRoute(routeId)
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val route = responseBody.route.toRoute()
                    Result.success(route)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun startRoute(routeId: Int): Result<Route> {
        return try {
            val response = routeApiService.startRoute(routeId)
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val route = responseBody.route.toRoute()
                    Result.success(route)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun completeStop(
        routeId: Int,
        stopId: Int,
        actualArrival: LocalDateTime,
        actualDeparture: LocalDateTime,
        notes: String?
    ): Result<RouteStop> {
        return try {
            val request = CompleteStopRequest(
                actualArrival = actualArrival.format(dateTimeFormatter),
                actualDeparture = actualDeparture.format(dateTimeFormatter),
                notes = notes
            )
            
            val response = routeApiService.completeStop(routeId, stopId, request)
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val stop = responseBody.stop.toRouteStop()
                    Result.success(stop)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun skipStop(
        routeId: Int,
        stopId: Int,
        skipReason: String
    ): Result<RouteStop> {
        return try {
            val request = SkipStopRequest(reason = skipReason)
            
            val response = routeApiService.skipStop(routeId, stopId, request)
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val stop = responseBody.stop.toRouteStop()
                    Result.success(stop)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun completeRoute(routeId: Int): Result<Route> {
        return try {
            val response = routeApiService.completeRoute(routeId)
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val route = responseBody.route.toRoute()
                    Result.success(route)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun cancelRoute(routeId: Int): Result<Unit> {
        return try {
            val response = routeApiService.cancelRoute(routeId)
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
