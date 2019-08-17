package com.hims.personal_node

import com.hims.personal_node.Model.Message.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService{
    @FormUrlEncoded
    @POST("Authentication/getNodeInfoByUID")
    fun getNodeInfoByUID(@Field("node_uid") node_uid: String): Call<String?>

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

    @POST("Authentication/insertNodeInfo")
    fun insertNodeInfo(@Body message: Message): Call<Boolean>

    @POST("Authentication/updateNodeAP")
    fun updateNodeAP(@Body message: Message): Call<Boolean>

    @POST("Authentication/deletePublicKey")
    fun deletePublicKey(@Body message: Message): Call<ResponseBody>

    @FormUrlEncoded
    @POST("Authentication/getServerPublicKey")
    fun getServerPublicKey(@Field("node_kn") node_kn:String): Call<String>

    @POST("Authentication/checkPW")
    fun checkPW(@Body message: Message): Call<Boolean>
}