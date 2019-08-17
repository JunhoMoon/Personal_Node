package com.hims.personal_node.DataMamager.Repository.PersonalData

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.hims.personal_node.DataMamager.PersonalDataDAO
import com.hims.personal_node.Model.Personal.PersonalData

class PersonalDataRepository (private val personalDataDAO: PersonalDataDAO){
    val getall: LiveData<List<PersonalData>> = personalDataDAO.getall()
    @WorkerThread
    suspend fun getByNodeKn(node_kn:String) : PersonalData{
        return personalDataDAO.getByNodeKn(node_kn)
    }
    @WorkerThread
    suspend fun checkPersonalData(node_kn:String) : Int{
        return personalDataDAO.checkPersonalData()
    }
    @WorkerThread
    suspend fun insert(personalData: PersonalData){
        personalDataDAO.insert(personalData)
    }
    @WorkerThread
    suspend fun update(personalData: PersonalData){
        personalDataDAO.update(personalData)
    }
    @WorkerThread
    suspend fun delete(personalData: PersonalData){
        personalDataDAO.delete(personalData)
    }
}