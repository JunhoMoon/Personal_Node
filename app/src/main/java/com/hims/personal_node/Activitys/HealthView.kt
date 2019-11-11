package com.hims.personal_node

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import kotlinx.android.synthetic.main.activity_health_view.*
import com.hims.personal_node.DataMamager.DeviceDB
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import android.graphics.PorterDuff
import androidx.viewpager2.widget.ViewPager2
import com.hims.personal_node.Activitys.HealthPagerAdapter
//import com.hims.personal_node.Model.Health.Health

class HealthView : AppCompatActivity() {

    var server: RetrofitService? = null
    private var deviceDB: DeviceDB? = null
    private var himsdb: HIMSDB? = null

    private var context: Context? = null
    var node_kn: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_view)

        deviceDB = DeviceDB.getInstance(this)
        context = applicationContext

        setSupportActionBar(toolbar)

        if (intent.hasExtra("node_kn")) {
            node_kn = intent.getStringExtra("node_kn")
            himsdb = HIMSDB.getInstance(this, node_kn!!)
        } else {
            finish()
        }

//        var healths:List<Health>? = null
//        var check:Boolean = false
//
//        val addRunnable = Runnable{
//            var health = Health(null, "test111", "testset", 1)
//            himsdb?.healthDAO()?.insert(health)
//            himsdb?.healthDAO()?.insert(health)
//            himsdb?.healthDAO()?.insert(health)
//            himsdb?.healthDAO()?.insert(health)
//            himsdb?.healthDAO()?.insert(health)
//            himsdb?.healthDAO()?.insert(health)
//            himsdb?.healthDAO()?.insert(health)
//            himsdb?.healthDAO()?.insert(health)
//            himsdb?.healthDAO()?.insert(health)
//            himsdb?.healthDAO()?.insert(health)
//            himsdb?.healthDAO()?.insert(health)
//        }
//        val addThread = Thread(addRunnable)
//        addThread.start()
//
//        do {
//            if (healths!=null){
//                for(health in healths!!){
//                    println("health : $health")
//                }
//            }
//        }while (!check)

        val actionBar = supportActionBar

        supportActionBar?.title = "ID : $node_kn"
        actionBar?.subtitle = "Point : "
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setDisplayUseLogoEnabled(true)

        tab_layout!!.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

                override fun onTabSelected(tab: TabLayout.Tab) {
                    pager!!.currentItem = tab.position
                    var color: Int = Color.parseColor("#ffffff")
                    tab.icon!!.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    var color: Int = Color.parseColor("#000000")
                    tab!!.icon!!.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                }
            }
        )
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tab_layout.getTabAt(position)?.select()
            }
        })
        tab_layout!!.addTab(tab_layout!!.newTab().setIcon(R.drawable.ic_folder_open_black_24dp), true)
        tab_layout!!.addTab(tab_layout!!.newTab().setIcon(R.drawable.ic_lock_outline_black_24dp))
        tab_layout!!.addTab(tab_layout!!.newTab().setIcon(R.drawable.ic_outline_email_24px))
        pager.adapter = HealthPagerAdapter(supportFragmentManager, lifecycle, tab_layout.tabCount, node_kn!!, this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.health_view_menu, menu)       // main_menu 메뉴를 toolbar 메뉴 버튼으로 설정
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.health_data_view -> {
//                var intent = Intent(this@HealthView, CreatePrivateInformation::class.java)
//                intent.putExtra("node_kn", "asdf")
//                startActivity(intent)
//                finish()
                true
            }
            R.id.account_data_view -> {
                messageToast("test1")
                true
            }
            R.id.action_personal_data -> {
                messageToast("test2")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun messageToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}