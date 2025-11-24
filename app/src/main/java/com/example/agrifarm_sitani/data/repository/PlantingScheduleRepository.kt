package com.example.agrifarm_sitani.data.repository

import com.example.agrifarm_sitani.data.dao.PlantingScheduleDao
import com.example.agrifarm_sitani.data.entity.PlantingScheduleEntity
import com.example.agrifarm_sitani.data.entity.ScheduleItemEntity
import com.example.agrifarm_sitani.model.ScheduleItem
import kotlinx.coroutines.flow.Flow

class PlantingScheduleRepository(private val dao: PlantingScheduleDao) {

    fun getAllSchedules(): Flow<List<PlantingScheduleEntity>> = dao.getAllSchedules()

    fun getScheduleItems(scheduleId: Int): Flow<List<ScheduleItemEntity>> =
        dao.getScheduleItems(scheduleId)

    suspend fun saveSchedule(name: String, startDate: Long, items: List<ScheduleItem>) {
        val scheduleEntity = PlantingScheduleEntity(name = name, startDate = startDate)
        val scheduleId = dao.insertSchedule(scheduleEntity).toInt()

        val itemEntities = items.map { item ->
            ScheduleItemEntity(
                scheduleId = scheduleId,
                day = item.day,
                activity = item.activity,
                description = item.description
            )
        }
        dao.insertScheduleItems(itemEntities)
    }

    suspend fun deleteSchedule(schedule: PlantingScheduleEntity) {
        dao.deleteScheduleItems(schedule.id)
        dao.deleteSchedule(schedule)
    }
}
