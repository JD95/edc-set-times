package com.example.festivalsettimes.presentation

import androidx.compose.ui.graphics.Color
import java.time.LocalDate

fun <T> interleave (xs: List<T>, ys: List<T>) : List<T> {
    return if (xs.size != ys.size) {
        val smaller = listOf(xs, ys).minBy { it.size }
        val larger = listOf(xs, ys).maxBy { it.size }
        val sizeDiff = larger.size - smaller.size
        val fitted = larger.dropLast(sizeDiff)
        val extra = larger.takeLast(sizeDiff)
        fitted
            .zip(smaller)
            .flatMap { listOf(it.first, it.second)}
            .plus(extra)
    } else {
        xs
            .zip(ys)
            .flatMap { listOf(it.first, it.second)}
    }
}

fun stageColors(numStages: Int): List<Color> {
    val inc = (360f / numStages)
    var hue = 0.0f
    var hues = emptyList<Float>()
    for (i in 0 until numStages) {
        hues = hues.plus(hue)
        hue += inc
    }
    val half = numStages / 2
    val head = hues.subList(0, half)
    val tail = hues.subList(half, numStages)
    return interleave(head,tail).map { Color.hsv(it, 1f, 1f) }
}

data class FestivalDay(val date: LocalDate, val stages: List<Stage>)
data class Stage(val name: String, val setTimes: List<SetTime>)
data class SetTime(val artist: String, val start: String)

val friday: FestivalDay = FestivalDay(
    date = LocalDate.parse("2024-05-10", dateOnlyFormat),
    stages = listOf(
        Stage(
            "Kinetic Field",
            listOf(
                SetTime("Carola", "10:00"),
                SetTime("Pauline Herr", "11:00"),
                SetTime("Kream", "13:05"),
                SetTime("Sidepiece", "22:16"),
            )
        ),
        Stage(
            "Cosmic Meadow",
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
            "Circuit Grounds",
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            "Neon Garden",
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            "Basspod",
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            "Wasteland",
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            "Quantum Valley",
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            "Stereo Bloom",
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            "Bionic Jungle",
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
            "Kinetic Field",
            listOf(
                SetTime("DJ RON", "19:00"),
                SetTime("Backseat", "20:00"),
                SetTime("Whoopie", "21:05"),
                SetTime("Toast", "22:16"),
            )
        ),
        Stage(
            "Cosmic Meadow",
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
            "Circuit Grounds",
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            "Neon Garden",
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            "Basspod",
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            "Wasteland",
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            "Quantum Valley",
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            "Stereo Bloom",
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            "Bionic Jungle",
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
            "Kinetic Field",
            listOf(
                SetTime("Arnold", "19:00"),
                SetTime("Bach", "20:00"),
                SetTime("Beethoven", "21:05"),
                SetTime("Sidepiece", "22:16"),
            )
        ),
        Stage(
            "Cosmic Meadow",
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
            "Circuit Grounds",
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            "Neon Garden",
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            "Basspod",
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            "Wasteland",
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            "Quantum Valley",
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            "Stereo Bloom",
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
        Stage(
            "Bionic Jungle",
            listOf(
                SetTime("Carola", "19:00")
            )
        ),
    )
)