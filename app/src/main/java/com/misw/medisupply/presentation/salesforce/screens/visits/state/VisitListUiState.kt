package com.misw.medisupply.presentation.salesforce.screens.visits.state

data class VisitListUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val visits: List<Visit> = emptyList(),
    val searchQuery: String = "",
    val filteredVisits: List<Visit> = emptyList()
)