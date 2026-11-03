package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.data.remote.GeocodingResultDto
import com.google.android.gms.location.LocationServices
import java.util.Locale

@Composable
fun LocationPickerDialog(
    currentLocationName: String,
    searchResults: List<GeocodingResultDto>,
    isSearching: Boolean,
    onDismiss: () -> Unit,
    onSearch: (String) -> Unit,
    onSelectLocation: (name: String, lat: Double, lon: Double) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var locationError by remember { mutableStateOf<String?>(null) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
                    if (loc != null) {
                        val geo = Geocoder(context, Locale.getDefault())
                        val addresses = try {
                            geo.getFromLocation(loc.latitude, loc.longitude, 1)
                        } catch (e: Exception) {
                            null
                        }
                        val name = addresses?.firstOrNull()?.let {
                            "${it.locality ?: it.subAdminArea ?: "Current Location"}, ${it.adminArea ?: ""}"
                        } ?: "Current Location"
                        onSelectLocation(name, loc.latitude, loc.longitude)
                        onDismiss()
                    } else {
                        locationError = "Unable to get GPS fix. Please search city below."
                    }
                }
            } catch (e: SecurityException) {
                locationError = "Location permission needed."
            }
        } else {
            locationError = "Location permission denied. You can search by city name below."
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Barn Location",
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_location_dialog")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Current: $currentLocationName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Use Current GPS button
                OutlinedButton(
                    onClick = {
                        val hasFine = ContextCompat.checkSelfPermission(
                            context, Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                        val hasCoarse = ContextCompat.checkSelfPermission(
                            context, Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasFine || hasCoarse) {
                            try {
                                fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
                                    if (loc != null) {
                                        val geo = Geocoder(context, Locale.getDefault())
                                        val addresses = try {
                                            geo.getFromLocation(loc.latitude, loc.longitude, 1)
                                        } catch (e: Exception) {
                                            null
                                        }
                                        val name = addresses?.firstOrNull()?.let {
                                            "${it.locality ?: "Current Loc"}, ${it.adminArea ?: ""}"
                                        } ?: "Current GPS Location"
                                        onSelectLocation(name, loc.latitude, loc.longitude)
                                        onDismiss()
                                    } else {
                                        locationError = "GPS position not yet available. Search below."
                                    }
                                }
                            } catch (e: SecurityException) {
                                locationError = "GPS error. Search below."
                            }
                        } else {
                            requestPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("use_gps_location_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Use Device GPS Location")
                }

                if (locationError != null) {
                    Text(
                        text = locationError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                HorizontalDivider()

                // City / Barn Search TextField
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        onSearch(it)
                    },
                    label = { Text("Search City or Barn Area") },
                    placeholder = { Text("e.g. Lexington, Ocala, Wellington") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (isSearching) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_location_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Search Results
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (searchResults.isNotEmpty()) {
                        items(searchResults) { item ->
                            val fullName = buildString {
                                append(item.name)
                                if (!item.admin1.isNullOrBlank()) append(", ${item.admin1}")
                                if (!item.country.isNullOrBlank()) append(", ${item.country}")
                            }
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelectLocation(fullName, item.latitude, item.longitude)
                                        onDismiss()
                                    }
                                    .testTag("location_result_${item.name}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = fullName,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    } else if (searchQuery.isNotBlank() && !isSearching) {
                        item {
                            Text(
                                text = "No matching locations found.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    } else {
                        // Popular Equine Regions
                        item {
                            Text(
                                text = "Popular Barn Regions:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                            )
                        }
                        val presets = listOf(
                            Triple("Lexington, Kentucky", 38.0406, -84.5037),
                            Triple("Ocala, Florida", 29.1872, -82.1401),
                            Triple("Wellington, Florida", 26.6618, -80.2414),
                            Triple("Middleburg, Virginia", 38.9690, -77.7369),
                            Triple("Aiken, South Carolina", 33.5604, -81.7196)
                        )
                        items(presets) { preset ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelectLocation(preset.first, preset.second, preset.third)
                                        onDismiss()
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(preset.first, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dismiss_location_dialog")
            ) {
                Text("Cancel")
            }
        }
    )
}
