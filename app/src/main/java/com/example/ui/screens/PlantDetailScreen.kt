package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.PlantCatalog
import com.example.data.entity.CareActionLog
import com.example.data.entity.HealthHistoryLog
import com.example.data.entity.TrackedPlant
import com.example.ui.PlantViewModel
import com.example.ui.components.HealthGauge
import com.example.ui.components.HealthTrendGraph
import com.example.ui.components.PlantPhotoView
import com.example.ui.theme.AttentionAmber
import com.example.ui.theme.CriticalRed
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.HealthyGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PlantDetailScreen(
    viewModel: PlantViewModel,
    plant: TrackedPlant,
    healthLogs: List<HealthHistoryLog>,
    careLogs: List<CareActionLog>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var showHealthCheckupDialog by remember { mutableStateOf(false) }
    var showTreatmentDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val species = remember(plant.speciesId) {
        PlantCatalog.getSpeciesById(plant.speciesId)
    }

    val now = System.currentTimeMillis()
    val oneDayMs = 24 * 60 * 60 * 1000L

    val daysSinceWater = ((now - plant.lastWatered) / oneDayMs).toInt()
    val daysUntilWater = plant.wateringIntervalDays - daysSinceWater
    val daysSinceFertilizer = ((now - plant.lastFertilized) / oneDayMs).toInt()
    val daysUntilFertilizer = plant.fertilizingIntervalDays - daysSinceFertilizer

    val imageRes = when (plant.imageResName) {
        "sample_fiddle" -> R.drawable.sample_fiddle
        "sample_snake" -> R.drawable.sample_snake
        "sample_pothos" -> R.drawable.sample_pothos
        else -> R.drawable.sample_monstera
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("plant_detail_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Navigation Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("detail_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to garden",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = plant.nickname,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = { showDeleteConfirmDialog = true },
                    modifier = Modifier.testTag("delete_plant_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete plant",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Hero Header Card
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
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        PlantPhotoView(
                            imageUri = plant.imageUri,
                            imageResName = plant.imageResName,
                            contentDescription = plant.nickname,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = plant.nickname,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = plant.scientificName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "📍 ${plant.location}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    HealthGauge(
                        score = plant.currentHealthScore,
                        statusLabel = plant.healthStatus,
                        size = 80.dp,
                        strokeWidth = 7.dp
                    )
                }
            }
        }

        // Quick Care Action Toolbar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Quick Care Actions",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CareActionButton(
                            icon = Icons.Default.WaterDrop,
                            label = "Water",
                            color = Color(0xFF0288D1),
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.waterPlant(plant.id) }
                        )
                        CareActionButton(
                            icon = Icons.Default.Eco,
                            label = "Fertilize",
                            color = ForestGreen,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.fertilizePlant(plant.id) }
                        )
                        CareActionButton(
                            icon = Icons.Default.Science,
                            label = "Mist",
                            color = Color(0xFF26A69A),
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.mistPlant(plant.id) }
                        )
                        CareActionButton(
                            icon = Icons.Default.Healing,
                            label = "Treatment",
                            color = AttentionAmber,
                            modifier = Modifier.weight(1f),
                            onClick = { showTreatmentDialog = true }
                        )
                    }
                }
            }
        }

        // Section: Health Trend Graph
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Health Status Progression",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Button(
                        onClick = { showHealthCheckupDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp).testTag("log_checkup_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Log Checkup", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                HealthTrendGraph(healthLogs = healthLogs)
            }
        }

        // Future Care Schedule & Reminders
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Future Care Reminders",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    ReminderItem(
                        title = "Next Watering",
                        dueText = if (daysUntilWater <= 0) "Water Today!" else "Due in $daysUntilWater days",
                        detail = "Every ${plant.wateringIntervalDays} days (${species.waterDesc})",
                        isUrgent = daysUntilWater <= 0
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ReminderItem(
                        title = "Next Fertilizing",
                        dueText = if (daysUntilFertilizer <= 0) "Fertilize Today!" else "Due in $daysUntilFertilizer days",
                        detail = "Every ${plant.fertilizingIntervalDays} days (${species.fertilizerSchedule})",
                        isUrgent = daysUntilFertilizer <= 0
                    )
                }
            }
        }

        // Plant Care Guidelines
        item {
            Column {
                Text(
                    text = "Care Guidelines (${species.commonName})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                CareRequirementsGrid(species = species)
            }
        }

        // Care Activity Log History
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Care & Activity History",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                if (careLogs.isEmpty()) {
                    Text(
                        text = "No care actions recorded yet. Use the quick buttons above to log watering or treatments.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            careLogs.take(8).forEach { log ->
                                CareHistoryRow(log = log)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Health Checkup Dialog
    if (showHealthCheckupDialog) {
        HealthCheckupDialog(
            currentScore = plant.currentHealthScore,
            onDismiss = { showHealthCheckupDialog = false },
            onConfirm = { score, note ->
                showHealthCheckupDialog = false
                viewModel.performHealthCheckup(plant.id, score, note)
            }
        )
    }

    // Treatment Dialog
    if (showTreatmentDialog) {
        TreatmentDialog(
            onDismiss = { showTreatmentDialog = false },
            onConfirm = { treatmentTitle ->
                showTreatmentDialog = false
                viewModel.applyTreatment(plant.id, treatmentTitle)
            }
        )
    }

    // Delete Plant Confirmation
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Remove Plant?") },
            text = { Text("Are you sure you want to stop tracking \"${plant.nickname}\"? All its health logs and care history will be removed.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        viewModel.deletePlant(plant.id)
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun CareActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun ReminderItem(
    title: String,
    dueText: String,
    detail: String,
    isUrgent: Boolean
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isUrgent) AttentionAmber.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isUrgent) AttentionAmber else HealthyGreen.copy(alpha = 0.15f)
            ) {
                Text(
                    text = dueText,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isUrgent) Color.White else HealthyGreen
                )
            }
        }
    }
}

@Composable
private fun CareHistoryRow(log: CareActionLog) {
    val dateFmt = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    val icon = when (log.actionType) {
        "WATERED" -> Icons.Default.WaterDrop
        "FERTILIZED" -> Icons.Default.Eco
        "MISTED" -> Icons.Default.Science
        "TREATED" -> Icons.Default.Healing
        else -> Icons.Default.CheckCircle
    }
    val iconColor = when (log.actionType) {
        "WATERED" -> Color(0xFF0288D1)
        "FERTILIZED" -> ForestGreen
        "MISTED" -> Color(0xFF26A69A)
        "TREATED" -> AttentionAmber
        else -> HealthyGreen
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(iconColor.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${log.actionType.lowercase().replaceFirstChar { it.uppercase() }}: ${log.note.ifBlank { "Recorded" }}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = dateFmt.format(Date(log.timestamp)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HealthCheckupDialog(
    currentScore: Int,
    onDismiss: () -> Unit,
    onConfirm: (score: Int, note: String) -> Unit
) {
    var score by remember { mutableFloatStateOf(currentScore.toFloat()) }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Health Checkup", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    text = "Update health status based on latest visual inspection. This appends a new tracking point to the health trend graph.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Health Score:", style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = "${score.toInt()}%",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = when {
                            score >= 80 -> HealthyGreen
                            score >= 50 -> AttentionAmber
                            else -> CriticalRed
                        }
                    )
                }
                Slider(
                    value = score,
                    onValueChange = { score = it },
                    valueRange = 10f..100f,
                    steps = 18,
                    colors = SliderDefaults.colors(thumbColor = ForestGreen, activeTrackColor = ForestGreen),
                    modifier = Modifier.testTag("health_score_slider")
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Checkup observations / notes") },
                    placeholder = { Text("e.g. New growth visible, leaves firm") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalNote = if (note.isNotBlank()) note else "Manual checkup: ${score.toInt()}% health"
                    onConfirm(score.toInt(), finalNote)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                modifier = Modifier.testTag("confirm_checkup_button")
            ) {
                Text("Save to Graph")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun TreatmentDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String) -> Unit
) {
    val commonTreatments = listOf(
        "Organic Neem Oil Spray",
        "Copper Fungicide Treatment",
        "Insecticidal Soap Application",
        "Pruned Yellow / Damaged Foliage",
        "Repotted in Fresh Drainage Soil",
        "Deep Bottom Watering Soak"
    )
    var selectedTreatment by remember { mutableStateOf(commonTreatments.first()) }
    var customText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Treatment Applied", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    text = "Select a treatment solution applied to this plant:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                commonTreatments.forEach { t ->
                    val isSelected = selectedTreatment == t && customText.isBlank()
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) ForestGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = if (isSelected) BorderStroke(1.5.dp, ForestGreen) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable {
                                selectedTreatment = t
                                customText = ""
                            }
                    ) {
                        Text(
                            text = t,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) ForestGreen else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = customText,
                    onValueChange = { customText = it },
                    label = { Text("Or enter custom treatment") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalTreatment = if (customText.isNotBlank()) customText else selectedTreatment
                    onConfirm(finalTreatment)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
            ) {
                Text("Log Treatment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
