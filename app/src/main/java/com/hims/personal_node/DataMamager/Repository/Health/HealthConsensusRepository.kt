package com.hims.personal_node.DataMamager.Repository.Health

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.hims.personal_node.DataMamager.HealthConsensusDAO
import com.hims.personal_node.Model.Health.HealthConsensus

class HealthConsensusRepository (private val healthConsensusDAO: HealthConsensusDAO){
    @WorkerThread
    suspend fun insert(healthConsensus: HealthConsensus){
        healthConsensusDAO.insert(healthConsensus)
    }
    @WorkerThread
    suspend fun update(healthConsensus: HealthConsensus){
        healthConsensusDAO.update(healthConsensus)
    }
    @WorkerThread
    suspend fun delete(healthConsensus: HealthConsensus){
        healthConsensusDAO.delete(healthConsensus)
    }
//    val getall: List<HealthConsensus> = healthConsensusDAO.getall()
    val getall: LiveData<List<HealthConsensus>> = healthConsensusDAO.getall()
}