package com.hims.personal_node.DataMamager

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.hims.personal_node.BaseDao
import com.hims.personal_node.Model.Health.PrimaryPhysician

@Dao
interface PrimaryPhysicianDAO:BaseDao<PrimaryPhysician>{
    @Query("SELECT * FROM PrimaryPhysician where node_kn like :node_kn")
    fun getall(node_kn: String) : LiveData<List<PrimaryPhysician>>

    @Query("SELECT * FROM PrimaryPhysician where node_kn like :node_kn")
    fun getall2(node_kn: String) : List<PrimaryPhysician>

    @Query("SELECT * FROM PrimaryPhysician where node_kn like :node_kn and primaryPhysician_id like :primaryPhysician_id")
    fun getByPK(node_kn: String, primaryPhysician_id: String) : PrimaryPhysician
//    fun getall() : List<HealthAuthority>
}