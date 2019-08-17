package com.hims.personal_node

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.hims.personal_node.DataMamager.DeviceDB
import com.hims.personal_node.Messaging.NullOnEmptyConverterFactory
import com.hims.personal_node.Model.Device.DeviceUser
import com.hims.personal_node.Model.Message.Message
import com.hims.personal_node.Model.Message.NodeInfo
import com.hims.personal_node.Model.ParsingJSON
import kotlinx.android.synthetic.main.activity_create_node_info.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.regex.Pattern

class ChangeDevice : AppCompatActivity() {
    var server: RetrofitService? = null
    private var deviceDB: DeviceDB? = null

    var VALID_PASSWOLD_REGEX_ALPHA_NUM_C: Pattern = Pattern.compile("[^a-zA-Z0-9!@.#\$%^&*?_~]")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_device)

        deviceDB = DeviceDB.getInstance(this)

        var retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.HIMS_Server_AP))
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        server = retrofit.create(RetrofitService::class.java)
        var checkResult: Boolean = false

        var node_kn: String? = null

        if (intent.hasExtra("node_kn")) {
            node_kn = intent.getStringExtra("node_kn")
            user_kn.setText(node_kn)
            user_kn.isEnabled = false
            user_kn.setBackgroundColor(Color.GRAY)
        } else {
            finish()
        }

        submit.setOnClickListener() {
            val user = FirebaseAuth.getInstance().currentUser
            var nodeInfo: NodeInfo =
                NodeInfo(null, null, null, null, null)
            nodeInfo.node_uid = user?.uid
            nodeInfo.node_kn = node_kn!!
            nodeInfo.node_ap_type = "personal"
            nodeInfo.node_pw = EncryptionSHA.encryptSha(node_pw.getText().toString())

            server?.getServerPublicKey(node_kn!!)?.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    var public_key = response.body().toString()
                    if (public_key != "null") {
                        var aes_key = EncryptionAES.init()
                        var jsonValue = ParsingJSON.modelToJson(nodeInfo)
                        var value = EncryptionAES.encryptAES(jsonValue, aes_key)
                        var enAes_key = EncryptionRSA.encryptByOtherKey(aes_key, public_key)
                        var sha_key = EncryptionSHA.encryptSha(jsonValue)
                        var message = Message(
                            "center",
                            nodeInfo.node_kn,
                            value,
                            public_key,
                            enAes_key,
                            sha_key
                        )
                        server?.checkPW(message)?.enqueue(object : Callback<Boolean> {
                            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                                var insertResult = response.body()
                                if (insertResult!!) {
                                    message.aes_key = aes_key
                                    MyFirebaseInstanceIDService.initToken(this@ChangeDevice.applicationContext, message)
                                    val addRunnable = Runnable {
                                        val deviceUser = DeviceUser(
                                            node_kn
                                        )
                                        deviceDB?.DeviceUserDAO()?.insert(deviceUser)
                                    }
                                    val addThread = Thread(addRunnable)
                                    addThread.start()
                                    var intent = Intent(this@ChangeDevice, CreatePrivateInformation::class.java)
                                    intent.putExtra("node_kn", node_kn)
                                    server?.deletePublicKey(message)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    messageToast("Unknown error\nDo the whole process again.\nContact system administrator if the error persists.")
                                    checkResult = false
                                    node_pw.setText("")
                                    node_pw.requestFocus()
                                    server?.deletePublicKey(message)
                                    messageToast("The password is incorrect.")
                                }
                            }
                            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                                messageToast("Connection Error2: "+t.message)
                            }
                        })
                    }else{
                        messageToast("Unknown error\nDo the whole process again.\nContact system administrator if the error persists.")
                        checkResult = false
                        user_kn.isEnabled = true
                        bt_check_kn.setText("CHECK")
                        user_kn.setBackgroundColor(Color.WHITE)
                        user_kn.setText("")
                        user_kn.requestFocus()
                    }
                }
                override fun onFailure(call: Call<String>, t: Throwable) {
                    messageToast("Connection Error: "+t.message)
                }
            })
        }
    }

    fun messageToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
