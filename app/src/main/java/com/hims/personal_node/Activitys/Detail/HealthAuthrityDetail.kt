package com.hims.personal_node.Activitys.Detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Switch
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hims.personal_node.Activitys.Adaoter.HealthAuthorityDetailRecycleAdapter
import com.hims.personal_node.Activitys.ViewModel.Health.PrimaryPhysicianFactory
import com.hims.personal_node.Activitys.ViewModel.Health.PrimaryPhysicianViewModel
import com.hims.personal_node.Model.Health.HealthAuthority
import com.hims.personal_node.R

class HealthAuthrityDetail : AppCompatActivity() {
    private lateinit var primaryPhysicianViewModel: PrimaryPhysicianViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_authrity_detail)

        var node_kn:String
        var target:String
        if (intent.hasExtra("node_kn") && intent.hasExtra("target")){
            target = intent.getStringExtra("target")
            node_kn = intent.getStringExtra("node_kn")

            var health_authrity_detail_nodekn:TextView = findViewById<TextView>(R.id.health_authrity_detail_nodekn)
            var health_authrity_detail_nodename:TextView = findViewById<TextView>(R.id.health_authrity_detail_nodename)
            var health_authrity_detail_patientno:TextView = findViewById<TextView>(R.id.health_authrity_detail_patientno)
            var health_authrity_detail_noderagdate:TextView = findViewById<TextView>(R.id.health_authrity_detail_noderagdate)
            var health_authrity_detail_node_write_bt:Switch = findViewById<Switch>(R.id.health_authrity_detail_node_write_bt)
            var health_authrity_detail_node_read_bt:Switch = findViewById<Switch>(R.id.health_authrity_detail_node_read_bt)
            val recyclerView = findViewById<RecyclerView>(R.id.recycler_health_authority_detail_form)

            var adapter = HealthAuthorityDetailRecycleAdapter(this, node_kn)
            recyclerView?.adapter = adapter
            recyclerView?.layoutManager = LinearLayoutManager(this)

            primaryPhysicianViewModel = ViewModelProvider(this, PrimaryPhysicianFactory(this.application, node_kn)).get(PrimaryPhysicianViewModel::class.java)

            var healthAuthority : HealthAuthority? = null

            var addRunnable = Runnable {
                Looper.prepare()
                healthAuthority = primaryPhysicianViewModel.healthAuthorityRepository.getByNodeKn(target)

                health_authrity_detail_nodekn.setText(healthAuthority!!.node_kn)
                health_authrity_detail_nodename.setText(healthAuthority!!.node_name)
                health_authrity_detail_patientno.setText(healthAuthority!!.patient_no)
                health_authrity_detail_noderagdate.setText(healthAuthority!!.reg_date)
                health_authrity_detail_node_write_bt.isChecked = healthAuthority!!.record_auth == 1
                health_authrity_detail_node_read_bt.isChecked = healthAuthority!!.read_auth == 1

                adapter.setPrimaryPhysician(primaryPhysicianViewModel.primaryPhysicianRepository.getall2(target))
                Looper.loop()
            }
            var addThread = Thread(addRunnable)
            addThread.start()

            health_authrity_detail_node_write_bt.setOnClickListener {
                if (health_authrity_detail_node_write_bt.isChecked){
                    healthAuthority!!.record_auth = 1
                }else{
                    healthAuthority!!.record_auth = 0
                }
                addRunnable = Runnable {
                    primaryPhysicianViewModel.healthAuthorityRepository.update(healthAuthority!!)
                }
                addThread = Thread(addRunnable)
                addThread.start()
            }

            health_authrity_detail_node_read_bt.setOnClickListener {
                if (health_authrity_detail_node_read_bt.isChecked){
                    healthAuthority!!.read_auth = 1
                }else{
                    healthAuthority!!.read_auth = 0
                }
                addRunnable = Runnable {
                    primaryPhysicianViewModel.healthAuthorityRepository.update(healthAuthority!!)
                }
                addThread = Thread(addRunnable)
                addThread.start()
            }
        }else{
            finish()
        }
    }
}
