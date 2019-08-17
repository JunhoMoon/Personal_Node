package com.hims.personal_node.DataMamager.Repository.Health

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.hims.personal_node.DataMamager.*
import com.hims.personal_node.Model.Health.Health

class HealthRepository(private  val healthDAO: HealthDAO){
    @WorkerThread
    suspend fun insert(health: Health){
        healthDAO.insert(health)
    }
    @WorkerThread
    suspend fun update(health: Health){
        healthDAO.update(health)
    }
    @WorkerThread
    suspend fun delete(health: Health){
        healthDAO.delete(health)
    }
//    val getall: List<Health> = healthDAO.getall()
    val getall: LiveData<List<Health>> = healthDAO.getall()
}