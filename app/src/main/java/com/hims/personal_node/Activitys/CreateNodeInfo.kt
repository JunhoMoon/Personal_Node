package com.hims.personal_node

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.hims.personal_node.DataMamager.DeviceDB
import com.hims.personal_node.Messaging.NullOnEmptyConverterFactory
import com.hims.personal_node.Model.*
import com.hims.personal_node.Model.Device.DeviceUser
import com.hims.personal_node.Model.Message.Communication_Key
import com.hims.personal_node.Model.Message.Message
import com.hims.personal_node.Model.Health.NodeInfo
import kotlinx.android.synthetic.main.activity_create_node_info.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.regex.Pattern

class CreateNodeInfo : AppCompatActivity() {

    var server: RetrofitService? = null
    private var deviceDB: DeviceDB? = null

    var VALID_EMAIL_ADDRESS_REGEX_C: Pattern = Pattern.compile("[^a-zA-Z0-9!@.#\$%^&*?_~]")
    var VALID_EMAIL_ADDRESS_REGEX_C2: Pattern = Pattern.compile("[^a-zA-Z]")
    var VALID_PASSWOLD_REGEX_ALPHA_NUM_C: Pattern = Pattern.compile("[^a-zA-Z0-9!@.#\$%^&*?_~]")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_node_info)

        deviceDB = DeviceDB.getInstance(this)

        var retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.HIMS_Server_AP))
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        server = retrofit.create(RetrofitService::class.java)
        var checkResult: Boolean = false
        user_kn.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (VALID_EMAIL_ADDRESS_REGEX_C.matcher(user_kn.getText().toString()).find()) {
                    messageToast("Invalid character")
                    user_kn.setText(
                        user_kn.text.toString().substring(0,user_kn.text.toString().length - 1)
                    )
                    user_kn.setSelection(user_kn.text.toString().length)
                } else if (user_kn.getText().toString().length > 16) {
                    messageToast("Too long. \nPlease write within 16 characters.")
                    user_kn.setText(
                        user_kn.text.toString().substring(0,user_kn.text.toString().length - 1)
                    )
                } else if (user_kn.getText().toString().length == 1 && VALID_EMAIL_ADDRESS_REGEX_C2.matcher(user_kn.getText().toString()).find()) {
                    messageToast("It must necessarily start with an alphabet.")
                    user_kn.setText(user_kn.text.toString().substring(0,user_kn.text.toString().length - 1))
                }
            }
        })

        bt_check_kn.setOnClickListener() {
            when {
                checkResult -> {
                    checkResult = false
                    user_kn.isEnabled = true
                    bt_check_kn.setText("CHECK")
                    user_kn.setBackgroundColor(Color.WHITE)
                    user_kn.setText("")
                    user_kn.requestFocus()
                }
                user_kn.getText().toString().length < 4 -> {
                    messageToast("Too short\nPlease enter at least 4 characters.")
                    user_kn.requestFocus()
                }
                else -> {
                    var kn = user_kn.getText().toString()
                    server?.checkKN(kn)?.enqueue(object : Callback<Boolean> {
                        override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                            checkResult = response.body()!!
                            if (checkResult) {
                                user_kn.isEnabled = false
                                bt_check_kn.setText("RESET")
                                user_kn.setBackgroundColor(Color.GRAY)
                            } else {
                                messageToast("This nickname cannot be used.")
                                user_kn.requestFocus()
                            }
                        }
                        override fun onFailure(call: Call<Boolean>, t: Throwable) {
                            messageToast("Connection Error1: "+t.message)
                            user_kn.requestFocus()
                        }
                    })
                }
            }
        }

        node_pw.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (VALID_PASSWOLD_REGEX_ALPHA_NUM_C.matcher(node_pw.getText().toString()).find()) {
                    messageToast("Invalid character")
                    user_kn.setText(user_kn.text.toString().substring(0,user_kn.text.toString().length - 1))
                    user_kn.setSelection(user_kn.text.toString().length)
                } else if (user_kn.getText().toString().length > 30) {
                    messageToast("Too long. \nPlease write within 16 characters.")
                    user_kn.setText(user_kn.text.toString().substring(0,user_kn.text.toString().length - 1))
                }
            }
        })

        submit.setOnClickListener() {
            when {
                !checkResult -> {
                    user_kn.requestFocus()
                    messageToast("Please check your nickname.")
                }
                node_pw.getText().toString().length < 8 -> {
                    messageToast("Too short\nPlease enter a password of at least 8 characters.")
                    user_kn.requestFocus()
                }
                else -> {
                    val user = FirebaseAuth.getInstance().currentUser
                    var nodeInfo: NodeInfo =
                        NodeInfo(null, null, null, null, null)
                    nodeInfo.node_uid = user?.uid
                    nodeInfo.node_kn = user_kn.getText().toString()
                    nodeInfo.node_ap_type = "personal"
                    nodeInfo.node_pw = EncryptionSHA.encryptSha(node_pw.getText().toString())

                    FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            return@OnCompleteListener
                        }
                        nodeInfo.node_ap = task.result!!.token

                        //서버 공개키 획듯
                        server?.getPublicKey(null, null, "center")?.enqueue(object : Callback<Communication_Key?> {
                            override fun onResponse(call: Call<Communication_Key?>, response: Response<Communication_Key?>) {
                                var communication_Key = response.body()
                                if (communication_Key != null) {
                                    var aes_key = EncryptionAES.init()
                                    var jsonValue = ParsingJSON.modelToJson(nodeInfo)

                                    var value = EncryptionAES.encryptAES(jsonValue, aes_key)
                                    var enAes_key = EncryptionRSA.encrypt(aes_key, communication_Key.key)
                                    var sha_key = EncryptionSHA.encryptSha(jsonValue)
                                    var message = Message(
                                        "center",
                                        nodeInfo.node_kn,
                                        value,
                                        communication_Key.key_no,
                                        enAes_key,
                                        sha_key
                                    )

                                    server?.joinNode(message)?.enqueue(object : Callback<Boolean> {
                                        override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                                            var result = response.body()
                                            if (!result!!){
                                                messageToast("Unknown error\nDo the whole process again.\nContact system administrator if the error persists.")
                                                checkResult = false
                                                user_kn.isEnabled = true
                                                bt_check_kn.setText("CHECK")
                                                user_kn.setBackgroundColor(Color.WHITE)
                                                user_kn.setText("")
                                                user_kn.requestFocus()
                                                server?.deleteKey(communication_Key.key_no, "center")?.enqueue(object : Callback<Void> {
                                                    override fun onFailure(call: Call<Void>, t: Throwable) {
                                                    }
                                                    override fun onResponse(call: Call<Void>,response: Response<Void>) {
                                                    }
                                                })
                                            }else{
                                                val addRunnable = Runnable {
                                                    val deviceUser = DeviceUser(nodeInfo.node_kn!!, null)
                                                    deviceDB?.DeviceUserDAO()?.insert(deviceUser)
                                                }
                                                val addThread = Thread(addRunnable)
                                                addThread.start()

                                                server?.refreshCertKey(nodeInfo.node_kn!!)?.enqueue(object : Callback<Void> {
                                                    override fun onFailure(call: Call<Void>, t: Throwable) {
                                                    }
                                                    override fun onResponse(call: Call<Void>,response: Response<Void>) {
                                                    }
                                                })

                                                var intent = Intent(this@CreateNodeInfo, CreatePrivateInformation::class.java)
                                                intent.putExtra("node_kn", nodeInfo.node_kn)
                                                startActivity(intent)
                                                finish()
                                            }
                                        }
                                        override fun onFailure(call: Call<Boolean>, t: Throwable) {
                                            messageToast("Connection Error: "+t.message)
                                            checkResult = false
                                            user_kn.isEnabled = true
                                            bt_check_kn.setText("CHECK")
                                            user_kn.setBackgroundColor(Color.WHITE)
                                            user_kn.setText("")
                                            user_kn.requestFocus()
                                            server?.deleteKey(communication_Key.key_no, "center")?.enqueue(object : Callback<Void> {
                                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                                }
                                                override fun onResponse(call: Call<Void>,response: Response<Void>) {
                                                }
                                            })
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
                            override fun onFailure(call: Call<Communication_Key?>, t: Throwable) {
                                messageToast("Connection Error: "+t.message)
                                checkResult = false
                                user_kn.isEnabled = true
                                bt_check_kn.setText("CHECK")
                                user_kn.setBackgroundColor(Color.WHITE)
                                user_kn.setText("")
                                user_kn.requestFocus()
                            }
                        })
                    })
                }
            }
        }
    }

    fun messageToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}