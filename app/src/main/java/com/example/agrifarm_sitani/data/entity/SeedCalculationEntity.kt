package com.example.agrifarm_sitani.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seed_calculations")
data class SeedCalculationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String, // "Kebutuhan Bibit" or "Luas Lahan"
    val inputDetails: String,
    val resultSummary: String
)
