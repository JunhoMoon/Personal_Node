package com.hims.personal_node

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hims.personal_node.DataMamager.*
import com.hims.personal_node.Model.Health.*
import com.hims.personal_node.Model.Personal.*

@Database(entities = [PersonalData::class, HealthHistory::class, Health::class, HealthNotary::class,HealthDetail::class, HealthMessage::class, HealthAuthority::class, PrimaryPhysician::class], version = 13, exportSchema = false)
abstract class HIMSDB: RoomDatabase(){
    abstract fun personalDataDAO(): PersonalDataDAO
    abstract fun healthHistoryDAO():HealthHistoryDAO
    abstract fun healthDAO():HealthDAO
    abstract fun healthDetailDAO(): HealthDetailDAO
    abstract fun healthNotaryDAO():HealthNotaryDAO
    abstract fun healthMessageDAO():HealthMessageDAO
    abstract fun healthAuthorityDAO():HealthAuthorityDAO
    abstract fun primaryPhysicianDAO():PrimaryPhysicianDAO

    companion object {
        private var INSTANCE: HIMSDB? = null

        fun getInstance(context: Context, alias:String): HIMSDB?{
            if (INSTANCE == null){
                synchronized(HIMSDB::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext, HIMSDB::class.java, "$alias.DB")
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
