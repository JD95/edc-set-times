package com.example.festivalsettimes.presentation

import androidx.compose.ui.graphics.Color
import java.time.LocalDate

sealed class FestivalStage {
    object KineticField : FestivalStage()
    object CosmicMeadow : FestivalStage()
    object CircuitGrounds : FestivalStage()
    object NeonGarden : FestivalStage()
    object Basspod : FestivalStage()
    object Wasteland : FestivalStage()
    object BionicJungle : FestivalStage()
    object QuantumValley : FestivalStage()
    object StereoBloom : FestivalStage()
}

fun stageName(stage: FestivalStage): String {
    return when (stage) {
        FestivalStage.KineticField -> "Kinetic Field"
        FestivalStage.Basspod -> "Basspod"
        FestivalStage.BionicJungle -> "Bionic Jungle"
        FestivalStage.CircuitGrounds -> "Circuit Grounds"
        FestivalStage.CosmicMeadow -> "Cosmic Meadow"
        FestivalStage.NeonGarden -> "Neon Garden"
        FestivalStage.QuantumValley -> "Quantum Valley"
        FestivalStage.StereoBloom -> "Stereo Bloom"
        FestivalStage.Wasteland -> "Wasteland"
    }
}

fun stageColor(stage: FestivalStage): Color {
    return when (stage) {
        FestivalStage.KineticField -> Color.Red
        FestivalStage.Basspod -> Color.hsv(240f, 1.0f, 1.0f)
        FestivalStage.BionicJungle -> Color.Green
        FestivalStage.CircuitGrounds -> Color.Yellow
        FestivalStage.CosmicMeadow -> Color.Cyan
        FestivalStage.NeonGarden -> Color.hsv(30.0f, 1.0f, 1.0f)
        FestivalStage.QuantumValley -> Color.White
        FestivalStage.StereoBloom -> Color.hsv(300.0f, 1.0f, 1.0f)
        FestivalStage.Wasteland -> Color.hsv(270.0f, 1.0f, 1.0f)
    }
}

data class FestivalDay(val date: LocalDate, val stages: List<Stage>)
data class Stage(val name: FestivalStage, val setTimes: List<SetTime>)
data class SetTime(val artist: String, val start: String)

val friday: FestivalDay = FestivalDay(
    date = LocalDate.parse("2024-05-09", dateOnlyFormat),
    stages = listOf(
        Stage(
            FestivalStage.KineticField,
            listOf(
                SetTime("Carola", "10:00"),
                SetTime("Pauline Herr", "11:00"),
                SetTime("Kream", "13:05"),
                SetTime("Sidepiece", "22:16"),
            )
        ),
        Stage(
            FestivalStage.CosmicMeadow,
            listOf(
                SetTime("Frost-Top", "19:00"),
                SetTime("Memba", "20:25"),
                SetTime("Kaivon", "21:30"),
                SetTime("Matroda", "22:30"),
                SetTime("Griz", "22:30"),
                SetTime("Said the Sky", "01:35"),
            )
        ),
        Stage(
            FestivalStage.CircuitGrounds,
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            FestivalStage.NeonGarden,
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            FestivalStage.Basspod,
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            FestivalStage.Wasteland,
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            FestivalStage.QuantumValley,
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            FestivalStage.StereoBloom,
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            FestivalStage.BionicJungle,
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
    )
)
val saturday: FestivalDay = FestivalDay(
    date = LocalDate.parse("2024-05-18", dateOnlyFormat),
    stages = listOf(
        Stage(
            FestivalStage.KineticField,
            listOf(
                SetTime("DJ RON", "19:00"),
                SetTime("Backseat", "20:00"),
                SetTime("Whoopie", "21:05"),
                SetTime("Toast", "22:16"),
            )
        ),
        Stage(
            FestivalStage.CosmicMeadow,
            listOf(
                SetTime("Buttercream", "19:00"),
                SetTime("Rizz", "20:25"),
                SetTime("Gawk Gawk", "21:30"),
                SetTime("Get2DaChoppa", "22:30"),
                SetTime("Griz", "22:30"),
                SetTime("Said the Sky", "01:35"),
            )
        ),
        Stage(
            FestivalStage.CircuitGrounds,
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            FestivalStage.NeonGarden,
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            FestivalStage.Basspod,
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            FestivalStage.Wasteland,
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            FestivalStage.QuantumValley,
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            FestivalStage.StereoBloom,
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            FestivalStage.BionicJungle,
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
    )
)
val sunday: FestivalDay = FestivalDay(
    date = LocalDate.parse("2024-05-19", dateOnlyFormat),
    stages = listOf(
        Stage(
            FestivalStage.KineticField,
            listOf(
                SetTime("Arnold", "19:00"),
                SetTime("Bach", "20:00"),
                SetTime("Beethoven", "21:05"),
                SetTime("Sidepiece", "22:16"),
            )
        ),
        Stage(
            FestivalStage.CosmicMeadow,
            listOf(
                SetTime("Iron Man", "19:00"),
                SetTime("Dr Strange", "20:25"),
                SetTime("Harvey Dent", "21:30"),
                SetTime("Peter Parker", "22:30"),
                SetTime("Griz", "22:30"),
                SetTime("Said the Sky", "01:35"),
            )
        ),
        Stage(
            FestivalStage.CircuitGrounds,
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            FestivalStage.NeonGarden,
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            FestivalStage.Basspod,
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            FestivalStage.Wasteland,
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            FestivalStage.QuantumValley,
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            FestivalStage.StereoBloom,
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            FestivalStage.BionicJungle,
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
    )
)