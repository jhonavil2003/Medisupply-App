package com.misw.medisupply.data.remote.dto.product

import com.google.gson.annotations.SerializedName
import com.misw.medisupply.domain.model.product.*

/**
 * Product DTO from catalog service
 */
data class ProductDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("sku")
    val sku: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("category")
    val category: String,
    @SerializedName("subcategory")
    val subcategory: String?,
    @SerializedName("unit_price")
    val unitPrice: Float,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("unit_of_measure")
    val unitOfMeasure: String,
    @SerializedName("supplier_id")
    val supplierId: Int,
    @SerializedName("supplier_name")
    val supplierName: String?,
    @SerializedName("requires_cold_chain")
    val requiresColdChain: Boolean,
    @SerializedName("storage_conditions")
    val storageConditions: StorageConditionsDto?,
    @SerializedName("regulatory_info")
    val regulatoryInfo: RegulatoryInfoDto?,
    @SerializedName("physical_dimensions")
    val physicalDimensions: PhysicalDimensionsDto?,
    @SerializedName("manufacturer")
    val manufacturer: String?,
    @SerializedName("country_of_origin")
    val countryOfOrigin: String?,
    @SerializedName("barcode")
    val barcode: String?,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("is_discontinued")
    val isDiscontinued: Boolean,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("certifications")
    val certifications: List<ProductCertificationDto>?,
    @SerializedName("regulatory_conditions")
    val regulatoryConditions: List<ProductRegulatoryConditionDto>?
) {
    /**
     * Convert DTO to domain model
     */
    fun toDomain(): Product {
        return Product(
            id = id,
            sku = sku,
            name = name,
            description = description,
            category = category,
            subcategory = subcategory,
            unitPrice = unitPrice,
            currency = currency,
            unitOfMeasure = unitOfMeasure,
            supplierId = supplierId,
            supplierName = supplierName,
            requiresColdChain = requiresColdChain,
            storageConditions = storageConditions?.let {
                StorageConditions(
                    temperatureMin = it.temperatureMin,
                    temperatureMax = it.temperatureMax,
                    humidityMax = it.humidityMax
                )
            },
            regulatoryInfo = regulatoryInfo?.let {
                RegulatoryInfo(
                    sanitaryRegistration = it.sanitaryRegistration,
                    requiresPrescription = it.requiresPrescription,
                    regulatoryClass = it.regulatoryClass
                )
            },
            physicalDimensions = physicalDimensions?.let {
                PhysicalDimensions(
                    weightKg = it.weightKg,
                    lengthCm = it.lengthCm,
                    widthCm = it.widthCm,
                    heightCm = it.heightCm
                )
            },
            manufacturer = manufacturer,
            countryOfOrigin = countryOfOrigin,
            barcode = barcode,
            imageUrl = imageUrl,
            isActive = isActive,
            isDiscontinued = isDiscontinued,
            createdAt = createdAt,
            updatedAt = updatedAt,
            certifications = certifications?.map {
                ProductCertification(
                    id = it.id,
                    certificationType = it.certificationType,
                    certificationNumber = it.certificationNumber,
                    issuingAuthority = it.issuingAuthority,
                    issueDate = it.issueDate,
                    expirationDate = it.expirationDate,
                    isValid = it.isValid,
                    documentUrl = it.documentUrl,
                    createdAt = it.createdAt
                )
            },
            regulatoryConditions = regulatoryConditions?.map {
                ProductRegulatoryCondition(
                    id = it.id,
                    conditionType = it.conditionType,
                    description = it.description,
                    isMandatory = it.isMandatory,
                    createdAt = it.createdAt
                )
            }
        )
    }
}
