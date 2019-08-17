package com.hims.personal_node.Model.Health

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Health",
    indices = [Index(value = arrayOf("_id"), unique = true)])
data class Health(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var _id: Long?,
    @ColumnInfo(name = "provider_kn")
    var provider_kn:String,
    @ColumnInfo(name = "genDate")
    var genDate: String,
    @ColumnInfo(name = "sLevel")
    var sLevel:Int = 0
)