package com.misw.medisupply.data.network.dto.visit

import com.google.gson.annotations.SerializedName

data class CreateVisitApiResponse(
    @SerializedName("message")
    val message: String,
    
    @SerializedName("visit")
    val visit: CreateVisitResponse
)