package com.hims.personal_node.DataMamager.Repository.Health

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.hims.personal_node.DataMamager.HealthDetailDAO
import com.hims.personal_node.Model.Health.HealthDetail

class HealthDetailRepository (private val healthDetailDAO: HealthDetailDAO){
    @WorkerThread
    fun insert(healthDetail: HealthDetail){
        healthDetailDAO.insert(healthDetail)
    }
    @WorkerThread
    suspend fun update(healthDetail: HealthDetail){
        healthDetailDAO.update(healthDetail)
    }
    @WorkerThread
    suspend fun delete(healthDetail: HealthDetail){
        healthDetailDAO.delete(healthDetail)
    }
//    val getall: List<HealthDetail> = healthDetailDAO.getall()
    val getall: LiveData<List<HealthDetail>> = healthDetailDAO.getall()
}