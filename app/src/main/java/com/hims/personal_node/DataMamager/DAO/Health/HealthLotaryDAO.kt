package com.hims.personal_node.DataMamager

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.hims.personal_node.BaseDao
import com.hims.personal_node.Model.Health.HealthLotary

@Dao
interface HealthLotaryDAO:BaseDao<HealthLotary>{
    @Query("SELECT * FROM HealthLotary")
    fun getall() : LiveData<List<HealthLotary>>
//    fun getall() : List<HealthLotary>
}