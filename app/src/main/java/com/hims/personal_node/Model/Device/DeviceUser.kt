package com.hims.personal_node.Model.Device

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DeviceUser")
data class DeviceUser(
    @PrimaryKey
    @ColumnInfo(name = "user_kn")
    var user_kn:String
)