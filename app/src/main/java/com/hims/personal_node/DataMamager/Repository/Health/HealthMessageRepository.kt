package com.hims.personal_node.DataMamager.Repository.Health

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.hims.personal_node.DataMamager.HealthMessageDAO
import com.hims.personal_node.Model.Health.HealthMessage

class HealthMessageRepository (private val healthMessageDAO: HealthMessageDAO){
    @WorkerThread
    suspend fun insert(healthMessage: HealthMessage){
        healthMessageDAO.insert(healthMessage)
    }
    @WorkerThread
    suspend fun update(healthMessage: HealthMessage){
        healthMessageDAO.update(healthMessage)
    }
    @WorkerThread
    suspend fun delete(healthMessage: HealthMessage){
        healthMessageDAO.delete(healthMessage)
    }
//    val getall: List<HealthMessage> = healthMessageDAO.getall()
    val getall: LiveData<List<HealthMessage>> = healthMessageDAO.getall()
}