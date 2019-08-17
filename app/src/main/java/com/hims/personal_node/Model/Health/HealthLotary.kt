package com.hims.personal_node.Model.Health

import androidx.room.*
import androidx.room.ForeignKey.NO_ACTION
import com.hims.personal_node.Model.Health.Health

@Entity(
    tableName = "HealthLotary",
    foreignKeys = [ForeignKey(
        entity = Health::class,
        parentColumns = arrayOf("_id"),
        childColumns = arrayOf("healthId"),
        onDelete = NO_ACTION,
        onUpdate = NO_ACTION
    )],
    indices = [Index(value = arrayOf("healthId"), unique = false)]
)
data class HealthLotary(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var _id: Long?,
    @ColumnInfo(name = "healthId")
    var healthId:Long,
    @ColumnInfo(name = "lotary_kn")
    var lotary_kn:String,
    @ColumnInfo(name = "lotary_data_id")
    var lotary_data_id:Long,
    @ColumnInfo(name = "regDate")
    var regDate:String
)