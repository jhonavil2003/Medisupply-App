package com.misw.medisupply

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import com.misw.medisupply.ui.theme.NavBarBackground
import com.misw.medisupply.ui.theme.NavBarIconBlue
import com.misw.medisupply.ui.theme.NavBarIconGreen

@Composable
fun NavigationBarWithScaffold() {
    Scaffold(
        bottomBar = { NavigationBarM3() },
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(50) {
                ListItem(
                    headlineContent = { Text(text = "Suministro médico $it") },
                    leadingContent = {
                        Icon(imageVector = Icons.Filled.Archive, contentDescription = null)
                    }
                )
            }
        }
    }
}

@Composable
fun NavigationBarM3() {
    var selectedItem by remember { mutableStateOf(0) }
    val barItems = listOf(
        BarItem(
            title = "Inicio",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            route = "home",
            iconColor = NavBarIconBlue
        ),
        BarItem(
            title = "Visitas",
            selectedIcon = Icons.Filled.Route,
            unselectedIcon = Icons.Outlined.Route,
            route = "visitas",
            iconColor = NavBarIconGreen
        ),
        BarItem(
            title = "Pedidos",
            selectedIcon = Icons.Filled.Archive,
            unselectedIcon = Icons.Outlined.Archive,
            route = "pedidos",
            iconColor = NavBarIconBlue
        ),
        BarItem(
            title = "Cuenta",
            selectedIcon = Icons.Filled.Search,
            unselectedIcon = Icons.Outlined.Search,
            route = "cuenta",
            iconColor = NavBarIconGreen
        )
    )

    NavigationBar(
        containerColor = NavBarBackground
    ) {
        barItems.forEachIndexed { index, barItem ->
            val selected = selectedItem == index
            NavigationBarItem(
                selected = selected,
                onClick = {
                    selectedItem = index
                    /* navigate to selected route */
                },
                icon = {
                    Icon(
                        imageVector = if (selected) barItem.selectedIcon else barItem.unselectedIcon,
                        contentDescription = barItem.title,
                        tint = barItem.iconColor,
                        modifier = Modifier.size(28.dp)
                    )
                },
                label = { Text(text = barItem.title) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = barItem.iconColor,
                    unselectedIconColor = barItem.iconColor,
                    selectedTextColor = barItem.iconColor,
                    unselectedTextColor = barItem.iconColor,
                    indicatorColor = NavBarBackground
                )
            )
        }
    }
}

data class BarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String,
    val iconColor: Color
)