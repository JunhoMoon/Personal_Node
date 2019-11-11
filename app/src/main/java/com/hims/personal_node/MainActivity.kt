package com.hims.personal_node

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.UserCancellationException
import com.google.firebase.auth.FirebaseAuth
import com.hims.personal_node.DataMamager.DeviceDB
import com.hims.personal_node.Messaging.NullOnEmptyConverterFactory
import com.hims.personal_node.Model.Health.Health
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

//안드로이드 스튜디오 해시키 가져오기
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.content.pm.Signature;
//import android.util.Base64;
//import android.util.Log;
//
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var server: RetrofitService

    private var deviceDB: DeviceDB? = null
    private var himsdDB: HIMSDB? = null

    companion object {
        private const val RC_SIGN_IN = 730
    }


    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        deviceDB = DeviceDB.getInstance(this)

        auth = FirebaseAuth.getInstance()
//안드로이드 스튜디오 해시키 가져오기
//        getHashKey();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
            AlertDialog.Builder(this).setTitle("Do not Support").setMessage("Your device is not supported.\nPlease use another device.").setPositiveButton("OK"){dialog, which ->
                finish()
            }.show()
        }else{
            try {
                if (auth.currentUser != null) {
                    auth.signOut()
                    createSignInIntent()
                } else {
                    createSignInIntent()
                }
            }catch (e: UserCancellationException){
                finish()
            }
        }
    }

    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.logo3)
                .setTheme(R.style.FirebaseUI) // Set theme
                .setIsSmartLockEnabled(false)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                var retrofit = Retrofit.Builder()
                    .baseUrl(getString(R.string.HIMS_Server_AP))
                    .addConverterFactory(NullOnEmptyConverterFactory())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                server = retrofit.create(RetrofitService::class.java)
                server.getNodeInfoByUID(user!!.uid, "personal")?.enqueue(object : Callback<String?> {
                    override fun onFailure(call: Call<String?>, t: Throwable) {
                        messageToast("Server Connect Error : " + t.message)
                    }
                    override fun onResponse(call: Call<String?>, response: Response<String?>) {
                        var node_kn = response.body().toString()
                        var intent:Intent? = null
                        if (node_kn == "null"){
                            intent = Intent(this@MainActivity, CreateNodeInfo::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val addRunnable = Runnable {
                                if (deviceDB?.DeviceUserDAO()?.checkDevice(node_kn) == 1){
                                    val addRunnable2 = Runnable{
                                        himsdDB = HIMSDB.getInstance(this@MainActivity, node_kn)
                                        if (himsdDB?.personalDataDAO()?.checkPersonalData() == 1){
                                            server.refreshCertKey(node_kn).execute()
                                            intent = Intent(this@MainActivity, HealthView::class.java)
                                            intent!!.putExtra("node_kn", node_kn)
                                            startActivity(intent)
                                            finish()
                                        }else{
                                            intent = Intent(this@MainActivity, CreatePrivateInformation::class.java)
                                            intent!!.putExtra("node_kn", node_kn)
                                            startActivity(intent)
                                            finish()
                                        }
//                                        val addRunnable3 = Runnable{
//                                            var health = Health(0,"test", "2019.01.01", 1)
//                                            himsdDB?.healthDAO()?.insert(health)
//                                        }
//                                        val addThread3 = Thread(addRunnable3)
//                                        addThread3.start()
                                    }
                                    val addThread2 = Thread(addRunnable2)
                                    addThread2.start()
                                }else{
                                    intent = Intent(this@MainActivity, ChangeDevice::class.java)
                                    intent!!.putExtra("node_kn", node_kn)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                            val addThread = Thread(addRunnable)
                            addThread.start()
                        }
                    }
                })
            } else {
                finish()
            }
        }
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
            }
    }

    private fun delete() {
        AuthUI.getInstance()
            .delete(this)
            .addOnCompleteListener {
            }
    }

    //안드로이드 스튜디오 해시키 가져오기
//    private fun getHashKey() {
//        var packageInfo: PackageInfo? = null
//        try {
//            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
//        } catch (e: PackageManager.NameNotFoundException) {
//            e.printStackTrace()
//        }
//
//        if (packageInfo == null)
//            Log.e("KeyHash", "KeyHash:null")
//
//        for (signature in packageInfo!!.signatures) {
//            try {
//                val md = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT))
//            } catch (e: NoSuchAlgorithmException) {
//                Log.e("KeyHash", "Unable to get MessageDigest. signature=$signature", e)
//            }
//
//        }
//    }

    fun messageToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}