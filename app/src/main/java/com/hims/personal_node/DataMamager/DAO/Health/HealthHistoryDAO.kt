package com.hims.personal_node.DataMamager

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.hims.personal_node.BaseDao
import com.hims.personal_node.Model.Health.HealthHistory

@Dao
interface HealthHistoryDAO:BaseDao<HealthHistory>{
    @Query("SELECT * FROM HealthHistory")
    fun getall() : LiveData<List<HealthHistory>>
//    fun getall() : List<HealthHistory>
}