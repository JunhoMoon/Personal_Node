package com.hims.personal_node.Model.Health

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "HealthMessage")
data class HealthMessage(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var _id: Long?,
    @ColumnInfo(name = "sender")
    var sender:String,
    @ColumnInfo(name = "messageType")
    var messageType:String,
    @ColumnInfo(name = "message")
    var message:String,
    @ColumnInfo(name = "reg_date")
    var reg_date:String
)