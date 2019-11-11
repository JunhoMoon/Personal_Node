package com.hims.personal_node.Activitys.Detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.hims.personal_node.Activitys.ViewModel.Health.HealthDataFactory
import com.hims.personal_node.Activitys.ViewModel.Health.HealthDataViewModel
import com.hims.personal_node.DataMamager.DeviceDB
import com.hims.personal_node.EncryptionSHA
import com.hims.personal_node.HIMSDB
import com.hims.personal_node.Messaging.NullOnEmptyConverterFactory
import com.hims.personal_node.Model.Health.*
import com.hims.personal_node.Model.Message.NotaryData
import com.hims.personal_node.Model.ParsingJSON
import com.hims.personal_node.R
import com.hims.personal_node.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class HealthMessageAddHealthDetail : AppCompatActivity() {
    private lateinit var healthDataViewModel: HealthDataViewModel
    private lateinit var server: RetrofitService
    private var deviceDB: DeviceDB? = null
    private var himsdb: HIMSDB? = null
    val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_message_add_health_detail)
        deviceDB = DeviceDB.getInstance(this)

        var node_kn:String
        var message_id:Long
        if (intent.hasExtra("node_kn") && intent.hasExtra("message_id")){
            message_id = intent.getStringExtra("message_id").toLong()
            node_kn = intent.getStringExtra("node_kn")

            himsdb = HIMSDB.getInstance(this, node_kn)


            var health_message_add_health_detail_nodekn: TextView = findViewById(R.id.health_message_add_health_detail_nodekn)
            var health_message_add_health_detail_patientno: TextView = findViewById(R.id.health_message_add_health_detail_patientno)
            var health_message_add_health_detail_noderagdate: TextView = findViewById(R.id.health_message_add_health_detail_noderagdate)
            var health_message_add_health_detail_physician_id: TextView = findViewById(R.id.health_message_add_health_detail_physician_id)
            var health_message_add_health_detail_details: LinearLayout = findViewById(R.id.health_message_add_health_detail_details)
            var health_message_add_health_detail_accept_bt: Button = findViewById(R.id.health_message_add_health_detail_accept_bt)
            var health_message_add_health_detail_reject_bt: Button = findViewById(R.id.health_message_add_health_detail_reject_bt)

            var health : Health? = null
            var healthMessage: HealthMessage? = null
            var details:MutableList<HealthDetail> = mutableListOf()

            healthDataViewModel = ViewModelProvider(this, HealthDataFactory(this.application, node_kn)).get(
                HealthDataViewModel::class.java)

            var addRunnable = Runnable {
                healthMessage = healthDataViewModel.healtMessageRepository.getByID(message_id)

                var jsonObj = JsonParser().parse(healthMessage!!.message) as JsonObject
                health = gson.fromJson(jsonObj.get("health"), Health::class.java)

                for(item in jsonObj.getAsJsonArray("healthDetail")){
                    var detail = gson.fromJson(item, HealthDetail::class.java)
                    details.add(detail)
                }

                health_message_add_health_detail_nodekn.setText(health!!.node_kn)
                health_message_add_health_detail_patientno.setText(health!!.patient_no)
                health_message_add_health_detail_noderagdate.setText(health!!.reg_date)
                health_message_add_health_detail_physician_id.setText(health!!.physician_id)

                for (detail in details!!){
                    var data_name = TextView(this)
                    data_name.setText("Data Name : "+ detail.data_name)
                    var data_value:TextView
                    if (detail.data_type.equals("Number")){
                        data_value = TextView(this)
                        data_value.setText("Data Value : "+ detail.data_num_value.toString())
                    }else{
                        data_value = TextView(this)
                        data_value.setText("Data Value : "+ detail.data_text_value)
                    }
                    var ll = LinearLayout(this)
                    ll.orientation = LinearLayout.HORIZONTAL
                    ll.addView(data_name)
                    ll.addView(data_value)
                    health_message_add_health_detail_details.addView(ll)
                }
            }
            var addThread = Thread(addRunnable)
            addThread.start()

            health_message_add_health_detail_accept_bt.setOnClickListener {
                addRunnable = Runnable {
                    health?.node_kn = healthMessage?.sender!!

                    var rowNum:Long = if (himsdb?.healthDAO()?.getall2() != null){
                        himsdb?.healthDAO()?.getall2()?.size!!.toLong()
                    }else{
                        0
                    }

                    health?.subject_health_no = rowNum + 1
                    healthDataViewModel.healthRepository.insert(health!!)
                    for (detail in details!!){
                        detail.health_no = health?.subject_health_no!!
                        healthDataViewModel?.healthDetailRepository?.insert(detail)
                    }
                    healthDataViewModel.healtMessageRepository.deleteByID(message_id)
                    var deviceUser = deviceDB?.DeviceUserDAO()?.getByNodeKN(node_kn)

                    var retrofit = Retrofit.Builder()
                        .baseUrl(getString(R.string.HIMS_Server_AP))
                        .addConverterFactory(NullOnEmptyConverterFactory())
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    server = retrofit.create(RetrofitService::class.java)

                    var healthNotarys: MutableList<HealthNotary> = mutableListOf()

                    server.getNotaryNodeAP(deviceUser?.user_kn!!, deviceUser?.cert_key!!, healthMessage?.sender!!)?.enqueue(object :
                        Callback<MutableList<NodeInfo>?> {
                        override fun onFailure(call: Call<MutableList<NodeInfo>?>, t: Throwable) {
                        }
                        override fun onResponse(call: Call<MutableList<NodeInfo>?>, response: Response<MutableList<NodeInfo>?>) {
                            var notarys = response.body()
                            var sha = EncryptionSHA.encryptSha(gson.toJson(details))
                            for (notary in notarys!!){
                                println(notary.node_ap)
                                var server1: RetrofitService
                                var retrofit1 = Retrofit.Builder()
                                    .baseUrl("http://"+notary.node_ap)
                                    .addConverterFactory(NullOnEmptyConverterFactory())
                                    .addConverterFactory(ScalarsConverterFactory.create())
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build()
                                server1 = retrofit1.create(RetrofitService::class.java)

                                server1.addNotary(deviceUser?.user_kn!!, deviceUser?.cert_key!!, sha)?.enqueue(object :
                                    Callback<NotaryData?> {
                                    override fun onFailure(call: Call<NotaryData?>, t: Throwable) {
                                    }
                                    override fun onResponse(call: Call<NotaryData?>, response: Response<NotaryData?>) {
                                        var notaryData = response.body()
                                        var healthNotary = HealthNotary(health!!.subject_health_no!!, notary.node_kn!!, notaryData?.notary_data_no!!, notaryData?.reg_date.toString()!!)
                                        healthNotarys.add(healthNotary)
                                        var addRunnable1 = Runnable {
                                            healthDataViewModel.healthNotaryRepository.insert(healthNotary)
                                        }
                                        var addThread1 = Thread(addRunnable1)
                                        addThread1.start()
                                    }
                                })
                            }

                            server.updateLastHealthNo(deviceUser.user_kn, deviceUser.cert_key!!, health?.subject_health_no!!)?.enqueue(object :
                                Callback<Void?> {
                                override fun onFailure(call: Call<Void?>, t: Throwable) {
                                }
                                override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                                }
                            })

                            Thread.sleep(1000)
                            server.getNodeAP(deviceUser?.user_kn!!, deviceUser?.cert_key!!, healthMessage?.sender!!)?.enqueue(object :
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

                                    println(healthNotarys.toString())
                                    println(ParsingJSON.modelToJson(healthNotarys))
                                    var notaryDatas = ParsingJSON.modelToJson(healthNotarys)
                                    notaryDatas.replace("\"", "")
                                    server1.acceptHealth(deviceUser.user_kn, deviceUser.cert_key!!, health?.issuer_health_no!!, health?.subject_health_no!!, notaryDatas)?.enqueue(object :
                                        Callback<Void?> {
                                        override fun onFailure(call: Call<Void?>, t: Throwable) {
                                        }
                                        override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                                        }
                                    })
                                }
                            })
                        }
                    })
                }
                addThread = Thread(addRunnable)
                addThread.start()
                finish()
            }

            health_message_add_health_detail_reject_bt.setOnClickListener {
                addRunnable = Runnable {
                    healthDataViewModel.healtMessageRepository.deleteByID(message_id)

                    var deviceUser = deviceDB?.DeviceUserDAO()?.getByNodeKN(node_kn)

                    var retrofit = Retrofit.Builder()
                        .baseUrl(getString(R.string.HIMS_Server_AP))
                        .addConverterFactory(NullOnEmptyConverterFactory())
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    server = retrofit.create(RetrofitService::class.java)
                    server.getNodeAP(deviceUser?.user_kn!!, deviceUser?.cert_key!!, healthMessage?.sender!!)?.enqueue(object :
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

                            server1.rejectHealth(deviceUser.user_kn, deviceUser.cert_key!!, health?.issuer_health_no!!)?.enqueue(object :
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
