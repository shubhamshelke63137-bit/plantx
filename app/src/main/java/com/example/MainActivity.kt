package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppTab
import com.example.ui.PlantViewModel
import com.example.ui.screens.CareScheduleScreen
import com.example.ui.screens.MyGardenScreen
import com.example.ui.screens.PlantDetailScreen
import com.example.ui.screens.PlantGuideScreen
import com.example.ui.screens.ScanScreen
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.PlantXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlantXTheme {
                val plantViewModel: PlantViewModel = viewModel()
                PlantXApp(viewModel = plantViewModel)
            }
        }
    }
}

@Composable
fun PlantXApp(viewModel: PlantViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val trackedPlants by viewModel.trackedPlants.collectAsStateWithLifecycle()
    val allSpecies by viewModel.allSpecies.collectAsStateWithLifecycle()
    val selectedPlantId by viewModel.selectedPlantId.collectAsStateWithLifecycle()
    val selectedPlant by viewModel.selectedPlant.collectAsStateWithLifecycle()
    val selectedPlantHealthLogs by viewModel.selectedPlantHealthLogs.collectAsStateWithLifecycle()
    val selectedPlantCareLogs by viewModel.selectedPlantCareLogs.collectAsStateWithLifecycle()
    val currentScanResult by viewModel.currentScanResult.collectAsStateWithLifecycle()
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            // Only display main bottom bar if not in subscreen detail
            if (selectedPlantId == null) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = ForestGreen,
                    modifier = Modifier.testTag("main_bottom_nav")
                ) {
                    NavigationBarItem(
                        selected = selectedTab == AppTab.SCAN,
                        onClick = { viewModel.setTab(AppTab.SCAN) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.CenterFocusStrong,
                                contentDescription = "Scan & Diagnose"
                            )
                        },
                        label = { Text("Diagnose", fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ForestGreen,
                            selectedTextColor = ForestGreen,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("tab_scan")
                    )

                    NavigationBarItem(
                        selected = selectedTab == AppTab.GARDEN,
                        onClick = { viewModel.setTab(AppTab.GARDEN) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.LocalFlorist,
                                contentDescription = "My Garden"
                            )
                        },
                        label = { Text("My Garden", fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ForestGreen,
                            selectedTextColor = ForestGreen,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("tab_garden")
                    )

                    NavigationBarItem(
                        selected = selectedTab == AppTab.SCHEDULE,
                        onClick = { viewModel.setTab(AppTab.SCHEDULE) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Care Schedule"
                            )
                        },
                        label = { Text("Schedule", fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ForestGreen,
                            selectedTextColor = ForestGreen,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("tab_schedule")
                    )

                    NavigationBarItem(
                        selected = selectedTab == AppTab.GUIDE,
                        onClick = { viewModel.setTab(AppTab.GUIDE) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = "Plant Guide"
                            )
                        },
                        label = { Text("Guide", fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ForestGreen,
                            selectedTextColor = ForestGreen,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("tab_guide")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedPlantId != null && selectedPlant != null) {
                PlantDetailScreen(
                    viewModel = viewModel,
                    plant = selectedPlant!!,
                    healthLogs = selectedPlantHealthLogs,
                    careLogs = selectedPlantCareLogs,
                    onBack = { viewModel.selectPlant(null) }
                )
            } else {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tabContentTransition"
                ) { targetTab ->
                    when (targetTab) {
                        AppTab.SCAN -> {
                            ScanScreen(
                                viewModel = viewModel,
                                diagnosis = currentScanResult,
                                isScanning = isScanning
                            )
                        }
                        AppTab.GARDEN -> {
                            MyGardenScreen(
                                viewModel = viewModel,
                                plants = trackedPlants,
                                onPlantClick = { id -> viewModel.selectPlant(id) },
                                onNavigateToScan = { viewModel.setTab(AppTab.SCAN) }
                            )
                        }
                        AppTab.SCHEDULE -> {
                            CareScheduleScreen(
                                viewModel = viewModel,
                                plants = trackedPlants,
                                onPlantClick = { id -> viewModel.selectPlant(id) }
                            )
                        }
                        AppTab.GUIDE -> {
                            PlantGuideScreen(speciesList = allSpecies)
                        }
                    }
                }
            }
        }
    }
}
