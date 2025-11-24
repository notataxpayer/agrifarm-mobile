package com.example.agrifarm_sitani.data.dao

import androidx.room.*
import com.example.agrifarm_sitani.data.entity.PlantingScheduleEntity
import com.example.agrifarm_sitani.data.entity.ScheduleItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantingScheduleDao {
    @Insert
    suspend fun insertSchedule(schedule: PlantingScheduleEntity): Long

    @Insert
    suspend fun insertScheduleItems(items: List<ScheduleItemEntity>)

    @Query("SELECT * FROM planting_schedules")
    fun getAllSchedules(): Flow<List<PlantingScheduleEntity>>

    @Query("SELECT * FROM schedule_items WHERE scheduleId = :scheduleId ORDER BY day ASC")
    fun getScheduleItems(scheduleId: Int): Flow<List<ScheduleItemEntity>>

    @Delete
    suspend fun deleteSchedule(schedule: PlantingScheduleEntity)

    @Query("DELETE FROM schedule_items WHERE scheduleId = :scheduleId")
    suspend fun deleteScheduleItems(scheduleId: Int)
}
