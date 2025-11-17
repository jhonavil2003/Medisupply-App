package com.misw.medisupply.data.network.api

import com.misw.medisupply.data.network.dto.route.*
import retrofit2.Response
import retrofit2.http.*

/**
 * API Service para endpoints de rutas de visitas
 * Base URL: Logistics Service (Puerto 3002)
 */
interface RouteApiService {
    
    /**
     * Generar ruta optimizada
     * POST /routes/visits/generate
     */
    @POST("routes/visits/generate")
    suspend fun generateRoute(
        @Body request: GenerateRouteRequest
    ): Response<GenerateRouteResponse>
    
    /**
     * Obtener detalle de una ruta específica
     * GET /routes/visits/{id}
     */
    @GET("routes/visits/{id}")
    suspend fun getRoute(
        @Path("id") routeId: Int
    ): Response<RouteDto>
    
    /**
     * Listar rutas de un vendedor
     * GET /routes/visits/salesperson/{id}
     */
    @GET("routes/visits/salesperson/{id}")
    suspend fun getSalespersonRoutes(
        @Path("id") salespersonId: Int,
        @Query("date") date: String? = null,
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10
    ): Response<RoutesListResponse>
    
    /**
     * Confirmar ruta (DRAFT -> CONFIRMED)
     * PUT /routes/visits/{id}/confirm
     */
    @PUT("routes/visits/{id}/confirm")
    suspend fun confirmRoute(
        @Path("id") routeId: Int
    ): Response<RouteActionResponse>
    
    /**
     * Iniciar ruta (CONFIRMED -> IN_PROGRESS)
     * PUT /routes/visits/{id}/start
     */
    @PUT("routes/visits/{id}/start")
    suspend fun startRoute(
        @Path("id") routeId: Int
    ): Response<RouteActionResponse>
    
    /**
     * Completar una parada
     * PUT /routes/visits/{id}/stops/{stopId}/complete
     */
    @PUT("routes/visits/{id}/stops/{stopId}/complete")
    suspend fun completeStop(
        @Path("id") routeId: Int,
        @Path("stopId") stopId: Int,
        @Body request: CompleteStopRequest
    ): Response<StopActionResponse>
    
    /**
     * Omitir una parada
     * PUT /routes/visits/{id}/stops/{stopId}/skip
     */
    @PUT("routes/visits/{id}/stops/{stopId}/skip")
    suspend fun skipStop(
        @Path("id") routeId: Int,
        @Path("stopId") stopId: Int,
        @Body request: SkipStopRequest
    ): Response<StopActionResponse>
    
    /**
     * Completar ruta (IN_PROGRESS -> COMPLETED)
     * PUT /routes/visits/{id}/complete
     */
    @PUT("routes/visits/{id}/complete")
    suspend fun completeRoute(
        @Path("id") routeId: Int
    ): Response<RouteActionResponse>
    
    /**
     * Cancelar ruta
     * DELETE /routes/visits/{id}
     */
    @DELETE("routes/visits/{id}")
    suspend fun cancelRoute(
        @Path("id") routeId: Int
    ): Response<MessageResponse>
}
