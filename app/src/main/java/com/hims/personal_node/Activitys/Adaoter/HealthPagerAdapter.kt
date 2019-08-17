package com.hims.personal_node.Activitys

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hims.personal_node.Activitys.Fragment.Health.HealthAuthority
import com.hims.personal_node.Activitys.Fragment.Health.HealthList
import com.hims.personal_node.Activitys.Fragment.Health.HealthHistory
import com.hims.personal_node.Activitys.Fragment.Health.HealthMessage

class HealthPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, var pagerNo:Int) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HealthList()
            1 -> HealthAuthority()
            2 -> HealthMessage()
            3 -> HealthHistory()
            else -> HealthList()
        }
    }

    override fun getItemCount(): Int {
        return pagerNo
    }
}