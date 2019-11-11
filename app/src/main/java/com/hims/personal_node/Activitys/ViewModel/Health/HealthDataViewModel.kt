package com.hims.personal_node.Activitys.ViewModel.Health

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.hims.personal_node.DataMamager.Repository.Health.*
import com.hims.personal_node.HIMSDB
import com.hims.personal_node.Model.Health.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HealthDataViewModel (application: Application, node_kn:String): AndroidViewModel(application){
    val healthAuthorityRepository:HealthAuthorityRepository
    val primaryPhysicianRepository:PrimaryPhysicianRepository
    val healtMessageRepository: HealthMessageRepository
    val healthRepository: HealthRepository
    val healthDetailRepository: HealthDetailRepository
    var healthNotaryRepository:HealthNotaryRepository

//    val healthAuthority: HealthAuthority
//    val allPrimaryPhysician : LiveData<List<PrimaryPhysician>>

    init {
        val himsdb = HIMSDB.getInstance(application, node_kn)!!
        healthAuthorityRepository = HealthAuthorityRepository(himsdb.healthAuthorityDAO())
        primaryPhysicianRepository = PrimaryPhysicianRepository(himsdb.primaryPhysicianDAO())
        healtMessageRepository = HealthMessageRepository(himsdb.healthMessageDAO())
        healthRepository = HealthRepository(himsdb.healthDAO())
        healthDetailRepository = HealthDetailRepository(himsdb.healthDetailDAO())
        healthNotaryRepository = HealthNotaryRepository(himsdb.healthNotaryDAO())

//        healthAuthority = healthAuthorityRepository.getByNodeKn(target)
//        allPrimaryPhysician = primaryPhysicianRepository.getall(target)
    }
}

class HealthDataFactory(private val mApplication: Application, private val node_kn: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HealthDataViewModel(mApplication, node_kn) as T
    }
}