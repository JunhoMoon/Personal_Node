package com.hims.personal_node.DataMamager.Repository.Health

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.hims.personal_node.DataMamager.HealthLotaryDAO
import com.hims.personal_node.Model.Health.HealthLotary

class HealthLotaryRepository (private val healthLotaryDAO: HealthLotaryDAO){
    @WorkerThread
    suspend fun insert(healthLotary: HealthLotary){
        healthLotaryDAO.insert(healthLotary)
    }
    @WorkerThread
    suspend fun update(healthLotary: HealthLotary){
        healthLotaryDAO.update(healthLotary)
    }
    @WorkerThread
    suspend fun delete(healthLotary: HealthLotary){
        healthLotaryDAO.delete(healthLotary)
    }
//    val getall: List<HealthLotary> = healthLotaryDAO.getall()
    val getall: LiveData<List<HealthLotary>> = healthLotaryDAO.getall()
}