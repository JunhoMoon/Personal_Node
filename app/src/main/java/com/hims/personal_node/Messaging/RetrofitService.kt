package com.hims.personal_node

import com.hims.personal_node.Model.Health.NodeInfo
import com.hims.personal_node.Model.Message.*
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService{
    @FormUrlEncoded
    @POST("Authentication/getNodeInfoByUID")
    fun getNodeInfoByUID(@Field("node_uid") node_uid: String, @Field("node_ap_type") node_ap_type: String): Call<String?>

    @GET("Authentication/getGender")
    fun getGender(): Call<MutableList<Gender>>

    @GET("Authentication/getOtherGenders")
    fun getOtherGenders(): Call<MutableList<Gender>>

    @GET("Authentication/getRace")
    fun getRace(): Call<MutableList<Race>>

    @GET("Authentication/getCountry")
    fun getCountry(): Call<MutableList<Country>>

    @FormUrlEncoded
    @POST("Authentication/getAdmin")
    fun getAdmin(@Field("country_no") country_no:Int): Call<MutableList<Admin>>

    @FormUrlEncoded
    @POST("Authentication/getCity")
    fun getCity(@Field("admin_no") admin_no:Int): Call<MutableList<City>>

    @FormUrlEncoded
    @POST("Authentication/getJob")
    fun getJob(@Field("parent_key") parent_key:String): Call<MutableList<Job>>

    @FormUrlEncoded
    @POST("Authentication/checkKN")
    fun checkKN(@Field("node_kn") node_kn:String): Call<Boolean>

    @FormUrlEncoded
    @POST("Authentication/getPublicKey")
    fun getPublicKey(@Field("node_kn") node_kn:String?, @Field("cert_key") cert_key:String?, @Field("target") target:String): Call<Communication_Key?>

    @FormUrlEncoded
    @POST("Authentication/getPrivateKey")
    fun getPrivateKey(@Field("node_kn") node_kn:String?, @Field("cert_key") cert_key:String?, @Field("key_no") key_no:Long): Call<String?>

    @FormUrlEncoded
    @POST("Authentication/deleteKey")
    fun deleteKey(@Field("key_no") key_no:Long, @Field("target") target:String): Call<Void>

    @POST("Authentication/joinNode")
    fun joinNode(@Body message: Message): Call<Boolean>

    @POST("Authentication/changeNodeAP")
    fun changeNodeAP(@Body message: Message): Call<Boolean>

    @FormUrlEncoded
    @POST("Authentication/refreshCertKey")
    fun refreshCertKey(@Field("node_kn") node_kn:String): Call<Void>

    @FormUrlEncoded
    @POST("Authentication/CheckCertKey")
    fun checkCertKey(@Field("node_kn") node_kn:String, @Field("cert_key") cert_key:String): Call<Boolean>

    @FormUrlEncoded
    @POST("MessageQueue/getMessageStack")
    fun getMessageStack(@Field("message_stack_no") message_stack_no:Long, @Field("node_kn") node_kn:String, @Field("cert_key") cert_key:String): Call<MessageStack>

    @FormUrlEncoded
    @POST("Messaging/getMessage")
    fun getMessage(@Field("message_stack_no") message_stack_no:Long, @Field("node_kn") node_kn:String, @Field("cert_key") cert_key:String): Call<Message>

    @FormUrlEncoded
    @POST("Authentication/getNodeAP")
    fun getNodeAP(@Field("node_kn") node_kn:String, @Field("cert_key") cert_key:String, @Field("target") target:String): Call<String?>

    @FormUrlEncoded
    @POST("Messaging/acceptNodeMapping")
    fun acceptNodeMapping(@Field("node_kn") node_kn:String, @Field("cert_key") cert_key:String): Call<Void?>

    @FormUrlEncoded
    @POST("Messaging/rejectNodeMapping")
    fun rejectNodeMapping(@Field("node_kn") node_kn:String, @Field("cert_key") cert_key:String): Call<Void?>

    @FormUrlEncoded
    @POST("Messaging/acceptPrimaryPhysician")
    fun acceptPrimaryPhysician(@Field("node_kn") node_kn:String, @Field("cert_key") cert_key:String, @Field("primaryPhysician_id") primaryPhysician_id:String): Call<Void?>

    @FormUrlEncoded
    @POST("Messaging/rejectPrimaryPhysician")
    fun rejectPrimaryPhysician(@Field("node_kn") node_kn:String, @Field("cert_key") cert_key:String, @Field("primaryPhysician_id") primaryPhysician_id:String): Call<Void?>

    @FormUrlEncoded
    @POST("Messaging/acceptHealth")
    fun acceptHealth(@Field("node_kn") node_kn:String, @Field("cert_key") cert_key:String, @Field("issuer_health_no") issuer_health_no:Long, @Field("subject_health_no") subject_health_no:Long, @Field("healthNotarys") healthNotarys:String): Call<Void?>

    @FormUrlEncoded
    @POST("Messaging/rejectHealth")
    fun rejectHealth(@Field("node_kn") node_kn:String, @Field("cert_key") cert_key:String, @Field("issuer_health_no") issuer_health_no:Long): Call<Void?>

    @FormUrlEncoded
    @POST("Authentication/UpdateLastHealthNo")
    fun updateLastHealthNo(@Field("node_kn") node_kn:String, @Field("cert_key") cert_key:String, @Field("last_health_no") subject_health_no:Long): Call<Void?>

    @FormUrlEncoded
    @POST("Authentication/getNotaryNodeAP")
    fun getNotaryNodeAP(@Field("node_kn") node_kn:String, @Field("cert_key") cert_key:String, @Field("issuer") issuer:String): Call<MutableList<NodeInfo>?>

    @FormUrlEncoded
    @POST("Messaging/addNotary")
    fun addNotary(@Field("node_kn") node_kn:String, @Field("cert_key") cert_key:String, @Field("sha") sha:String): Call<NotaryData?>
}