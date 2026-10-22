package com.example.ui

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PlantCatalog
import com.example.data.PlantDatabase
import com.example.data.PlantRepository
import com.example.data.entity.CareActionLog
import com.example.data.entity.DiagnosisLogEntity
import com.example.data.entity.HealthHistoryLog
import com.example.data.entity.TrackedPlant
import com.example.model.DiagnosisResult
import com.example.model.HealthLevel
import com.example.model.PlantSpecies
import com.example.model.TreatmentStep
import com.example.service.PlantScannerEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val label: String) {
    SCAN("Diagnose"),
    GARDEN("My Garden"),
    SCHEDULE("Care Schedule"),
    GUIDE("Plant Care Guide")
}

class PlantViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PlantRepository

    // Room Database StateFlows for persistent storage across app launches
    val trackedPlants: StateFlow<List<TrackedPlant>>
    val allSpecies: StateFlow<List<PlantSpecies>>
    val diagnosisLogs: StateFlow<List<DiagnosisLogEntity>>

    private val _selectedTab = MutableStateFlow(AppTab.SCAN)
    val selectedTab: StateFlow<AppTab> = _selectedTab.asStateFlow()

    private val _selectedPlantId = MutableStateFlow<Long?>(null)
    val selectedPlantId: StateFlow<Long?> = _selectedPlantId.asStateFlow()

    val selectedPlant: StateFlow<TrackedPlant?>
    val selectedPlantHealthLogs: StateFlow<List<HealthHistoryLog>>
    val selectedPlantCareLogs: StateFlow<List<CareActionLog>>

    private val _currentScanResult = MutableStateFlow<DiagnosisResult?>(null)
    val currentScanResult: StateFlow<DiagnosisResult?> = _currentScanResult.asStateFlow()

    private var currentDiagnosisLogId: Long? = null

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _scanProgress = MutableStateFlow(0f)
    val scanProgress: StateFlow<Float> = _scanProgress.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        val db = PlantDatabase.getDatabase(application, viewModelScope)
        repository = PlantRepository(db.plantDao())

        // Persistent Room queries
        trackedPlants = repository.allTrackedPlants.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allSpecies = repository.allPlantSpecies.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlantCatalog.SPECIES_LIST
        )

        diagnosisLogs = repository.allDiagnosisLogs.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        selectedPlant = _selectedPlantId.flatMapLatest { id ->
            if (id == null) flowOf(null) else repository.getTrackedPlant(id)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        selectedPlantHealthLogs = _selectedPlantId.flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repository.getHealthLogs(id)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        selectedPlantCareLogs = _selectedPlantId.flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repository.getCareLogs(id)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun setTab(tab: AppTab) {
        _selectedTab.value = tab
    }

    fun selectPlant(plantId: Long?) {
        _selectedPlantId.value = plantId
    }

    fun scanSample(sample: DiagnosisResult) {
        viewModelScope.launch {
            _isScanning.value = true
            _scanProgress.value = 0f
            for (i in 1..10) {
                delay(60)
                _scanProgress.value = i / 10f
            }
            _currentScanResult.value = sample
            // Persist diagnosis into Room database
            currentDiagnosisLogId = repository.saveDiagnosisLog(sample)
            _isScanning.value = false
        }
    }

    fun scanImageUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            _isScanning.value = true
            _scanProgress.value = 0f
            for (i in 1..5) {
                delay(60)
                _scanProgress.value = i * 0.15f
            }
            val result = PlantScannerEngine.diagnoseImageUri(context, uri)
            _scanProgress.value = 1f
            delay(100)
            _currentScanResult.value = result
            // Persist diagnosis into Room database
            currentDiagnosisLogId = repository.saveDiagnosisLog(result)
            _isScanning.value = false
        }
    }

    fun scanBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            _isScanning.value = true
            _scanProgress.value = 0f
            for (i in 1..5) {
                delay(60)
                _scanProgress.value = i * 0.15f
            }
            val result = PlantScannerEngine.diagnoseBitmap(bitmap)
            _scanProgress.value = 1f
            delay(100)
            _currentScanResult.value = result
            // Persist diagnosis into Room database
            currentDiagnosisLogId = repository.saveDiagnosisLog(result)
            _isScanning.value = false
        }
    }

    fun restoreDiagnosisFromLog(log: DiagnosisLogEntity) {
        val species = allSpecies.value.find { it.id == log.plantSpeciesId }
            ?: PlantCatalog.getSpeciesById(log.plantSpeciesId)

        val healthLevel = when {
            log.healthScore >= 80 -> HealthLevel.HEALTHY
            log.healthScore >= 50 -> HealthLevel.ATTENTION
            else -> HealthLevel.CRITICAL
        }

        val restoredResult = DiagnosisResult(
            plant = species,
            healthScore = log.healthScore,
            statusLevel = healthLevel,
            conditionTitle = log.conditionTitle,
            symptoms = log.symptomsSummary.split(" • ").filter { it.isNotBlank() },
            rootCause = log.rootCause,
            treatmentSteps = listOf(
                TreatmentStep(
                    step = 1,
                    title = "Follow Saved Treatment Plan",
                    description = log.treatmentStepsSummary,
                    timeframe = "Recommended Routine"
                )
            ),
            preventiveTips = listOf("Check soil moisture regularly.", "Ensure drainage holes are not blocked."),
            customImageUri = log.customImageUri,
            sampleImageRes = log.sampleImageRes ?: species.imageResName
        )
        currentDiagnosisLogId = log.id
        _currentScanResult.value = restoredResult
        _selectedTab.value = AppTab.SCAN
    }

    fun deleteDiagnosisLog(id: Long) {
        viewModelScope.launch {
            repository.deleteDiagnosisLog(id)
            _userMessage.value = "Diagnosis scan record deleted."
        }
    }

    fun addCurrentScanToGarden(nickname: String, location: String) {
        val scan = _currentScanResult.value ?: return
        viewModelScope.launch {
            val plant = TrackedPlant(
                nickname = if (nickname.isNotBlank()) nickname else scan.plant.commonName,
                speciesId = scan.plant.id,
                speciesName = scan.plant.commonName,
                scientificName = scan.plant.scientificName,
                location = if (location.isNotBlank()) location else "Living Room",
                currentHealthScore = scan.healthScore,
                healthStatus = scan.statusLevel.label,
                wateringIntervalDays = scan.plant.wateringFrequencyDays,
                imageResName = scan.sampleImageRes ?: scan.plant.imageResName,
                imageUri = scan.customImageUri,
                notes = "${scan.conditionTitle}. Treatment plan initiated."
            )
            val newId = repository.addTrackedPlant(
                plant = plant,
                initialLogNote = "Diagnosed: ${scan.conditionTitle} (${scan.healthScore}%)"
            )

            currentDiagnosisLogId?.let { logId ->
                repository.markDiagnosisSavedToGarden(logId)
            }

            _userMessage.value = "Added \"${plant.nickname}\" to My Garden!"
            // Select the newly added plant and open details
            _selectedPlantId.value = newId
            _selectedTab.value = AppTab.GARDEN
        }
    }

    fun waterPlant(plantId: Long) {
        viewModelScope.launch {
            repository.waterPlantNow(plantId)
            _userMessage.value = "Watering recorded! Fresh hydration logged."
        }
    }

    fun fertilizePlant(plantId: Long) {
        viewModelScope.launch {
            repository.fertilizePlantNow(plantId)
            _userMessage.value = "Fertilizing recorded! Nutrients supplied."
        }
    }

    fun mistPlant(plantId: Long) {
        viewModelScope.launch {
            repository.mistPlantNow(plantId)
            _userMessage.value = "Misting recorded! Humidity boosted."
        }
    }

    fun applyTreatment(plantId: Long, treatmentTitle: String) {
        viewModelScope.launch {
            repository.applyTreatmentNow(plantId, treatmentTitle)
            _userMessage.value = "Treatment logged: $treatmentTitle"
        }
    }

    fun performHealthCheckup(plantId: Long, newScore: Int, note: String) {
        viewModelScope.launch {
            repository.logHealthCheckup(plantId, newScore, note)
            _userMessage.value = "Health checkup logged! Health score updated to $newScore%."
        }
    }

    fun deletePlant(plantId: Long) {
        viewModelScope.launch {
            repository.deletePlant(plantId)
            if (_selectedPlantId.value == plantId) {
                _selectedPlantId.value = null
            }
            _userMessage.value = "Plant removed from garden."
        }
    }

    fun clearScanResult() {
        _currentScanResult.value = null
        currentDiagnosisLogId = null
    }

    fun dismissMessage() {
        _userMessage.value = null
    }
}
