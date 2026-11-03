package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.components.LocationPickerDialog
import com.example.ui.screens.*
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

enum class AppScreen(val title: String) {
    TONIGHT("Tonight"),
    ALERTS("Alerts"),
    HORSES("Horses"),
    HISTORY("History"),
    GUIDE("Guide")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()
            val isDark = when (uiState.themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> systemDark
            }
            MyApplicationTheme(darkTheme = isDark) {
                EquiBlanketApp(
                    viewModel = viewModel,
                    isDarkTheme = isDark
                )
            }
        }
    }
}

@Composable
fun EquiBlanketApp(
    viewModel: MainViewModel,
    isDarkTheme: Boolean
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(AppScreen.TONIGHT) }
    var showLocationDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Show status messages
    LaunchedEffect(uiState.statusMessage) {
        uiState.statusMessage?.let { msg ->
            scope.launch {
                snackbarHostState.showSnackbar(msg)
                viewModel.clearStatusMessage()
            }
        }
    }

    // BackHandler to return to Tonight screen if on another tab
    BackHandler(enabled = currentScreen != AppScreen.TONIGHT) {
        currentScreen = AppScreen.TONIGHT
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.TONIGHT,
                    onClick = { currentScreen = AppScreen.TONIGHT },
                    icon = {
                        Icon(
                            if (currentScreen == AppScreen.TONIGHT) Icons.Filled.NightsStay else Icons.Outlined.NightsStay,
                            contentDescription = "Tonight"
                        )
                    },
                    label = { Text("Tonight", fontWeight = if (currentScreen == AppScreen.TONIGHT) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_item_tonight")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.ALERTS,
                    onClick = { currentScreen = AppScreen.ALERTS },
                    icon = {
                        Icon(
                            if (currentScreen == AppScreen.ALERTS) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                            contentDescription = "Alerts"
                        )
                    },
                    label = { Text("Alerts", fontWeight = if (currentScreen == AppScreen.ALERTS) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_item_alerts")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.HORSES,
                    onClick = { currentScreen = AppScreen.HORSES },
                    icon = {
                        Icon(
                            if (currentScreen == AppScreen.HORSES) Icons.Filled.Pets else Icons.Outlined.Pets,
                            contentDescription = "Horses"
                        )
                    },
                    label = { Text("Horses", fontWeight = if (currentScreen == AppScreen.HORSES) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_item_horses")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.HISTORY,
                    onClick = { currentScreen = AppScreen.HISTORY },
                    icon = {
                        Icon(
                            if (currentScreen == AppScreen.HISTORY) Icons.Filled.History else Icons.Outlined.History,
                            contentDescription = "History"
                        )
                    },
                    label = { Text("History", fontWeight = if (currentScreen == AppScreen.HISTORY) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_item_history")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.GUIDE,
                    onClick = { currentScreen = AppScreen.GUIDE },
                    icon = {
                        Icon(
                            if (currentScreen == AppScreen.GUIDE) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                            contentDescription = "Guide"
                        )
                    },
                    label = { Text("Guide", fontWeight = if (currentScreen == AppScreen.GUIDE) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_item_guide")
                )
            }
        }
    ) { innerPadding ->
        when (currentScreen) {
            AppScreen.TONIGHT -> {
                HomeScreen(
                    uiState = uiState,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { viewModel.toggleTheme() },
                    onSelectHorse = { viewModel.selectHorse(it) },
                    onRefreshWeather = { viewModel.refreshWeather() },
                    onOpenLocationPicker = { showLocationDialog = true },
                    onSaveHorse = { viewModel.saveHorse(it) },
                    onLogBlanketing = { blanket, comfort, notes ->
                        viewModel.logTonightBlanketing(blanket, comfort, notes)
                    },
                    onNavigateToAlerts = { currentScreen = AppScreen.ALERTS },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            AppScreen.ALERTS -> {
                AlertsScreen(
                    uiState = uiState,
                    onUpdateAlertConfig = { viewModel.updateAlertConfig(it) },
                    onSendTestAlert = { viewModel.sendTestAlertNow(context) },
                    onSetThemeMode = { viewModel.setThemeMode(it) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            AppScreen.HORSES -> {
                HorsesScreen(
                    uiState = uiState,
                    onSaveHorse = { viewModel.saveHorse(it) },
                    onDeleteHorse = { viewModel.deleteHorse(it) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            AppScreen.HISTORY -> {
                HistoryScreen(
                    uiState = uiState,
                    onDeleteLog = { viewModel.deleteLog(it) },
                    onAddManualLog = { horse, blanket, comfort, notes, tempF ->
                        viewModel.logTonightBlanketing(blanket, comfort, notes)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            AppScreen.GUIDE -> {
                GuideScreen(
                    customBlankets = uiState.customBlankets,
                    onSaveCustomBlanket = { viewModel.saveCustomBlanket(it) },
                    onDeleteCustomBlanket = { viewModel.deleteCustomBlanket(it) },
                    onResetDefaultBlankets = { viewModel.resetDefaultBlankets() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

        if (showLocationDialog) {
            LocationPickerDialog(
                currentLocationName = uiState.locationName,
                searchResults = uiState.locationSearchResults,
                isSearching = uiState.isSearchingLocation,
                onDismiss = { showLocationDialog = false },
                onSearch = { query -> viewModel.searchLocations(query) },
                onSelectLocation = { name, lat, lon ->
                    viewModel.setLocation(name, lat, lon)
                }
            )
        }
    }
}
