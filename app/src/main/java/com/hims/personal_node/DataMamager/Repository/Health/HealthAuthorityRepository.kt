package com.hims.personal_node.DataMamager.Repository.Health

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.hims.personal_node.DataMamager.HealthAuthorityDAO
import com.hims.personal_node.Model.Health.HealthAuthority

class HealthAuthorityRepository (private val healthAuthorityDAO: HealthAuthorityDAO){
    @WorkerThread
    suspend fun insert(healthAuthority: HealthAuthority){
        healthAuthorityDAO.insert(healthAuthority)
    }
    @WorkerThread
    suspend fun update(healthAuthority: HealthAuthority){
        healthAuthorityDAO.update(healthAuthority)
    }
    @WorkerThread
    suspend fun delete(healthAuthority: HealthAuthority){
        healthAuthorityDAO.delete(healthAuthority)
    }
//    val getall: List<HealthAuthority> = healthAuthorityDAO.getall()
    val getall: LiveData<List<HealthAuthority>> = healthAuthorityDAO.getall()
}