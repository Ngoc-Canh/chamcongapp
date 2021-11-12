package com.example.demo.screen

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.demo.Constant
import com.example.demo.R
import com.example.demo.adapter.AdapterRequestDayOff
import com.example.demo.adapter.DeleteItem
import com.example.demo.backend.ApiClient
import com.example.demo.backend.RestAPI
import com.example.demo.backend.SessionManager
import com.example.demo.backend.entities.DayOffEntities
import com.example.demo.backend.entities.ListDayOffEntities
import okhttp3.Request
import okio.Timeout
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RequestDayOffActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, DeleteItem {
    private lateinit var btnCreateDayOff: Button
    private lateinit var btnClose: Button
    private lateinit var btnWaiting: Button
    private lateinit var btnAccept: Button
    private lateinit var btnDecline: Button
    private lateinit var type: String
    private lateinit var timeStart: String
    private lateinit var timeEnd: String
    private lateinit var edtDateStart: TextView
    private lateinit var edtDateEnd: TextView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: AdapterRequestDayOff
    private lateinit var rcvDayOff: RecyclerView
    private lateinit var lstData: ArrayList<DayOffEntities>
    private lateinit var lstWaiting: ArrayList<DayOffEntities>
    private lateinit var lstApprove: ArrayList<DayOffEntities>
    private lateinit var lstDecline: ArrayList<DayOffEntities>
    private var tsStartForStartDate: Long = 0
    private var tsEndForStartDate: Long = 0
    private var tsStartForEndDate: Long = 0
    private var tsEndForEndDate: Long = 0
    private val request = ApiClient.getClient().create(RestAPI::class.java)
    lateinit var token: String

    val BUTTON_WAITING = 1
    val BUTTON_APPROVE = 2
    val BUTTON_DECLINE = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_day_off)
        sessionManager = SessionManager(this)
        token = "Token ${sessionManager.fetchAuthToken()}"

        btnCreateDayOff = findViewById(R.id.btn_createRequestDayOff)
        btnClose = findViewById(R.id.btn_closeFragmentRequestDayOff)
        btnWaiting = findViewById(R.id.btnWaiting)
        btnAccept = findViewById(R.id.btnApprove)
        btnDecline = findViewById(R.id.btnDecline)
        rcvDayOff = findViewById(R.id.rcv_requestDayOff)
        swipeRefresh = findViewById(R.id.swipeRefreshRequestDayOffActivity)

        swipeRefresh.isRefreshing = true
        swipeRefresh.setOnRefreshListener {
            loadData()
        }

        if(sessionManager.fetchManagerEmail() == ""){
            btnCreateDayOff.visibility = View.GONE
        }

        lstData = ArrayList()
        loadData()

        btnCreateDayOff.setOnClickListener {
            openDialog(Gravity.CENTER)
        }

        btnClose.setOnClickListener {
            finish()
        }

        btnWaiting.setOnClickListener {
            buttonActive(BUTTON_WAITING)
            adapter.setData(lstWaiting)
        }

        btnAccept.setOnClickListener {
            buttonActive(BUTTON_APPROVE)
            adapter.setData(lstApprove)
        }

        btnDecline.setOnClickListener {
            buttonActive(BUTTON_DECLINE)
            adapter.setData(lstDecline)
        }

    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun openDialog(gravity: Int){
        val dialog: Dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_dayoff_layout)

        val window = dialog.window ?: return

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttribute: WindowManager.LayoutParams = window.attributes
        windowAttribute.gravity = gravity
        window.attributes = windowAttribute

        dialog.setCancelable(true)

        val spn_type = dialog.findViewById<Spinner>(R.id.spinner1)
        val spinnerEnd = dialog.findViewById<Spinner>(R.id.spinnerEnd)
        val spinnerStart = dialog.findViewById<Spinner>(R.id.spinnerStart)
        edtDateStart = dialog.findViewById(R.id.edt_dateStart)
        edtDateEnd = dialog.findViewById(R.id.edt_dateEnd)
        val tvManager = dialog.findViewById<TextView>(R.id.tvManagerDayOff)
        val btnClose = dialog.findViewById<Button>(R.id.btn_close)
        val btnSend = dialog.findViewById<Button>(R.id.btn_send)
        val validLayout = dialog.findViewById<LinearLayout>(R.id.layout_valid)
        val validMessage = dialog.findViewById<TextView>(R.id.message_valid)

        val currentDate = Calendar.getInstance().time
        val dfm = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = dfm.format(currentDate)
        edtDateStart.text = date
        edtDateEnd.text = date

        tvManager.text = sessionManager.fetchManagerName()
        sessionManager.myDatePickerDialog(edtDateStart, this)
        sessionManager.myDatePickerDialog(edtDateEnd, this)

        edtDateStart.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                when(timeStart){
                    "Ca Sáng" -> {
                        tsStartForStartDate = convertDateTimeToTimestamp("$p0 01:00")
                        tsEndForStartDate = convertDateTimeToTimestamp("$p0 12:00")
                    }
                    "Ca Chiều" -> {
                        tsStartForStartDate = convertDateTimeToTimestamp("$p0 13:00")
                        tsEndForStartDate = convertDateTimeToTimestamp("$p0 23:00")
                    }
                    "Cả Ngày" -> {
                        tsStartForStartDate = convertDateTimeToTimestamp("$p0 01:00")
                        tsEndForStartDate = convertDateTimeToTimestamp("$p0 23:00")
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        edtDateEnd.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                when(timeEnd){
                    "Ca Sáng" -> {
                        tsStartForEndDate = convertDateTimeToTimestamp("$p0 01:00")
                        tsEndForEndDate = convertDateTimeToTimestamp("$p0 12:00")
                    }
                    "Ca Chiều" -> {
                        tsStartForEndDate = convertDateTimeToTimestamp("$p0 13:00")
                        tsEndForEndDate = convertDateTimeToTimestamp("$p0 23:00")
                    }
                    "Cả Ngày" -> {
                        tsStartForEndDate = convertDateTimeToTimestamp("$p0 01:00")
                        tsEndForEndDate = convertDateTimeToTimestamp("$p0 23:00")
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        })

        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(this, R.array.dayOff, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spn_type.adapter = adapter
        spn_type.onItemSelectedListener = this

        val adapter2: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(this, R.array.ca, android.R.layout.simple_spinner_item)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEnd.adapter = adapter2
        spinnerStart.adapter = adapter2
        spinnerEnd.onItemSelectedListener = this
        spinnerStart.onItemSelectedListener = this


        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        btnSend.setOnClickListener {
            swipeRefresh.isRefreshing = true
            if(edtDateStart.text.isEmpty() || edtDateEnd.text.isEmpty()){
                validLayout.visibility = View.VISIBLE
                validMessage.text = "Bạn chưa nhập đầy đủ thông tin"
            }else{
                validLayout.visibility = View.GONE
                val dateStart = dfm.parse(edtDateStart.text.toString())
                val dateEnd = dfm.parse(edtDateEnd.text.toString())

                val data = DayOffEntities(null, tsStartForStartDate, tsEndForStartDate, tsStartForEndDate, tsEndForEndDate,
                    dateStart, dateEnd, type,null ,null ,
                    null ,null, null, null, null)
                val call = request.createDayOff(token, data)
                call.enqueue(object : Callback<DayOffEntities> {
                    override fun onResponse(
                        call: Call<DayOffEntities>,
                        response: Response<DayOffEntities>
                    ) {
                        if (response.code() == 202){
                            dialog.dismiss()
                            loadData()
                            validLayout.visibility = View.GONE
                        }else{
                            validLayout.visibility = View.VISIBLE
                            val jObjError = JSONObject(response.errorBody()?.string())
                            validMessage.text = jObjError["msg"].toString()
                            swipeRefresh.isRefreshing = false
                        }
                    }

                    override fun onFailure(call: Call<DayOffEntities>, t: Throwable) {
                        swipeRefresh.isRefreshing = false
                        validLayout.visibility = View.VISIBLE
                        validMessage.text = "Có lỗi xảy ra xin hãy thử lại."
                    }

                })
            }
        }

        dialog.show()
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        when (p0?.id) {
            R.id.spinner1 -> {
                type = if (p2 == 0){
                    "paid_leave"
                }else {
                    "unpaid_leave"
                }
            }
            R.id.spinnerStart ->  {
                timeStart = p0.getItemAtPosition(p2).toString()
                val chooseDate = edtDateStart.text
                when(timeStart){
                    "Ca Sáng" -> {
                        tsStartForStartDate = convertDateTimeToTimestamp("$chooseDate 01:00")
                        tsEndForStartDate = convertDateTimeToTimestamp("$chooseDate 12:00")
                    }
                    "Ca Chiều" -> {
                        tsStartForStartDate = convertDateTimeToTimestamp("$chooseDate 13:00")
                        tsEndForStartDate = convertDateTimeToTimestamp("$chooseDate 23:00")
                    }
                    "Cả Ngày" -> {
                        tsStartForStartDate = convertDateTimeToTimestamp("$chooseDate 01:00")
                        tsEndForStartDate = convertDateTimeToTimestamp("$chooseDate 23:00")
                    }
                }
            }
            R.id.spinnerEnd ->  {
                timeEnd = p0.getItemAtPosition(p2).toString()
                val chooseDate = edtDateEnd.text

                when(timeEnd){
                    "Ca Sáng" -> {
                        tsStartForEndDate = convertDateTimeToTimestamp("$chooseDate 01:00")
                        tsEndForEndDate = convertDateTimeToTimestamp("$chooseDate 12:00")
                    }
                    "Ca Chiều" -> {
                        tsStartForEndDate = convertDateTimeToTimestamp("$chooseDate 13:00")
                        tsEndForEndDate = convertDateTimeToTimestamp("$chooseDate 23:00")
                    }
                    "Cả Ngày" -> {
                        tsStartForEndDate = convertDateTimeToTimestamp("$chooseDate 01:00")
                        tsEndForEndDate = convertDateTimeToTimestamp("$chooseDate 23:00")
                    }
                }
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    private fun loadData(){
        lstData = ArrayList()
        val call = request.listDayOff(token)
        call.enqueue(object: Callback<ListDayOffEntities>{
            override fun onResponse(
                call: Call<ListDayOffEntities>,
                response: Response<ListDayOffEntities>
            ) {
                if (response.code() == 200){
                    response.body()?.data?.forEach {
                        lstData.add(it)
                        setLayout()
                        swipeRefresh.isRefreshing = false
                    }
                }
            }

            override fun onFailure(call: Call<ListDayOffEntities>, t: Throwable) {
                swipeRefresh.isRefreshing = false
                Constant.dialogError(this@RequestDayOffActivity, "Có lỗi xảy ra vui lòng thử lại.")
            }

        })
    }

    private fun buttonActive(btn: Int){
        when(btn){
            BUTTON_WAITING -> {
                btnWaiting.setBackgroundColor(Color.LTGRAY)
                btnAccept.setBackgroundColor(Color.WHITE)
                btnDecline.setBackgroundColor(Color.WHITE)
            }

            BUTTON_APPROVE -> {
                btnWaiting.setBackgroundColor(Color.WHITE)
                btnAccept.setBackgroundColor(Color.LTGRAY)
                btnDecline.setBackgroundColor(Color.WHITE)
            }

            BUTTON_DECLINE -> {
                btnWaiting.setBackgroundColor(Color.WHITE)
                btnAccept.setBackgroundColor(Color.WHITE)
                btnDecline.setBackgroundColor(Color.LTGRAY)
            }
        }
    }

    private fun setLayout(){
        lstWaiting = ArrayList()
        lstApprove = ArrayList()
        lstDecline = ArrayList()
        
        if (lstData.size >= 2){
            rcvDayOff.layoutManager = GridLayoutManager(this, 2)
        }else{
            rcvDayOff.layoutManager = LinearLayoutManager(this)
        }
        lstData.forEach {
            when (it.manager_confirm){
                "waiting" -> {
                    lstWaiting.add(it)
                }
                "accept" -> {
                    lstApprove.add(it)
                }
                else -> {
                    lstDecline.add(it)
                }
            }
        }

        adapter = AdapterRequestDayOff(lstData,this, this)
        rcvDayOff.adapter = adapter
        adapter.setData(lstWaiting)

    }

    @SuppressLint("SimpleDateFormat")
    private fun convertDateTimeToTimestamp(input: String): Long{
        val formatter= SimpleDateFormat("dd/MM/yyyy HH:ss")
        formatter.timeZone = TimeZone.getTimeZone("GMT+0700")
        val date = formatter.parse(input)
        return date.time
    }

    override fun onClickItem(id: Int) {
        swipeRefresh.isRefreshing = true
        openDialog(id, "Bạm có muốn xóa yêu cầu này không ??")
    }

    @SuppressLint("SetTextI18n")
    private fun openDialog(id: Int, msg: String){
        val dialog = Dialog(this)
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
        val messageConfirm = dialog.findViewById<TextView>(R.id.message_confirm)

        messageConfirm.text = msg

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        btnSend.setOnClickListener {
            dialog.dismiss()
            swipeRefresh.isRefreshing = true
            val call = request.deleteDayOff(token, id = id)
            call.enqueue(object : Callback<List<String>> {
                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                    if (response.code() == 200){
                        loadData()
                        swipeRefresh.isRefreshing = false
                        val msg = response.body()!![0].split(":")[1]
                        Constant.dialogSuccess(this@RequestDayOffActivity, msg.toString())
                    }else{
                        swipeRefresh.isRefreshing = false
                        val jObjError = JSONObject(response.errorBody()?.string())
                        Constant.dialogError(this@RequestDayOffActivity, jObjError["msg"].toString())
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    swipeRefresh.isRefreshing = false
                    Constant.dialogError(this@RequestDayOffActivity, "${t.message}")
                }
            })
        }

        dialog.show()
    }
}