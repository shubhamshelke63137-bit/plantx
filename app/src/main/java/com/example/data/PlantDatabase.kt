package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.entity.CareActionLog
import com.example.data.entity.DiagnosisLogEntity
import com.example.data.entity.HealthHistoryLog
import com.example.data.entity.PlantSpeciesEntity
import com.example.data.entity.TrackedPlant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PlantSpeciesEntity::class,
        TrackedPlant::class,
        HealthHistoryLog::class,
        CareActionLog::class,
        DiagnosisLogEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class PlantDatabase : RoomDatabase() {

    abstract fun plantDao(): PlantDao

    companion object {
        @Volatile
        private var INSTANCE: PlantDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): PlantDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlantDatabase::class.java,
                    "plantx_care_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.plantDao())
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        // Ensure species catalog is populated if empty
                        if (database.plantDao().getPlantSpeciesCount() == 0) {
                            val speciesEntities = PlantCatalog.SPECIES_LIST.map {
                                PlantSpeciesEntity.fromDomainModel(it)
                            }
                            database.plantDao().insertPlantSpecies(speciesEntities)
                        }
                    }
                }
            }
        }

        private suspend fun populateDatabase(dao: PlantDao) {
            val now = System.currentTimeMillis()
            val oneDayMs = 24 * 60 * 60 * 1000L

            // 1. Populate Plant Species Table in Room
            val speciesEntities = PlantCatalog.SPECIES_LIST.map {
                PlantSpeciesEntity.fromDomainModel(it)
            }
            dao.insertPlantSpecies(speciesEntities)

            // 2. Starter Tracked Plant 1: Monstera Deliciosa (Healthy Recovery)
            val monsteraId = dao.insertTrackedPlant(
                TrackedPlant(
                    nickname = "Living Room Monstera",
                    speciesId = "monstera_deliciosa",
                    speciesName = "Monstera Deliciosa",
                    scientificName = "Monstera deliciosa",
                    location = "Living Room",
                    currentHealthScore = 92,
                    healthStatus = "Healthy & Vibrant",
                    lastWatered = now - (2 * oneDayMs),
                    wateringIntervalDays = 7,
                    lastFertilized = now - (12 * oneDayMs),
                    fertilizingIntervalDays = 21,
                    lastMisted = now - (oneDayMs / 2),
                    imageResName = "sample_monstera",
                    notes = "Thriving near East-facing window. New fenestrated leaf opening up nicely!",
                    dateAdded = now - (14 * oneDayMs)
                )
            )

            // Health trend progression logs for Monstera
            dao.insertHealthLog(HealthHistoryLog(plantId = monsteraId, healthScore = 78, statusNote = "Initial scan: Slight tip browning", timestamp = now - (14 * oneDayMs)))
            dao.insertHealthLog(HealthHistoryLog(plantId = monsteraId, healthScore = 80, statusNote = "Adjusted to bright indirect light", timestamp = now - (11 * oneDayMs)))
            dao.insertHealthLog(HealthHistoryLog(plantId = monsteraId, healthScore = 85, statusNote = "Misting daily, foliage perking up", timestamp = now - (7 * oneDayMs)))
            dao.insertHealthLog(HealthHistoryLog(plantId = monsteraId, healthScore = 89, statusNote = "Balanced fertilizer added", timestamp = now - (3 * oneDayMs)))
            dao.insertHealthLog(HealthHistoryLog(plantId = monsteraId, healthScore = 92, statusNote = "Fully vibrant! Health score 92%", timestamp = now))

            dao.insertCareLog(CareActionLog(plantId = monsteraId, actionType = "WATERED", note = "Watered until drainage", timestamp = now - (2 * oneDayMs)))
            dao.insertCareLog(CareActionLog(plantId = monsteraId, actionType = "FERTILIZED", note = "Organic 10-10-10 feed", timestamp = now - (12 * oneDayMs)))
            dao.insertCareLog(CareActionLog(plantId = monsteraId, actionType = "MISTED", note = "Humidity boost", timestamp = now - (oneDayMs / 2)))

            // 3. Starter Tracked Plant 2: Fiddle Leaf Fig (In recovery with treatments)
            val fiddleId = dao.insertTrackedPlant(
                TrackedPlant(
                    nickname = "Office Figgy",
                    speciesId = "fiddle_leaf_fig",
                    speciesName = "Fiddle Leaf Fig",
                    scientificName = "Ficus lyrata",
                    location = "Home Office",
                    currentHealthScore = 68,
                    healthStatus = "Needs Attention",
                    lastWatered = now - (7 * oneDayMs),
                    wateringIntervalDays = 7,
                    lastFertilized = now - (25 * oneDayMs),
                    fertilizingIntervalDays = 30,
                    lastMisted = now - (3 * oneDayMs),
                    imageResName = "sample_fiddle",
                    notes = "Treated for brown spots. Soil moisture monitored with wooden dowel.",
                    dateAdded = now - (10 * oneDayMs)
                )
            )

            dao.insertHealthLog(HealthHistoryLog(plantId = fiddleId, healthScore = 52, statusNote = "Identified brown leaf spot fungus", timestamp = now - (10 * oneDayMs)))
            dao.insertHealthLog(HealthHistoryLog(plantId = fiddleId, healthScore = 58, statusNote = "Pruned 2 affected lower leaves", timestamp = now - (7 * oneDayMs)))
            dao.insertHealthLog(HealthHistoryLog(plantId = fiddleId, healthScore = 64, statusNote = "Applied neem organic treatment", timestamp = now - (3 * oneDayMs)))
            dao.insertHealthLog(HealthHistoryLog(plantId = fiddleId, healthScore = 68, statusNote = "Steady recovery, no new spots", timestamp = now))

            dao.insertCareLog(CareActionLog(plantId = fiddleId, actionType = "TREATED", note = "Applied organic copper fungicide spray", timestamp = now - (7 * oneDayMs)))
            dao.insertCareLog(CareActionLog(plantId = fiddleId, actionType = "WATERED", note = "Deep watering, drained thoroughly", timestamp = now - (7 * oneDayMs)))

            // 4. Starter Diagnosis Scan Logs
            dao.insertDiagnosisLog(
                DiagnosisLogEntity(
                    plantSpeciesId = "monstera_deliciosa",
                    speciesCommonName = "Monstera Deliciosa",
                    scientificName = "Monstera deliciosa",
                    healthScore = 96,
                    statusLevel = "Healthy & Vibrant",
                    conditionTitle = "Vibrant & Thriving",
                    symptomsSummary = "Lush foliage, firm petioles, healthy fenestrations",
                    rootCause = "Optimal light exposure, healthy root aeration, and balanced moisture.",
                    treatmentStepsSummary = "Maintain current watering; monthly damp cloth wipe; weekly pot rotation.",
                    sampleImageRes = "sample_monstera",
                    timestamp = now - (2 * oneDayMs),
                    isSavedToGarden = true
                )
            )

            dao.insertDiagnosisLog(
                DiagnosisLogEntity(
                    plantSpeciesId = "fiddle_leaf_fig",
                    speciesCommonName = "Fiddle Leaf Fig",
                    scientificName = "Ficus lyrata",
                    healthScore = 64,
                    statusLevel = "Needs Attention",
                    conditionTitle = "Brown Leaf Spot (Fungal / Over-wetting)",
                    symptomsSummary = "Necrotic patches on lower leaves with yellow haloes",
                    rootCause = "Water droplets on leaves and poor air circulation promoting fungal spore growth.",
                    treatmentStepsSummary = "Prune infected leaves; spray copper fungicide; water only at root level.",
                    sampleImageRes = "sample_fiddle",
                    timestamp = now - (4 * oneDayMs),
                    isSavedToGarden = true
                )
            )
        }
    }
}
