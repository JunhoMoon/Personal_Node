package com.hims.personal_node.Activitys.Fragment.Health

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hims.personal_node.Activitys.Adaoter.HealthAuthorityRecycleAdapter
import com.hims.personal_node.Activitys.ViewModel.Health.HealthViewModel
import com.hims.personal_node.Activitys.ViewModel.Health.HealthViewModelFactory
import com.hims.personal_node.R

class HealthAuthority(var node_kn:String, var mContext: Context) : Fragment() {
    private lateinit var healthViewModel: HealthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_health_authority, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity = activity as Context
        val recyclerView = view?.findViewById<RecyclerView>(R.id.health_authority_recycle_form)
        var adapter = HealthAuthorityRecycleAdapter(mContext, node_kn)
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(activity)

        healthViewModel = ViewModelProvider(this, HealthViewModelFactory(getActivity()?.application!!, node_kn)).get(HealthViewModel::class.java)
        healthViewModel.allHealthAuthority.observe(this, androidx.lifecycle.Observer { healths ->
            healths?.let { adapter.setHealth(it) }
        })
    }
}
