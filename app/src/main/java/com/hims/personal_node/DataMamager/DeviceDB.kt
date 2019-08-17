package com.hims.personal_node.DataMamager

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hims.personal_node.Model.Device.DeviceUser

@Database(entities = [DeviceUser::class], version = 1, exportSchema = false)
abstract class DeviceDB: RoomDatabase(){
    abstract fun DeviceUserDAO(): DeviceUserDAO

    companion object {
        private var INSTANCE: DeviceDB? = null

        fun getInstance(context: Context): DeviceDB?{
            if (INSTANCE == null){
                synchronized(DeviceDB::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext, DeviceDB::class.java, "Device.DB")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance(){
            INSTANCE = null
        }
    }
}
