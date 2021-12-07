package com.example.demo.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.demo.Constant
import com.example.demo.R
import com.example.demo.adapter.LoadingDialog
import com.example.demo.backend.ApiClient
import com.example.demo.backend.RestAPI
import com.example.demo.backend.SessionManager
import com.example.demo.backend.entities.Event
import com.example.demo.backend.entities.EventDetail
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class homeFragment : Fragment() {
    private lateinit var btnRequest: Button
    private lateinit var btnRequestDayOff: Button
    private lateinit var btnUserManager: Button
    private lateinit var btnApprove: Button
    private lateinit var sessionManager: SessionManager
    private lateinit var tvUserName: TextView
    private lateinit var tvCheckInTime: TextView
    private lateinit var tvCheckOutTime: TextView
    private lateinit var tvNotFound: TextView
    private lateinit var dialog: LoadingDialog
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var token: String
    private lateinit var request: RestAPI

    private var arrWeek = arrayOf(
        "Chủ Nhật",
        "Thứ Hai",
        "Thứ Ba",
        "Thứ Bốn",
        "Thứ Năm",
        "Thứ Sáu",
        "Thứ Bảy"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            homeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    @SuppressLint("SetTextI18n", "UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog = LoadingDialog(activity!!)

        val currentDateHome = view.findViewById<TextView>(R.id.currentDateHome)
        btnRequest = view.findViewById(R.id.btnRequest)
        btnRequestDayOff = view.findViewById(R.id.btnRequestDayOff)
        btnUserManager = view.findViewById(R.id.btn_user_manager)
        btnApprove = view.findViewById(R.id.btn_approve)
        tvUserName = view.findViewById(R.id.helloUser)
        tvCheckInTime = view.findViewById(R.id.checkInTime)
        tvCheckOutTime = view.findViewById(R.id.checkOutTime)
        tvNotFound = view.findViewById(R.id.notFoundCheckInOut)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh)

        swipeRefreshLayout.setOnRefreshListener { loadData() }

        swipeRefreshLayout.isRefreshing = true

        sessionManager = SessionManager(activity?.applicationContext!!)

        tvUserName.text = "Hi, ${sessionManager.fetchUserName()}"
        val calendar = Calendar.getInstance()
        val time = calendar.time
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1


        val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val strDate = df.format(time)

        currentDateHome.text = "${convertDayOfWeek(dayOfWeek)}, Ngày $strDate"

        if (sessionManager.fetchIsUser()) {
            btnRequest.visibility = View.VISIBLE
            btnRequestDayOff.visibility = View.VISIBLE

            btnRequest.setOnClickListener {
                val intent = Intent(context?.applicationContext, RequestActivity::class.java)
                startActivity(intent)
            }

            btnRequestDayOff.setOnClickListener {
                val intent = Intent(context?.applicationContext, RequestDayOffActivity::class.java)
                startActivity(intent)
            }
        }

        if (sessionManager.fetchIsHR()) {
            btnUserManager.visibility = View.VISIBLE

            btnUserManager.setOnClickListener {
                val intent = Intent(context?.applicationContext, HrActivity::class.java)
                startActivity(intent)
            }
        }

        if (sessionManager.fetchIsManager()) {
            btnApprove.visibility = View.VISIBLE

            btnApprove.setOnClickListener {
                val intent = Intent(context?.applicationContext, ApproveActivity::class.java)
                startActivity(intent)
            }
        }

        token = "Token ${sessionManager.fetchAuthToken()}"
        request = ApiClient.getClient().create(RestAPI::class.java)
        loadData()
    }

    private fun loadData() {
//        val user = User(sessionManager.fetchUserName(), sessionManager.fetchMyEmail(), sessionManager.fetchTokenDevice())
        val call = request.getEvent(token)
        call.enqueue(object : Callback<Event> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<Event>, response: Response<Event>) {
                val list: ArrayList<EventDetail>? = response.body()?.event
                if (list != null) {
                    for (item in list) {
                        when (item.event_type) {
                            Constant.CHECK_IN -> {
                                val startTime = sessionManager.convertTimeStampToTime(
                                    item.created_at!!,
                                    "HH:mm:ss"
                                )
                                tvCheckInTime.visibility = View.VISIBLE
                                tvNotFound.visibility = View.GONE
                                tvCheckInTime.text = "Lúc đến: $startTime"
                            }
                            Constant.CHECK_OUT -> {
                                val endTime = sessionManager.convertTimeStampToTime(
                                    item.created_at!!,
                                    "HH:mm:ss"
                                )
                                tvCheckOutTime.visibility = View.VISIBLE
                                tvNotFound.visibility = View.GONE
                                tvCheckOutTime.text = "Lúc về: $endTime"
                            }
                            else -> {
                                tvCheckOutTime.visibility = View.VISIBLE
                                tvCheckOutTime.text = "Bạn chưa chấm công"
                            }
                        }
                    }
                }
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onFailure(call: Call<Event>, t: Throwable) {
                Constant.dialogError(activity!!, "Có lỗi xảy ra vui lòng thử lại.")
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    private fun convertDayOfWeek(day: Int): String {
        return arrWeek[day]
    }
}