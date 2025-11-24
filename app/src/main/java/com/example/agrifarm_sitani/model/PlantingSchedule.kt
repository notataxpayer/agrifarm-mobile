package com.example.agrifarm_sitani.model

import java.time.LocalDate
import androidx.room.Entity
import androidx.room.PrimaryKey

data class PlantingSchedule(
    val startDate: LocalDate,
    val scheduleItems: List<ScheduleItem>
)

data class ScheduleItem(
    val date: LocalDate,
    val day: Int,
    val activity: String,
    val description: String
)

fun generatePlantingSchedule(startDate: LocalDate): PlantingSchedule {
    val items = mutableListOf<ScheduleItem>()

    // Penyiraman (setiap 2 hari)
    for (i in 0..60 step 2) {
        items.add(
            ScheduleItem(
                date = startDate.plusDays(i.toLong()),
                day = i,
                activity = "Penyiraman",
                description = "Siram tanaman secara rutin"
            )
        )
    }

    // Pupuk A (hari ke-7, 21, 35, 49)
    listOf(7, 21, 35, 49).forEach { day ->
        items.add(
            ScheduleItem(
                date = startDate.plusDays(day.toLong()),
                day = day,
                activity = "Pupuk A",
                description = "Berikan pupuk dasar NPK"
            )
        )
    }

    // Pupuk B (hari ke-14, 28, 42, 56)
    listOf(14, 28, 42, 56).forEach { day ->
        items.add(
            ScheduleItem(
                date = startDate.plusDays(day.toLong()),
                day = day,
                activity = "Pupuk B",
                description = "Berikan pupuk organik"
            )
        )
    }

    return PlantingSchedule(startDate, items.sortedBy { it.date })
}
