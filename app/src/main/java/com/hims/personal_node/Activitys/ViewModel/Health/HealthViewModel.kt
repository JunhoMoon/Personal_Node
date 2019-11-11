package com.hims.personal_node.Activitys.ViewModel.Health

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.hims.personal_node.DataMamager.Repository.Health.*
import com.hims.personal_node.HIMSDB
import com.hims.personal_node.Model.Health.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HealthViewModel (application: Application, node_kn:String): AndroidViewModel(application){
    private val healthAuthorityRepository:HealthAuthorityRepository
    private val healthDetailRepository: HealthDetailRepository
    private val healthHistoryRepository: HealthHistoryRepository
//    private val healthNotaryRepository: HealthNotaryRepository
    private val healthMessageRepository:HealthMessageRepository
    private val healthRepository:HealthRepository

    val allHealthAuthority: LiveData<List<HealthAuthority>>
    val allHealthDetail: LiveData<List<HealthDetail>>
    val allHealthHistory: LiveData<List<HealthHistory>>
//    val allHealthNotary: LiveData<List<HealthNotary>>
    val allHealthMessage: LiveData<List<HealthMessage>>
    val allHealth: LiveData<List<Health>>

    init {
        val himsdb = HIMSDB.getInstance(application, node_kn)!!
        healthAuthorityRepository = HealthAuthorityRepository(himsdb.healthAuthorityDAO())
        healthDetailRepository = HealthDetailRepository(himsdb.healthDetailDAO())
        healthHistoryRepository = HealthHistoryRepository(himsdb.healthHistoryDAO())
//        healthNotaryRepository = HealthNotaryRepository(himsdb.healthNotaryDAO())
        healthMessageRepository = HealthMessageRepository(himsdb.healthMessageDAO())
        healthRepository = HealthRepository(himsdb.healthDAO())

        allHealthAuthority = healthAuthorityRepository.getall
        allHealthDetail = healthDetailRepository.getall
        allHealthHistory = healthHistoryRepository.getall
//        allHealthNotary = healthNotaryRepository.getall
        allHealthMessage = healthMessageRepository.getall
        allHealth = healthRepository.getall
    }
}

class HealthViewModelFactory(private val mApplication: Application, private val node_kn: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HealthViewModel(mApplication, node_kn) as T
    }
}