package com.misw.medisupply.data.network.api

import com.misw.medisupply.data.network.dto.visit.CreateVisitRequest
import com.misw.medisupply.data.network.dto.visit.CreateVisitResponse
import com.misw.medisupply.data.network.dto.visit.CreateVisitApiResponse
import com.misw.medisupply.data.network.dto.visit.UpdateVisitRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface VisitApiService {
    
    @POST("visits")
    suspend fun createVisit(
        @Body request: CreateVisitRequest
    ): Response<CreateVisitApiResponse>
    
    @PUT("visits/{id}")
    suspend fun updateVisit(
        @Path("id") visitId: Int,
        @Body request: UpdateVisitRequest
    ): Response<CreateVisitApiResponse>
    
    @POST("visits/{id}/complete")
    suspend fun completeVisit(
        @Path("id") visitId: Int
    ): Response<CreateVisitApiResponse>
}