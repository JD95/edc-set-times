package com.example.festivalsettimes.presentation

import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.LocalTime

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
data class SetTime(val artist: String, val start: LocalTime)
