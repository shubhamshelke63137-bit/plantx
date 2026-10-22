package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.CareActionLog
import com.example.data.entity.DiagnosisLogEntity
import com.example.data.entity.HealthHistoryLog
import com.example.data.entity.PlantSpeciesEntity
import com.example.data.entity.TrackedPlant
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {

    // ----------------------------------------------------
    // Plant Species Persistent Catalog
    // ----------------------------------------------------
    @Query("SELECT * FROM plant_species ORDER BY commonName ASC")
    fun getAllPlantSpecies(): Flow<List<PlantSpeciesEntity>>

    @Query("SELECT * FROM plant_species WHERE id = :id")
    fun getPlantSpeciesById(id: String): Flow<PlantSpeciesEntity?>

    @Query("SELECT * FROM plant_species WHERE id = :id")
    suspend fun getPlantSpeciesByIdSync(id: String): PlantSpeciesEntity?

    @Query("SELECT COUNT(*) FROM plant_species")
    suspend fun getPlantSpeciesCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlantSpecies(species: List<PlantSpeciesEntity>)

    // ----------------------------------------------------
    // User Tracked Plants
    // ----------------------------------------------------
    @Query("SELECT * FROM tracked_plants ORDER BY dateAdded DESC")
    fun getAllTrackedPlants(): Flow<List<TrackedPlant>>

    @Query("SELECT * FROM tracked_plants WHERE id = :id")
    fun getTrackedPlant(id: Long): Flow<TrackedPlant?>

    @Query("SELECT * FROM tracked_plants WHERE id = :id")
    suspend fun getTrackedPlantSync(id: Long): TrackedPlant?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackedPlant(plant: TrackedPlant): Long

    @Update
    suspend fun updateTrackedPlant(plant: TrackedPlant)

    @Query("DELETE FROM tracked_plants WHERE id = :id")
    suspend fun deleteTrackedPlant(id: Long)

    // ----------------------------------------------------
    // User Tracked Health History & Progress
    // ----------------------------------------------------
    @Query("SELECT * FROM health_history_logs WHERE plantId = :plantId ORDER BY timestamp ASC")
    fun getHealthLogs(plantId: Long): Flow<List<HealthHistoryLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthLog(log: HealthHistoryLog): Long

    @Query("DELETE FROM health_history_logs WHERE plantId = :plantId")
    suspend fun deleteHealthLogsForPlant(plantId: Long)

    // ----------------------------------------------------
    // User Tracked Care Action Logs
    // ----------------------------------------------------
    @Query("SELECT * FROM care_action_logs WHERE plantId = :plantId ORDER BY timestamp DESC")
    fun getCareLogs(plantId: Long): Flow<List<CareActionLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCareLog(log: CareActionLog): Long

    @Query("DELETE FROM care_action_logs WHERE plantId = :plantId")
    suspend fun deleteCareLogsForPlant(plantId: Long)

    // ----------------------------------------------------
    // Diagnosis Scan Logs
    // ----------------------------------------------------
    @Query("SELECT * FROM diagnosis_logs ORDER BY timestamp DESC")
    fun getAllDiagnosisLogs(): Flow<List<DiagnosisLogEntity>>

    @Query("SELECT * FROM diagnosis_logs WHERE id = :id")
    fun getDiagnosisLog(id: Long): Flow<DiagnosisLogEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiagnosisLog(log: DiagnosisLogEntity): Long

    @Query("UPDATE diagnosis_logs SET isSavedToGarden = 1 WHERE id = :id")
    suspend fun markDiagnosisSaved(id: Long)

    @Query("DELETE FROM diagnosis_logs WHERE id = :id")
    suspend fun deleteDiagnosisLog(id: Long)
}
