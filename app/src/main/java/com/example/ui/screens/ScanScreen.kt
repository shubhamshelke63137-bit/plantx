package com.example.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.PlantCatalog
import com.example.ui.components.CameraXScannerView
import com.example.ui.components.PlantPhotoView
import com.example.model.DiagnosisResult
import com.example.model.HealthLevel
import com.example.model.TreatmentStep
import com.example.ui.PlantViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.DiagnosisLogEntity
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.ui.components.HealthGauge
import com.example.ui.theme.AttentionAmber
import com.example.ui.theme.CriticalRed
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.HealthyGreen
import com.example.ui.theme.MintLight

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScanScreen(
    viewModel: PlantViewModel,
    diagnosis: DiagnosisResult?,
    isScanning: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAddToGardenDialog by remember { mutableStateOf(false) }
    var showLiveCamera by remember { mutableStateOf(false) }
    var showPermissionRationaleDialog by remember { mutableStateOf(false) }
    var cameraErrorMessage by remember { mutableStateOf<String?>(null) }

    // Modern photo picker launcher (zero permission needed)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.scanImageUri(context, uri)
        }
    }

    // Camera permission request launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showLiveCamera = true
        } else {
            showPermissionRationaleDialog = true
        }
    }

    val requestCameraLaunch: () -> Unit = {
        val isGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (isGranted) {
            showLiveCamera = true
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val diagnosisLogs by viewModel.diagnosisLogs.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        if (showLiveCamera) {
            // Live CameraX Viewfinder Fullscreen
            CameraXScannerView(
                onImageCaptured = { bitmap, fileUri ->
                    showLiveCamera = false
                    viewModel.scanBitmap(bitmap)
                },
                onClose = {
                    showLiveCamera = false
                },
                onError = { err ->
                    showLiveCamera = false
                    cameraErrorMessage = err
                }
            )
        } else if (diagnosis != null) {
            // Full Detailed Diagnosis Sheet
            DiagnosisDetailContent(
                diagnosis = diagnosis,
                onAddGardenClick = { showAddToGardenDialog = true },
                onScanAnotherClick = { viewModel.clearScanResult() }
            )
        } else {
            // Scanner Viewfinder & Instant Test Scans
            ScannerLandingContent(
                diagnosisLogs = diagnosisLogs,
                onSnapPhoto = requestCameraLaunch,
                onPickGallery = {
                    photoPickerLauncher.launch(
                        androidx.activity.result.PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                onSelectSample = { sample ->
                    viewModel.scanSample(sample)
                },
                onSelectDiagnosisLog = { log ->
                    viewModel.restoreDiagnosisFromLog(log)
                },
                onDeleteDiagnosisLog = { id ->
                    viewModel.deleteDiagnosisLog(id)
                }
            )
        }

        // Scanning Animation Overlay
        if (isScanning) {
            ScanningProgressOverlay()
        }

        // Add to Garden Dialog
        if (showAddToGardenDialog && diagnosis != null) {
            AddToGardenDialog(
                defaultName = diagnosis.plant.commonName,
                onDismiss = { showAddToGardenDialog = false },
                onConfirm = { nickname, room ->
                    showAddToGardenDialog = false
                    viewModel.addCurrentScanToGarden(nickname, room)
                }
            )
        }

        // Camera Permission Rationale Dialog
        if (showPermissionRationaleDialog) {
            AlertDialog(
                onDismissRequest = { showPermissionRationaleDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(32.dp)
                    )
                },
                title = {
                    Text(
                        text = "Camera Permission Needed",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "PlantX uses your camera to scan leaves in real time, extract discoloration patterns, and diagnose plant health. You can grant access or upload plant photos from your gallery instead.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showPermissionRationaleDialog = false
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        modifier = Modifier.testTag("grant_camera_permission_button")
                    ) {
                        Text("Grant Camera Access")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showPermissionRationaleDialog = false
                            photoPickerLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                    ) {
                        Text("Pick from Gallery")
                    }
                }
            )
        }

        // Camera Error Dialog (Graceful fallback)
        if (cameraErrorMessage != null) {
            AlertDialog(
                onDismissRequest = { cameraErrorMessage = null },
                title = { Text(text = "Camera Notice", fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        text = cameraErrorMessage ?: "Unable to access camera hardware.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            cameraErrorMessage = null
                            photoPickerLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                    ) {
                        Text("Use Gallery Instead")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { cameraErrorMessage = null }) {
                        Text("Dismiss")
                    }
                }
            )
        }
    }
}

@Composable
private fun ScannerLandingContent(
    diagnosisLogs: List<DiagnosisLogEntity>,
    onSnapPhoto: () -> Unit,
    onPickGallery: () -> Unit,
    onSelectSample: (DiagnosisResult) -> Unit,
    onSelectDiagnosisLog: (DiagnosisLogEntity) -> Unit,
    onDeleteDiagnosisLog: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("scan_landing_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Hero Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PlantX Scanner",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Snap or upload any plant to identify species, detect health status, and get instant treatment solutions.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Hero Viewfinder Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clickable(onClick = onSnapPhoto)
                    .testTag("viewfinder_preview_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Plant background preview
                    Image(
                        painter = painterResource(id = R.drawable.sample_monstera),
                        contentDescription = "Monstera background",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 0.5f
                    )

                    // Scanner Viewfinder Reticle
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .border(
                                BorderStroke(2.5.dp, Color.White.copy(alpha = 0.85f)),
                                RoundedCornerShape(24.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Animated Scanning Pulse Bar
                        val infiniteTransition = rememberInfiniteTransition(label = "scanner")
                        val scanOffsetY by infiniteTransition.animateFloat(
                            initialValue = -70f,
                            targetValue = 70f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1500, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "scanLine"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .offset(y = scanOffsetY.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color.Transparent,
                                            HealthyGreen,
                                            Color.White,
                                            HealthyGreen,
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Align Plant Leaf",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons: Snap Camera & Photo Library
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onSnapPhoto,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("snap_photo_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Snap Photo", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onPickGallery,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("upload_gallery_button"),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, ForestGreen)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Gallery", color = ForestGreen, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Instant Test Plants Carousel
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Instant Demo Scans",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MintLight
                    ) {
                        Text(
                            text = "Tap to test",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = ForestGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = "Try real diagnostic cases directly in the emulator without a camera:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(PlantCatalog.SAMPLE_SCANS) { sample ->
                    SampleScanCard(
                        sample = sample,
                        onClick = { onSelectSample(sample) }
                    )
                }
            }
        }

        // Persistent Diagnosis Scan History (Stored in Room DB)
        if (diagnosisLogs.isNotEmpty()) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = ForestGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Scan History (${diagnosisLogs.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "Saved in Database",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Text(
                        text = "Persistent diagnosis records across app launches:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(diagnosisLogs, key = { it.id }) { log ->
                        DiagnosisHistoryCard(
                            log = log,
                            onClick = { onSelectDiagnosisLog(log) },
                            onDelete = { onDeleteDiagnosisLog(log.id) }
                        )
                    }
                }
            }
        }

        // Tips card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(ForestGreen.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Tips for Accurate Diagnosis",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Capture clear, well-lit photos focusing on both healthy leaves and any damaged or yellowing areas.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SampleScanCard(
    sample: DiagnosisResult,
    onClick: () -> Unit
) {
    val imageRes = when (sample.sampleImageRes) {
        "sample_fiddle" -> R.drawable.sample_fiddle
        "sample_snake" -> R.drawable.sample_snake
        "sample_pothos" -> R.drawable.sample_pothos
        else -> R.drawable.sample_monstera
    }

    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick)
            .testTag("sample_scan_${sample.plant.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = sample.plant.commonName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Health Badge on top corner
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = when (sample.statusLevel) {
                        HealthLevel.HEALTHY -> HealthyGreen
                        HealthLevel.ATTENTION -> AttentionAmber
                        HealthLevel.CRITICAL -> CriticalRed
                    }
                ) {
                    Text(
                        text = "${sample.healthScore}%",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = sample.plant.commonName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = sample.conditionTitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = when (sample.statusLevel) {
                        HealthLevel.HEALTHY -> HealthyGreen
                        HealthLevel.ATTENTION -> AttentionAmber
                        HealthLevel.CRITICAL -> CriticalRed
                    },
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun DiagnosisHistoryCard(
    log: DiagnosisLogEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFmt = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())

    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable(onClick = onClick)
            .testTag("diagnosis_log_card_${log.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(105.dp)
            ) {
                PlantPhotoView(
                    imageUri = log.customImageUri,
                    imageResName = log.sampleImageRes,
                    contentDescription = log.speciesCommonName,
                    modifier = Modifier.fillMaxSize()
                )

                // Health Badge on top corner
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = when {
                        log.healthScore >= 80 -> HealthyGreen
                        log.healthScore >= 50 -> AttentionAmber
                        else -> CriticalRed
                    }
                ) {
                    Text(
                        text = "${log.healthScore}%",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Delete Button on top left
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                        .size(28.dp)
                        .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                        .testTag("delete_diagnosis_log_${log.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete scan log",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = log.speciesCommonName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = log.conditionTitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = when {
                        log.healthScore >= 80 -> HealthyGreen
                        log.healthScore >= 50 -> AttentionAmber
                        else -> CriticalRed
                    },
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateFmt.format(Date(log.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DiagnosisDetailContent(
    diagnosis: DiagnosisResult,
    onAddGardenClick: () -> Unit,
    onScanAnotherClick: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Treatment, 1: Care Tips, 2: About Plant

    val imageRes = when (diagnosis.sampleImageRes) {
        "sample_fiddle" -> R.drawable.sample_fiddle
        "sample_snake" -> R.drawable.sample_snake
        "sample_pothos" -> R.drawable.sample_pothos
        else -> R.drawable.sample_monstera
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("diagnosis_result_sheet"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar: Back / Rescan
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onScanAnotherClick,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Scan Another")
                }

                Button(
                    onClick = onAddGardenClick,
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("add_to_garden_cta")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Track in My Garden", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Plant Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Image thumbnail
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        PlantPhotoView(
                            imageUri = diagnosis.customImageUri,
                            imageResName = diagnosis.sampleImageRes,
                            contentDescription = diagnosis.plant.commonName,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = diagnosis.plant.commonName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = diagnosis.plant.scientificName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Family: ${diagnosis.plant.family}",
                            style = MaterialTheme.typography.labelSmall,
                            color = ForestGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Health Score Gauge Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Health Status Assessment",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = diagnosis.conditionTitle,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = when (diagnosis.statusLevel) {
                                HealthLevel.HEALTHY -> HealthyGreen
                                HealthLevel.ATTENTION -> AttentionAmber
                                HealthLevel.CRITICAL -> CriticalRed
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = diagnosis.rootCause,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    HealthGauge(
                        score = diagnosis.healthScore,
                        statusLabel = diagnosis.statusLevel.label,
                        size = 100.dp
                    )
                }
            }
        }

        // Symptoms Detected
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Symptoms Identified",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    diagnosis.symptoms.forEach { symptom ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = if (diagnosis.statusLevel == HealthLevel.HEALTHY) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (diagnosis.statusLevel == HealthLevel.HEALTHY) HealthyGreen else AttentionAmber,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = symptom,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Navigation Tabs for Details
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = ForestGreen
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Treatment Plan", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Care Guide", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Plant Facts", fontWeight = FontWeight.Bold) }
                )
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                // Step-by-Step Treatment Solutions
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Recommended Treatment Steps",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        diagnosis.treatmentSteps.forEach { step ->
                            TreatmentStepCard(step = step)
                        }

                        if (diagnosis.preventiveTips.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Preventive Care",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            diagnosis.preventiveTips.forEach { tip ->
                                Text(
                                    text = "• $tip",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
            1 -> {
                // Care Requirements (PlantX Guide style)
                item {
                    CareRequirementsGrid(species = diagnosis.plant)
                }
            }
            2 -> {
                // Plant Description & Toxicity
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "About this Plant",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = diagnosis.plant.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Native Habitat: ${diagnosis.plant.origin}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ForestGreen
                                )
                            }
                        }

                        // Toxicity Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = AttentionAmber.copy(alpha = 0.08f)
                            ),
                            border = BorderStroke(1.dp, AttentionAmber.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Pet Toxicity",
                                    tint = AttentionAmber,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Toxicity & Pet Safety",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AttentionAmber
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = diagnosis.plant.toxicityWarning,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom CTA
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onAddGardenClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add to My Garden & Track Health", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TreatmentStepCard(step: TreatmentStep) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(ForestGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${step.step}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = step.timeframe,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = step.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CareRequirementsGrid(species: com.example.model.PlantSpecies) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        CareMetricRow(
            icon = Icons.Default.LightMode,
            title = "Light Requirement",
            value = species.lightLevel,
            desc = species.lightDesc,
            color = Color(0xFFF59E0B)
        )
        CareMetricRow(
            icon = Icons.Default.WaterDrop,
            title = "Watering Schedule",
            value = "Every ${species.wateringFrequencyDays} days",
            desc = species.waterDesc,
            color = Color(0xFF0288D1)
        )
        CareMetricRow(
            icon = Icons.Default.Thermostat,
            title = "Temperature & Humidity",
            value = species.tempRange,
            desc = "Ideal humidity: ${species.humidityRange}",
            color = Color(0xFFE64A19)
        )
        CareMetricRow(
            icon = Icons.Default.Eco,
            title = "Soil & Fertilizer",
            value = species.soilType,
            desc = species.fertilizerSchedule,
            color = ForestGreen
        )
    }
}

@Composable
private fun CareMetricRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    desc: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(color.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ScanningProgressOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = ForestGreen,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Analyzing Plant Health...",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Detecting leaf pigmentation, cell turgidity & fungal spots",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AddToGardenDialog(
    defaultName: String,
    onDismiss: () -> Unit,
    onConfirm: (nickname: String, room: String) -> Unit
) {
    var nickname by remember { mutableStateOf(defaultName) }
    var location by remember { mutableStateOf("Living Room") }
    val rooms = listOf("Living Room", "Bedroom", "Balcony", "Office", "Kitchen", "Garden")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Track in My Garden", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    text = "Give your plant a nickname and select its room to track its watering schedule and health trend graph.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("Plant Nickname") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("nickname_input")
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Location / Room:", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(rooms) { room ->
                        val isSelected = location == room
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) ForestGreen else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { location = room }
                        ) {
                            Text(
                                text = room,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(nickname, location) },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                modifier = Modifier.testTag("confirm_add_garden")
            ) {
                Text("Start Tracking")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
