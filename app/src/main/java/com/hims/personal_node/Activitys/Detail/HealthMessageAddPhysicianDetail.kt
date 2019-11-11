package com.hims.personal_node.Activitys.Detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.hims.personal_node.Activitys.ViewModel.Health.HealthAuthorityFactory
import com.hims.personal_node.Activitys.ViewModel.Health.HealthAuthorityViewModel
import com.hims.personal_node.Activitys.ViewModel.Health.PrimaryPhysicianFactory
import com.hims.personal_node.Activitys.ViewModel.Health.PrimaryPhysicianViewModel
import com.hims.personal_node.DataMamager.DeviceDB
import com.hims.personal_node.Messaging.NullOnEmptyConverterFactory
import com.hims.personal_node.Model.Health.HealthAuthority
import com.hims.personal_node.Model.Health.PrimaryPhysician
import com.hims.personal_node.R
import com.hims.personal_node.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class HealthMessageAddPhysicianDetail : AppCompatActivity() {
    private lateinit var primaryPhysicianViewModel: PrimaryPhysicianViewModel
    private lateinit var server: RetrofitService
    private var deviceDB: DeviceDB? = null
    val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_message_add_physician_detail)
        deviceDB = DeviceDB.getInstance(this)

        var node_kn:String
        var message_id:Long
        if (intent.hasExtra("node_kn") && intent.hasExtra("message_id")){
            message_id = intent.getStringExtra("message_id").toLong()
            node_kn = intent.getStringExtra("node_kn")


            var health_message_add_physician_detail_nodekn: TextView = findViewById<TextView>(R.id.health_message_add_physician_detail_nodekn)
            var health_message_add_physician_detail_primaryPhysician_id: TextView = findViewById<TextView>(R.id.health_message_add_physician_detail_primaryPhysician_id)
            var health_message_add_physician_detail_physician_name: TextView = findViewById<TextView>(R.id.health_message_add_physician_detail_physician_name)
            var health_message_add_physician_detail_noderagdate: TextView = findViewById<TextView>(R.id.health_message_add_physician_detail_noderagdate)
            var health_message_add_physician_detail_accept_bt: Button = findViewById<Button>(R.id.health_message_add_physician_detail_accept_bt)
            var health_message_add_physician_detail_reject_bt: Button = findViewById<Button>(R.id.health_message_add_physician_detail_reject_bt)

            var primaryPhysician:PrimaryPhysician? = null

            primaryPhysicianViewModel = ViewModelProvider(this, PrimaryPhysicianFactory(this.application, node_kn)).get(
                PrimaryPhysicianViewModel::class.java)

            var addRunnable = Runnable {
                var healthMessage = primaryPhysicianViewModel.healtMessageRepository.getByID(message_id)

                primaryPhysician = gson.fromJson(healthMessage.message, PrimaryPhysician::class.java)

                health_message_add_physician_detail_nodekn.setText(primaryPhysician!!.node_kn)
                health_message_add_physician_detail_primaryPhysician_id.setText(primaryPhysician!!.primaryPhysician_id)
                health_message_add_physician_detail_physician_name.setText(primaryPhysician!!.primaryPhysician_name)
                health_message_add_physician_detail_noderagdate.setText(primaryPhysician!!.reg_date)
            }
            var addThread = Thread(addRunnable)
            addThread.start()

            health_message_add_physician_detail_accept_bt.setOnClickListener {
                addRunnable = Runnable {
                    primaryPhysicianViewModel.primaryPhysicianRepository.insert(primaryPhysician!!)
                    primaryPhysicianViewModel.healtMessageRepository.deleteByID(message_id)
                    var deviceUser = deviceDB?.DeviceUserDAO()?.getByNodeKN(node_kn)

                    var retrofit = Retrofit.Builder()
                        .baseUrl(getString(R.string.HIMS_Server_AP))
                        .addConverterFactory(NullOnEmptyConverterFactory())
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    server = retrofit.create(RetrofitService::class.java)
                    server.getNodeAP(deviceUser?.user_kn!!, deviceUser?.cert_key!!, primaryPhysician?.node_kn!!)?.enqueue(object :
                        Callback<String?> {
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

                            server1.acceptPrimaryPhysician(deviceUser.user_kn, deviceUser.cert_key!!, primaryPhysician!!.primaryPhysician_id)?.enqueue(object :
                                Callback<Void?> {
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

            health_message_add_physician_detail_reject_bt.setOnClickListener {
                addRunnable = Runnable {
                    primaryPhysicianViewModel.healtMessageRepository.deleteByID(message_id)

                    var deviceUser = deviceDB?.DeviceUserDAO()?.getByNodeKN(node_kn)

                    var retrofit = Retrofit.Builder()
                        .baseUrl(getString(R.string.HIMS_Server_AP))
                        .addConverterFactory(NullOnEmptyConverterFactory())
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    server = retrofit.create(RetrofitService::class.java)
                    server.getNodeAP(deviceUser?.user_kn!!, deviceUser?.cert_key!!, primaryPhysician?.node_kn!!)?.enqueue(object :
                        Callback<String?> {
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

                            server1.rejectPrimaryPhysician(deviceUser.user_kn, deviceUser.cert_key!!, primaryPhysician!!.primaryPhysician_id)?.enqueue(object :
                                Callback<Void?> {
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
