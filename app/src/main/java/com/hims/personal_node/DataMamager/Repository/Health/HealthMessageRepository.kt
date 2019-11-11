package com.hims.personal_node.DataMamager.Repository.Health

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.room.Transaction
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
    fun delete(healthMessage: HealthMessage){
        healthMessageDAO.delete(healthMessage)
    }
    @WorkerThread
    @Transaction
    fun deleteByID(_id: Long){
        healthMessageDAO.deleteByID(_id)
    }
//    val getall: List<HealthMessage> = healthMessageDAO.getall()
    val getall: LiveData<List<HealthMessage>> = healthMessageDAO.getall()

    @WorkerThread
    fun getByID(message_id: Long):HealthMessage{
        return healthMessageDAO.getByID(message_id)
    }
}