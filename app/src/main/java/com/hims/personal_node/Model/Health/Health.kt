package com.hims.personal_node.Model.Health

import androidx.room.*

@Entity(tableName = "Health")
data class Health(
    @PrimaryKey
    @ColumnInfo(name = "subject_health_no")
    var subject_health_no: Long?,
    @ColumnInfo(name = "issuer_health_no")
    var issuer_health_no: Long?,
    @ColumnInfo(name = "issuer_node_kn")
    var node_kn:String?,
    @ColumnInfo(name = "physician_id")
    var physician_id:String?,
    @ColumnInfo(name = "patient_no")
    var patient_no:String?,
    @ColumnInfo(name = "reg_date")
    var reg_date: String?,
    @ColumnInfo(name = "sLevel")
    var sLevel:Int = 0
)