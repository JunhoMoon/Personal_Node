package com.hims.personal_node.DataMamager.Repository.Device

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.hims.personal_node.DataMamager.DeviceUserDAO
import com.hims.personal_node.Model.Device.DeviceUser

class DeviceUserRepository (private val deviceUserDAO: DeviceUserDAO){
//    val getall: List<DeviceUser> = deviceUserDAO.getall()
    val getall: LiveData<List<DeviceUser>> = deviceUserDAO.getall()
    @WorkerThread
    suspend fun checkPersonalData(node_kn:String) : Int{
        return deviceUserDAO.checkDevice(node_kn)
    }
    @WorkerThread
    suspend fun insert(deviceUser: DeviceUser){
        deviceUserDAO.insert(deviceUser)
    }
    @WorkerThread
    suspend fun update(deviceUser: DeviceUser){
        deviceUserDAO.update(deviceUser)
    }
    @WorkerThread
    suspend fun delete(deviceUser: DeviceUser){
        deviceUserDAO.delete(deviceUser)
    }
}