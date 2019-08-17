package com.hims.personal_node.DataMamager

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.hims.personal_node.BaseDao
import com.hims.personal_node.Model.Personal.PersonalData

@Dao
interface PersonalDataDAO:BaseDao<PersonalData>{
    @Query("SELECT * FROM PersonalData")
    fun getall() : LiveData<List<PersonalData>>

    @Query("SELECT * FROM PersonalData where node_kn like :node_kn")
    fun getByNodeKn(node_kn:String) : PersonalData

    @Query("SELECT count(*) FROM PersonalData")
    fun checkPersonalData() : Int
}