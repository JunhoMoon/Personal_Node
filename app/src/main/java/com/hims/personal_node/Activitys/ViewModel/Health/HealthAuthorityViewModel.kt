package com.hims.personal_node.Activitys.ViewModel.Health

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.reactivex.disposables.CompositeDisposable

class HealthAuthorityViewModel (application: Application): AndroidViewModel(application){
    private val disposable: CompositeDisposable = CompositeDisposable()
}