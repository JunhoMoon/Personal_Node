package com.hims.personal_node

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.hims.personal_node.DataMamager.DeviceDB
import com.hims.personal_node.Messaging.NullOnEmptyConverterFactory
import com.hims.personal_node.Model.Device.DeviceUser
import com.hims.personal_node.Model.Health.*
import com.hims.personal_node.Model.Message.Message
import com.hims.personal_node.Model.Message.MessageStack
import com.hims.personal_node.Model.Message.NotaryData
import com.hims.personal_node.Model.ParsingJSON
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.sql.Timestamp

class MyFirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {
    private var deviceDB: DeviceDB? = null
    private var himsdbDB: HIMSDB? = null
    private lateinit var server: RetrofitService

    val gson = Gson()

    // 메시지 수신
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        var context = this
        deviceDB = DeviceDB.getInstance(context)

        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${remoteMessage?.from}")

        // Check if message contains a data payload.
        remoteMessage?.data?.isNotEmpty()?.let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)

            var data = remoteMessage.data

            if (data["type"].equals("update_cert_key")) {
                var node_kn = data["node_kn"]
                var cert_key = data["cert_key"]

                var retrofit = Retrofit.Builder()
                    .baseUrl(getString(R.string.HIMS_Server_AP))
                    .addConverterFactory(NullOnEmptyConverterFactory())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                server = retrofit.create(RetrofitService::class.java)
                server.checkCertKey(node_kn!!, cert_key!!)?.enqueue(object : Callback<Boolean?> {
                    override fun onFailure(call: Call<Boolean?>, t: Throwable) {
                        server.refreshCertKey(node_kn).execute()
                    }

                    override fun onResponse(call: Call<Boolean?>, response: Response<Boolean?>) {
                        var check = response.body()
                        if (check == true) {
                            var deviceUser = DeviceUser(node_kn, cert_key)
                            val addRunnable = Runnable {
                                deviceDB?.DeviceUserDAO()?.update(deviceUser)
                            }
                            val addThread = Thread(addRunnable)
                            addThread.start()
                        } else {
                            server.refreshCertKey(node_kn).execute()
                        }
                    }
                })
            } else if (data["type"].equals("node_mapping")) {
                var target = data["target"]
                var message_stack_no = data["message_stack_no"]?.toLong()
                if (target != null && message_stack_no != null) {
                    val addRunnable = Runnable {
                        var deviceUser = deviceDB?.DeviceUserDAO()?.getByNodeKN(target)
                        if (deviceUser != null){
                            var retrofit = Retrofit.Builder()
                                .baseUrl(getString(R.string.HIMS_Server_AP))
                                .addConverterFactory(NullOnEmptyConverterFactory())
                                .addConverterFactory(ScalarsConverterFactory.create())
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()
                            server = retrofit.create(RetrofitService::class.java)
                            server.getMessageStack(message_stack_no, deviceUser.user_kn, deviceUser.cert_key!!)?.enqueue(object : Callback<MessageStack?> {
                                override fun onFailure(call: Call<MessageStack?>, t: Throwable) {
                                }
                                override fun onResponse(call: Call<MessageStack?>, response: Response<MessageStack?>) {
                                    var messageStack = response.body()
                                    var server1: RetrofitService
                                    var retrofit1 = Retrofit.Builder()
                                        .baseUrl("http://"+messageStack!!.sender_ap)
                                        .addConverterFactory(NullOnEmptyConverterFactory())
                                        .addConverterFactory(ScalarsConverterFactory.create())
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build()
                                    server1 = retrofit1.create(RetrofitService::class.java)
                                    server1.getMessage(messageStack.stack_no!!, deviceUser.user_kn, deviceUser.cert_key!!)?.enqueue(object : Callback<Message?> {
                                        override fun onFailure(call: Call<Message?>, t: Throwable) {
                                        }
                                        override fun onResponse(call: Call<Message?>, response: Response<Message?>) {
                                            var message = response.body()
                                            if (message != null){
                                                if (message.receiver.equals(deviceUser.user_kn)){
                                                    server.getPrivateKey(deviceUser.user_kn, deviceUser.cert_key,message.key_no!!)?.enqueue(object : Callback<String?> {
                                                        override fun onFailure(call: Call<String?>, t: Throwable) {
                                                        }
                                                        override fun onResponse(call: Call<String?>, response: Response<String?>) {
                                                            var key = response.body()
                                                            var aes_key = EncryptionRSA.decrypt(message.aes_key!!, key!!)
                                                            var jsonValue = EncryptionAES.decryptAES(message.value!!, aes_key)
                                                            if (EncryptionSHA.encryptSha(jsonValue).equals(message.sha_key)){
                                                                var nodeMappingMessage = gson.fromJson(jsonValue, HealthAuthorityMessage::class.java)
                                                                var healthAuthority = HealthAuthority(nodeMappingMessage.node_kn, nodeMappingMessage.node_name, nodeMappingMessage.patient_no, nodeMappingMessage.reg_date.toString(), 0, 0)
                                                                var time = System.currentTimeMillis()
                                                                var tsTemp = Timestamp(time)
                                                                var healthMessage = HealthMessage(null, healthAuthority.node_kn, data["type"]!!, gson.toJson(healthAuthority), tsTemp.toString())
                                                                himsdbDB = HIMSDB.getInstance(context, deviceUser.user_kn)
                                                                val addRunnable2 = Runnable {
                                                                    himsdbDB?.healthMessageDAO()?.insert(healthMessage)
                                                                }
                                                                val addThread2 = Thread(addRunnable2)
                                                                addThread2.start()
                                                                var notiMessage = "New agency : "+nodeMappingMessage.node_name
                                                                sendNotification(notiMessage)
                                                            }
                                                        }
                                                    })
                                                }
                                            }
                                        }
                                    })
                                }
                            })
                        }
                    }
                    val addThread = Thread(addRunnable)
                    addThread.start()
                }
            }else if (data["type"].equals("addPrimaryPhysician")) {
                var target = data["target"]
                var message_stack_no = data["message_stack_no"]?.toLong()
                if (target != null && message_stack_no != null) {
                    val addRunnable = Runnable {
                        var deviceUser = deviceDB?.DeviceUserDAO()?.getByNodeKN(target)
                        if (deviceUser != null){
                            var retrofit = Retrofit.Builder()
                                .baseUrl(getString(R.string.HIMS_Server_AP))
                                .addConverterFactory(NullOnEmptyConverterFactory())
                                .addConverterFactory(ScalarsConverterFactory.create())
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()
                            server = retrofit.create(RetrofitService::class.java)
                            server.getMessageStack(message_stack_no, deviceUser.user_kn, deviceUser.cert_key!!)?.enqueue(object : Callback<MessageStack?> {
                                override fun onFailure(call: Call<MessageStack?>, t: Throwable) {
                                }
                                override fun onResponse(call: Call<MessageStack?>, response: Response<MessageStack?>) {
                                    var messageStack = response.body()
                                    var server1: RetrofitService
                                    var retrofit1 = Retrofit.Builder()
                                        .baseUrl("http://"+messageStack!!.sender_ap)
                                        .addConverterFactory(NullOnEmptyConverterFactory())
                                        .addConverterFactory(ScalarsConverterFactory.create())
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build()
                                    server1 = retrofit1.create(RetrofitService::class.java)
                                    server1.getMessage(messageStack.stack_no!!, deviceUser.user_kn, deviceUser.cert_key!!)?.enqueue(object : Callback<Message?> {
                                        override fun onFailure(call: Call<Message?>, t: Throwable) {
                                        }
                                        override fun onResponse(call: Call<Message?>, response: Response<Message?>) {
                                            var message = response.body()
                                            if (message != null){
                                                if (message.receiver.equals(deviceUser.user_kn)){
                                                    server.getPrivateKey(deviceUser.user_kn, deviceUser.cert_key,message.key_no!!)?.enqueue(object : Callback<String?> {
                                                        override fun onFailure(call: Call<String?>, t: Throwable) {
                                                        }
                                                        override fun onResponse(call: Call<String?>, response: Response<String?>) {
                                                            var key = response.body()
                                                            var aes_key = EncryptionRSA.decrypt(message.aes_key!!, key!!)
                                                            var jsonValue = EncryptionAES.decryptAES(message.value!!, aes_key)
                                                            if (EncryptionSHA.encryptSha(jsonValue).equals(message.sha_key)){
                                                                // 수신 형태별 변경
                                                                var primaryPhysicianMessage = gson.fromJson(jsonValue, PrimaryPhysicianMessage::class.java)
                                                                var primaryPhysician = PrimaryPhysician(primaryPhysicianMessage.node_kn, primaryPhysicianMessage.primaryPhysician_id,
                                                                    primaryPhysicianMessage.primaryPhysician_name!!, primaryPhysicianMessage.reg_date,0, 0
                                                                )
                                                                himsdbDB = HIMSDB.getInstance(context, deviceUser.user_kn)
                                                                val addRunnable2 = Runnable {
                                                                    var healthAuthority = himsdbDB?.healthAuthorityDAO()?.getByNodeKN(primaryPhysician.node_kn)
                                                                    if (healthAuthority != null){
                                                                        if (healthAuthority.record_auth == 1){
                                                                            himsdbDB?.primaryPhysicianDAO()?.insert(primaryPhysician)

                                                                            server1.acceptPrimaryPhysician(deviceUser.user_kn, deviceUser.cert_key!!, primaryPhysician!!.primaryPhysician_id)?.enqueue(object :
                                                                                Callback<Void?> {
                                                                                override fun onFailure(call: Call<Void?>, t: Throwable) {
                                                                                }
                                                                                override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                                                                                }
                                                                            })
                                                                        }else{
                                                                            var time = System.currentTimeMillis()
                                                                            var tsTemp = Timestamp(time)
                                                                            var healthMessage = HealthMessage(null, primaryPhysicianMessage.node_kn, data["type"]!!, gson.toJson(primaryPhysician), tsTemp.toString())
                                                                            himsdbDB?.healthMessageDAO()?.insert(healthMessage)
                                                                        }
                                                                    }else{
                                                                        var time = System.currentTimeMillis()
                                                                        var tsTemp = Timestamp(time)
                                                                        var healthMessage = HealthMessage(null, primaryPhysicianMessage.node_kn, data["type"]!!, gson.toJson(primaryPhysician), tsTemp.toString())
                                                                        himsdbDB?.healthMessageDAO()?.insert(healthMessage)
                                                                    }
                                                                }
                                                                val addThread2 = Thread(addRunnable2)
                                                                addThread2.start()
                                                                var notiMessage = "New Primary Physician : "+ primaryPhysician.primaryPhysician_name
                                                                sendNotification(notiMessage)
                                                            }
                                                        }
                                                    })
                                                }
                                            }
                                        }
                                    })
                                }
                            })
                        }
                    }
                    val addThread = Thread(addRunnable)
                    addThread.start()
                }
            }else if (data["type"].equals("recordHealthData")) {
                var target = data["target"]
                var message_stack_no = data["message_stack_no"]?.toLong()
                if (target != null && message_stack_no != null) {
                    val addRunnable = Runnable {
                        var deviceUser = deviceDB?.DeviceUserDAO()?.getByNodeKN(target)
                        if (deviceUser != null){
                            var retrofit = Retrofit.Builder()
                                .baseUrl(getString(R.string.HIMS_Server_AP))
                                .addConverterFactory(NullOnEmptyConverterFactory())
                                .addConverterFactory(ScalarsConverterFactory.create())
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()
                            server = retrofit.create(RetrofitService::class.java)
                            server.getMessageStack(message_stack_no, deviceUser.user_kn, deviceUser.cert_key!!)?.enqueue(object : Callback<MessageStack?> {
                                override fun onFailure(call: Call<MessageStack?>, t: Throwable) {
                                }
                                override fun onResponse(call: Call<MessageStack?>, response: Response<MessageStack?>) {
                                    var messageStack = response.body()
                                    var server1: RetrofitService
                                    var retrofit1 = Retrofit.Builder()
                                        .baseUrl("http://"+messageStack!!.sender_ap)
                                        .addConverterFactory(NullOnEmptyConverterFactory())
                                        .addConverterFactory(ScalarsConverterFactory.create())
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build()
                                    server1 = retrofit1.create(RetrofitService::class.java)
                                    server1.getMessage(messageStack.stack_no!!, deviceUser.user_kn, deviceUser.cert_key!!)?.enqueue(object : Callback<Message?> {
                                        override fun onFailure(call: Call<Message?>, t: Throwable) {
                                        }
                                        override fun onResponse(call: Call<Message?>, response: Response<Message?>) {
                                            var message = response.body()
                                            if (message != null){
                                                if (message.receiver.equals(deviceUser.user_kn)){
                                                    server.getPrivateKey(deviceUser.user_kn, deviceUser.cert_key,message.key_no!!)?.enqueue(object : Callback<String?> {
                                                        override fun onFailure(call: Call<String?>, t: Throwable) {
                                                        }
                                                        override fun onResponse(call: Call<String?>, response: Response<String?>) {
                                                            var key = response.body()
                                                            var aes_key = EncryptionRSA.decrypt(message.aes_key!!, key!!)
                                                            var jsonValue = EncryptionAES.decryptAES(message.value!!, aes_key)
                                                            if (EncryptionSHA.encryptSha(jsonValue).equals(message.sha_key)){
                                                                // 수신 형태별 변경
                                                                var health: Health
                                                                var jsonObj = JsonParser().parse(jsonValue) as JsonObject

                                                                var details:MutableList<HealthDetail> = mutableListOf()
                                                                health = gson.fromJson(jsonObj.get("health"), Health::class.java)

                                                                for(item in jsonObj.getAsJsonArray("healthDetail")){
                                                                    var detail = gson.fromJson(item, HealthDetail::class.java)
                                                                    details.add(detail)
                                                                }

                                                                himsdbDB = HIMSDB.getInstance(context, deviceUser.user_kn)
                                                                val addRunnable2 = Runnable {
                                                                    var healthAuthority = himsdbDB?.healthAuthorityDAO()?.getByNodeKN(message.sender!!)
                                                                    if (healthAuthority != null){
                                                                        if (healthAuthority.record_auth == 1){
                                                                            health.node_kn = message.sender!!

                                                                            var rowNum:Long = if (himsdbDB?.healthDAO()?.getall2()!! != null){
                                                                                himsdbDB?.healthDAO()?.getall2()?.size!!.toLong()
                                                                            }else{
                                                                                0
                                                                            }
                                                                            health.subject_health_no = rowNum + 1
                                                                            himsdbDB?.healthDAO()?.insert(health)
                                                                            for (detail in details!!){
                                                                                detail.health_no = health.subject_health_no!!
                                                                                himsdbDB?.healthDetailDAO()?.insert(detail)
                                                                            }

                                                                            var healthNotarys: MutableList<HealthNotary> = mutableListOf()

                                                                            server.getNotaryNodeAP(deviceUser?.user_kn!!, deviceUser?.cert_key!!, message.sender!!)?.enqueue(object :
                                                                                Callback<MutableList<NodeInfo>?> {
                                                                                override fun onFailure(call: Call<MutableList<NodeInfo>?>, t: Throwable) {
                                                                                }
                                                                                override fun onResponse(call: Call<MutableList<NodeInfo>?>, response: Response<MutableList<NodeInfo>?>) {
                                                                                    var notarys = response.body()
                                                                                    println(details.toString())
                                                                                    var sha = EncryptionSHA.encryptSha(gson.toJson(details))
                                                                                    for (notary in notarys!!){
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
                                                                                                    himsdbDB?.healthNotaryDAO()?.insert(healthNotary)
                                                                                                }
                                                                                                var addThread1 = Thread(addRunnable1)
                                                                                                addThread1.start()
                                                                                            }
                                                                                        })
                                                                                    }
                                                                                }
                                                                            })

                                                                            server.updateLastHealthNo(deviceUser.user_kn, deviceUser.cert_key!!, health?.subject_health_no!!)?.enqueue(object :
                                                                                Callback<Void?> {
                                                                                override fun onFailure(call: Call<Void?>, t: Throwable) {
                                                                                }
                                                                                override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                                                                                }
                                                                            })

                                                                            Thread.sleep(1000)
                                                                            server1.acceptHealth(deviceUser.user_kn, deviceUser.cert_key!!,health?.issuer_health_no!!, health?.subject_health_no!!, ParsingJSON.modelToJson(healthNotarys))?.enqueue(object :
                                                                                Callback<Void?> {
                                                                                override fun onFailure(call: Call<Void?>, t: Throwable) {
                                                                                }
                                                                                override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                                                                                }
                                                                            })
                                                                        }else{

                                                                            var primaryPhysician = himsdbDB?.primaryPhysicianDAO()?.getByPK(message.sender!!,
                                                                                health.physician_id!!
                                                                            )
                                                                            if (primaryPhysician != null){
                                                                                if (primaryPhysician.record_auth == 1){
                                                                                    health.node_kn = message.sender!!
                                                                                    var rowNum:Long = if (himsdbDB?.healthDAO()?.getall2() != null){
                                                                                        himsdbDB?.healthDAO()?.getall2()?.size!!.toLong()
                                                                                    }else{
                                                                                        0
                                                                                    }
                                                                                    health.subject_health_no = rowNum + 1
                                                                                    himsdbDB?.healthDAO()?.insert(health)

                                                                                    for (detail in details!!){
                                                                                        detail.health_no = health.subject_health_no!!
                                                                                        himsdbDB?.healthDetailDAO()?.insert(detail)
                                                                                    }
                                                                                    var healthNotarys: MutableList<HealthNotary> = mutableListOf()

                                                                                    server.getNotaryNodeAP(deviceUser?.user_kn!!, deviceUser?.cert_key!!, message.sender!!)?.enqueue(object :
                                                                                        Callback<MutableList<NodeInfo>?> {
                                                                                        override fun onFailure(call: Call<MutableList<NodeInfo>?>, t: Throwable) {
                                                                                        }
                                                                                        override fun onResponse(call: Call<MutableList<NodeInfo>?>, response: Response<MutableList<NodeInfo>?>) {
                                                                                            var notarys = response.body()
                                                                                            println(details.toString())
                                                                                            var sha = EncryptionSHA.encryptSha(gson.toJson(details))
                                                                                            for (notary in notarys!!){
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
                                                                                                            himsdbDB?.healthNotaryDAO()?.insert(healthNotary)
                                                                                                        }
                                                                                                        var addThread1 = Thread(addRunnable1)
                                                                                                        addThread1.start()
                                                                                                    }
                                                                                                })
                                                                                            }
                                                                                        }
                                                                                    })

                                                                                    server.updateLastHealthNo(deviceUser.user_kn, deviceUser.cert_key!!, health?.subject_health_no!!)?.enqueue(object :
                                                                                        Callback<Void?> {
                                                                                        override fun onFailure(call: Call<Void?>, t: Throwable) {
                                                                                        }
                                                                                        override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                                                                                        }
                                                                                    })
                                                                                    Thread.sleep(1000)
                                                                                    server1.acceptHealth(deviceUser.user_kn, deviceUser.cert_key!!,health?.issuer_health_no!!, health?.subject_health_no!!, ParsingJSON.modelToJson(healthNotarys))?.enqueue(object :
                                                                                        Callback<Void?> {
                                                                                        override fun onFailure(call: Call<Void?>, t: Throwable) {
                                                                                        }
                                                                                        override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                                                                                        }
                                                                                    })
                                                                                }else{
                                                                                    var time = System.currentTimeMillis()
                                                                                    var tsTemp = Timestamp(time)
                                                                                    var healthMessage = HealthMessage(null, message.sender!!, data["type"]!!, jsonValue, tsTemp.toString())
                                                                                    himsdbDB?.healthMessageDAO()?.insert(healthMessage)
                                                                                }
                                                                            }else{
                                                                                var time = System.currentTimeMillis()
                                                                                var tsTemp = Timestamp(time)
                                                                                var healthMessage = HealthMessage(null, message.sender!!, data["type"]!!, jsonValue, tsTemp.toString())
                                                                                himsdbDB?.healthMessageDAO()?.insert(healthMessage)
                                                                            }
                                                                        }
                                                                    }else{
                                                                        var time = System.currentTimeMillis()
                                                                        var tsTemp = Timestamp(time)
                                                                        var healthMessage = HealthMessage(null, message.sender!!, data["type"]!!, jsonValue, tsTemp.toString())
                                                                        himsdbDB?.healthMessageDAO()?.insert(healthMessage)
                                                                    }
                                                                }
                                                                val addThread2 = Thread(addRunnable2)
                                                                addThread2.start()
                                                                var notiMessage = "New HealthData"
                                                                sendNotification(notiMessage)
                                                            }
                                                        }
                                                    })
                                                }
                                            }
                                        }
                                    })
                                }
                            })
                        }
                    }
                    val addThread = Thread(addRunnable)
                    addThread.start()
                }
            }

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob()
            } else {
                // Handle message within 10 seconds
                handleNow()
            }
        }

        // Check if message contains a notification payload.
        remoteMessage?.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String?) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private fun scheduleJob() {
        // [START dispatch_job]
//        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
//        WorkManager.getInstance().beginWith(work).enqueue()
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setContentTitle(getString(R.string.fcm_message))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    companion object {

        private const val TAG = "MyFirebaseMsgService"
    }
}