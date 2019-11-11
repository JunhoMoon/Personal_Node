package com.hims.personal_node.Model.Health

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "HealthAuthority")
data class HealthAuthority(
    @PrimaryKey
    @ColumnInfo(name = "node_kn")
    var node_kn:String,
    @ColumnInfo(name = "node_name")
    var node_name:String,
    @ColumnInfo(name = "patient_no")
    var patient_no:String,
    @ColumnInfo(name = "reg_date")
    var reg_date:String,
    @ColumnInfo(name = "read_auth")
    var read_auth:Int = 0,
    @ColumnInfo(name = "record_auth")
    var record_auth:Int = 0
)

data class HealthAuthorityMessage(
    var node_kn:String, var node_name:String, var patient_no:String, var reg_date: Timestamp, var accepted:Timestamp?
)