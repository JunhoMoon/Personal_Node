package com.hims.personal_node.Activitys

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hims.personal_node.Activitys.Fragment.Health.HealthAuthority
import com.hims.personal_node.Activitys.Fragment.Health.HealthList
import com.hims.personal_node.Activitys.Fragment.Health.HealthMessage

class HealthPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, var pagerNo:Int, var node_kn:String, var mContext: Context) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HealthList(node_kn, mContext)
            1 -> HealthAuthority(node_kn, mContext)
            2 -> HealthMessage(node_kn, mContext)
            else -> HealthList(node_kn, mContext)
        }
    }

    override fun getItemCount(): Int {
        return pagerNo
    }
}