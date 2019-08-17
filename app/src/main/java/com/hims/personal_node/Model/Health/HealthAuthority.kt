package com.hims.personal_node.Model.Health

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "HealthAuthority")
data class HealthAuthority(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var _id: Long,
    @ColumnInfo(name = "node_kn")
    var node_kn:String,
    @ColumnInfo(name = "user_id")
    var user_id:String = "*",
    @ColumnInfo(name = "read_auth")
    var read_auth:Int = 0,
    @ColumnInfo(name = "record_auth")
    var record_auth:Int = 0
)