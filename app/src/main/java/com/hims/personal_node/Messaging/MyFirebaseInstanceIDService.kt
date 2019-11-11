package com.hims.personal_node

object MyFirebaseInstanceIDService {

//    internal fun initToken(context: Context, nodeInfo: NodeInfo) {
//        FirebaseInstanceId.getInstance().instanceId
//            .addOnCompleteListener(OnCompleteListener { task ->
//                if (!task.isSuccessful) {
//                    return@OnCompleteListener
//                }
//                nodeInfo.node_ap = task.result!!.token
//                saveToken(context, nodeInfo)
//            })
////        FirebaseInstanceId.getInstance().deleteInstanceId()
//    }
//
//    private fun saveToken(context: Context, nodeInfo: NodeInfo) {
//        var server: RetrofitService?
//        var retrofit = Retrofit.Builder()
//            .baseUrl(context.getString(R.string.HIMS_Server_AP))
//            .addConverterFactory(NullOnEmptyConverterFactory())
//            .addConverterFactory(ScalarsConverterFactory.create())
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        server = retrofit.create(RetrofitService::class.java)
//
//        //서버 공개키 획듯
//        server?.getServerPublicKey(nodeInfo.node_uid!!)?.enqueue(object : Callback<String> {
//            override fun onResponse(call: Call<String>, response: Response<String>) {
//                var public_key = response.body().toString()
//                if (public_key != "null") {
//                    var aes_key = EncryptionAES.init()
//                    var jsonValue = ParsingJSON.modelToJson(nodeInfo)
//
//                    var map:MutableMap<String, String> = mutableMapOf()
//                    map["message_type"] = "update_nodeinfo"
//                    map["message_value"] = jsonValue
//
//                    var jsonMessage = ParsingJSON.modelToJson(map)
//
//                    var value = EncryptionAES.encryptAES(jsonMessage, aes_key)
//                    var enAes_key = EncryptionRSA.encryptByOtherKey(aes_key, public_key)
//                    var sha_key = EncryptionSHA.encryptSha(jsonValue)
//                    var message = Message(
//                        "center",
//                        nodeInfo.node_kn,
//                        value,
//                        public_key,
//                        enAes_key,
//                        sha_key
//                    )
//
////                    server?.updateNodeAP(message)?.enqueue(object : Callback<Boolean> {
////                        override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
////                            var result = response.body()
////                            if (!result!!){
////                                Toast.makeText(context, "Fail Join", Toast.LENGTH_LONG).show()
////                            }
////                        }
////                        override fun onFailure(call: Call<Boolean>, t: Throwable) {
////                        }
////                    })
//                }
//            }
//            override fun onFailure(call: Call<String>, t: Throwable) {
//            }
//        })
//    }
}