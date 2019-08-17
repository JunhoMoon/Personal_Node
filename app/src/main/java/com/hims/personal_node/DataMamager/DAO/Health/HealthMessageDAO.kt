package com.hims.personal_node.DataMamager

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.hims.personal_node.BaseDao
import com.hims.personal_node.Model.Health.HealthMessage

@Dao
interface HealthMessageDAO:BaseDao<HealthMessage>{
    @Query("SELECT * FROM HealthMessage")
    fun getall() : LiveData<List<HealthMessage>>
//    fun getall() : List<HealthMessage>
}