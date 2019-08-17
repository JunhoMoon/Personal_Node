package com.hims.personal_node.DataMamager

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.hims.personal_node.BaseDao
import com.hims.personal_node.Model.Health.HealthDetail

@Dao
interface HealthDetailDAO:BaseDao<HealthDetail>{
    @Query("SELECT * FROM HealthDetail")
    fun getall() : LiveData<List<HealthDetail>>
//    fun getall() : List<HealthDetail>
}