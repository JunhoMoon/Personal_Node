package com.hims.personal_node.Model.Health

import androidx.room.*
import com.hims.personal_node.Model.Health.Health

@Entity(
    tableName = "HealthDetail",
    foreignKeys = [ForeignKey(
        entity = Health::class,
        parentColumns = arrayOf("_id"),
        childColumns = arrayOf("healthId")
    )],
    indices = [Index(value = arrayOf("healthId"), unique = false)]
)
data class HealthDetail(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var _id: Long,
    @ColumnInfo(name = "dataName")
    var dataName: String,
    @ColumnInfo(name = "type")
    var type: String,
    @ColumnInfo(name = "value")
    var value: String,
    @ColumnInfo(name = "healthId")
    var healthId: Long
)