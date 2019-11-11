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

    @Query("SELECT * FROM HealthMessage where _id = :message_id")
    fun getByID(message_id:Long) : HealthMessage

    @Query("delete FROM HealthMessage where _id = :message_id")
    fun deleteByID(message_id:Long)
//    fun getall() : List<HealthMessage>
}