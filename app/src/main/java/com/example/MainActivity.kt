package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.ui.navigation.*
import com.example.ui.screens.*
import com.example.ui.theme.GPUInsightTheme
import com.example.ui.viewmodel.GpuInsightViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: GpuInsightViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GPUInsightTheme { GpuInsightApp(viewModel) } }
    }
}

@Composable
fun GpuInsightApp(viewModel: GpuInsightViewModel) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: GpuScreen.Dashboard.route
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(bottomBar = {
        GpuBottomNavigation(currentRoute) { route ->
            navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true; restoreState = true
            }
        }
    }) { inner ->
        NavHost(navController, GpuScreen.Dashboard.route,
            modifier = Modifier.padding(inner).fillMaxSize()) {
            composable(GpuScreen.Dashboard.route)  { DashboardScreen(uiState) }
            composable(GpuScreen.AiAdvisor.route)  { AiAdvisorScreen(uiState, viewModel::analyzeError) }
            composable(GpuScreen.Alerts.route)     { AlertsScreen() }
            composable(GpuScreen.Processes.route)  { ProcessMonitorScreen() }
            composable(GpuScreen.Security.route)   { SecurityReportsScreen() }
        }
    }
}
