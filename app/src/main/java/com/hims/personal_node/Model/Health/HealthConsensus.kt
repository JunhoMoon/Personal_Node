package com.hims.personal_node.Model.Health

import androidx.room.*
import androidx.room.ForeignKey.*
import com.hims.personal_node.Model.Health.Health

@Entity(
    tableName = "HealthConsensus",
    foreignKeys = [ForeignKey(
        entity = Health::class,
        parentColumns = arrayOf("_id"),
        childColumns = arrayOf("healthId"),
        onDelete = NO_ACTION,
        onUpdate = NO_ACTION
    )],
    indices = [Index(value = arrayOf("healthId"), unique = false)]
)
data class HealthConsensus(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var _id: Long,
    @ColumnInfo(name = "healthId")
    var healthId: Long?,
    @ColumnInfo(name = "regDate")
    var regDate:String,
    @ColumnInfo(name = "provider_healthId")
    var provider_healthId:Long
)