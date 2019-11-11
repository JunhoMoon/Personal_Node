package com.hims.personal_node.Model.Health

import androidx.room.*
import androidx.room.ForeignKey.NO_ACTION

@Entity(
    tableName = "HealthNotary",
    primaryKeys = ["subject_health_no", "notary_kn"],
    foreignKeys = [ForeignKey(
        entity = Health::class,
        parentColumns = arrayOf("subject_health_no"),
        childColumns = arrayOf("subject_health_no"),
        onDelete = NO_ACTION,
        onUpdate = NO_ACTION
    )]
)
data class HealthNotary(
    @ColumnInfo(name = "subject_health_no")
    var subject_health_no:Long,
    @ColumnInfo(name = "notary_kn")
    var notary_kn:String,
    @ColumnInfo(name = "notary_data_no")
    var notary_data_no:Long,
    @ColumnInfo(name = "regDate")
    var regDate:String
)