package com.hims.personal_node.Activitys.Fragment.Health

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import kotlinx.android.synthetic.main.fragment_health_list.*
import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import com.hims.personal_node.R
import java.util.*


class HealthList : Fragment() {

    var myCalendar = Calendar.getInstance()
    var startDate_check = false
    var endtDate_check = false

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
        //뷰 설정
        startDate.setOnClickListener {
            DatePickerDialog(
                this@HealthList.context,
                StartDatePicker,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        endDate.setOnClickListener {
            DatePickerDialog(
                this@HealthList.context,
                EndDatePicker,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun startupdateLabel() {
        val myFormat = "yyyy/MM/dd"    // 출력형식   2018/11/28
        val sdf = SimpleDateFormat(myFormat, Locale.KOREA)

        startDate.setText(sdf.format(myCalendar.time))
    }
    private fun endupdateLabel() {
        val myFormat = "yyyy/MM/dd"    // 출력형식   2018/11/28
        val sdf = SimpleDateFormat(myFormat, Locale.KOREA)

        endDate.setText(sdf.format(myCalendar.time))
    }
}
