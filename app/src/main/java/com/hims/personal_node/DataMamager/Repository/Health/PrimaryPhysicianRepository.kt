package com.hims.personal_node.DataMamager.Repository.Health

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.hims.personal_node.DataMamager.HealthAuthorityDAO
import com.hims.personal_node.DataMamager.PrimaryPhysicianDAO
import com.hims.personal_node.Model.Health.HealthAuthority
import com.hims.personal_node.Model.Health.PrimaryPhysician

class PrimaryPhysicianRepository (private val primaryPhysicianDAO : PrimaryPhysicianDAO){
    @WorkerThread
    fun insert(primaryPhysician: PrimaryPhysician){
        primaryPhysicianDAO.insert(primaryPhysician)
    }
    @WorkerThread
    fun update(primaryPhysician: PrimaryPhysician){
        primaryPhysicianDAO.update(primaryPhysician)
    }
    @WorkerThread
    suspend fun delete(primaryPhysician: PrimaryPhysician){
        primaryPhysicianDAO.delete(primaryPhysician)
    }
    @WorkerThread
    fun getall(node_kn:String):LiveData<List<PrimaryPhysician>>{
        return primaryPhysicianDAO.getall(node_kn)
    }
    @WorkerThread
    fun getall2(node_kn:String):List<PrimaryPhysician>{
        return primaryPhysicianDAO.getall2(node_kn)
    }
}