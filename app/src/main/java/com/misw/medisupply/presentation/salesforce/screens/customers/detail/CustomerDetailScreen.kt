package com.misw.medisupply.presentation.salesforce.screens.customers.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.misw.medisupply.R
import com.misw.medisupply.presentation.components.localizedStringResource
import com.misw.medisupply.presentation.salesforce.screens.customers.detail.viewmodel.CustomerDetailScreenViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.misw.medisupply.core.utils.FormatUtils
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderStatus
import com.misw.medisupply.domain.model.visit.Visit
import com.misw.medisupply.domain.model.visit.VisitStatus
import com.misw.medisupply.presentation.salesforce.viewmodel.customers.CustomerDetailViewModel
import com.misw.medisupply.presentation.salesforce.viewmodel.customers.CustomerStatistics
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Customer Detail Screen
 * Displays comprehensive information about a specific customer including salesperson assignment
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    customer: Customer,
    onNavigateBack: () -> Unit = {},
    viewModel: CustomerDetailViewModel = hiltViewModel(),
    screenViewModel: CustomerDetailScreenViewModel = hiltViewModel()
) {
    // Obtener LocaleManager del ViewModel
    val localeManager = screenViewModel.localeManager
    val currentLanguage = localeManager.currentLanguage.collectAsState().value
    
    // Load customer orders and visits on screen launch
    DisposableEffect(customer.id) {
        viewModel.loadCustomerOrders(customer.id)
        
        // Load visits if customer has salesperson assigned
        customer.salesperson?.let { salesperson ->
            viewModel.loadCustomerVisits(customer.id, salesperson.id)
        }
        
        onDispose {
            viewModel.clearState()
        }
    }
    
    val state by viewModel.state.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = localizedStringResource(R.string.customer_detail_title, localeManager),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = localizedStringResource(R.string.back_button, localeManager)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        CustomerDetailContent(
            customer = customer,
            state = state,
            localeManager = localeManager,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

/**
 * Main content for customer detail
 */
@Composable
private fun CustomerDetailContent(
    customer: Customer,
    state: com.misw.medisupply.presentation.salesforce.viewmodel.customers.CustomerDetailState,
    localeManager: com.misw.medisupply.core.i18n.LocaleManager,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Customer Header Card
        item {
            CustomerHeaderCard(customer = customer, localeManager = localeManager)
        }
        
        // Sales Statistics Card (if orders loaded)
        if (!state.isLoadingOrders && state.statistics.totalOrders > 0) {
            item {
                SalesStatisticsCard(statistics = state.statistics, localeManager = localeManager)
            }
        }
        
        // Business Information Card
        item {
            BusinessInformationCard(customer = customer, localeManager = localeManager)
        }
        
        // Contact Information Card
        item {
            ContactInformationCard(customer = customer, localeManager = localeManager)
        }
        
        // Address Information Card
        item {
            AddressInformationCard(customer = customer, localeManager = localeManager)
        }
        
        // Location Map (if coordinates available)
        if (customer.latitude != null && customer.longitude != null) {
            item {
                LocationMapCard(
                    latitude = customer.latitude,
                    longitude = customer.longitude,
                    businessName = customer.getDisplayName(),
                    localeManager = localeManager
                )
            }
        }
        
        // Recent Orders Card
        if (!state.isLoadingOrders && state.recentOrders.isNotEmpty()) {
            item {
                RecentOrdersCard(orders = state.recentOrders, localeManager = localeManager)
            }
        }
        
        // Recent Visits Card
        if (!state.isLoadingVisits && state.visits.isNotEmpty()) {
            item {
                RecentVisitsCard(visits = state.visits.take(5), localeManager = localeManager)
            }
        }
        
        // No Visits Message (only if customer has salesperson)
        if (!state.isLoadingVisits && state.visits.isEmpty() && state.visitsError == null && customer.salesperson != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = localizedStringResource(R.string.no_visits_title, localeManager),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = localizedStringResource(R.string.no_visits_message, localeManager),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        
        // Loading Orders Indicator
        if (state.isLoadingOrders) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        
        // Loading Visits Indicator
        if (state.isLoadingVisits) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        
        // Orders Error Message
        state.ordersError?.let { error ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
        
        // Visits Error Message
        state.visitsError?.let { error ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = localizedStringResource(R.string.visits_error_message, localeManager) + ": $error",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
        
        // Salesperson Information Card (if assigned)
        customer.salesperson?.let { salesperson ->
            item {
                SalespersonInformationCard(salesperson = salesperson, localeManager = localeManager)
            }
        }
    }
}

/**
 * Customer header card with main identification
 */
@Composable
private fun CustomerHeaderCard(customer: Customer, localeManager: com.misw.medisupply.core.i18n.LocaleManager) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Customer Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = customer.getDisplayName(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = customer.customerType.getLocalizedDisplayName(localeManager),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Status badge
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = if (customer.isActive) Color(0xFF4CAF50) else Color(0xFFF44336),
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (customer.isActive) 
                            localizedStringResource(R.string.active_status, localeManager) 
                        else 
                            localizedStringResource(R.string.inactive_status, localeManager),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

/**
 * Business information card
 */
@Composable
private fun BusinessInformationCard(customer: Customer, localeManager: com.misw.medisupply.core.i18n.LocaleManager) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader(title = localizedStringResource(R.string.business_information, localeManager))
            
            Spacer(modifier = Modifier.height(12.dp))
            
            InfoRow(
                label = localizedStringResource(R.string.business_name_label, localeManager),
                value = customer.businessName
            )
            
            customer.tradeName?.let { tradeName ->
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(
                    label = localizedStringResource(R.string.trade_name_label, localeManager),
                    value = tradeName
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            InfoRow(
                label = localizedStringResource(R.string.document_type_label, localeManager),
                value = customer.documentType.displayName
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            InfoRow(
                label = localizedStringResource(R.string.document_number_label, localeManager),
                value = customer.documentNumber
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            InfoRow(
                label = localizedStringResource(R.string.customer_code_label, localeManager),
                value = customer.id.toString()
            )
        }
    }
}

/**
 * Contact information card
 */
@Composable
private fun ContactInformationCard(customer: Customer, localeManager: com.misw.medisupply.core.i18n.LocaleManager) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader(title = localizedStringResource(R.string.contact_information, localeManager))
            
            Spacer(modifier = Modifier.height(12.dp))
            
            customer.contactName?.let { name ->
                InfoRowWithIcon(
                    icon = Icons.Default.Person,
                    label = localizedStringResource(R.string.contact_name_label, localeManager),
                    value = name
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            customer.contactEmail?.let { email ->
                InfoRowWithIcon(
                    icon = Icons.Default.Email,
                    label = localizedStringResource(R.string.email_label, localeManager),
                    value = email
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            customer.contactPhone?.let { phone ->
                ClickablePhoneRow(
                    icon = Icons.Default.Phone,
                    label = localizedStringResource(R.string.phone_label, localeManager),
                    value = phone,
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:$phone")
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

/**
 * Address information card
 */
@Composable
private fun AddressInformationCard(customer: Customer, localeManager: com.misw.medisupply.core.i18n.LocaleManager) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader(title = localizedStringResource(R.string.location_information, localeManager))
            
            Spacer(modifier = Modifier.height(12.dp))
            
            customer.address?.let { address ->
                InfoRowWithIcon(
                    icon = Icons.Default.LocationOn,
                    label = localizedStringResource(R.string.address_label, localeManager),
                    value = address
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            customer.city?.let { city ->
                InfoRow(
                    label = localizedStringResource(R.string.city_label, localeManager),
                    value = city
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            customer.department?.let { department ->
                InfoRow(
                    label = localizedStringResource(R.string.department_label, localeManager),
                    value = department
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            InfoRow(
                label = localizedStringResource(R.string.country_label, localeManager),
                value = customer.country
            )
        }
    }
}

/**
 * Salesperson information card
 */
@Composable
private fun SalespersonInformationCard(
    salesperson: com.misw.medisupply.domain.model.salesperson.Salesperson,
    localeManager: com.misw.medisupply.core.i18n.LocaleManager
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD) // Light blue background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader(title = localizedStringResource(R.string.assigned_salesperson, localeManager))
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = salesperson.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = salesperson.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    salesperson.phone?.let { phone ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = phone,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Section header component
 */
@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
}

/**
 * Info row component
 */
@Composable
private fun InfoRow(
    label: String,
    value: String,
    valueStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyLarge
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = valueStyle,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.6f)
        )
    }
}

/**
 * Info row with icon component
 */
@Composable
private fun InfoRowWithIcon(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Clickable phone row component for initiating phone calls
 */
@Composable
private fun ClickablePhoneRow(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

/**
 * Location map card showing customer GPS coordinates
 */
@Composable
private fun LocationMapCard(
    latitude: Double,
    longitude: Double,
    businessName: String,
    localeManager: com.misw.medisupply.core.i18n.LocaleManager
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = localizedStringResource(R.string.gps_location, localeManager),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Coordinates
            Text(
                text = "Lat: $latitude, Lng: $longitude",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Google Map
            val position = remember { LatLng(latitude, longitude) }
            val cameraPositionState = rememberCameraPositionState {
                this.position = CameraPosition.fromLatLngZoom(position, 15f)
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(
                        color = Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(
                        state = MarkerState(position = position),
                        title = businessName,
                        snippet = localizedStringResource(R.string.customer_location, localeManager)
                    )
                }
            }
        }
    }
}

@Composable
private fun SalesStatisticsCard(
    statistics: CustomerStatistics,
    localeManager: com.misw.medisupply.core.i18n.LocaleManager,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = localizedStringResource(R.string.sales_statistics, localeManager),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider(modifier = Modifier.padding(bottom = 12.dp))

            // Grid de estadísticas principales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatisticItem(
                    label = localizedStringResource(R.string.total_orders, localeManager),
                    value = statistics.totalOrders.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatisticItem(
                    label = localizedStringResource(R.string.active_orders, localeManager),
                    value = statistics.activeOrdersCount.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatisticItem(
                    label = localizedStringResource(R.string.total_revenue, localeManager),
                    value = "$${String.format("%,.2f", statistics.totalRevenue)}",
                    modifier = Modifier.weight(1f)
                )
                StatisticItem(
                    label = localizedStringResource(R.string.average_order_value, localeManager),
                    value = "$${String.format("%,.2f", statistics.averageOrderValue)}",
                    modifier = Modifier.weight(1f)
                )
            }

            // Top productos
            if (statistics.topProducts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider(modifier = Modifier.padding(bottom = 8.dp))
                Text(
                    text = localizedStringResource(R.string.top_requested_products, localeManager),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                statistics.topProducts.take(3).forEach { product ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${product.totalQuantity} " + localizedStringResource(R.string.units, localeManager),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatisticItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RecentOrdersCard(
    orders: List<Order>,
    localeManager: com.misw.medisupply.core.i18n.LocaleManager,
    modifier: Modifier = Modifier
) {
    var showAllOrders by remember { mutableStateOf(false) }
    val maxOrdersToShow = 3
    val ordersToDisplay = if (showAllOrders) orders else orders.take(maxOrdersToShow)
    val hasMoreOrders = orders.size > maxOrdersToShow
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = localizedStringResource(R.string.recent_orders, localeManager),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider(modifier = Modifier.padding(bottom = 8.dp))

            if (orders.isEmpty()) {
                Text(
                    text = localizedStringResource(R.string.no_recent_orders, localeManager),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                ordersToDisplay.forEach { order ->
                    OrderListItem(order = order)
                    if (order != ordersToDisplay.last()) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
                
                if (hasMoreOrders && !showAllOrders) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAllOrders = true }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+ " + localizedStringResource(R.string.view_more, localeManager) + " ${orders.size - maxOrdersToShow} " + if (orders.size - maxOrdersToShow > 1) localizedStringResource(R.string.orders_plural, localeManager) else localizedStringResource(R.string.order_singular, localeManager),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                
                if (showAllOrders && hasMoreOrders) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAllOrders = false }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "- " + localizedStringResource(R.string.view_less, localeManager),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderListItem(
    order: Order,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pedido #${order.id ?: "N/A"}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = order.createdAt?.let { 
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it) 
                } ?: "N/A",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$${String.format("%,.2f", order.totalAmount)}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            // Badge de estado
            Surface(
                color = when (order.status) {
                    OrderStatus.PENDING -> MaterialTheme.colorScheme.tertiaryContainer
                    OrderStatus.CONFIRMED, OrderStatus.PROCESSING -> MaterialTheme.colorScheme.primaryContainer
                    OrderStatus.SHIPPED -> MaterialTheme.colorScheme.secondaryContainer
                    OrderStatus.DELIVERED -> MaterialTheme.colorScheme.secondaryContainer
                    OrderStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
                },
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = order.status.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = when (order.status) {
                        OrderStatus.PENDING -> MaterialTheme.colorScheme.onTertiaryContainer
                        OrderStatus.CONFIRMED, OrderStatus.PROCESSING -> MaterialTheme.colorScheme.onPrimaryContainer
                        OrderStatus.SHIPPED -> MaterialTheme.colorScheme.onSecondaryContainer
                        OrderStatus.DELIVERED -> MaterialTheme.colorScheme.onSecondaryContainer
                        OrderStatus.CANCELLED -> MaterialTheme.colorScheme.onErrorContainer
                    }
                )
            }
        }

        // Mostrar cantidad de productos si está disponible
        if (order.items.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${order.items.size} producto${if (order.items.size != 1) "s" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RecentVisitsCard(
    visits: List<Visit>,
    localeManager: com.misw.medisupply.core.i18n.LocaleManager,
    modifier: Modifier = Modifier
) {
    var showAllVisits by remember { mutableStateOf(false) }
    val maxVisitsToShow = 3
    val visitsToDisplay = if (showAllVisits) visits else visits.take(maxVisitsToShow)
    val hasMoreVisits = visits.size > maxVisitsToShow
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = localizedStringResource(R.string.visits, localeManager),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider(modifier = Modifier.padding(bottom = 8.dp))

            if (visits.isEmpty()) {
                Text(
                    text = localizedStringResource(R.string.no_visits_registered, localeManager),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                visitsToDisplay.forEach { visit ->
                    VisitListItem(visit = visit, localeManager = localeManager)
                    if (visit != visitsToDisplay.last()) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
                
                if (hasMoreVisits && !showAllVisits) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAllVisits = true }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+ " + localizedStringResource(R.string.view_more, localeManager) + " ${visits.size - maxVisitsToShow} " + if (visits.size - maxVisitsToShow > 1) localizedStringResource(R.string.visits_plural, localeManager) else localizedStringResource(R.string.visit_singular, localeManager),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                
                if (showAllVisits && hasMoreVisits) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAllVisits = false }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "- " + localizedStringResource(R.string.view_less, localeManager),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VisitListItem(
    visit: Visit,
    localeManager: com.misw.medisupply.core.i18n.LocaleManager,
    modifier: Modifier = Modifier
) {
    val dateFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm")
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Visita #${visit.id}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = visit.visitDate.format(dateFormatter),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Hora y estado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = visit.visitTime.format(timeFormatter),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Badge de estado
            Surface(
                color = when (visit.status) {
                    VisitStatus.PROGRAMADA -> MaterialTheme.colorScheme.tertiaryContainer
                    VisitStatus.COMPLETADA -> MaterialTheme.colorScheme.secondaryContainer
                    VisitStatus.ELIMINADA -> MaterialTheme.colorScheme.errorContainer
                },
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = visit.status.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = when (visit.status) {
                        VisitStatus.PROGRAMADA -> MaterialTheme.colorScheme.onTertiaryContainer
                        VisitStatus.COMPLETADA -> MaterialTheme.colorScheme.onSecondaryContainer
                        VisitStatus.ELIMINADA -> MaterialTheme.colorScheme.onErrorContainer
                    }
                )
            }
        }

        // Dirección si está disponible
        visit.address?.let { address ->
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Hallazgos clínicos si están disponibles
        visit.clinicalFindings?.let { findings ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = localizedStringResource(R.string.clinical_findings, localeManager),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = findings,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
