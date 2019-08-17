package com.hims.personal_node.Model.Health

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "HealthHistory")
data class HealthHistory(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var _id: Long,
    @ColumnInfo(name = "type")
    var type:String,
    @ColumnInfo(name = "data")
    var data:String,
    @ColumnInfo(name = "node_kn")
    var node_kn:String?,
    @ColumnInfo(name = "message")
    var message:String
)