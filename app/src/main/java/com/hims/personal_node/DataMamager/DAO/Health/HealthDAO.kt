package com.hims.personal_node.DataMamager

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.hims.personal_node.BaseDao
import com.hims.personal_node.Model.Health.Health

@Dao
interface HealthDAO:BaseDao<Health>{
    @Query("SELECT * FROM Health")
    fun getall() : LiveData<List<Health>>

    @Query("SELECT * FROM Health")
    fun getall2() : List<Health>
}