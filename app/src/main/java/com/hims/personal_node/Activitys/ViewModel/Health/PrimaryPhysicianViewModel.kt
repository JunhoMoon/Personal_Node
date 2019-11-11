package com.hims.personal_node.Activitys.ViewModel.Health

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.hims.personal_node.DataMamager.Repository.Health.*
import com.hims.personal_node.HIMSDB
import com.hims.personal_node.Model.Health.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PrimaryPhysicianViewModel (application: Application, node_kn:String): AndroidViewModel(application){
    val healthAuthorityRepository:HealthAuthorityRepository
    val primaryPhysicianRepository:PrimaryPhysicianRepository
    val healtMessageRepository: HealthMessageRepository

//    val healthAuthority: HealthAuthority
//    val allPrimaryPhysician : LiveData<List<PrimaryPhysician>>

    init {
        val himsdb = HIMSDB.getInstance(application, node_kn)!!
        healthAuthorityRepository = HealthAuthorityRepository(himsdb.healthAuthorityDAO())
        primaryPhysicianRepository = PrimaryPhysicianRepository(himsdb.primaryPhysicianDAO())
        healtMessageRepository = HealthMessageRepository(himsdb.healthMessageDAO())

//        healthAuthority = healthAuthorityRepository.getByNodeKn(target)
//        allPrimaryPhysician = primaryPhysicianRepository.getall(target)
    }
}

class PrimaryPhysicianFactory(private val mApplication: Application, private val node_kn: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PrimaryPhysicianViewModel(mApplication, node_kn) as T
    }
}