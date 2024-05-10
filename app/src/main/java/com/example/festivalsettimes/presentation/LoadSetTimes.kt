package com.example.festivalsettimes.presentation

import java.time.LocalDate
import java.time.LocalTime

data class Entry(val day: String, val stage: String, val artist: String, val time: String)

fun <K, A> updateMap(map: Map<K, A>, key: K, default: A, update: (A) -> A): Map<K, A> {
    val value = map[key]
    return if (value != null) {
        map.plus(key to update(value))
    } else {
        map.plus(key to default)
    }
}

fun getFestivalDaysFromString(setTimes: String): List<FestivalDay> {
    val entries = setTimes
        .lines()
        .map { it.split(',') }
    val sets = entries
        .map { Entry(it[0], it[1], it[2], it[3]) }
        .fold(emptyMap<LocalDate, Map<String, List<Pair<String, LocalTime>>>>()) { xs, entry ->
            val time = LocalTime.parse(entry.time, timeOnlyFormatParse)
            val parsedDay = LocalDate.parse(entry.day, dateOnlyFormat)
            val actualDay =
                if (time.hour < 6) {
                    parsedDay.minusDays(1)
                } else {
                    parsedDay
                }
            val newSet = Pair(entry.artist, time)
            val newStage = listOf(newSet)
            val newDay = mapOf(entry.stage to newStage)
            updateMap(xs, actualDay, newDay) { day ->
                updateMap(day, entry.stage, newStage) { sets ->
                    sets.plus(newSet)
                }
            }
        }
    return sets.entries.map { day ->
        FestivalDay(day.key, day.value.entries.map { stage ->
            Stage(stage.key, stage.value.map {
                SetTime(it.first, it.second)
            })
        })
    }
}