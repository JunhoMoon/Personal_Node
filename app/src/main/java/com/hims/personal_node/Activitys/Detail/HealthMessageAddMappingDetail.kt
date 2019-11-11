package com.hims.personal_node.Activitys.Detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.hims.personal_node.Activitys.ViewModel.Health.HealthAuthorityFactory
import com.hims.personal_node.Activitys.ViewModel.Health.HealthAuthorityViewModel
import com.hims.personal_node.DataMamager.DeviceDB
import com.hims.personal_node.Messaging.NullOnEmptyConverterFactory
import com.hims.personal_node.Model.Health.HealthAuthority
import com.hims.personal_node.R
import com.hims.personal_node.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class HealthMessageAddMappingDetail : AppCompatActivity() {
    private lateinit var healthAuthorityViewModel: HealthAuthorityViewModel
    private lateinit var server: RetrofitService
    private var deviceDB: DeviceDB? = null
    val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_message_add_mapping_detail)
        deviceDB = DeviceDB.getInstance(this)

        var node_kn:String
        var message_id:Long
        if (intent.hasExtra("node_kn") && intent.hasExtra("message_id")){
            message_id = intent.getStringExtra("message_id").toLong()
            node_kn = intent.getStringExtra("node_kn")


            var health_message_add_mapping_detail_nodekn: TextView = findViewById<TextView>(R.id.health_message_add_mapping_detail_nodekn)
            var health_message_add_mapping_detail_nodename:TextView = findViewById<TextView>(R.id.health_message_add_mapping_detail_nodename)
            var health_message_add_mapping_detail_patientno:TextView = findViewById<TextView>(R.id.health_message_add_mapping_detail_patientno)
            var health_message_add_mapping_detail_noderagdate:TextView = findViewById<TextView>(R.id.health_message_add_mapping_detail_noderagdate)
            var health_message_add_mapping_detail_accept_bt:Button = findViewById<Button>(R.id.health_message_add_mapping_detail_accept_bt)
            var health_message_add_mapping_detail_reject_bt:Button = findViewById<Button>(R.id.health_message_add_mapping_detail_reject_bt)

            var healthAuthority : HealthAuthority? = null

            healthAuthorityViewModel = ViewModelProvider(this, HealthAuthorityFactory(this.application, node_kn)).get(HealthAuthorityViewModel::class.java)

            var addRunnable = Runnable {
                var healthMessage = healthAuthorityViewModel.healtMessageRepository.getByID(message_id)

                healthAuthority = gson.fromJson(healthMessage.message, HealthAuthority::class.java)

                health_message_add_mapping_detail_nodekn.setText(healthAuthority!!.node_kn)
                health_message_add_mapping_detail_nodename.setText(healthAuthority!!.node_name)
                health_message_add_mapping_detail_patientno.setText(healthAuthority!!.patient_no)
                health_message_add_mapping_detail_noderagdate.setText(healthAuthority!!.reg_date)
            }
            var addThread = Thread(addRunnable)
            addThread.start()

            health_message_add_mapping_detail_accept_bt.setOnClickListener {
                addRunnable = Runnable {
                    healthAuthorityViewModel.healthAuthorityRepository.insert(healthAuthority!!)
                    healthAuthorityViewModel.healtMessageRepository.deleteByID(message_id)
                    var deviceUser = deviceDB?.DeviceUserDAO()?.getByNodeKN(node_kn)

                    var retrofit = Retrofit.Builder()
                        .baseUrl(getString(R.string.HIMS_Server_AP))
                        .addConverterFactory(NullOnEmptyConverterFactory())
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    server = retrofit.create(RetrofitService::class.java)
                    server.getNodeAP(deviceUser?.user_kn!!, deviceUser?.cert_key!!, healthAuthority?.node_kn!!)?.enqueue(object : Callback<String?> {
                        override fun onFailure(call: Call<String?>, t: Throwable) {
                        }
                        override fun onResponse(call: Call<String?>, response: Response<String?>) {
                            var target_ap = response.body()
                            var server1: RetrofitService
                            var retrofit1 = Retrofit.Builder()
                                .baseUrl("http://"+target_ap)
                                .addConverterFactory(NullOnEmptyConverterFactory())
                                .addConverterFactory(ScalarsConverterFactory.create())
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()
                            server1 = retrofit1.create(RetrofitService::class.java)

                            server1.acceptNodeMapping(deviceUser.user_kn, deviceUser.cert_key!!)?.enqueue(object : Callback<Void?> {
                                override fun onFailure(call: Call<Void?>, t: Throwable) {
                                }
                                override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                                }
                            })
                        }
                    })
                }
                addThread = Thread(addRunnable)
                addThread.start()
                finish()
            }

            health_message_add_mapping_detail_reject_bt.setOnClickListener {
                addRunnable = Runnable {
                    healthAuthorityViewModel.healtMessageRepository.deleteByID(message_id)

                    var deviceUser = deviceDB?.DeviceUserDAO()?.getByNodeKN(node_kn)

                    var retrofit = Retrofit.Builder()
                        .baseUrl(getString(R.string.HIMS_Server_AP))
                        .addConverterFactory(NullOnEmptyConverterFactory())
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    server = retrofit.create(RetrofitService::class.java)
                    server.getNodeAP(deviceUser?.user_kn!!, deviceUser?.cert_key!!, healthAuthority?.node_kn!!)?.enqueue(object : Callback<String?> {
                        override fun onFailure(call: Call<String?>, t: Throwable) {
                        }
                        override fun onResponse(call: Call<String?>, response: Response<String?>) {
                            var target_ap = response.body()
                            var server1: RetrofitService
                            var retrofit1 = Retrofit.Builder()
                                .baseUrl("http://"+target_ap)
                                .addConverterFactory(NullOnEmptyConverterFactory())
                                .addConverterFactory(ScalarsConverterFactory.create())
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()
                            server1 = retrofit1.create(RetrofitService::class.java)

                            server1.rejectNodeMapping(deviceUser.user_kn, deviceUser.cert_key!!)?.enqueue(object : Callback<Void?> {
                                override fun onFailure(call: Call<Void?>, t: Throwable) {
                                }
                                override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                                }
                            })
                        }
                    })
                }
                addThread = Thread(addRunnable)
                addThread.start()
                finish()
            }
        }else{
            finish()
        }
    }
}
