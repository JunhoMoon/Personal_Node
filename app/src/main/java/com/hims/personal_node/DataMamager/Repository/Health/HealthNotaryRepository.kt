package com.hims.personal_node.DataMamager.Repository.Health

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.hims.personal_node.DataMamager.HealthNotaryDAO
import com.hims.personal_node.Model.Health.HealthNotary

class HealthNotaryRepository (private val healthNotaryDAO: HealthNotaryDAO){
    @WorkerThread
    fun insert(healthNotary: HealthNotary){
        healthNotaryDAO.insert(healthNotary)
    }
    @WorkerThread
    suspend fun update(healthNotary: HealthNotary){
        healthNotaryDAO.update(healthNotary)
    }
    @WorkerThread
    suspend fun delete(healthNotary: HealthNotary){
        healthNotaryDAO.delete(healthNotary)
    }
//    val getall: List<HealthLotary> = healthLotaryDAO.getall()
    val getall: LiveData<List<HealthNotary>> = healthNotaryDAO.getall()
}