package com.example.agrifarm_sitani.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.agrifarm_sitani.data.dao.PlantingScheduleDao
import com.example.agrifarm_sitani.data.dao.SeedCalculationDao
import com.example.agrifarm_sitani.data.entity.PlantingScheduleEntity
import com.example.agrifarm_sitani.data.entity.ScheduleItemEntity
import com.example.agrifarm_sitani.data.entity.SeedCalculationEntity

@Database(
    entities = [
        PlantingScheduleEntity::class, 
        ScheduleItemEntity::class,
        SeedCalculationEntity::class
    ], 
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plantingScheduleDao(): PlantingScheduleDao
    abstract fun seedCalculationDao(): SeedCalculationDao
}
