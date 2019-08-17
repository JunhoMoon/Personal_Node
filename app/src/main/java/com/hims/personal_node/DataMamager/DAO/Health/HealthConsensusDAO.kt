package com.hims.personal_node.DataMamager

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.hims.personal_node.BaseDao
import com.hims.personal_node.Model.Health.HealthConsensus

@Dao
interface HealthConsensusDAO:BaseDao<HealthConsensus>{
    @Query("SELECT * FROM HealthConsensus")
    fun getall() : LiveData<List<HealthConsensus>>
//    fun getall() : List<HealthConsensus>
}