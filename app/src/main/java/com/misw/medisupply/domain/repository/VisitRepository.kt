package com.misw.medisupply.domain.repository

import com.misw.medisupply.domain.model.visit.Visit
import com.misw.medisupply.domain.model.visit.VisitFile
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import java.io.File

interface VisitRepository {
    suspend fun createVisit(visit: Visit): Result<Visit>
    suspend fun updateVisit(visitId: Int, visit: Visit): Result<Visit>
    suspend fun completeVisit(visitId: Int): Result<Visit>
    
    // ================================
    // MÉTODOS PARA ARCHIVOS
    // ================================
    
    /**
     * Subir archivo a una visita
     */
    suspend fun uploadFile(visitId: Int, file: File, originalFileName: String? = null): Result<VisitFile>
    
    /**
     * Obtener lista de archivos de una visita
     */
    suspend fun getVisitFiles(visitId: Int): Result<List<VisitFile>>
    
    /**
     * Descargar archivo
     */
    suspend fun downloadFile(visitId: Int, fileId: Int): Result<ResponseBody>
    
    /**
     * Eliminar archivo
     */
    suspend fun deleteFile(fileId: Int): Result<Boolean>
}