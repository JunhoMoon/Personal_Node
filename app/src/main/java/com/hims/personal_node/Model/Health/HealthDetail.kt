package com.hims.personal_node.Model.Health

import androidx.room.*
import com.hims.personal_node.Model.Health.Health

@Entity(
    tableName = "HealthDetail",
    primaryKeys = ["health_no", "health_detail_no"],
    foreignKeys = [ForeignKey(
        entity = Health::class,
        parentColumns = arrayOf("subject_health_no"),
        childColumns = arrayOf("health_no")
    )]
)
data class HealthDetail(
    @ColumnInfo(name = "health_no")
    var health_no: Long,
    @ColumnInfo(name = "health_detail_no")
    var health_detail_no: Long,
    @ColumnInfo(name = "data_name")
    var data_name:String?,
    @ColumnInfo(name = "data_type")
    var data_type:String?,
    @ColumnInfo(name = "data_text_value")
    var data_text_value: String?,
    @ColumnInfo(name = "data_num_value")
    var data_num_value:Double?
)