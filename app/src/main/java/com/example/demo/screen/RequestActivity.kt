package com.example.demo.screen

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.demo.Constant
import com.example.demo.Constant.Companion.CHECK_IN
import com.example.demo.Constant.Companion.CHECK_OUT
import com.example.demo.R
import com.example.demo.adapter.DeleteItem
import com.example.demo.adapter.RecyclerViewChamBu
import com.example.demo.backend.ApiClient
import com.example.demo.backend.RestAPI
import com.example.demo.backend.SessionManager
import com.example.demo.backend.entities.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class RequestActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, DeleteItem {
    lateinit var btnCreate: Button
    lateinit var btnClose: Button
    lateinit var type: String
    lateinit var btnWaiting: Button
    lateinit var btnApprove: Button
    lateinit var btnDecline: Button
    lateinit var rcvRequest: RecyclerView
    lateinit var lstData: ArrayList<EventDetail>
    lateinit var lstWaiting: ArrayList<EventDetail>
    lateinit var lstDecline: ArrayList<EventDetail>
    lateinit var lstAccept: ArrayList<EventDetail>
    lateinit var sessionManager: SessionManager
    lateinit var recycleViewAdapter: RecyclerViewChamBu
    lateinit var swipeRefreshSubmission: SwipeRefreshLayout
    lateinit var token: String
    private val request = ApiClient.getClient().create(RestAPI::class.java)

    val BUTTON_WAITING = 1
    val BUTTON_APPROVE = 2
    val BUTTON_DECLINE = 3
    var isActive = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)

        btnCreate = findViewById(R.id.btn_createRequest)
        btnClose = findViewById(R.id.btn_closeFragmentRequest)
        btnWaiting = findViewById(R.id.btnWaiting)
        btnApprove = findViewById(R.id.btnApprove)
        btnDecline = findViewById(R.id.btnDecline)
        rcvRequest = findViewById(R.id.rcv_request)
        swipeRefreshSubmission = findViewById(R.id.swipeRefreshSubmission)

        swipeRefreshSubmission.setOnRefreshListener {
            loadData()
        }
        swipeRefreshSubmission.isRefreshing = true



        sessionManager = SessionManager(this)
        token = "Token ${sessionManager.fetchAuthToken()}"

        if (sessionManager.fetchManagerName() == "") {
            btnCreate.visibility = View.GONE
        }

        btnClose.setOnClickListener {
            finish()
        }

        btnCreate.setOnClickListener {
            openDialog(Gravity.CENTER)
        }

        isActive = buttonActive(BUTTON_WAITING)

        btnWaiting.setOnClickListener {
            isActive = buttonActive(BUTTON_WAITING)
            recycleViewAdapter.setData(lstWaiting)
        }

        btnApprove.setOnClickListener {
            isActive = buttonActive(BUTTON_APPROVE)
            recycleViewAdapter.setData(lstAccept)
        }

        btnDecline.setOnClickListener {
            isActive = buttonActive(BUTTON_DECLINE)
            recycleViewAdapter.setData(lstDecline)
        }

        loadData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openDialog(positionGravity: Int) {
        val dialog: Dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_layout)

        val window = dialog.window ?: return

        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttribute: WindowManager.LayoutParams = window.attributes
        windowAttribute.gravity = positionGravity
        window.attributes = windowAttribute

        dialog.setCancelable(true)

        val spn_type = dialog.findViewById<Spinner>(R.id.spinner1)

        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.type,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spn_type.adapter = adapter
        spn_type.onItemSelectedListener = this

        val btn_close = dialog.findViewById<Button>(R.id.btn_close)
        val btn_send = dialog.findViewById<Button>(R.id.btn_send)
        val edt_date = dialog.findViewById<TextView>(R.id.edt_date)
        val edt_time = dialog.findViewById<TextView>(R.id.edt_time)
        val valid_layout = dialog.findViewById<LinearLayout>(R.id.layout_valid)
        val tvRequestManager = dialog.findViewById<TextView>(R.id.tvRequestManager)

        tvRequestManager.text = sessionManager.fetchManagerName()

        sessionManager.myDatePickerDialog(edt_date, this)
        sessionManager.myTimePickerDialog(edt_time, this)

        btn_close.setOnClickListener {
            dialog.dismiss()
        }

        btn_send.setOnClickListener {
            if (edt_time.text.isEmpty() || edt_date.text.isEmpty()) {
                valid_layout.visibility = View.VISIBLE
            } else {
                valid_layout.visibility = View.GONE
                val timeRequest = edt_time.text


                val dayRequest = edt_date.text
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
                val date = LocalDate.parse(dayRequest, formatter)
                val ts = SimpleDateFormat("dd/MM/yyyy HH:mm").parse("$dayRequest $timeRequest")

                val entity = EventEntities(
                    created_at = ts.time,
                    event_type = type,
                    created_date = CustomDate(
                        month = date.month.value,
                        day = date.dayOfMonth,
                        year = date.year
                    ),
                    reason = "invalid_forgot_check",
                    status = null
                )

                val call = request.createSubmission(token, entity)
                call.enqueue(object : Callback<EventEntities> {
                    override fun onResponse(
                        call: Call<EventEntities>,
                        response: Response<EventEntities>
                    ) {
                        if (response.isSuccessful && response.code() == 200) {
                            dialog.dismiss()
                            loadData()
                            Constant.dialogSuccess(
                                this@RequestActivity,
                                "Tạo yêu cầu mới thành công."
                            )
                        } else {
                            if (response.errorBody() != null){
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                Constant.dialogError(
                                    this@RequestActivity,
                                    jObjError["msg"].toString()
                                )
                            }else{
                                println(response.body())
                            }
                        }
                    }

                    override fun onFailure(call: Call<EventEntities>, t: Throwable) {
                        Constant.dialogError(
                            this@RequestActivity,
                            "Có lỗi xảy ra vui lòng thử lại."
                        )
                    }

                })
            }
        }
        dialog.show()
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        type = p0?.getItemAtPosition(p2).toString()
        if (type == "Lúc Đến") {
            type = CHECK_IN
        } else {
            type = CHECK_OUT
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    private fun buttonActive(btn: Int): Int {
        when (btn) {
            BUTTON_WAITING -> {
                btnWaiting.setBackgroundColor(Color.LTGRAY)
                btnApprove.setBackgroundColor(Color.WHITE)
                btnDecline.setBackgroundColor(Color.WHITE)
            }

            BUTTON_APPROVE -> {
                btnWaiting.setBackgroundColor(Color.WHITE)
                btnApprove.setBackgroundColor(Color.LTGRAY)
                btnDecline.setBackgroundColor(Color.WHITE)
            }

            BUTTON_DECLINE -> {
                btnWaiting.setBackgroundColor(Color.WHITE)
                btnApprove.setBackgroundColor(Color.WHITE)
                btnDecline.setBackgroundColor(Color.LTGRAY)
            }
        }
        return btn
    }

    private fun loadData() {
        // call api
        val call = request.listSubmission(token = token)
        lstData = ArrayList()
        lstWaiting = ArrayList()
        lstAccept = ArrayList()
        lstDecline = ArrayList()

        call.enqueue(object : Callback<Event> {
            override fun onResponse(call: Call<Event>, response: Response<Event>) {
                if (response.code() == 200) {
                    response.body()?.event?.forEach {
                        lstData.add(it)
                    }
                    setAdapter()
                    swipeRefreshSubmission.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<Event>, t: Throwable) {
                swipeRefreshSubmission.isRefreshing = false
                Constant.dialogError(this@RequestActivity, "Có lỗi xảy ra vui lòng thử lại.")
            }

        })
    }

    private fun setAdapter() {
        recycleViewAdapter =
            RecyclerViewChamBu(lstData, this, applicationContext)

        if (lstData.size >= 2) {
            rcvRequest.layoutManager = GridLayoutManager(this, 2)
        } else {
            rcvRequest.layoutManager = LinearLayoutManager(this)
        }
        rcvRequest.adapter = recycleViewAdapter

        lstData.forEach {
            when (it.manager_confirm) {
                "waiting" -> {
                    lstWaiting.add(it)
                }
                "accept" -> {
                    lstAccept.add(it)
                }
                else -> {
                    lstDecline.add(it)
                }
            }
        }

        when (isActive){
            1 -> {
                recycleViewAdapter.setData(lstWaiting)
            }
            2 -> {
                recycleViewAdapter.setData(lstAccept)
            }
            else -> {
                recycleViewAdapter.setData(lstDecline)
            }
        }

        swipeRefreshSubmission.isRefreshing = false
    }

    override fun onClickItem(id: Int) {
        swipeRefreshSubmission.isRefreshing = true
        openDialog(id, "Bạn có đồng ý xóa yêu cầu này không ??")
    }

    @SuppressLint("SetTextI18n")
    private fun openDialog(id: Int, msg: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_confirm)

        val window = dialog.window ?: return

        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
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
            val call = request.deleteSubmission(token, id = id)
            call.enqueue(object : Callback<EventEntities> {
                override fun onResponse(
                    call: Call<EventEntities>,
                    response: Response<EventEntities>
                ) {
                    if (response.code() == 200) {
                        loadData()
                        swipeRefreshSubmission.isRefreshing = false
                        val jObjError = JSONObject(response.errorBody()?.string())
                        Constant.dialogSuccess(this@RequestActivity, jObjError["msg"].toString())
                    } else {
                        swipeRefreshSubmission.isRefreshing = false
                        val jObjError = JSONObject(response.errorBody()?.string())
                        Constant.dialogError(this@RequestActivity, jObjError["msg"].toString())
                    }
                }

                override fun onFailure(call: Call<EventEntities>, t: Throwable) {
                    Constant.dialogError(this@RequestActivity, "Có lỗi xày ra vui lòng thử lại.")
                }
            })
        }

        dialog.show()
    }
}