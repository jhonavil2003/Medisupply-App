package com.misw.medisupply.data.network.api

import com.misw.medisupply.data.network.dto.visit.CreateVisitRequest
import com.misw.medisupply.data.network.dto.visit.CreateVisitResponse
import com.misw.medisupply.data.network.dto.visit.CreateVisitApiResponse
import com.misw.medisupply.data.network.dto.visit.UpdateVisitRequest
import com.misw.medisupply.domain.model.visit.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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
    
    // ================================
    // ENDPOINTS PARA ARCHIVOS
    // ================================
    
    /**
     * Subir archivo a una visita
     * POST /visits/{visit_id}/files
     */
    @Multipart
    @POST("visits/{visit_id}/files")
    suspend fun uploadFile(
        @Path("visit_id") visitId: Int,
        @Part file: MultipartBody.Part
    ): Response<UploadFileResponse>
    
    /**
     * Obtener lista de archivos de una visita
     * GET /visits/{visit_id}/files
     */
    @GET("visits/{visit_id}/files")
    suspend fun getVisitFiles(
        @Path("visit_id") visitId: Int,
        @Query("include_metadata") includeMetadata: Boolean = false
    ): Response<List<VisitFile>>
    
    /**
     * Obtener archivos con metadata
     * GET /visits/{visit_id}/files?include_metadata=true
     */
    @GET("visits/{visit_id}/files")
    suspend fun getVisitFilesWithMetadata(
        @Path("visit_id") visitId: Int,
        @Query("include_metadata") includeMetadata: Boolean = true
    ): Response<FilesWithMetadataResponse>
    
    /**
     * Descargar archivo
     * GET /visits/{visit_id}/files/{file_id}/download
     */
    @GET("visits/{visit_id}/files/{file_id}/download")
    suspend fun downloadFile(
        @Path("visit_id") visitId: Int,
        @Path("file_id") fileId: Int
    ): Response<ResponseBody>
    
    /**
     * Eliminar archivo por visita y ID
     * DELETE /visits/{visit_id}/files/{file_id}
     */
    @DELETE("visits/{visit_id}/files/{file_id}")
    suspend fun deleteFile(
        @Path("visit_id") visitId: Int,
        @Path("file_id") fileId: Int
    ): Response<DeleteFileResponse>
    
    /**
     * Eliminar archivo usando solo file ID (ruta global)
     * DELETE /visits/files/{file_id}
     */
    @DELETE("visits/files/{file_id}")
    suspend fun deleteFileGlobal(
        @Path("file_id") fileId: Int
    ): Response<DeleteFileResponse>
}