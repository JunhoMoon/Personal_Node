package com.hims.personal_node.DataMamager.Repository.Health

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.hims.personal_node.DataMamager.HealthHistoryDAO
import com.hims.personal_node.Model.Health.HealthHistory

class HealthHistoryRepository (private val healthHistoryDAO: HealthHistoryDAO){
    @WorkerThread
    suspend fun insert(healthHistory: HealthHistory){
        healthHistoryDAO.insert(healthHistory)
    }
    @WorkerThread
    suspend fun update(healthHistory: HealthHistory){
        healthHistoryDAO.update(healthHistory)
    }
    @WorkerThread
    suspend fun delete(healthHistory: HealthHistory){
        healthHistoryDAO.delete(healthHistory)
    }
//    val getall: List<HealthHistory> = healthHistoryDAO.getall()
    val getall: LiveData<List<HealthHistory>> = healthHistoryDAO.getall()
}