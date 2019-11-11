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

    @Query("SELECT * FROM HealthAuthority where node_kn like :node_kn order by date(reg_date)")
    fun getByNodeKN(node_kn: String) : HealthAuthority
//    fun getall() : List<HealthAuthority>
}