package com.hims.personal_node.Model.Message

//import com.google.gson.JsonObject
//import com.google.gson.annotations.SerializedName

data class Message(var receiver:String?, var sender:String?, var value:String?, var public_key:String?, var aes_key:String?, var sha_key:String?)
//{
//    @SerializedName("request")
//    private lateinit var request:JsonObject
//
//    @SerializedName("response")
//    private lateinit var response:JsonObject
//
//    fun setRequest(request: JsonObject) {
//        this.request = request
//    }
//
//    fun getResponse(): JsonObject {
//        return response
//    }
//
//    fun setResponse(response: JsonObject) {
//        this.response = response
//    }
//}