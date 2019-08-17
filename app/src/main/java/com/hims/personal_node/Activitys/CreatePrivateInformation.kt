package com.hims.personal_node

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import com.hims.personal_node.Messaging.NullOnEmptyConverterFactory
import com.hims.personal_node.Model.Message.*
import com.hims.personal_node.Model.Personal.PersonalData
import kotlinx.android.synthetic.main.activity_create_private_information.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CreatePrivateInformation : AppCompatActivity() {

    var server: RetrofitService? = null
    private var himsdb: HIMSDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_private_information)

        var node_kn:String
        if (intent.hasExtra("node_kn")){
            node_kn = intent.getStringExtra("node_kn")

            himsdb = HIMSDB.getInstance(this, node_kn)

            registration_title.text =
                HtmlCompat.fromHtml("<u>" + registration_title.text + "</u>", HtmlCompat.FROM_HTML_MODE_LEGACY)

            var retrofit = Retrofit.Builder()
                .baseUrl(getString(R.string.HIMS_Server_AP))
                .addConverterFactory(NullOnEmptyConverterFactory())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            server = retrofit.create(RetrofitService::class.java)

            server?.getGender()?.enqueue(object : Callback<MutableList<Gender>> {
                override fun onFailure(call: Call<MutableList<Gender>>?, t: Throwable?) {
                    Log.d("onFailure : ", t?.message)
                    messageToast("onFailure : " + t?.message)

                    finish()
                }

                override fun onResponse(call: Call<MutableList<Gender>>?, response: Response<MutableList<Gender>>?) {
                    if (response!!.isSuccessful) {
                        if (response.body() != null) {
                            Log.i("onSuccess", response.body().toString())
                            var gender_list: MutableList<Gender> = response.body()!!
                            gender_list.add(Gender(0, null, "Select Your Gender"))
                            gender_list.sortBy { gender -> gender.gender_key }

                            var adapt = object :
                                ArrayAdapter<Gender>(
                                    this@CreatePrivateInformation,
                                    android.R.layout.simple_spinner_item,
                                    gender_list
                                ) {
                                override fun isEnabled(position: Int): Boolean {
                                    return position != 0
                                }

                                override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                                    var view = super.getDropDownView(position, convertView, parent)
                                    var tv = view as TextView
                                    if (position == 0) {
                                        tv.setTextColor(Color.WHITE)
                                        tv.setBackgroundColor(Color.GRAY)
                                    } else {
                                        tv.setTextColor(Color.BLACK)
                                        tv.setBackgroundColor(Color.WHITE)
                                    }
                                    return view
                                }
                            }
                            adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            gender_spinner.adapter = adapt
                        } else {
                            Log.i("onEmptyResponse", "Returned empty response")
                        }
                    }
                    Log.d("onResponse : ", response.body().toString())
                }
            })

            gender_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val gender = parent.selectedItem as Gender
                    when (gender.gender_key) {
                        0 -> {
                        }
                        3 -> {
                            server?.getOtherGenders()?.enqueue(object : Callback<MutableList<Gender>> {
                                override fun onFailure(call: Call<MutableList<Gender>>?, t: Throwable?) {
                                    Log.d("onFailure : ", t?.message)
                                    messageToast("onFailure : " + t?.message)

                                    finish()
                                }

                                override fun onResponse(
                                    call: Call<MutableList<Gender>>?,
                                    response: Response<MutableList<Gender>>?
                                ) {
                                    if (response!!.isSuccessful) {
                                        if (response.body() != null) {
                                            Log.i("onSuccess", response.body().toString())

                                            var gender_list: MutableList<Gender>? = response.body()
                                            if (gender_list != null) {
                                                gender_list.add(
                                                    Gender(
                                                        0,
                                                        null,
                                                        "Select Your Other Genders"
                                                    )
                                                )
                                            }
                                            if (gender_list != null) {
                                                gender_list.sortBy { gender -> gender.gender_key }
                                            }

                                            var adapt = object : ArrayAdapter<Gender>(
                                                this@CreatePrivateInformation,
                                                android.R.layout.simple_spinner_item,
                                                gender_list
                                            ) {
                                                override fun isEnabled(position: Int): Boolean {
                                                    return position != 0
                                                }

                                                override fun getDropDownView(
                                                    position: Int,
                                                    convertView: View?,
                                                    parent: ViewGroup
                                                ): View {
                                                    var view = super.getDropDownView(position, convertView, parent)
                                                    var tv = view as TextView
                                                    if (position == 0) {
                                                        tv.setTextColor(Color.WHITE)
                                                        tv.setBackgroundColor(Color.GRAY)
                                                    } else {
                                                        tv.setTextColor(Color.BLACK)
                                                        tv.setBackgroundColor(Color.WHITE)
                                                    }
                                                    return view
                                                }
                                            }
                                            adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                            others_gender_spinner.adapter = adapt
                                            others_gender_spinner.visibility = View.VISIBLE
                                        } else {
                                            Log.i("onEmptyResponse", "Returned empty response")
                                        }
                                    }
                                    Log.d("onResponse : ", response.body().toString())
                                }
                            })
                        }
                        else -> {
                            others_gender_spinner.visibility = View.GONE
                            gender_text.visibility = View.GONE
                            gender_text.setText(gender.gender_name)
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            others_gender_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val gender = parent.selectedItem as Gender
                    when (gender.gender_key) {
                        0 -> {
                        }
                        99 -> {
                            gender_text.setText("")
                            gender_text.visibility = View.VISIBLE
                        }
                        else -> {
                            gender_text.setText(gender.gender_name)
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
            birth_year.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    if (birth_year.text.toString() != "") {
                        var year = birth_year.text.toString().toInt()
                        var nowYear = LocalDate.now().year
                        if (year > nowYear || year < 1880) {
                            messageToast("Invalid Year")
                            birth_year.setText("")
                        }
                    }
                }
            }

            birth_month.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    if (birth_month.text.toString() != "") {
                        var month = birth_month.text.toString().toInt()
                        if (month > 12 || month < 1) {
                            messageToast("Invalid Month")
                            birth_month.setText("")
                        } else if (birth_year.text.toString() != "") {
                            var now = LocalDate.now()
                            if (birth_month.text.toString().length<2){
                                birth_month.setText("0"+birth_month.text.toString())
                            }
                            var str: String = birth_year.text.toString() + "-" + birth_month.text.toString() + "-01"
                            var strdate: LocalDate = LocalDate.parse(
                                str.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                DateTimeFormatter.ISO_DATE
                            )
                            if (strdate > now) {
                                messageToast("Invalid Month")
                                birth_month.setText("")
                            }
                        }
                    }
                }
            }

            server?.getRace()?.enqueue(object : Callback<MutableList<Race>> {
                override fun onFailure(call: Call<MutableList<Race>>?, t: Throwable?) {
                    Log.d("onFailure : ", t?.message)
                    messageToast("onFailure : " + t?.message)

                    finish()
                }

                override fun onResponse(call: Call<MutableList<Race>>?, response: Response<MutableList<Race>>?) {
                    if (response!!.isSuccessful) {
                        if (response.body() != null) {
                            Log.i("onSuccess", response.body().toString())

                            var race_list: MutableList<Race> = mutableListOf<Race>()
                            race_list = response.body()!!
                            race_list?.add(Race(0, "Select Your Race"))
                            race_list?.sortBy { race -> race.race_key }

                            var adapt = object :
                                ArrayAdapter<Race>(
                                    this@CreatePrivateInformation,
                                    android.R.layout.simple_spinner_item,
                                    race_list
                                ) {
                                override fun isEnabled(position: Int): Boolean {
                                    return position != 0
                                }

                                override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                                    var view = super.getDropDownView(position, convertView, parent)
                                    var tv = view as TextView
                                    if (position == 0) {
                                        tv.setTextColor(Color.WHITE)
                                        tv.setBackgroundColor(Color.GRAY)
                                    } else {
                                        tv.setTextColor(Color.BLACK)
                                        tv.setBackgroundColor(Color.WHITE)
                                    }
                                    return view
                                }
                            }
                            adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            race_spinner.adapter = adapt
                        } else {
                            Log.i("onEmptyResponse", "Returned empty response")
                        }
                    }
                    Log.d("onResponse : ", response.body().toString())
                }
            })

            race_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val race = parent.selectedItem as Race
                    when (race.race_key) {
                        0 -> {
                        }
                        99 -> {
                            race_text.setText("")
                            race_text.visibility = View.VISIBLE
                        }
                        else -> {
                            race_text.setText(race.race_name)
                            race_text.visibility = View.GONE
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            server?.getCountry()?.enqueue(object : Callback<MutableList<Country>> {
                override fun onFailure(call: Call<MutableList<Country>>?, t: Throwable?) {
                    Log.d("onFailure : ", t?.message)
                    messageToast("onFailure : " + t?.message)

                    finish()
                }

                override fun onResponse(call: Call<MutableList<Country>>?, response: Response<MutableList<Country>>?) {
                    if (response!!.isSuccessful) {
                        if (response.body() != null) {
                            Log.i("onSuccess", response.body().toString())
                            var country_list = response.body()
                            country_list?.add(Country(0, "Select Your Country"))
                            country_list?.sortBy { country -> country.country_no }

                            var adapt = object :
                                ArrayAdapter<Country>(
                                    this@CreatePrivateInformation,
                                    android.R.layout.simple_spinner_item,
                                    country_list
                                ) {
                                override fun isEnabled(position: Int): Boolean {
                                    return position != 0
                                }

                                override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                                    var view = super.getDropDownView(position, convertView, parent)
                                    var tv = view as TextView
                                    if (position == 0) {
                                        tv.setTextColor(Color.WHITE)
                                        tv.setBackgroundColor(Color.GRAY)
                                    } else {
                                        tv.setTextColor(Color.BLACK)
                                        tv.setBackgroundColor(Color.WHITE)
                                    }
                                    return view
                                }
                            }
                            adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            country_spinner.adapter = adapt
                        } else {
                            Log.i("onEmptyResponse", "Returned empty response")
                        }
                    }
                    Log.d("onResponse : ", response.body().toString())
                }
            })

            country_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val country = parent.selectedItem as Country
                    when (country.country_no) {
                        0 -> {
                        }
                        999 -> {
                            country_text.setText("")
                            country_text.visibility = View.VISIBLE
                            admin_spinner.visibility = View.GONE
                            admin_text.setText("")
                            admin_text.visibility = View.VISIBLE
                            city_spinner.visibility = View.GONE
                            city_text.setText("")
                            city_text.visibility = View.VISIBLE
                        }
                        else -> {
                            country_text.setText(country.country_name)
                            country_text.visibility = View.GONE
                            admin_spinner.visibility = View.GONE
                            admin_text.setText("")
                            admin_text.visibility = View.GONE
                            city_spinner.visibility = View.GONE
                            city_text.setText("")
                            city_text.visibility = View.GONE

                            server?.getAdmin(country.country_no)?.enqueue(object : Callback<MutableList<Admin>> {
                                override fun onFailure(call: Call<MutableList<Admin>>?, t: Throwable?) {
                                    Log.d("onFailure : ", t?.message)
                                    messageToast("onFailure : " + t?.message)

                                    finish()
                                }

                                override fun onResponse(
                                    call: Call<MutableList<Admin>>?,
                                    response: Response<MutableList<Admin>>?
                                ) {
                                    if (response!!.isSuccessful) {
                                        if (response.body() != null) {
                                            Log.i("onSuccess", response.body().toString())
                                            var admin_list = response.body()
                                            admin_list?.add(
                                                Admin(
                                                    0,
                                                    "Select Your Admin",
                                                    country.country_no
                                                )
                                            )
                                            admin_list?.sortBy { admin -> admin.admin_no }

                                            var adapt = object : ArrayAdapter<Admin>(
                                                this@CreatePrivateInformation,
                                                android.R.layout.simple_spinner_item,
                                                admin_list
                                            ) {
                                                override fun isEnabled(position: Int): Boolean {
                                                    return position != 0
                                                }

                                                override fun getDropDownView(
                                                    position: Int,
                                                    convertView: View?,
                                                    parent: ViewGroup
                                                ): View {
                                                    var view = super.getDropDownView(position, convertView, parent)
                                                    var tv = view as TextView
                                                    if (position == 0) {
                                                        tv.setTextColor(Color.WHITE)
                                                        tv.setBackgroundColor(Color.GRAY)
                                                    } else {
                                                        tv.setTextColor(Color.BLACK)
                                                        tv.setBackgroundColor(Color.WHITE)
                                                    }
                                                    return view
                                                }
                                            }
                                            adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                            admin_spinner.adapter = adapt
                                            if (admin_list?.size!! > 2) {
                                                admin_spinner.visibility = View.VISIBLE
                                            }
                                        } else {
                                            Log.i("onEmptyResponse", "Returned empty response")
                                        }
                                    }
                                    Log.d("onResponse : ", response.body().toString())
                                }
                            })
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            admin_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val admin = parent.selectedItem as Admin
                    when (admin.admin_no) {
                        0 -> {
                        }
                        9999 -> {
                            admin_text.setText("")
                            admin_text.visibility = View.VISIBLE
                            city_spinner.visibility = View.GONE
                            city_text.setText("")
                            city_text.visibility = View.VISIBLE
                        }
                        else -> {
                            admin_text.setText(admin.admin_name)
                            admin_text.visibility = View.GONE
                            city_spinner.visibility = View.GONE
                            city_text.setText("")
                            city_text.visibility = View.GONE

                            server?.getCity(admin.admin_no)?.enqueue(object : Callback<MutableList<City>> {
                                override fun onFailure(call: Call<MutableList<City>>?, t: Throwable?) {
                                    Log.d("onFailure : ", t?.message)
                                    messageToast("onFailure : " + t?.message)

                                    finish()
                                }

                                override fun onResponse(
                                    call: Call<MutableList<City>>?,
                                    response: Response<MutableList<City>>?
                                ) {
                                    if (response!!.isSuccessful) {
                                        if (response.body() != null) {
                                            Log.i("onSuccess", response.body().toString())
                                            var city_list = response.body()
                                            city_list?.add(
                                                City(
                                                    0,
                                                    "Select Your City",
                                                    admin.admin_no
                                                )
                                            )
                                            city_list?.sortBy { city -> city.city_no }

                                            var adapt = object : ArrayAdapter<City>(
                                                this@CreatePrivateInformation,
                                                android.R.layout.simple_spinner_item,
                                                city_list
                                            ) {
                                                override fun isEnabled(position: Int): Boolean {
                                                    return position != 0
                                                }

                                                override fun getDropDownView(
                                                    position: Int,
                                                    convertView: View?,
                                                    parent: ViewGroup
                                                ): View {
                                                    var view = super.getDropDownView(position, convertView, parent)
                                                    var tv = view as TextView
                                                    if (position == 0) {
                                                        tv.setTextColor(Color.WHITE)
                                                        tv.setBackgroundColor(Color.GRAY)
                                                    } else {
                                                        tv.setTextColor(Color.BLACK)
                                                        tv.setBackgroundColor(Color.WHITE)
                                                    }
                                                    return view
                                                }
                                            }
                                            adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                            city_spinner.adapter = adapt
                                            if (city_list?.size!! > 2) {
                                                city_spinner.visibility = View.VISIBLE
                                            }
                                        } else {
                                            Log.i("onEmptyResponse", "Returned empty response")
                                        }
                                    }
                                    Log.d("onResponse : ", response.body().toString())
                                }
                            })
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            city_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val city = parent.selectedItem as City
                    when (city.city_no) {
                        0 -> {
                        }
                        99999 -> {
                            city_text.setText("")
                            city_text.visibility = View.VISIBLE
                        }
                        else -> {
                            city_text.setText(city.city_name)
                            city_text.visibility = View.GONE
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            server?.getJob("first")?.enqueue(object : Callback<MutableList<Job>> {
                override fun onFailure(call: Call<MutableList<Job>>?, t: Throwable?) {
                    Log.d("onFailure : ", t?.message)
                    messageToast("onFailure : " + t?.message)

                    finish()
                }

                override fun onResponse(call: Call<MutableList<Job>>?, response: Response<MutableList<Job>>?) {
                    if (response!!.isSuccessful) {
                        if (response.body() != null) {
                            Log.i("onSuccess", response.body().toString())
                            var job_list = response.body()
                            job_list?.add(
                                Job(
                                    "0",
                                    null,
                                    "Select Your Job Category 1st"
                                )
                            )
                            job_list?.sortBy { job -> job.job_key }

                            var adapt = object :
                                ArrayAdapter<Job>(
                                    this@CreatePrivateInformation,
                                    android.R.layout.simple_spinner_item,
                                    job_list
                                ) {
                                override fun isEnabled(position: Int): Boolean {
                                    return position != 0
                                }

                                override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                                    var view = super.getDropDownView(position, convertView, parent)
                                    var tv = view as TextView
                                    if (position == 0) {
                                        tv.setTextColor(Color.WHITE)
                                        tv.setBackgroundColor(Color.GRAY)
                                    } else {
                                        tv.setTextColor(Color.BLACK)
                                        tv.setBackgroundColor(Color.WHITE)
                                    }
                                    return view
                                }
                            }
                            adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            job1_spinner.adapter = adapt
                        } else {
                            Log.i("onEmptyResponse", "Returned empty response")
                        }
                    }
                    Log.d("onResponse : ", response.body().toString())
                }
            })

            job1_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val job = parent.selectedItem as Job
                    when (job.job_key) {
                        "0" -> {
                        }
                        "99-9999" -> {
                            job_text.setText("")
                            job_text.visibility = View.VISIBLE
                            job2_spinner.visibility = View.GONE
                            job3_spinner.visibility = View.GONE
                            job4_spinner.visibility = View.GONE
                        }
                        else -> {
                            job_text.setText(job.job_name)
                            job_text.visibility = View.GONE
                            job2_spinner.visibility = View.GONE
                            job3_spinner.visibility = View.GONE
                            job4_spinner.visibility = View.GONE

                            server?.getJob(job.job_key)?.enqueue(object : Callback<MutableList<Job>> {
                                override fun onFailure(call: Call<MutableList<Job>>?, t: Throwable?) {
                                    Log.d("onFailure : ", t?.message)
                                    messageToast("onFailure : " + t?.message)

                                    finish()
                                }

                                override fun onResponse(
                                    call: Call<MutableList<Job>>?,
                                    response: Response<MutableList<Job>>?
                                ) {
                                    if (response!!.isSuccessful) {
                                        if (response.body() != null) {
                                            Log.i("onSuccess", response.body().toString())
                                            var job_list = response.body()
                                            job_list?.add(
                                                Job(
                                                    "0",
                                                    null,
                                                    "Select Your Job Category 2nd"
                                                )
                                            )
                                            job_list?.sortBy { job -> job.job_key }

                                            var adapt = object :
                                                ArrayAdapter<Job>(
                                                    this@CreatePrivateInformation,
                                                    android.R.layout.simple_spinner_item,
                                                    job_list
                                                ) {
                                                override fun isEnabled(position: Int): Boolean {
                                                    return position != 0
                                                }

                                                override fun getDropDownView(
                                                    position: Int,
                                                    convertView: View?,
                                                    parent: ViewGroup
                                                ): View {
                                                    var view = super.getDropDownView(position, convertView, parent)
                                                    var tv = view as TextView
                                                    if (position == 0) {
                                                        tv.setTextColor(Color.WHITE)
                                                        tv.setBackgroundColor(Color.GRAY)
                                                    } else {
                                                        tv.setTextColor(Color.BLACK)
                                                        tv.setBackgroundColor(Color.WHITE)
                                                    }
                                                    return view
                                                }
                                            }
                                            adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                            job2_spinner.adapter = adapt
                                            if (job_list?.size!! > 3) {
                                                job2_spinner.visibility = View.VISIBLE
                                            }
                                        } else {
                                            Log.i("onEmptyResponse", "Returned empty response")
                                        }
                                    }
                                    Log.d("onResponse : ", response.body().toString())
                                }
                            })
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            job2_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val job = parent.selectedItem as Job
                    when (job.job_key) {
                        "0" -> {
                        }
                        "99-9999" -> {
                            job_text.setText("")
                            job_text.visibility = View.VISIBLE
                            job3_spinner.visibility = View.GONE
                            job4_spinner.visibility = View.GONE
                        }
                        else -> {
                            job_text.setText(job.job_name)
                            job_text.visibility = View.GONE
                            job3_spinner.visibility = View.GONE
                            job4_spinner.visibility = View.GONE

                            server?.getJob(job.job_key)?.enqueue(object : Callback<MutableList<Job>> {
                                override fun onFailure(call: Call<MutableList<Job>>?, t: Throwable?) {
                                    Log.d("onFailure : ", t?.message)
                                    messageToast("onFailure : " + t?.message)

                                    finish()
                                }

                                override fun onResponse(
                                    call: Call<MutableList<Job>>?,
                                    response: Response<MutableList<Job>>?
                                ) {
                                    if (response!!.isSuccessful) {
                                        if (response.body() != null) {
                                            Log.i("onSuccess", response.body().toString())
                                            var job_list = response.body()
                                            job_list?.add(
                                                Job(
                                                    "0",
                                                    null,
                                                    "Select Your Job Category 3nd"
                                                )
                                            )
                                            job_list?.sortBy { job -> job.job_key }

                                            var adapt = object :
                                                ArrayAdapter<Job>(
                                                    this@CreatePrivateInformation,
                                                    android.R.layout.simple_spinner_item,
                                                    job_list
                                                ) {
                                                override fun isEnabled(position: Int): Boolean {
                                                    return position != 0
                                                }

                                                override fun getDropDownView(
                                                    position: Int,
                                                    convertView: View?,
                                                    parent: ViewGroup
                                                ): View {
                                                    var view = super.getDropDownView(position, convertView, parent)
                                                    var tv = view as TextView
                                                    if (position == 0) {
                                                        tv.setTextColor(Color.WHITE)
                                                        tv.setBackgroundColor(Color.GRAY)
                                                    } else {
                                                        tv.setTextColor(Color.BLACK)
                                                        tv.setBackgroundColor(Color.WHITE)
                                                    }
                                                    return view
                                                }
                                            }
                                            adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                            job3_spinner.adapter = adapt
                                            if (job_list?.size!! > 3) {
                                                job3_spinner.visibility = View.VISIBLE
                                            }
                                        } else {
                                            Log.i("onEmptyResponse", "Returned empty response")
                                        }
                                    }
                                    Log.d("onResponse : ", response.body().toString())
                                }
                            })
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            job3_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val job = parent.selectedItem as Job
                    when (job.job_key) {
                        "0" -> {
                        }
                        "99-9999" -> {
                            job_text.setText("")
                            job_text.visibility = View.VISIBLE
                            job4_spinner.visibility = View.GONE
                        }
                        else -> {
                            job4_spinner.visibility = View.GONE

                            server?.getJob(job.job_key)?.enqueue(object : Callback<MutableList<Job>> {
                                override fun onFailure(call: Call<MutableList<Job>>?, t: Throwable?) {
                                    Log.d("onFailure : ", t?.message)
                                    messageToast("onFailure : " + t?.message)

                                    finish()
                                }

                                override fun onResponse(
                                    call: Call<MutableList<Job>>?,
                                    response: Response<MutableList<Job>>?
                                ) {
                                    if (response!!.isSuccessful) {
                                        if (response.body() != null) {
                                            Log.i("onSuccess", response.body().toString())
                                            var job_list = response.body()
                                            job_list?.add(
                                                Job(
                                                    "0",
                                                    null,
                                                    "Select Your Job"
                                                )
                                            )
                                            job_list?.sortBy { job -> job.job_key }

                                            var adapt = object :
                                                ArrayAdapter<Job>(
                                                    this@CreatePrivateInformation,
                                                    android.R.layout.simple_spinner_item,
                                                    job_list
                                                ) {
                                                override fun isEnabled(position: Int): Boolean {
                                                    return position != 0
                                                }

                                                override fun getDropDownView(
                                                    position: Int,
                                                    convertView: View?,
                                                    parent: ViewGroup
                                                ): View {
                                                    var view = super.getDropDownView(position, convertView, parent)
                                                    var tv = view as TextView
                                                    if (position == 0) {
                                                        tv.setTextColor(Color.WHITE)
                                                        tv.setBackgroundColor(Color.GRAY)
                                                    } else {
                                                        tv.setTextColor(Color.BLACK)
                                                        tv.setBackgroundColor(Color.WHITE)
                                                    }
                                                    return view
                                                }
                                            }
                                            adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                            job4_spinner.adapter = adapt
                                            if (job_list?.size!! > 3) {
                                                job4_spinner.visibility = View.VISIBLE
                                            }
                                        } else {
                                            Log.i("onEmptyResponse", "Returned empty response")
                                        }
                                    }
                                    Log.d("onResponse : ", response.body().toString())
                                }
                            })
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }

            job4_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val job = parent.selectedItem as Job
                    when (job.job_key) {
                        "0" -> {
                        }
                        "99-9999" -> {
                            job_text.setText("")
                            job_text.visibility = View.VISIBLE
                        }
                        else -> {
                            job_text.setText(job.job_name)
                            job_text.visibility = View.GONE
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            var edu_list = mutableListOf<String>(
                "Level of Education",
                "Elementary school graduation",
                "middle School graduation",
                "high school graduation",
                "Associate Degree of Arts",
                "Associate Degree of Science",
                "Bachelor of Arts",
                "Bachelor of Science",
                "Bachelor of Fine Art",
                "Bachelor of Law",
                "Master of Arts",
                "Master of Science",
                "Master of Fine Art",
                "Master of Business Administration",
                "Doctor of Philosophy",
                "Doctor of the science of Law",
                "Doctor of Medicine",
                "etc."
            )

            var adapt = object :
                ArrayAdapter<String>(this@CreatePrivateInformation, android.R.layout.simple_spinner_item, edu_list) {
                override fun isEnabled(position: Int): Boolean {
                    return position != 0
                }

                override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                    var view = super.getDropDownView(position, convertView, parent)
                    var tv = view as TextView
                    if (position == 0) {
                        tv.setTextColor(Color.WHITE)
                        tv.setBackgroundColor(Color.GRAY)
                    } else {
                        tv.setTextColor(Color.BLACK)
                        tv.setBackgroundColor(Color.WHITE)
                    }
                    return view
                }
            }

            adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            edu_spinner.adapter = adapt
            edu_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val grade = parent.selectedItem as String
                    when (grade) {
                        "Level of Education" -> {
                        }
                        "etc." -> {
                            edu_text.setText("")
                            edu_text.visibility = View.VISIBLE
                        }
                        else -> {
                            edu_text.setText(grade)
                            edu_text.visibility = View.GONE
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            submit.setOnClickListener() {
                when {
                    gender_text.text.toString().equals("") -> {
                        messageToast("Please enter your gender.")
                    }
                    birth_year.text.toString().equals("") -> {
                        messageToast("Please enter your birth year.")
                        birth_year.requestFocus()
                    }
                    birth_month.text.toString().equals("") -> {
                        messageToast("Please enter your birth month.")
                        birth_month.requestFocus()
                    }
                    race_text.text.toString().equals("") -> {
                        messageToast("Please enter your race.")
                    }
                    country_text.text.toString().equals("") -> {
                        messageToast("Please enter your country.")
                    }
                    admin_text.text.toString().equals("") -> {
                        messageToast("Please enter your admin.")
                    }
                    job_text.text.toString().equals("") -> {
                        messageToast("Please enter your job.")
                    }
                    annual_income.text.toString().equals("") -> {
                        messageToast("Please enter your annual income.")
                        annual_income.requestFocus()
                    }
                    edu_text.text.toString().equals("") -> {
                        messageToast("Please enter your Education Level.")
                    }
                    else -> {
                        val addRunnable = Runnable {
                            val personalData = PersonalData(
                                node_kn,
                                gender_text.text.toString(),
                                birth_year.text.toString() + "." + birth_month.text.toString(),
                                race_text.text.toString(),
                                country_text.text.toString() + "//" + admin_text.text.toString() + "//" + city_text.text.toString(),
                                job_text.text.toString(),
                                annual_income.text.toString().toInt(),
                                edu_text.text.toString()
                            )
                            if (himsdb?.personalDataDAO()?.getByNodeKn(node_kn) == null){
                                himsdb?.personalDataDAO()?.insert(personalData)
                            }else{
                                himsdb?.personalDataDAO()?.update(personalData)
                            }

                        }
                        val addThread = Thread(addRunnable)
                        addThread.start()

                        intent = Intent(this, HealthView::class.java)
                        intent.putExtra("node_kn", node_kn)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }else{
            finish()
        }
    }

    fun messageToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
