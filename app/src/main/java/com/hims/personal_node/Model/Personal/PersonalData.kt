package com.hims.personal_node.Model.Personal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PersonalData")
data class PersonalData(
    @PrimaryKey
    @ColumnInfo(name = "node_kn")
    var node_kn:String,
    @ColumnInfo(name = "gender")
    var gender: String?,
    @ColumnInfo(name = "birth")
    var birth: String?,
    @ColumnInfo(name = "race")
    var race: String?,
    @ColumnInfo(name = "address")
    var address: String?,
    @ColumnInfo(name = "job")
    var job: String?,
    @ColumnInfo(name = "income")
    var income: Int?,
    @ColumnInfo(name = "education")
    var education: String?
)