package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.model.PlantSpecies
import com.example.ui.theme.AttentionAmber
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.HealthyGreen
import com.example.ui.theme.MintLight

data class PlantIssue(
    val title: String,
    val symptom: String,
    val cause: String,
    val remedy: String
)

val COMMON_PLANT_PROBLEMS = listOf(
    PlantIssue(
        title = "Yellowing Lower Leaves",
        symptom = "Leaves turn pale yellow, soft, and eventually drop from the bottom up.",
        cause = "Usually overwatering and stagnant root saturation. Infrequently, natural older leaf senescence.",
        remedy = "Allow top 2-3 inches to dry thoroughly before watering. Ensure drainage holes in bottom of pot are unblocked."
    ),
    PlantIssue(
        title = "Crispy Brown Leaf Tips",
        symptom = "Edges or tips of leaves become brittle and brown.",
        cause = "Low ambient indoor humidity or mineral buildup from hard tap water containing chlorine/fluoride.",
        remedy = "Group plants together on a humidity pebble tray, switch to filtered or rainwater, and keep away from hot heating vents."
    ),
    PlantIssue(
        title = "Drooping & Limp Foliage",
        symptom = "Plant stems lose turgor and sag down dramatically.",
        cause = "Severe thirst / dry soil, OR root rot preventing roots from absorbing water.",
        remedy = "Check soil: If bone dry, bottom-water for 30 minutes. If soaking wet, unpot and inspect for rotted mushy roots."
    ),
    PlantIssue(
        title = "Spider Mites / Fine Webbing",
        symptom = "Tiny speckled dots on leaf tops with delicate silky webs under leaf axils.",
        cause = "Microscopic arachnid pests that flourish in warm, dry household air.",
        remedy = "Rinse leaves in the shower with room temperature water, then thoroughly spray insecticidal soap or neem oil weekly."
    ),
    PlantIssue(
        title = "Brown Leaf Spots with Yellow Halos",
        symptom = "Scattered dark brown circular spots ringed by yellow rings.",
        cause = "Fungal or bacterial leaf spot caused by overhead water splashing on foliage.",
        remedy = "Prune heavily spotted foliage with sterile shears. Water exclusively into the soil, never wet the leaves directly."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantGuideScreen(
    speciesList: List<PlantSpecies> = PlantCatalog.SPECIES_LIST,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedSpeciesForDialog by remember { mutableStateOf<PlantSpecies?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Plant Encyclopedia, 1: Plant Doctor (Common Issues)

    val categories = listOf("All", "Easy", "Air Purifying", "Low Light", "Statement")

    val filteredPlants = remember(searchQuery, selectedCategory, speciesList) {
        speciesList.filter { plant ->
            val matchesQuery = plant.commonName.contains(searchQuery, ignoreCase = true) ||
                    plant.scientificName.contains(searchQuery, ignoreCase = true) ||
                    plant.family.contains(searchQuery, ignoreCase = true)

            val matchesCategory = when (selectedCategory) {
                "Easy" -> plant.difficulty.contains("Easy", ignoreCase = true)
                "Air Purifying" -> plant.tags.any { it.contains("Air Purif", ignoreCase = true) }
                "Low Light" -> plant.lightLevel.contains("Low", ignoreCase = true) || plant.tags.any { it.contains("Low Light", ignoreCase = true) }
                "Statement" -> plant.tags.any { it.contains("Statement", ignoreCase = true) }
                else -> true
            }

            matchesQuery && matchesCategory
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("plant_guide_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Plant Care Guide",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Botanical encyclopedia & plant health troubleshooting",
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
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Section Tabs: Encyclopedia vs Plant Doctor
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = ForestGreen
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Plant Catalog", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Plant Doctor & FAQ", fontWeight = FontWeight.Bold) }
                )
            }
        }

        if (selectedTab == 0) {
            // Search Input
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search plants, family, scientific names...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = ForestGreen)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("guide_search_field")
                )
            }

            // Category Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) ForestGreen else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { selectedCategory = cat }
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Plants List
            items(filteredPlants, key = { it.id }) { species ->
                PlantSpeciesCard(
                    species = species,
                    onClick = { selectedSpeciesForDialog = species }
                )
            }
        } else {
            // Plant Doctor Section
            item {
                Text(
                    text = "Common Symptoms & Solutions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(COMMON_PLANT_PROBLEMS) { issue ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Healing,
                                contentDescription = null,
                                tint = AttentionAmber,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = issue.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Symptom: ${issue.symptom}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Root Cause: ${issue.cause}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = ForestGreen
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MintLight
                        ) {
                            Text(
                                text = "💡 Solution: ${issue.remedy}",
                                modifier = Modifier.padding(10.dp),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF00391A)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Species Detail Dialog
    if (selectedSpeciesForDialog != null) {
        val species = selectedSpeciesForDialog!!
        val imageRes = when (species.imageResName) {
            "sample_fiddle" -> R.drawable.sample_fiddle
            "sample_snake" -> R.drawable.sample_snake
            "sample_pothos" -> R.drawable.sample_pothos
            else -> R.drawable.sample_monstera
        }

        AlertDialog(
            onDismissRequest = { selectedSpeciesForDialog = null },
            title = {
                Text(text = species.commonName, fontWeight = FontWeight.ExtraBold)
            },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(14.dp))
                        ) {
                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = species.commonName,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    item {
                        Text(
                            text = species.scientificName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = species.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    item {
                        CareRequirementsGrid(species = species)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedSpeciesForDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun PlantSpeciesCard(
    species: PlantSpecies,
    onClick: () -> Unit
) {
    val imageRes = when (species.imageResName) {
        "sample_fiddle" -> R.drawable.sample_fiddle
        "sample_snake" -> R.drawable.sample_snake
        "sample_pothos" -> R.drawable.sample_pothos
        else -> R.drawable.sample_monstera
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("species_card_${species.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = species.commonName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = species.commonName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MintLight
                    ) {
                        Text(
                            text = species.difficulty,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                    }
                }

                Text(
                    text = species.scientificName,
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.LightMode, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = species.lightLevel.take(12), style = MaterialTheme.typography.labelSmall)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF0288D1), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Every ${species.wateringFrequencyDays}d", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
