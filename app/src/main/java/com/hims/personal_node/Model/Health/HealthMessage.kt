package com.hims.personal_node.Model.Health

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "HealthMessage")
data class HealthMessage(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var _id: Long,
    @ColumnInfo(name = "node_kn")
    var node_kn:String,
    @ColumnInfo(name = "processType")
    var processType:String,
    @ColumnInfo(name = "message")
    var message:String,
    @ColumnInfo(name = "viewState")
    var viewState:Int = 0,
    @ColumnInfo(name = "decision")
    var decision:Int = 0
)