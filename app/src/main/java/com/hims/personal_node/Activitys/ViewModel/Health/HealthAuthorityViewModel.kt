package com.hims.personal_node.Activitys.ViewModel.Health

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hims.personal_node.DataMamager.Repository.Health.HealthAuthorityRepository
import com.hims.personal_node.DataMamager.Repository.Health.HealthMessageRepository
import com.hims.personal_node.HIMSDB
import io.reactivex.disposables.CompositeDisposable

class HealthAuthorityViewModel (application: Application, node_kn:String): AndroidViewModel(application){
    val healthAuthorityRepository: HealthAuthorityRepository
    val healtMessageRepository: HealthMessageRepository

    init {
        val himsdb = HIMSDB.getInstance(application, node_kn)!!
        healthAuthorityRepository = HealthAuthorityRepository(himsdb.healthAuthorityDAO())
        healtMessageRepository = HealthMessageRepository(himsdb.healthMessageDAO())
    }
}

class HealthAuthorityFactory(private val mApplication: Application, private val node_kn: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HealthAuthorityViewModel(mApplication, node_kn) as T
    }
}