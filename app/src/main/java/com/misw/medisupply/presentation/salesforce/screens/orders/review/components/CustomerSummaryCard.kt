package com.misw.medisupply.presentation.salesforce.screens.orders.review.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.misw.medisupply.domain.model.customer.Customer

/**
 * Customer summary card
 * Displays customer information in review screen
 */
@Composable
fun CustomerSummaryCard(customer: Customer) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            DetailRow(label = "Nombre", value = customer.getDisplayName())
            DetailRow(label = "NIT", value = customer.documentNumber)
            customer.contactEmail?.let { email ->
                DetailRow(label = "Email", value = email)
            }
            customer.contactPhone?.let { phone ->
                DetailRow(label = "Teléfono", value = phone)
            }
            customer.address?.let { address ->
                DetailRow(label = "Dirección", value = address)
            }
            DetailRow(
                label = "Ciudad",
                value = "${customer.city ?: "N/A"}, ${customer.department ?: "N/A"}"
            )
        }
    }
}
