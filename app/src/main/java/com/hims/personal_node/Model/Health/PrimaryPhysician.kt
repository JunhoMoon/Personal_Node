package com.hims.personal_node.Model.Health

import androidx.room.*

@Entity(tableName = "PrimaryPhysician", foreignKeys = [ForeignKey(entity = HealthAuthority::class,parentColumns = arrayOf("node_kn"),childColumns = arrayOf("node_kn"))],
    primaryKeys = ["node_kn", "primaryPhysician_id"])
data class PrimaryPhysician(
    @ColumnInfo(name = "node_kn")
    var node_kn: String,
    @ColumnInfo(name = "primaryPhysician_id")
    var primaryPhysician_id: String,
    @ColumnInfo(name = "primaryPhysician_name")
    var primaryPhysician_name: String?,
    @ColumnInfo(name = "reg_date")
    var reg_date:String?,
    @ColumnInfo(name = "read_auth")
    var read_auth:Int = 0,
    @ColumnInfo(name = "record_auth")
    var record_auth:Int = 0
)
data class PrimaryPhysicianMessage(
    var node_kn: String,
    var primaryPhysician_id: String,
    var primaryPhysician_name: String?,
    var reg_date:String?,
    var accepted:String?,
    var read_auth:Int = 0,
    var record_auth:Int = 0
)