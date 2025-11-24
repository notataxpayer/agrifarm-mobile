package com.example.agrifarm_sitani.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.agrifarm_sitani.data.entity.SeedCalculationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SeedCalculationDao {
    @Insert
    suspend fun insertCalculation(calculation: SeedCalculationEntity)

    @Query("SELECT * FROM seed_calculations ORDER BY timestamp DESC")
    fun getAllCalculations(): Flow<List<SeedCalculationEntity>>
}
