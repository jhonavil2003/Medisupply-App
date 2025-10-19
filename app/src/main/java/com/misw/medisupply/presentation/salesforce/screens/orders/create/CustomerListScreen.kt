package com.misw.medisupply.presentation.salesforce.screens.orders.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.medisupply.domain.model.customer.CustomerType
import com.misw.medisupply.presentation.salesforce.components.CustomerItem
import com.misw.medisupply.presentation.common.components.ErrorView
import com.misw.medisupply.presentation.common.components.LoadingIndicator
import com.misw.medisupply.presentation.common.components.MedisupplyAppBar
import com.misw.medisupply.presentation.salesforce.viewmodel.orders.OrdersEvent
import com.misw.medisupply.presentation.salesforce.viewmodel.orders.OrdersViewModel

/**
 * Customer List Screen
 * Displays list of customers to select for creating orders
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListScreen(
    viewModel: OrdersViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val pullToRefreshState = rememberPullToRefreshState()
    
    // Show error in snackbar
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(OrdersEvent.ClearError)
        }
    }
    
    Scaffold(
        topBar = {
            MedisupplyAppBar(
                title = "Consulta de Clientes",
                subtitle = "Fuerza de ventas - Medisupply",
                onNavigateBack = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.onEvent(OrdersEvent.RefreshCustomers) },
            state = pullToRefreshState,
            modifier = Modifier.padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Search bar
                SearchBar(
                    query = state.searchQuery,
                    onQueryChange = { viewModel.onEvent(OrdersEvent.SearchCustomers(it)) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Filter chips
                FilterChips(
                    customerTypes = viewModel.getCustomerTypes(),
                    selectedType = state.selectedFilter,
                    onTypeSelected = { viewModel.onEvent(OrdersEvent.FilterByType(it)) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Content
                when {
                    state.isLoading && !state.isRefreshing -> {
                        LoadingIndicator()
                    }
                    state.error != null && !state.hasCustomers() -> {
                        ErrorView(
                            message = state.error!!,
                            onRetry = { viewModel.onEvent(OrdersEvent.LoadCustomers) }
                        )
                    }
                    state.hasCustomers() -> {
                        CustomerList(
                            customers = state.getFilteredCustomers(),
                            onCustomerClick = { customer ->
                                viewModel.onEvent(OrdersEvent.SelectCustomer(customer))
                            }
                        )
                    }
                    else -> {
                        EmptyState()
                    }
                }
            }
        }
    }
}

/**
 * Search bar component
 */
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Buscar clientes...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar"
            )
        },
        singleLine = true
    )
}

/**
 * Filter chips component
 */
@Composable
private fun FilterChips(
    customerTypes: List<CustomerType>,
    selectedType: CustomerType?,
    onTypeSelected: (CustomerType?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "All" chip
        item {
            FilterChip(
                selected = selectedType == null,
                onClick = { onTypeSelected(null) },
                label = { Text("Todos") }
            )
        }
        
        // Type chips
        items(customerTypes) { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                label = { Text(type.displayName) }
            )
        }
    }
}

/**
 * Customer list component
 */
@Composable
private fun CustomerList(
    customers: List<com.misw.medisupply.domain.model.customer.Customer>,
    onCustomerClick: (com.misw.medisupply.domain.model.customer.Customer) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(customers, key = { it.id }) { customer ->
            CustomerItem(
                customer = customer,
                onClick = onCustomerClick
            )
        }
    }
}

/**
 * Empty state component
 */
@Composable
private fun EmptyState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No hay clientes",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No se encontraron clientes con los filtros seleccionados",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

