package com.hims.personal_node.DataMamager

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.hims.personal_node.BaseDao
import com.hims.personal_node.Model.Health.HealthNotary

@Dao
interface HealthNotaryDAO:BaseDao<HealthNotary>{
    @Query("SELECT * FROM HealthNotary")
    fun getall() : LiveData<List<HealthNotary>>
//    fun getall() : List<HealthLotary>
}