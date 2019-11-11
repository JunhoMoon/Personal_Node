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
    @ColumnInfo(name = "country")
    var country: String?,
    @ColumnInfo(name = "admin")
    var admin: String?,
    @ColumnInfo(name = "city")
    var city: String?,
    @ColumnInfo(name = "job1")
    var job1: String?,
    @ColumnInfo(name = "job2")
    var job2: String?,
    @ColumnInfo(name = "job3")
    var job3: String?,
    @ColumnInfo(name = "job4")
    var job4: String?,
    @ColumnInfo(name = "income")
    var income: Int?,
    @ColumnInfo(name = "education")
    var education: String?
)