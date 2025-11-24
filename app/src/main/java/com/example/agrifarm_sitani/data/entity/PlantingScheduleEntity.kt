package com.example.agrifarm_sitani.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planting_schedules")
data class PlantingScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val startDate: Long,
    val cropType: String = "Padi", // bisa disesuaikan
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "schedule_items")
data class ScheduleItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val scheduleId: Int,
    val day: Int,
    val activity: String,
    val description: String
)
