package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.entity.TrackedPlant
import com.example.ui.PlantViewModel
import com.example.ui.components.PlantPhotoView
import com.example.ui.theme.AttentionAmber
import com.example.ui.theme.CriticalRed
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.HealthyGreen
import com.example.ui.theme.MintLight

@Composable
fun MyGardenScreen(
    viewModel: PlantViewModel,
    plants: List<TrackedPlant>,
    onPlantClick: (Long) -> Unit,
    onNavigateToScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilterIndex by remember { mutableIntStateOf(0) } // 0: All, 1: Needs Water, 2: Needs Attention

    val now = System.currentTimeMillis()
    val oneDayMs = 24 * 60 * 60 * 1000L

    val filteredPlants = remember(plants, selectedFilterIndex) {
        when (selectedFilterIndex) {
            1 -> plants.filter { plant ->
                val daysSinceWater = ((now - plant.lastWatered) / oneDayMs).toInt()
                daysSinceWater >= plant.wateringIntervalDays
            }
            2 -> plants.filter { it.currentHealthScore < 75 }
            else -> plants
        }
    }

    val totalPlants = plants.size
    val waterDueCount = plants.count { plant ->
        val daysSinceWater = ((now - plant.lastWatered) / oneDayMs).toInt()
        daysSinceWater >= plant.wateringIntervalDays
    }
    val avgHealth = if (plants.isNotEmpty()) (plants.map { it.currentHealthScore }.average()).toInt() else 0

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("my_garden_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Screen Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "My Garden",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Track plant health, care intervals, and future recovery",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(ForestGreen.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFlorist,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Garden Health Overview Card
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
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatItem(
                            label = "Tracked",
                            value = "$totalPlants",
                            color = ForestGreen
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        )
                        StatItem(
                            label = "Avg Health",
                            value = "$avgHealth%",
                            color = if (avgHealth >= 80) HealthyGreen else AttentionAmber
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        )
                        StatItem(
                            label = "Needs Water",
                            value = "$waterDueCount",
                            color = if (waterDueCount > 0) AttentionAmber else HealthyGreen
                        )
                    }
                }
            }

            // Filter Tabs
            item {
                TabRow(
                    selectedTabIndex = selectedFilterIndex,
                    containerColor = Color.Transparent,
                    contentColor = ForestGreen
                ) {
                    Tab(
                        selected = selectedFilterIndex == 0,
                        onClick = { selectedFilterIndex = 0 },
                        text = { Text("All ($totalPlants)", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedFilterIndex == 1,
                        onClick = { selectedFilterIndex = 1 },
                        text = { Text("Needs Water ($waterDueCount)", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedFilterIndex == 2,
                        onClick = { selectedFilterIndex = 2 },
                        text = { Text("Attention (${plants.count { it.currentHealthScore < 75 }})", fontWeight = FontWeight.Bold) }
                    )
                }
            }

            // Plants List or Empty State
            if (filteredPlants.isEmpty()) {
                item {
                    EmptyGardenState(onScanPlantClick = onNavigateToScan)
                }
            } else {
                items(filteredPlants, key = { it.id }) { plant ->
                    TrackedPlantCard(
                        plant = plant,
                        onCardClick = { onPlantClick(plant.id) },
                        onWaterClick = { viewModel.waterPlant(plant.id) }
                    )
                }
            }

            // Bottom spacer for FAB
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        // Floating Action Button to Scan Plant
        FloatingActionButton(
            onClick = onNavigateToScan,
            containerColor = ForestGreen,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("scan_plant_fab")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Scan New Plant")
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Scan Plant", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TrackedPlantCard(
    plant: TrackedPlant,
    onCardClick: () -> Unit,
    onWaterClick: () -> Unit
) {
    val now = System.currentTimeMillis()
    val oneDayMs = 24 * 60 * 60 * 1000L
    val daysSinceWater = ((now - plant.lastWatered) / oneDayMs).toInt()
    val isWaterDue = daysSinceWater >= plant.wateringIntervalDays
    val daysRemaining = plant.wateringIntervalDays - daysSinceWater

    val imageRes = when (plant.imageResName) {
        "sample_fiddle" -> R.drawable.sample_fiddle
        "sample_snake" -> R.drawable.sample_snake
        "sample_pothos" -> R.drawable.sample_pothos
        else -> R.drawable.sample_monstera
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .testTag("tracked_plant_${plant.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Plant image
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                PlantPhotoView(
                    imageUri = plant.imageUri,
                    imageResName = plant.imageResName,
                    contentDescription = plant.nickname,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = plant.nickname,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )

                    // Health Score Pill
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when {
                            plant.currentHealthScore >= 80 -> HealthyGreen.copy(alpha = 0.12f)
                            plant.currentHealthScore >= 50 -> AttentionAmber.copy(alpha = 0.12f)
                            else -> CriticalRed.copy(alpha = 0.12f)
                        }
                    ) {
                        Text(
                            text = "${plant.currentHealthScore}% Health",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                plant.currentHealthScore >= 80 -> HealthyGreen
                                plant.currentHealthScore >= 50 -> AttentionAmber
                                else -> CriticalRed
                            }
                        )
                    }
                }

                Text(
                    text = "${plant.speciesName} • ${plant.location}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Water status & quick water button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = if (isWaterDue) AttentionAmber else Color(0xFF0288D1),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isWaterDue) "Water Today!" else "Water in $daysRemaining d",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isWaterDue) FontWeight.Bold else FontWeight.Normal,
                            color = if (isWaterDue) AttentionAmber else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Quick Water Action
                    OutlinedButton(
                        onClick = onWaterClick,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "Water plant",
                            modifier = Modifier.size(14.dp),
                            tint = ForestGreen
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Water", fontSize = 12.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyGardenState(onScanPlantClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(MintLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = ForestGreen,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Plants in this View",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Scan any plant from your camera or gallery to identify it and track its health progression graphs.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onScanPlantClick,
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Scan Plant Now")
            }
        }
    }
}
