package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

sealed class GpuScreen(val route: String, val label: String, val icon: ImageVector) {
    data object Dashboard : GpuScreen("dashboard",  "Dashboard",  Icons.Default.Dashboard)
    data object AiAdvisor : GpuScreen("ai_advisor", "AI",         Icons.Default.AutoAwesome)
    data object Alerts    : GpuScreen("alerts",     "Alerts",     Icons.Default.Notifications)
    data object Processes : GpuScreen("processes",  "Processes",  Icons.Default.Memory)
    data object Security  : GpuScreen("security",   "Security",   Icons.Default.Security)
}

val bottomNavItems = listOf(
    GpuScreen.Dashboard, GpuScreen.AiAdvisor,
    GpuScreen.Alerts, GpuScreen.Processes, GpuScreen.Security
)

@Composable
fun GpuBottomNavigation(currentRoute: String, onNavigate: (String) -> Unit) {
    NavigationBar {
        bottomNavItems.forEach { screen ->
            NavigationBarItem(
                selected  = currentRoute == screen.route,
                onClick   = { onNavigate(screen.route) },
                icon      = { Icon(screen.icon, contentDescription = screen.label) },
                label     = { Text(screen.label) }
            )
        }
    }
}
