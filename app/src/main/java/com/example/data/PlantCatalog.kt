package com.example.data

import com.example.model.DiagnosisResult
import com.example.model.HealthLevel
import com.example.model.PlantSpecies
import com.example.model.TreatmentStep

object PlantCatalog {

    val SPECIES_LIST = listOf(
        PlantSpecies(
            id = "monstera_deliciosa",
            commonName = "Monstera Deliciosa",
            scientificName = "Monstera deliciosa",
            family = "Araceae",
            origin = "Central & South America",
            description = "Iconic tropical plant famous for its lush perforated (fenestrated) leaves. Fast-growing, architectural, and very adaptable to indoor environments.",
            difficulty = "Easy",
            tags = listOf("Air Purifying", "Statement Plant", "Climbing"),
            lightLevel = "Bright Indirect Light",
            lightDesc = "Flourishes in medium to bright filtered light. Avoid prolonged direct harsh afternoon sun which scorches leaves.",
            wateringFrequencyDays = 7,
            waterDesc = "Water every 1-2 weeks. Allow top 2 to 3 inches (5-7 cm) of soil to dry out between waterings.",
            tempRange = "18°C - 29°C (65°F - 85°F)",
            humidityRange = "60% - 80% (Tolerates 40%+)",
            soilType = "Rich, well-draining chunky mix (peat/coco coir, perlite, orchid bark)",
            fertilizerSchedule = "Monthly in spring and summer with balanced liquid houseplant fertilizer",
            toxicityWarning = "Contains insoluble calcium oxalate crystals. Keep out of reach of curious cats and dogs.",
            imageResName = "sample_monstera"
        ),
        PlantSpecies(
            id = "fiddle_leaf_fig",
            commonName = "Fiddle Leaf Fig",
            scientificName = "Ficus lyrata",
            family = "Moraceae",
            origin = "Western Africa",
            description = "Dramatic indoor tree with large, violin-shaped waxy leaves. Highly prized by interior designers, loves stability and bright light.",
            difficulty = "Moderate",
            tags = listOf("Statement Tree", "Bright Spaces", "Foliage Focus"),
            lightLevel = "Bright Filtered Light",
            lightDesc = "Needs ample bright, indirect light to thrive. Place within a few feet of an East or South-facing window with sheer curtains.",
            wateringFrequencyDays = 7,
            waterDesc = "Water thoroughly when the top 50% of the soil feels dry. Never let it sit in standing water trays.",
            tempRange = "18°C - 26°C (65°F - 78°F)",
            humidityRange = "50% - 70%",
            soilType = "Premium potting soil with perlite and pine bark for rapid drainage",
            fertilizerSchedule = "High-nitrogen liquid feed every 3 to 4 weeks during active growth (spring/summer)",
            toxicityWarning = "Sap can irritate skin and cause mild stomach upset in pets.",
            imageResName = "sample_fiddle"
        ),
        PlantSpecies(
            id = "snake_plant",
            commonName = "Snake Plant",
            scientificName = "Sansevieria trifasciata",
            family = "Asparagaceae",
            origin = "Tropical West Africa",
            description = "Nearly indestructible succulent known as Mother-in-Law's Tongue. Features erect sword-like leaves with striking gold and emerald marbling.",
            difficulty = "Very Easy",
            tags = listOf("Indestructible", "Air Purifier", "Low Light Friendly", "Drought Tolerant"),
            lightLevel = "Low to Bright Indirect",
            lightDesc = "Extremely versatile. Survives in dark corners as well as bright filtered rooms.",
            wateringFrequencyDays = 18,
            waterDesc = "Drought champion! Water only when soil is 100% bone dry throughout the pot (every 2-4 weeks).",
            tempRange = "15°C - 29°C (60°F - 85°F)",
            humidityRange = "30% - 50% (Standard room humidity)",
            soilType = "Fast-draining cactus and succulent mix with coarse pumice or sand",
            fertilizerSchedule = "Light feed once in spring and once in summer. Do not fertilize in winter.",
            toxicityWarning = "Toxic to dogs and cats if chewed due to saponins.",
            imageResName = "sample_snake"
        ),
        PlantSpecies(
            id = "golden_pothos",
            commonName = "Golden Pothos",
            scientificName = "Epipremnum aureum",
            family = "Araceae",
            origin = "French Polynesia",
            description = "Vigorous trailing vine with glossy heart-shaped foliage marbled in green and cream-yellow. Perfect for hanging baskets and book shelves.",
            difficulty = "Very Easy",
            tags = listOf("Fast Growing", "Hanging Basket", "Air Purifier", "Beginner Favorite"),
            lightLevel = "Low to Bright Indirect",
            lightDesc = "Thrives in moderate indirect light. Variegation intensifies in brighter spots.",
            wateringFrequencyDays = 7,
            waterDesc = "Water when the top 2 inches dry out or when leaves begin to show slight limpness.",
            tempRange = "17°C - 30°C (62°F - 86°F)",
            humidityRange = "40% - 60%",
            soilType = "Standard standard houseplant potting soil with perlite",
            fertilizerSchedule = "Diluted balanced fertilizer every 4 weeks in warm months",
            toxicityWarning = "Mildly toxic to pets due to calcium oxalates.",
            imageResName = "sample_pothos"
        ),
        PlantSpecies(
            id = "peace_lily",
            commonName = "Peace Lily",
            scientificName = "Spathiphyllum wallisii",
            family = "Araceae",
            origin = "Tropical Americas",
            description = "Elegant dark green foliage plant that produces stunning white spathes (flowers). Dramatically droops to communicate when it needs a drink.",
            difficulty = "Easy",
            tags = listOf("Flowering", "Air Purifier", "Communicator", "Low Light"),
            lightLevel = "Medium to Low Indirect",
            lightDesc = "Prefers shadier spots. Direct sun will burn the dark green leaves and bleach flowers.",
            wateringFrequencyDays = 5,
            waterDesc = "Keep soil consistently lightly moist but never waterlogged. Sensitive to tap water chlorine.",
            tempRange = "18°C - 27°C (65°F - 80°F)",
            humidityRange = "55% - 75%",
            soilType = "Moisture-retentive yet aerated organic potting compost",
            fertilizerSchedule = "Weak fertilizer every 6 weeks spring through autumn",
            toxicityWarning = "Toxic to pets; keeps away from cats and dogs.",
            imageResName = "sample_monstera"
        ),
        PlantSpecies(
            id = "zz_plant",
            commonName = "ZZ Plant",
            scientificName = "Zamioculcas zamiifolia",
            family = "Araceae",
            origin = "Eastern Africa",
            description = "Modern architectural plant with gleaming waxy dark leaves. Stores water in bulbous underground rhizomes, making it virtually drought-proof.",
            difficulty = "Very Easy",
            tags = listOf("Low Maintenance", "Drought Resilient", "Office Friendly"),
            lightLevel = "Low to Bright Indirect",
            lightDesc = "Happy anywhere from fluorescent-lit cubicles to bright rooms.",
            wateringFrequencyDays = 18,
            waterDesc = "Water sparingly. Allow soil to dry out completely. Tolerates weeks without watering.",
            tempRange = "16°C - 28°C (60°F - 82°F)",
            humidityRange = "30% - 50%",
            soilType = "Well-draining potting soil cut with 30% perlite or pumice",
            fertilizerSchedule = "Half-strength liquid feed 2-3 times per year during spring/summer",
            toxicityWarning = "Toxic if ingested; causes mouth irritation.",
            imageResName = "sample_snake"
        )
    )

    fun getSpeciesById(id: String): PlantSpecies {
        return SPECIES_LIST.find { it.id == id } ?: SPECIES_LIST.first()
    }

    // Diagnostic presets for quick scanning and testing
    val SAMPLE_SCANS = listOf(
        DiagnosisResult(
            plant = getSpeciesById("monstera_deliciosa"),
            healthScore = 96,
            statusLevel = HealthLevel.HEALTHY,
            conditionTitle = "Vibrant & Thriving",
            symptoms = listOf(
                "Glossy, deep emerald foliage",
                "Firm petioles with no drooping",
                "Clean natural fenestrations (leaf splits)",
                "No signs of pest presence or fungal damage"
            ),
            rootCause = "Optimal light exposure, healthy root aeration, and balanced moisture levels.",
            treatmentSteps = listOf(
                TreatmentStep(
                    step = 1,
                    title = "Maintain Consistent Watering",
                    description = "Continue watering once the top 2-3 inches are dry. Use lukewarm or room temperature water.",
                    timeframe = "Weekly"
                ),
                TreatmentStep(
                    step = 2,
                    title = "Gently Wipe Leaves",
                    description = "Dust accumulation blocks photosynthesis. Wipe foliage with a soft, damp microfiber cloth monthly.",
                    timeframe = "Monthly"
                ),
                TreatmentStep(
                    step = 3,
                    title = "Rotate Pot for Balanced Growth",
                    description = "Rotate 90 degrees every week so all leaves receive equal light from your window.",
                    timeframe = "Weekly"
                )
            ),
            preventiveTips = listOf(
                "Keep away from direct heating or AC vents.",
                "Provide a moss pole or trellis as aerial roots emerge for upright climbing support."
            ),
            sampleImageRes = "sample_monstera"
        ),
        DiagnosisResult(
            plant = getSpeciesById("fiddle_leaf_fig"),
            healthScore = 64,
            statusLevel = HealthLevel.ATTENTION,
            conditionTitle = "Brown Leaf Spot (Fungal / Over-wetting)",
            symptoms = listOf(
                "Dark brown necrotic patches on lower leaves with yellow halos",
                "Localized leaf crisping along outer edges",
                "Sluggish new apical shoot development"
            ),
            rootCause = "Overly damp root zone combined with overhead leaf splashing creating an environment for fungal spores to proliferate.",
            treatmentSteps = listOf(
                TreatmentStep(
                    step = 1,
                    title = "Sanitize & Prune Affected Foliage",
                    description = "Using sterilized shears, remove leaves with more than 50% brown spotting to halt spore transmission.",
                    timeframe = "Immediate",
                    urgency = "High"
                ),
                TreatmentStep(
                    step = 2,
                    title = "Improve Air Flow & Sunlight",
                    description = "Move plant closer to bright indirect light and ensure circulating air to dry foliage rapidly.",
                    timeframe = "Day 1-3",
                    urgency = "Medium"
                ),
                TreatmentStep(
                    step = 3,
                    title = "Apply Organic Fungicide or Neem Spray",
                    description = "Mist upper and lower leaf surfaces with copper fungicide or organic cold-pressed neem spray.",
                    timeframe = "Once every 7 days (for 3 weeks)",
                    urgency = "High"
                ),
                TreatmentStep(
                    step = 4,
                    title = "Adjust Watering Technique",
                    description = "Water solely at soil level. Never spray or mist water onto Fiddle Leaf Fig leaves.",
                    timeframe = "Ongoing"
                )
            ),
            preventiveTips = listOf(
                "Always check soil moisture 3 inches down before watering.",
                "Ensure pot has at least 3 active drainage holes and never sits in a full saucer."
            ),
            sampleImageRes = "sample_fiddle"
        ),
        DiagnosisResult(
            plant = getSpeciesById("snake_plant"),
            healthScore = 52,
            statusLevel = HealthLevel.ATTENTION,
            conditionTitle = "Early Stage Overwatering & Soil Compaction",
            symptoms = listOf(
                "Yellowing at the base of several outer sword leaves",
                "Spongy, softened tissue near soil line",
                "Soil surface damp for over 10 consecutive days"
            ),
            rootCause = "Excessive watering frequency or heavy soil holding stagnant water around the sensitive underground rhizomes.",
            treatmentSteps = listOf(
                TreatmentStep(
                    step = 1,
                    title = "Immediately Halt All Watering",
                    description = "Allow the soil container to dry out completely. Snake plants easily survive weeks in dry soil.",
                    timeframe = "Immediate",
                    urgency = "High"
                ),
                TreatmentStep(
                    step = 2,
                    title = "Inspect & Prune Soft Tissue",
                    description = "Gently cut away any mushy, waterlogged leaf sections with a clean razor blade.",
                    timeframe = "Day 1",
                    urgency = "Medium"
                ),
                TreatmentStep(
                    step = 3,
                    title = "Aerate the Root Zone",
                    description = "Use a clean wooden chopstick to poke small aeration holes down through the soil to introduce oxygen.",
                    timeframe = "Day 1-2"
                ),
                TreatmentStep(
                    step = 4,
                    title = "Repot in Gritty Cactus Blend (if soil remains wet)",
                    description = "If soil stays wet after 48 hours, unpot and switch to a 50/50 mix of potting soil and perlite/pumice.",
                    timeframe = "Day 3-5"
                )
            ),
            preventiveTips = listOf(
                "Only water when the pot feels extremely light and soil is 100% dry from top to bottom.",
                "Terracotta pots are ideal for snake plants as clay breathes and wicks excess water."
            ),
            sampleImageRes = "sample_snake"
        ),
        DiagnosisResult(
            plant = getSpeciesById("golden_pothos"),
            healthScore = 58,
            statusLevel = HealthLevel.ATTENTION,
            conditionTitle = "Dehydration & Soil Hydrophobic Underwatering",
            symptoms = listOf(
                "Limp, drooping trailing stems and curled leaves",
                "Dry, crumbly soil pulling away from pot edges",
                "Featherweight pot indicating zero moisture in root ball"
            ),
            rootCause = "Potting medium has dried out severely and become hydrophobic, causing water to run down the sides without penetrating.",
            treatmentSteps = listOf(
                TreatmentStep(
                    step = 1,
                    title = "Perform Deep Bottom-Watering Soak",
                    description = "Place pot inside a sink or bowl with 3 inches of lukewarm water for 30-45 minutes until top soil feels moist.",
                    timeframe = "Today",
                    urgency = "High"
                ),
                TreatmentStep(
                    step = 2,
                    title = "Drain Completely",
                    description = "Lift the pot and let all excess water drain freely for 15 minutes before returning to decorative cachepot.",
                    timeframe = "Today",
                    urgency = "High"
                ),
                TreatmentStep(
                    step = 3,
                    title = "Observe Foliage Bounce-Back",
                    description = "Pothos cells will reinflate with water pressure within 4 to 12 hours, regaining turgidity.",
                    timeframe = "12 Hours"
                )
            ),
            preventiveTips = listOf(
                "Check pot weight every 5-7 days; a light pot is your easiest cue to water.",
                "Trim leggy vines in spring to stimulate bushier crown foliage."
            ),
            sampleImageRes = "sample_pothos"
        )
    )
}
