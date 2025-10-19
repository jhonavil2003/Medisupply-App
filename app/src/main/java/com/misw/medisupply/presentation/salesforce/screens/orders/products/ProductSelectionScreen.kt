package com.misw.medisupply.presentation.salesforce.screens.orders.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.order.CartItem
import com.misw.medisupply.domain.model.product.Product
import com.misw.medisupply.presentation.common.components.MedisupplyAppBar
import com.misw.medisupply.presentation.salesforce.viewmodel.orders.ProductsState
import com.misw.medisupply.presentation.salesforce.viewmodel.orders.ProductsViewModel

/**
 * Product Selection Screen
 * Second step in order creation - displays customer info and product catalog
 */
@Composable
fun ProductSelectionScreen(
    customer: Customer,
    onNavigateBack: () -> Unit,
    onConfirmOrder: (Map<String, CartItem>) -> Unit = {},
    viewModel: ProductsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var searchText by remember { mutableStateOf("") }
    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold(
        topBar = {
            MedisupplyAppBar(
                title = "Seleccionar Productos",
                subtitle = "Fuerza de ventas - Medisupply",
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            if (state.cartItems.isNotEmpty()) {
                CartBottomBar(
                    itemCount = state.cartItems.values.sumOf { it.quantity },
                    totalAmount = state.cartItems.values.sumOf { it.calculateSubtotal().toDouble() }.toFloat(),
                    onConfirmOrder = { 
                        onConfirmOrder(state.cartItems)
                    }
                )
            }
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.refresh() },
            state = pullToRefreshState,
            modifier = Modifier.padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                
                // Customer Information Card
                CustomerInfoCard(customer = customer)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Search Bar
                SearchBar(
                    searchText = searchText,
                    onSearchTextChange = { 
                        searchText = it
                    },
                    onClearSearch = { 
                        searchText = ""
                        viewModel.onSearchQueryChange("")
                    },
                    onSearch = {
                        // Search when pressing Enter
                        viewModel.onSearchQueryChange(searchText)
                    }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Cache Age Indicator and Stock Loading Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) { 
                    // Stock loading status
                    if (state.isLoadingStock) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "Cargando stock...",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Stock Error Banner
                state.stockError?.let { errorMessage ->
                    StockErrorBanner(
                        errorMessage = errorMessage,
                        onRetry = { viewModel.retryStockLoading() }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                // Products List or Loading/Error State
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when {
                        state.isLoading && state.products.isEmpty() -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        state.error != null && state.products.isEmpty() -> {
                            ErrorMessage(
                                message = state.error!!,
                                onRetry = { viewModel.refresh() },
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        state.products.isEmpty() -> {
                            EmptyProductsMessage(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        else -> {
                            ProductsList(
                                products = state.products,
                                productStockMap = state.productStockMap,
                                hasStockError = state.stockError != null,
                                pagination = state.pagination,
                                state = state,
                                viewModel = viewModel,
                                onLoadNextPage = { viewModel.loadNextPage() },
                                onLoadPreviousPage = { viewModel.loadPreviousPage() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomerInfoCard(customer: Customer) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "Cliente Seleccionado",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Customer details
            CustomerDetailRow(
                label = "Nombre:",
                value = customer.getDisplayName()
            )
            Spacer(modifier = Modifier.height(6.dp))
            
            CustomerDetailRow(
                label = "NIT:",
                value = customer.documentNumber
            )
            Spacer(modifier = Modifier.height(6.dp))
            
            CustomerDetailRow(
                label = "Código:",
                value = customer.id.toString()
            )
            
            customer.contactEmail?.let { email ->
                Spacer(modifier = Modifier.height(6.dp))
                CustomerDetailRow(
                    label = "Correo:",
                    value = email
                )
            }
        }
    }
}


@Composable
private fun CustomerDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
/**
 * Search bar component - Compact design
 */
@Composable
private fun SearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onSearch: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        placeholder = { 
            Text(
                "Buscar productos...",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            if (searchText.isNotEmpty()) {
                IconButton(
                    onClick = onClearSearch,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpiar búsqueda",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
                focusManager.clearFocus()
            }
        )
    )
}

@Composable
private fun ProductsList(
    products: List<Product>,
    productStockMap: Map<String, com.misw.medisupply.domain.model.stock.StockLevel>,
    hasStockError: Boolean,
    pagination: com.misw.medisupply.domain.model.product.Pagination?,
    state: ProductsState,
    viewModel: ProductsViewModel,
    onLoadNextPage: () -> Unit,
    onLoadPreviousPage: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Pagination info
        if (pagination != null) {
            Text(
                text = "Página ${pagination.page} de ${pagination.totalPages} - ${pagination.totalItems} productos",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        // Products list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = products,
                key = { product -> product.sku }
            ) { product ->
                // Normalize SKU for consistent lookup
                val normalizedSku = product.sku.uppercase().trim()
                val stockLevel = productStockMap[normalizedSku]
                
                ProductCard(
                    product = product,
                    stockLevel = stockLevel,
                    hasStockError = hasStockError,
                    cartItems = state.cartItems,
                    onAddToCart = { viewModel.addToCart(product) },
                    onRemoveFromCart = { viewModel.removeFromCart(product.sku) }
                )
            }
        }
        
        // Pagination buttons
        if (pagination != null && (pagination.hasNext || pagination.hasPrev)) {
            Spacer(modifier = Modifier.height(16.dp))
            PaginationButtons(
                hasPrev = pagination.hasPrev,
                hasNext = pagination.hasNext,
                onPreviousClick = onLoadPreviousPage,
                onNextClick = onLoadNextPage
            )
        }
    }
}


@Composable
private fun ProductCard(
    product: Product,
    stockLevel: com.misw.medisupply.domain.model.stock.StockLevel?,
    hasStockError: Boolean = false,
    cartItems: Map<String, com.misw.medisupply.domain.model.order.CartItem>,
    onAddToCart: () -> Unit,
    onRemoveFromCart: () -> Unit
) {
    // Get cart quantity from the reactive map
    val normalizedSku = product.sku.uppercase().trim()
    val cartQuantity = cartItems[normalizedSku]?.quantity ?: 0
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Product name and SKU with stock badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "SKU: ${product.sku}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Stock badge
                if (stockLevel != null) {
                    StockBadge(stockLevel)
                } else if (hasStockError) {
                    StockNotAvailableBadge()
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Price and cold chain indicator in row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.getFormattedPrice(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Cold chain indicator
                if (product.requiresColdChain) {
                    Text(
                        text = "❄️ Cadena de frío",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // Cart controls
            CartControls(
                cartQuantity = cartQuantity,
                stockAvailable = stockLevel?.totalAvailable,
                isOutOfStock = stockLevel?.isOutOfStock() ?: hasStockError,
                onAddToCart = onAddToCart,
                onRemoveFromCart = onRemoveFromCart
            )
        }
    }
}

@Composable
private fun PaginationButtons(
    hasPrev: Boolean,
    hasNext: Boolean,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous button - Compact text button
        TextButton(
            onClick = onPreviousClick,
            enabled = hasPrev,
            modifier = Modifier.height(32.dp)
        ) {
            Text(
                "← Anterior",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.width(24.dp))
        
        // Next button - Compact text button
        TextButton(
            onClick = onNextClick,
            enabled = hasNext,
            modifier = Modifier.height(32.dp)
        ) {
            Text(
                "Siguiente →",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
@Composable
private fun ErrorMessage(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "❌ Error",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}

@Composable
private fun EmptyProductsMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📦",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No se encontraron productos",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Intenta ajustar los filtros de búsqueda",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
@Composable
private fun StockBadge(stockLevel: com.misw.medisupply.domain.model.stock.StockLevel) {
    val (backgroundColor, textColor, text) = when {
        stockLevel.isOutOfStock() -> Triple(
            Color(0xFFFFEBEE), // Light red
            Color(0xFFC62828), // Dark red
            "Sin stock"
        )
        stockLevel.hasLowStock() -> Triple(
            Color(0xFFFFF3E0), // Light orange
            Color(0xFFEF6C00), // Dark orange
            "${stockLevel.totalAvailable} disponibles"
        )
        else -> Triple(
            Color(0xFFE8F5E9), // Light green
            Color(0xFF2E7D32), // Dark green
            "${stockLevel.totalAvailable} disponibles"
        )
    }
    
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun StockNotAvailableBadge() {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE0E0E0) // Gray background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = "Stock N/D",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF757575), // Dark gray text
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun StockErrorBanner(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3CD) // Light yellow/warning background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = Color(0xFFF57C00), // Orange warning color
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = "Stock no disponible",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF57C00)
                    )
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF795548),
                        maxLines = 2
                    )
                }
            }
            
            TextButton(
                onClick = onRetry,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Retry",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Reintentar")
            }
        }
    }
}

@Composable
private fun CartControls(
    cartQuantity: Int,
    stockAvailable: Int?,
    isOutOfStock: Boolean,
    onAddToCart: () -> Unit,
    onRemoveFromCart: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedIconButton(
                onClick = onRemoveFromCart,
                modifier = Modifier.size(36.dp),
                enabled = cartQuantity > 0,
                colors = IconButtonDefaults.outlinedIconButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Quitar del carrito",
                    modifier = Modifier.size(18.dp)
                )
            }
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (cartQuantity > 0) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.width(60.dp)
            ) {
                Text(
                    text = "$cartQuantity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (cartQuantity > 0) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            
            FilledIconButton(
                onClick = onAddToCart,
                modifier = Modifier.size(36.dp),
                enabled = !isOutOfStock && (stockAvailable == null || cartQuantity < stockAvailable),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar al carrito",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        
        // Stock status message below controls
        if (isOutOfStock) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sin stock disponible",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        } else if (stockAvailable != null && cartQuantity >= stockAvailable) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Cantidad máxima alcanzada",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CartBottomBar(
    itemCount: Int,
    totalAmount: Float,
    onConfirmOrder: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "$itemCount ${if (itemCount == 1) "producto" else "productos"}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Total:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${"$%,.0f".format(totalAmount)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Button(
                onClick = onConfirmOrder,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Confirmar Pedido",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
