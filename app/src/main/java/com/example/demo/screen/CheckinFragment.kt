package com.example.demo.screen

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.demo.Constant
import com.example.demo.Constant.Companion.CHECK_IN
import com.example.demo.Constant.Companion.CHECK_OUT
import com.example.demo.R
import com.example.demo.backend.ApiClient
import com.example.demo.backend.RestAPI
import com.example.demo.backend.SessionManager
import com.example.demo.backend.entities.Event
import com.example.demo.backend.entities.EventDetail
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CheckinFragment : Fragment(){
    private var handlerAnimation = Handler()
    private var arrWeek = arrayOf( "Chủ Nhật", "Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy")
    private lateinit var countDownGlobalVar: CountDownTimer
    private lateinit var sessionManager: SessionManager
    private lateinit var startTime: String
    private lateinit var endTime: String
    private lateinit var token: String
    private lateinit var btnCheckIn: Button
    private lateinit var txtCheckIn: TextView
    private lateinit var txtCheckOut: TextView
    private lateinit var txtNoneEvent: TextView
    private lateinit var txtValidateIp: TextView
    private lateinit var request: RestAPI
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private var currentHour: Int = 0
    private var flag: String? = CHECK_IN
    private var isRunning = false
    private var isValid = true
    private var acceptCheckOut = false


    override fun onResume() {
        super.onResume()
        runnable.run()
        currentTime.run()
    }

    override fun onDestroy() {
        super.onDestroy()
        handlerAnimation.removeCallbacks(runnable)
        handlerAnimation.removeCallbacks(currentTime)

        if (isRunning){
            countDownGlobalVar.onFinish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_checkin, container, false)
    }

    private var runnable = object : Runnable {
        override fun run() {
            val imgAnimation1 = view?.findViewById<ImageView>(R.id.imgAnimation1)
            val imgAnimation2 = view?.findViewById<ImageView>(R.id.imgAnimation2)

            imgAnimation1?.animate()?.scaleX(1.3f)?.scaleY(1.3f)?.alpha(0f)?.setDuration(1000)
                ?.withEndAction {
                    imgAnimation1.scaleX = 1f
                    imgAnimation1.scaleY = 1f
                    imgAnimation1.alpha = 1f
                }

            imgAnimation2?.animate()?.scaleX(1.3f)?.scaleY(1.3f)?.alpha(0f)?.setDuration(700)
                ?.withEndAction {
                    imgAnimation2.scaleX = 1f
                    imgAnimation2.scaleY = 1f
                    imgAnimation2.alpha = 1f
                }

            handlerAnimation.postDelayed(this, 1500)
        }
    }

    private var currentTime = object: Runnable {
        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        override fun run() {
            val tvRealTime = view?.findViewById<TextView>(R.id.realTime)
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"))
            val currentTime = calendar.time
            val fmDate = SimpleDateFormat("HH:mm:ss")
            fmDate.timeZone = TimeZone.getTimeZone("GMT+7")

            tvRealTime?.text = "Hôm nay " + fmDate.format(currentTime)
            handlerAnimation.postDelayed(this, 1000)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentDate = view.findViewById<TextView>(R.id.currentDate)
        btnCheckIn = view.findViewById(R.id.checkIn)

        txtCheckIn = view.findViewById(R.id.info_checkIn)
        txtCheckOut = view.findViewById(R.id.info_checkOut)
        txtNoneEvent = view.findViewById(R.id.none_event)
        txtValidateIp = view.findViewById(R.id.validateIp)
        txtCheckIn.visibility = View.GONE
        txtCheckOut.visibility = View.GONE

        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener{ loadData() }
        swipeRefresh.isRefreshing = true

        sessionManager = SessionManager(activity?.applicationContext!!)

        val calendar = Calendar.getInstance()
        currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val time = calendar.time
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

        val df = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val strDate = df.format(time)

        currentDate.text = "${convertDayOfWeek(dayOfWeek)}, Ngày $strDate"

        token = "Token ${sessionManager.fetchAuthToken()}"

        request = ApiClient.getClient().create(RestAPI::class.java)

        loadData()

        btnCheckIn.setOnClickListener {
            swipeRefresh.isRefreshing = true
            if(isValid){
                openDialog()
            }else{
                when (flag) {
                    CHECK_IN -> {
                        btnCheckIn.isEnabled = false
                        checkInFunc()
                    }

                    CHECK_OUT -> {
                        if (acceptCheckOut){
                            btnCheckIn.isEnabled = false
                            checkOutFunc()
                        }else{
                            Constant.dialogError(requireActivity(), "Thời gian check in phải trên 5' mới có thế check out")
                        }
                    }
                }
            }
        }
    }


    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun countUpFunction(btn_txt: Button?, startTime: String, endTime: String?): CountDownTimer {
        val intervalSeconds: Long = 1

        val format = SimpleDateFormat("HH:mm:ss")
        val startCheckIn = format.parse(startTime)
        val currentTimeStr = sessionManager.getCurrentFormat("HH:mm:ss")
        val currentTime: Date = format.parse(currentTimeStr)

        var diff = currentTime.time - startCheckIn.time
        if (endTime != null){
            val checkout = format.parse(endTime)
            diff = checkout.time - startCheckIn.time
        }

        var timeSecond = diff / 1000
        var hour = (timeSecond / 3600).toInt()
        timeSecond -= hour * 3600
        var min = (timeSecond / 60).toInt()
        timeSecond -= min * 60
        var sec = timeSecond.toInt()

        if (endTime != null || currentHour == 12){
            val strTimer = "${if (hour < 10) "0$hour" else hour}:" +
                    "${if (min < 10) "0$min" else min}:" +
                    "${if (sec < 10) "0$sec" else sec}"

            btn_txt?.text = "Chấm công \n$strTimer"
        }

        return object: CountDownTimer(300000000, intervalSeconds * 1000){
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                sec++
                if (sec == 60){
                    min++
                    sec=0
                }

                if(min==60){
                    min=0
                    hour++
                }

                if (hour == 24) {
                    hour = 0
                }

                val strTimer = "${if (hour < 10) "0$hour" else hour}:" +
                        "${if (min < 10) "0$min" else min}:" +
                        "${if (sec < 10) "0$sec" else sec}"

                btn_txt?.text = "Chấm công \n$strTimer"

                if (!isRunning){
                    isRunning = true
                }

                acceptCheckOut = if (min >= 5){
                    true
                }else{
                    true
                }

                if (endTime != null || currentHour == 12){
                    onFinish()
                }
            }

            override fun onFinish() {
                isRunning = false
                cancel()
            }
        }
    }

    private fun convertDayOfWeek(day: Int): String{
        return arrWeek[day]
    }

    private fun loadData(){
        val call = request.getEvent(token)
        call.enqueue(object: Callback<Event>{
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<Event>, response: Response<Event>) {
                if (response.body()?.valid_ip == true){
                    txtValidateIp.visibility = View.GONE
                    isValid = true
                }else{
                    txtValidateIp.visibility = View.VISIBLE
                    isValid = false
                }

                if (response.body()?.is_holiday == true || response.body()?.is_day_off == true){
                    if(response.body()?.is_holiday == true){
                        btnCheckIn.text = "TODAY\nIS\nHOLIDAY"
                    }

                    if (response.body()?.is_day_off == true){
                        btnCheckIn.text = "HAVE A\nNICE DAY OFF"
                    }

                    btnCheckIn.isEnabled = false
                    btnCheckIn.setBackgroundResource(R.drawable.circle_holiday)
                    return
                }

                val list: ArrayList<EventDetail>? = response.body()?.event
                if (list != null) {
                    for (item in list){
                        when (item.event_type) {
                            CHECK_IN -> {
                                startTime = sessionManager.convertTimeStampToTime(item.created_at!!, "HH:mm:ss")
                                countDownGlobalVar = countUpFunction(btnCheckIn, startTime, null)
                                countDownGlobalVar.start()
                                txtCheckIn.visibility = View.VISIBLE
                                txtNoneEvent.visibility = View.GONE
                                txtCheckIn.text = "Lúc đến: $startTime"
                                flag = CHECK_OUT
                            }
                            CHECK_OUT -> {
                                endTime = sessionManager.convertTimeStampToTime(item.created_at!!, "HH:mm:ss")
                                btnCheckIn.text = endTime
                                countDownGlobalVar.onFinish()
                                countDownGlobalVar = countUpFunction(btnCheckIn, startTime, endTime)
                                txtCheckOut.visibility = View.VISIBLE
                                txtNoneEvent.visibility = View.GONE
                                txtCheckOut.text = "Lúc về: $endTime"
                                flag = CHECK_IN
                            }
                            else -> {
                                txtNoneEvent.visibility = View.VISIBLE
                                txtNoneEvent.text = "Bạn chưa chấm công"
                                flag = CHECK_IN
                            }
                        }
                    }
                }
                swipeRefresh.isRefreshing = false
            }

            override fun onFailure(call: Call<Event>, t: Throwable) {
                Constant.dialogError(activity!!, "Có lỗi xảy ra vui lòng thử lại.")
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun openDialog(){
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_confirm)

        val window = dialog.window ?: return

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttribute: WindowManager.LayoutParams = window.attributes
        windowAttribute.gravity = Gravity.CENTER
        window.attributes = windowAttribute

        dialog.setCancelable(true)

        val btnClose = dialog.findViewById<Button>(R.id.cancelConfirm)
        val btnSend = dialog.findViewById<Button>(R.id.approveRequest)
        val messageConfirm = dialog.findViewById<Button>(R.id.message_confirm)

        messageConfirm.text = "Bạn đang chấm công ngoài phạm vi. Công này sẽ gửi tới quản lý của bạn để xét duyệt.\n Bạn có đồng ý chấm công không ?"

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        btnSend.setOnClickListener {
            when (flag) {
                CHECK_IN -> {
                    checkInFunc()
                }

                CHECK_OUT -> {
                    checkOutFunc()
                }
            }
        }

        dialog.show()
    }

    private fun checkInFunc(){
        flag = CHECK_OUT
        val callCheckIn = request.checkIn(token)
        callCheckIn.enqueue(object: Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.code() == 200){
                    loadData()
                    Constant.dialogSuccess(activity!!, "Check in thành công. Chúc bạn có một ngày làm việc hiệu quả.")
                    btnCheckIn.isEnabled = false
                }else{
                    val jObjError = JSONObject(response.errorBody()?.string())
                    Constant.dialogError(activity!!, jObjError["msg"].toString())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                flag = CHECK_IN
                Constant.dialogError(activity!!, "Có lỗi xảy ra vui lòng thử lại.")
                btnCheckIn.isEnabled = true
            }
        })
    }

    private fun checkOutFunc(){
        flag = CHECK_IN
        val callCheckOut = request.checkOut(token)
        callCheckOut.enqueue(object: Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.code() == 200){
                    loadData()
                    Constant.dialogSuccess(activity!!, "Check out thành công. Chúc bạn buổi chiều tốt lành.")
                }else{
                    val jObjError = JSONObject(response.errorBody()?.string())
                    Constant.dialogError(activity!!, jObjError["msg"].toString())
                }
                btnCheckIn.isEnabled = true
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                flag = CHECK_OUT
                Constant.dialogError(activity!!, "Có lỗi xảy ra vui lòng thử lại.")
                btnCheckIn.isEnabled = true
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun dialogTimeOut(){
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_error)

        val window = dialog.window ?: return

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttribute: WindowManager.LayoutParams = window.attributes
        windowAttribute.gravity = Gravity.CENTER
        window.attributes = windowAttribute

        dialog.setCancelable(true)

        val msgError = dialog.findViewById<TextView>(R.id.msgError)
        val btnOk = dialog.findViewById<Button>(R.id.btnOK)

        msgError.text = "Có lỗi xày ra vui lòng thử lại."

        btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}