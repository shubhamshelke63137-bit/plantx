package com.example.model

enum class HealthLevel(val label: String) {
    HEALTHY("Healthy & Vibrant"),
    ATTENTION("Needs Attention"),
    CRITICAL("Severe Distress")
}

data class TreatmentStep(
    val step: Int,
    val title: String,
    val description: String,
    val timeframe: String,
    val urgency: String = "Normal"
)

data class PlantSpecies(
    val id: String,
    val commonName: String,
    val scientificName: String,
    val family: String,
    val origin: String,
    val description: String,
    val difficulty: String, // "Easy", "Moderate", "Expert"
    val tags: List<String>,
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
)

data class DiagnosisResult(
    val plant: PlantSpecies,
    val healthScore: Int, // 0 - 100
    val statusLevel: HealthLevel,
    val conditionTitle: String,
    val symptoms: List<String>,
    val rootCause: String,
    val treatmentSteps: List<TreatmentStep>,
    val preventiveTips: List<String>,
    val customImageUri: String? = null,
    val sampleImageRes: String? = null
)
