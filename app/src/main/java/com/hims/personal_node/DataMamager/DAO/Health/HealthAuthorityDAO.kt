package com.hims.personal_node.DataMamager

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.hims.personal_node.BaseDao
import com.hims.personal_node.Model.Health.HealthAuthority

@Dao
interface HealthAuthorityDAO:BaseDao<HealthAuthority>{
    @Query("SELECT * FROM HealthAuthority")
    fun getall() : LiveData<List<HealthAuthority>>
//    fun getall() : List<HealthAuthority>
}