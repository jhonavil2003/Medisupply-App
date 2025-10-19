package com.misw.medisupply.domain.model.product

/**
 * Product domain model
 * Represents a medical product in the catalog service
 */
data class Product(
    val id: Int,
    val sku: String,
    val name: String,
    val description: String?,
    val category: String,
    val subcategory: String?,
    val unitPrice: Float,
    val currency: String,
    val unitOfMeasure: String,
    val supplierId: Int,
    val supplierName: String?,
    val requiresColdChain: Boolean,
    val storageConditions: StorageConditions?,
    val regulatoryInfo: RegulatoryInfo?,
    val physicalDimensions: PhysicalDimensions?,
    val manufacturer: String?,
    val countryOfOrigin: String?,
    val barcode: String?,
    val imageUrl: String?,
    val isActive: Boolean,
    val isDiscontinued: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val certifications: List<ProductCertification>? = null,
    val regulatoryConditions: List<ProductRegulatoryCondition>? = null
) {
    /**
     * Get product display name with SKU
     */
    fun getDisplayName(): String = "$name ($sku)"
    
    /**
     * Check if product requires special handling
     */
    fun requiresSpecialHandling(): Boolean = requiresColdChain || 
        regulatoryInfo?.requiresPrescription == true ||
        regulatoryInfo?.regulatoryClass == "Clase III"
    
    /**
     * Get formatted price with currency
     */
    fun getFormattedPrice(): String = "$currency ${String.format("%.2f", unitPrice)}"
}
