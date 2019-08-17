package com.hims.personal_node.DataMamager

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.hims.personal_node.BaseDao
import com.hims.personal_node.Model.Device.DeviceUser

@Dao
interface DeviceUserDAO: BaseDao<DeviceUser> {
    @Query("SELECT * FROM DeviceUser")
    fun getall() : LiveData<List<DeviceUser>>
//    fun getall() : List<DeviceUser>

    @Query("SELECT count(*) FROM DeviceUser where user_kn like :node_kn")
    fun checkDevice(node_kn: String) : Int
}