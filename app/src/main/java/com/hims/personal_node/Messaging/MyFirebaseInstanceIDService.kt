package com.hims.personal_node

import android.content.Context
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.hims.personal_node.Model.Message.Message
import com.hims.personal_node.Model.Message.NodeInfo
import com.hims.personal_node.Model.ParsingJSON
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MyFirebaseInstanceIDService {

    internal fun initToken(context: Context, message: Message) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                val token = task.result!!.token
                saveToken(token, context, message)
            })
//        FirebaseInstanceId.getInstance().deleteInstanceId()
    }

    private fun saveToken(token: String, context: Context, message: Message) {
        var server: RetrofitService?
        var retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.HIMS_Server_AP))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        server = retrofit.create(RetrofitService::class.java)
        var nodeInfo = NodeInfo(null, message.sender, null, token, null)

        var jsonValue = ParsingJSON.modelToJson(nodeInfo)
        message.value =  EncryptionAES.encryptAES(jsonValue, message.aes_key!!)
        message.sha_key = EncryptionSHA.encryptSha(jsonValue)
        message.aes_key = EncryptionRSA.encryptByOtherKey(message.aes_key!!, message.public_key!!)

        server?.updateNodeAP(message)?.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
            }
            override fun onFailure(call: Call<Boolean>, t: Throwable) {
            }
        })
    }
}