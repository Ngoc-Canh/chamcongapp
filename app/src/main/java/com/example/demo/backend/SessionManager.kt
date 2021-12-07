package com.example.demo.backend

import android.R
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.DatePicker
import android.widget.TextView
import com.example.demo.Constant
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("PREF", Context.MODE_PRIVATE)

    companion object{
        const val USER_TOKEN = "tokenUser"
        const val USER_NAME = "userName"
        const val MANAGER_EMAIL = "managerEmail"
        const val MANAGER_NAME = "managerName"
        const val EMAIL = "email"
        const val TOTAL_DAY_OFF = "totalDayOff"
        const val IS_MANAGER = "isManager"
        const val IS_USER = "isUser"
        const val IS_HR = "isHR"
        const val TOKEN_DEVICE = "tokenDevice"
        const val NOTIFY_1 = "notify_1"
        const val NOTIFY_2 = "notify_2"

        var currentMonth: Int = 0
    }

    @SuppressLint("SimpleDateFormat")
    fun convertDateTimeToTimestamp(input: String): Long{
        val formatter= SimpleDateFormat("dd/MM/yyyy HH:ss")
        val date = formatter.parse(input)
        return date.time
    }

    fun myDatePickerDialog(outPut: TextView, ctx: Context){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val setListener: DatePickerDialog.OnDateSetListener =
            DatePickerDialog.OnDateSetListener{ p0: DatePicker?, p1: Int, p2: Int, p3: Int ->
                val mth = p2+1
                var rsMonth = ""
                var rsDay = ""
                rsMonth = if (mth < 10){
                    "0$mth"
                }else{
                    "$mth"
                }

                rsDay = if (p3 < 10){
                    "0$p3"
                }else{
                    "$p3"
                }
                val date = "$rsDay/$rsMonth/$p1"
                outPut.setText(date, TextView.BufferType.EDITABLE)
            }

        outPut.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                ctx,
                R.style.Theme_Holo_Light_Dialog_MinWidth, setListener, year, month, day)
            datePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            datePickerDialog.show()
        }
    }

    fun myDatePickerDialogDayOff(outPut: TextView, ctx: Context, hourStart: String, hourEnd: String): String{
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        var date = ""
        var dateStart: Long = 0
        var dateEnd: Long = 0

        val setListener: DatePickerDialog.OnDateSetListener =
            DatePickerDialog.OnDateSetListener{ p0: DatePicker?, p1: Int, p2: Int, p3: Int ->
                val mth = p2+1
                var rsMonth = ""
                var rsDay = ""
                rsMonth = if (mth < 10){
                    "0$mth"
                }else{
                    "$mth"
                }

                rsDay = if (p3 < 10){
                    "0$p3"
                }else{
                    "$p3"
                }
                date = "$rsDay/$rsMonth/$p1"
                outPut.setText(date, TextView.BufferType.EDITABLE)
                dateStart = convertDateTimeToTimestamp("$date $hourStart")
                dateEnd = convertDateTimeToTimestamp("$date $hourEnd")
            }

        outPut.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                ctx,
                R.style.Theme_Holo_Light_Dialog_MinWidth, setListener, year, month, day)
            datePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            datePickerDialog.show()
        }
        return "$dateStart, $dateEnd"
    }

    fun myTimePickerDialog(outPut: TextView, ctx: Context){
        var tHour: Int = 0
        var tMinute: Int = 0

        outPut.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                ctx,
                R.style.Theme_Holo_Light_Dialog_MinWidth,
                TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                    tHour = hour
                    tMinute = minute

                    val time = "$tHour:$tMinute"
                    val fmt24Hour = SimpleDateFormat("HH:mm")
                    try {
                        val parse = fmt24Hour.parse(time)
                        val fmt12Hour = SimpleDateFormat("HH:mm")
                        outPut.setText(fmt12Hour.format(parse), TextView.BufferType.EDITABLE)
                    } catch (e: ParseException){
                        println(e)
                    }
                }, 6, 0, true
            )
            timePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            timePickerDialog.updateTime(tHour, tMinute)
            timePickerDialog.show()
        }
    }

    fun getCurrentFormat(pattern: String): String {
        val dfm = SimpleDateFormat(pattern, Locale.getDefault())
        dfm.timeZone = TimeZone.getTimeZone("GMT+0700")
        return dfm.format(Date())
    }

    fun getMonthYearCalendar(month: Int): String{
        var date = Date()
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MONTH, currentMonth + month)
        date = calendar.time
        currentMonth += month
        
        return ("${android.text.format.DateFormat.format("MM", date)}/${android.text.format.DateFormat.format("yyyy", date)}")
    }

    fun refreshMonthCalendar(){
        currentMonth = 0
    }

    fun convertTimeStampToTime(timestamp: Long, format: String): String{
        val formatter = SimpleDateFormat(format)
        val date = if (timestamp > 1000000000000){
            Date(timestamp)
        }else{
            Date("${timestamp * 1000}".toLong())
        }
        formatter.timeZone = TimeZone.getTimeZone("GMT+0700")
        return formatter.format(date)
    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun fetchAuthToken(): String {
        return prefs.getString(USER_TOKEN, null).toString()
    }

    fun saveUserName(userName: String){
        val editor = prefs.edit()
        editor.putString(USER_NAME, userName)
        editor.apply()
    }

    fun fetchUserName(): String {
        return prefs.getString(USER_NAME, "NaN").toString()
    }

    fun saveManagerName(managerName: String){
        val editor = prefs.edit()
        editor.putString(MANAGER_NAME, managerName)
        editor.apply()
    }

    fun fetchManagerName(): String {
        return prefs.getString(MANAGER_NAME, null).toString()
    }

    fun saveManagerEmail(managerEmail: String){
        val editor = prefs.edit()
        editor.putString(MANAGER_EMAIL, managerEmail)
        editor.apply()
    }

    fun fetchManagerEmail(): String {
        return prefs.getString(MANAGER_EMAIL, null).toString()
    }

    fun saveMyEmail(myEmail: String){
        val editor = prefs.edit()
        editor.putString(EMAIL, myEmail)
        editor.apply()
    }

    fun fetchMyEmail(): String {
        return prefs.getString(EMAIL, null).toString()
    }

    fun saveDayOff(myEmail: String){
        val editor = prefs.edit()
        editor.putString(TOTAL_DAY_OFF, myEmail)
        editor.apply()
    }

    fun fetchDayOff(): String {
        return prefs.getString(TOTAL_DAY_OFF, "0").toString()
    }

    fun isUser(bool: Boolean){
        val editor = prefs.edit()
        editor.putBoolean(IS_USER, bool)
        editor.apply()
    }

    fun fetchIsUser(): Boolean {
        return prefs.getBoolean(IS_USER, false)
    }

    fun isManager(bool: Boolean){
        val editor = prefs.edit()
        editor.putBoolean(IS_MANAGER, bool)
        editor.apply()
    }

    fun fetchIsManager(): Boolean {
        return prefs.getBoolean(IS_MANAGER, false)
    }

    fun isHR(bool: Boolean){
        val editor = prefs.edit()
        editor.putBoolean(IS_HR, bool)
        editor.apply()
    }

    fun fetchIsHR(): Boolean {
        return prefs.getBoolean(IS_HR, false)
    }

    fun saveTokenDevice(token: String){
        val editor = prefs.edit()
        editor.putString(TOKEN_DEVICE, token)
        editor.apply()
    }

    fun fetchTokenDevice(): String? {
        return prefs.getString(TOKEN_DEVICE, null)
    }

    fun saveNotify1(active: Boolean){
        val editor = prefs.edit()
        editor.putBoolean(NOTIFY_1, active)
        editor.apply()
    }

    fun saveNotify2(active: Boolean){
        val editor = prefs.edit()
        editor.putBoolean(NOTIFY_2, active)
        editor.apply()
    }

    fun fetchNotify1(): Boolean {
        return prefs.getBoolean(NOTIFY_1, false)
    }

    fun fetchNotify2(): Boolean {
        return prefs.getBoolean(NOTIFY_2, false)
    }

    fun refreshAll(){
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}