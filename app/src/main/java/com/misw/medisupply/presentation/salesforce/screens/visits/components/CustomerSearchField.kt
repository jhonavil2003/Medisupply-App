package com.misw.medisupply.presentation.salesforce.screens.visits.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.misw.medisupply.domain.model.customer.Customer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerSearchField(
    selectedCustomer: Customer?,
    searchQuery: String,
    searchResults: List<Customer>,
    isSearching: Boolean,
    showDropdown: Boolean,
    onQueryChange: (String) -> Unit,
    onCustomerSelected: (Customer) -> Unit,
    onClearSelection: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Campo de búsqueda
        OutlinedTextField(
            value = selectedCustomer?.businessName ?: searchQuery,
            onValueChange = { query ->
                android.util.Log.d("CustomerSearchField", "onValueChange called with: '$query', selectedCustomer: ${selectedCustomer?.businessName}")
                if (selectedCustomer == null) {
                    android.util.Log.d("CustomerSearchField", "Calling onQueryChange with: '$query'")
                    onQueryChange(query)
                }
            },
            label = { Text("Cliente *") },
            placeholder = { Text("Buscar cliente por nombre...") },
            leadingIcon = { 
                Icon(
                    imageVector = if (selectedCustomer != null) Icons.Default.Person else Icons.Default.Search,
                    contentDescription = if (selectedCustomer != null) "Cliente seleccionado" else "Buscar cliente",
                    tint = Color(0xFF1565C0)
                ) 
            },
            trailingIcon = {
                when {
                    isSearching -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color(0xFF1565C0)
                        )
                    }
                    selectedCustomer != null -> {
                        IconButton(onClick = onClearSelection) {
                            Icon(
                                Icons.Default.Clear, 
                                contentDescription = "Limpiar selección",
                                tint = Color(0xFF757575)
                            )
                        }
                    }
                }
            },
            readOnly = selectedCustomer != null,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1565C0),
                unfocusedBorderColor = Color(0xFFB6C6E3),
                focusedLabelColor = Color(0xFF1565C0),
                unfocusedLabelColor = Color(0xFF1565C0)
            )
        )

        // Dropdown con resultados de búsqueda
        LaunchedEffect(showDropdown, searchResults.size, selectedCustomer) {
            android.util.Log.d("CustomerSearchField", "State - showDropdown: $showDropdown, searchResults: ${searchResults.size}, selectedCustomer: ${selectedCustomer?.businessName}")
        }
        
        if (searchResults.isNotEmpty() && selectedCustomer == null && searchQuery.length >= 2) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .padding(top = 4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                LazyColumn {
                    items(searchResults) { customer ->
                        CustomerSearchItem(
                            customer = customer,
                            onClick = { onCustomerSelected(customer) }
                        )
                    }
                }
            }
        }

        // Mensaje cuando no hay resultados
        if (searchResults.isEmpty() && !isSearching && searchQuery.length >= 2 && selectedCustomer == null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Text(
                    text = "No se encontraron clientes con \"$searchQuery\"",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF757575)
                )
            }
        }

        // Información del cliente seleccionado
        if (selectedCustomer != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF0F7FF)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Cliente seleccionado:",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF1565C0)
                    )
                    Spacer(Modifier.height(4.dp))
                    
                    Text(
                        text = selectedCustomer.businessName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    )
                    
                    if (!selectedCustomer.contactName.isNullOrEmpty()) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Contacto: ${selectedCustomer.contactName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF757575)
                        )
                    }
                    
                    if (!selectedCustomer.contactPhone.isNullOrEmpty()) {
                        Text(
                            text = "Teléfono: ${selectedCustomer.contactPhone}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF757575)
                        )
                    }
                    
                    if (!selectedCustomer.address.isNullOrEmpty()) {
                        Text(
                            text = "Dirección: ${selectedCustomer.address}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF757575)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomerSearchItem(
    customer: Customer,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = customer.businessName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1565C0)
            )
            
            if (!customer.contactName.isNullOrEmpty()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = customer.contactName ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF757575)
                )
            }
            
            if (!customer.address.isNullOrEmpty()) {
                Text(
                    text = customer.address ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9E9E9E)
                )
            }
        }
    }
    
    HorizontalDivider(
        color = Color(0xFFE0E0E0),
        thickness = 0.5.dp
    )
}