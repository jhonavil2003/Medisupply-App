package com.misw.medisupply.domain.model.common

/**
 * Generic class to represent paginated results
 * 
 * @param T The type of items in the page
 * @property items List of items in the current page
 * @property total Total number of items across all pages
 * @property page Current page number (1-indexed)
 * @property perPage Number of items per page
 * @property totalPages Total number of pages available
 */
data class PaginatedResult<T>(
    val items: List<T>,
    val total: Int,
    val page: Int,
    val perPage: Int,
    val totalPages: Int
) {
    /**
     * Check if there are more pages available
     */
    val hasMore: Boolean
        get() = page < totalPages
    
    /**
     * Check if this is the first page
     */
    val isFirstPage: Boolean
        get() = page == 1
    
    /**
     * Check if this is the last page
     */
    val isLastPage: Boolean
        get() = page >= totalPages
    
    /**
     * Get the next page number, or null if on last page
     */
    val nextPage: Int?
        get() = if (hasMore) page + 1 else null
    
    /**
     * Get the previous page number, or null if on first page
     */
    val previousPage: Int?
        get() = if (page > 1) page - 1 else null
}
