package com.example.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.model.HealthLevel
import com.example.model.PlantSpecies

/**
 * Persistent Plant Species Entity stored in Room database.
 */
@Entity(tableName = "plant_species")
data class PlantSpeciesEntity(
    @PrimaryKey val id: String,
    val commonName: String,
    val scientificName: String,
    val family: String,
    val origin: String,
    val description: String,
    val difficulty: String,
    val tags: String, // Comma separated tags e.g. "Air Purifying, Easy, Climbing"
    val lightLevel: String,
    val lightDesc: String,
    val wateringFrequencyDays: Int,
    val waterDesc: String,
    val tempRange: String,
    val humidityRange: String,
    val soilType: String,
    val fertilizerSchedule: String,
    val toxicityWarning: String,
    val imageResName: String
) {
    fun toDomainModel(): PlantSpecies {
        return PlantSpecies(
            id = id,
            commonName = commonName,
            scientificName = scientificName,
            family = family,
            origin = origin,
            description = description,
            difficulty = difficulty,
            tags = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
            lightLevel = lightLevel,
            lightDesc = lightDesc,
            wateringFrequencyDays = wateringFrequencyDays,
            waterDesc = waterDesc,
            tempRange = tempRange,
            humidityRange = humidityRange,
            soilType = soilType,
            fertilizerSchedule = fertilizerSchedule,
            toxicityWarning = toxicityWarning,
            imageResName = imageResName
        )
    }

    companion object {
        fun fromDomainModel(species: PlantSpecies): PlantSpeciesEntity {
            return PlantSpeciesEntity(
                id = species.id,
                commonName = species.commonName,
                scientificName = species.scientificName,
                family = species.family,
                origin = species.origin,
                description = species.description,
                difficulty = species.difficulty,
                tags = species.tags.joinToString(", "),
                lightLevel = species.lightLevel,
                lightDesc = species.lightDesc,
                wateringFrequencyDays = species.wateringFrequencyDays,
                waterDesc = species.waterDesc,
                tempRange = species.tempRange,
                humidityRange = species.humidityRange,
                soilType = species.soilType,
                fertilizerSchedule = species.fertilizerSchedule,
                toxicityWarning = species.toxicityWarning,
                imageResName = species.imageResName
            )
        }
    }
}

/**
 * User-tracked individual plant entity stored in Room database.
 */
@Entity(tableName = "tracked_plants")
data class TrackedPlant(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nickname: String,
    val speciesId: String,
    val speciesName: String,
    val scientificName: String,
    val location: String, // e.g. "Living Room", "Balcony", "Office"
    val currentHealthScore: Int,
    val healthStatus: String,
    val lastWatered: Long = System.currentTimeMillis(),
    val wateringIntervalDays: Int = 7,
    val lastFertilized: Long = System.currentTimeMillis(),
    val fertilizingIntervalDays: Int = 30,
    val lastMisted: Long = System.currentTimeMillis(),
    val imageUri: String? = null,
    val imageResName: String? = null,
    val notes: String = "",
    val dateAdded: Long = System.currentTimeMillis()
)

/**
 * User-tracked plant health progress checkup logs stored in Room database.
 */
@Entity(
    tableName = "health_history_logs",
    indices = [Index(value = ["plantId"])]
)
data class HealthHistoryLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Long,
    val healthScore: Int,
    val statusNote: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * User-tracked plant care action logs (watering, fertilizing, misting, treatments) stored in Room.
 */
@Entity(
    tableName = "care_action_logs",
    indices = [Index(value = ["plantId"])]
)
data class CareActionLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Long,
    val actionType: String, // "WATERED", "FERTILIZED", "MISTED", "TREATED", "REPOTTED"
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Diagnosis Scan Logs entity stored in Room to keep historical records of all scanned plants.
 */
@Entity(tableName = "diagnosis_logs")
data class DiagnosisLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantSpeciesId: String,
    val speciesCommonName: String,
    val scientificName: String,
    val healthScore: Int,
    val statusLevel: String,
    val conditionTitle: String,
    val symptomsSummary: String,
    val rootCause: String,
    val treatmentStepsSummary: String,
    val customImageUri: String? = null,
    val sampleImageRes: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isSavedToGarden: Boolean = false
)
