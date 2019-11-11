package com.hims.personal_node.Activitys.Fragment.Health

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import kotlinx.android.synthetic.main.fragment_health_list.*
import android.app.DatePickerDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hims.personal_node.Activitys.Adaoter.HealthListRecycleAdapter
import com.hims.personal_node.Activitys.ViewModel.Health.HealthViewModel
import com.hims.personal_node.Activitys.ViewModel.Health.HealthViewModelFactory
import com.hims.personal_node.R
import java.util.*
import android.content.DialogInterface

class HealthList(var node_kn:String, var mContext:Context) : Fragment() {

    var myCalendar = Calendar.getInstance()
    var startDate_check = false
    var endtDate_check = false
    private lateinit var healthViewModel: HealthViewModel

    var StartDatePicker: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            startupdateLabel()
            startDate_check = true
        }

    var EndDatePicker: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            endupdateLabel()
            endtDate_check = true
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_health_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val recyclerView = view?.findViewById<RecyclerView>(R.id.health_list_recycle_form)
        var adapter = HealthListRecycleAdapter(mContext, node_kn)
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(mContext)

        healthViewModel = ViewModelProvider(this, HealthViewModelFactory(getActivity()?.application!!, node_kn)).get(HealthViewModel::class.java)
        healthViewModel.allHealth.observe(this, androidx.lifecycle.Observer { healths ->
            healths?.let { adapter.setHealth(it) }
        })
        //뷰 설정
        startDate.setOnClickListener {
            var datepicker = DatePickerDialog(
                this@HealthList.context,
                StartDatePicker,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datepicker.setButton(
                DialogInterface.BUTTON_NEGATIVE, getString(R.string.reset)
            ) { dialog, which ->
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    // Do Stuff
                    startupdateReset()
                }
            }
            datepicker.show()
        }
        endDate.setOnClickListener {
            var datepicker = DatePickerDialog(
                this@HealthList.context,
                EndDatePicker,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datepicker.setButton(
                DialogInterface.BUTTON_NEGATIVE, getString(R.string.reset)
            ) { dialog, which ->
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    // Do Stuff
                    endupdateReset()
                }
            }
            datepicker.show()
        }
    }

    private fun startupdateLabel() {
        val myFormat = "yyyy/MM/dd"    // 출력형식   2018/11/28
        val sdf = SimpleDateFormat(myFormat, Locale.KOREA)

        startDate.setText(sdf.format(myCalendar.time))
    }

    private fun startupdateReset() {
        startDate.setText(null)
        startDate.setHint("Start Date")
    }

    private fun endupdateLabel() {
        val myFormat = "yyyy/MM/dd"    // 출력형식   2018/11/28
        val sdf = SimpleDateFormat(myFormat, Locale.KOREA)

        endDate.setText(sdf.format(myCalendar.time))
    }

    private fun endupdateReset() {
        endDate.setText(null)
        endDate.setHint("Start Date")
    }
}
