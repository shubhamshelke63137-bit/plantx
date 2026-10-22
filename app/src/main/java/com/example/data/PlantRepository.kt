package com.example.data

import com.example.data.entity.CareActionLog
import com.example.data.entity.DiagnosisLogEntity
import com.example.data.entity.HealthHistoryLog
import com.example.data.entity.PlantSpeciesEntity
import com.example.data.entity.TrackedPlant
import com.example.model.DiagnosisResult
import com.example.model.PlantSpecies
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlantRepository(private val plantDao: PlantDao) {

    val allTrackedPlants: Flow<List<TrackedPlant>> = plantDao.getAllTrackedPlants()

    val allPlantSpecies: Flow<List<PlantSpecies>> = plantDao.getAllPlantSpecies().map { list ->
        if (list.isEmpty()) {
            PlantCatalog.SPECIES_LIST
        } else {
            list.map { it.toDomainModel() }
        }
    }

    val allDiagnosisLogs: Flow<List<DiagnosisLogEntity>> = plantDao.getAllDiagnosisLogs()

    fun getTrackedPlant(id: Long): Flow<TrackedPlant?> = plantDao.getTrackedPlant(id)

    fun getHealthLogs(plantId: Long): Flow<List<HealthHistoryLog>> = plantDao.getHealthLogs(plantId)

    fun getCareLogs(plantId: Long): Flow<List<CareActionLog>> = plantDao.getCareLogs(plantId)

    suspend fun addTrackedPlant(plant: TrackedPlant, initialLogNote: String = "Initial scan & health checkup"): Long {
        val newId = plantDao.insertTrackedPlant(plant)
        plantDao.insertHealthLog(
            HealthHistoryLog(
                plantId = newId,
                healthScore = plant.currentHealthScore,
                statusNote = initialLogNote,
                timestamp = System.currentTimeMillis()
            )
        )
        plantDao.insertCareLog(
            CareActionLog(
                plantId = newId,
                actionType = "PLANTED",
                note = "Added to my personal garden tracker",
                timestamp = System.currentTimeMillis()
            )
        )
        return newId
    }

    suspend fun updatePlant(plant: TrackedPlant) {
        plantDao.updateTrackedPlant(plant)
    }

    suspend fun deletePlant(id: Long) {
        plantDao.deleteHealthLogsForPlant(id)
        plantDao.deleteCareLogsForPlant(id)
        plantDao.deleteTrackedPlant(id)
    }

    suspend fun logCareAction(plantId: Long, actionType: String, note: String = "") {
        val now = System.currentTimeMillis()
        plantDao.insertCareLog(
            CareActionLog(
                plantId = plantId,
                actionType = actionType,
                note = note,
                timestamp = now
            )
        )
        val plant = plantDao.getTrackedPlantSync(plantId) ?: return
        val updated = when (actionType) {
            "WATERED" -> plant.copy(lastWatered = now)
            "FERTILIZED" -> plant.copy(lastFertilized = now)
            "MISTED" -> plant.copy(lastMisted = now)
            else -> plant
        }
        plantDao.updateTrackedPlant(updated)
    }

    suspend fun waterPlantNow(plantId: Long) {
        logCareAction(plantId, "WATERED", "Deep watering applied")
    }

    suspend fun fertilizePlantNow(plantId: Long) {
        logCareAction(plantId, "FERTILIZED", "Liquid nutrients fed")
    }

    suspend fun mistPlantNow(plantId: Long) {
        logCareAction(plantId, "MISTED", "Foliage humidity misted")
    }

    suspend fun applyTreatmentNow(plantId: Long, treatmentName: String) {
        logCareAction(plantId, "TREATED", treatmentName)
    }

    suspend fun logHealthCheckup(plantId: Long, newScore: Int, note: String, newStatus: String? = null) {
        val now = System.currentTimeMillis()
        plantDao.insertHealthLog(
            HealthHistoryLog(
                plantId = plantId,
                healthScore = newScore,
                statusNote = note,
                timestamp = now
            )
        )
        val plant = plantDao.getTrackedPlantSync(plantId) ?: return
        val calculatedStatus = newStatus ?: when {
            newScore >= 80 -> "Healthy & Vibrant"
            newScore >= 50 -> "Needs Attention"
            else -> "Severe Distress"
        }
        plantDao.updateTrackedPlant(
            plant.copy(
                currentHealthScore = newScore,
                healthStatus = calculatedStatus
            )
        )
    }

    suspend fun saveDiagnosisLog(result: DiagnosisResult): Long {
        val log = DiagnosisLogEntity(
            plantSpeciesId = result.plant.id,
            speciesCommonName = result.plant.commonName,
            scientificName = result.plant.scientificName,
            healthScore = result.healthScore,
            statusLevel = result.statusLevel.label,
            conditionTitle = result.conditionTitle,
            symptomsSummary = result.symptoms.joinToString(" • "),
            rootCause = result.rootCause,
            treatmentStepsSummary = result.treatmentSteps.joinToString("; ") { "${it.step}. ${it.title}" },
            customImageUri = result.customImageUri,
            sampleImageRes = result.sampleImageRes,
            timestamp = System.currentTimeMillis(),
            isSavedToGarden = false
        )
        return plantDao.insertDiagnosisLog(log)
    }

    suspend fun markDiagnosisSavedToGarden(id: Long) {
        plantDao.markDiagnosisSaved(id)
    }

    suspend fun deleteDiagnosisLog(id: Long) {
        plantDao.deleteDiagnosisLog(id)
    }
}
