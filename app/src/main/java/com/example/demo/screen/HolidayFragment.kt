package com.example.demo.screen

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.demo.Constant
import com.example.demo.R
import com.example.demo.adapter.DeleteItem
import com.example.demo.adapter.HolidayAdapter
import com.example.demo.backend.ApiClient
import com.example.demo.backend.RestAPI
import com.example.demo.backend.SessionManager
import com.example.demo.backend.entities.Holiday
import com.example.demo.backend.entities.ListHoliday
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HolidayFragment : Fragment(){
    private lateinit var buttonAdd: FloatingActionButton
    private lateinit var sessionManager: SessionManager
    private val request = ApiClient.getClient().create(RestAPI::class.java)
    private lateinit var token: String
    private lateinit var holidayAdapter: HolidayAdapter
    private lateinit var rcvHoliday: RecyclerView
    private lateinit var lstData: ArrayList<Holiday>
    private lateinit var refreshLayout: SwipeRefreshLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_holiday, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonAdd = view.findViewById(R.id.floatingButtonAdd)
        rcvHoliday = view.findViewById(R.id.rcv_holiday)
        refreshLayout = view.findViewById(R.id.swipeRefreshRequest)

        refreshLayout.isRefreshing = true

        refreshLayout.setOnRefreshListener {
            loadData()
        }

        sessionManager = SessionManager(activity?.applicationContext!!)
        holidayAdapter = HolidayAdapter(requireActivity())
        rcvHoliday.adapter = holidayAdapter
        rcvHoliday.layoutManager = LinearLayoutManager(requireActivity().applicationContext)

        val itemDecoration = DividerItemDecoration(requireActivity().applicationContext, DividerItemDecoration.VERTICAL)
        rcvHoliday.addItemDecoration(itemDecoration)

        token = "token ${sessionManager.fetchAuthToken()}"

        loadData()

        buttonAdd.setOnClickListener {
            openDialog()
        }
    }

    @SuppressLint("SimpleDateFormat", "UseRequireInsteadOfGet")
    private fun openDialog(){
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_holiday_create)

        val window = dialog.window ?: return

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttribute: WindowManager.LayoutParams = window.attributes
        windowAttribute.gravity = Gravity.CENTER
        window.attributes = windowAttribute

        dialog.setCancelable(true)

        val startDate = dialog.findViewById<TextView>(R.id.edt_dateStart)
        val endDate = dialog.findViewById<TextView>(R.id.edt_dateEnd)
        val desc = dialog.findViewById<EditText>(R.id.desc)
        val btnSend = dialog.findViewById<Button>(R.id.btn_send)
        val btnClose = dialog.findViewById<Button>(R.id.btn_close)

        sessionManager.myDatePickerDialog(startDate, activity!!)
        sessionManager.myDatePickerDialog(endDate, activity!!)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        btnSend.setOnClickListener {
            val dfm = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dateStart = dfm.parse(startDate.text.toString())
            val dateEnd = dfm.parse(endDate.text.toString())

            val holiday = Holiday(null, dateStart, dateEnd, desc.text.toString(),null)
            val call = request.createHoliday(token, holiday)
            call.enqueue(object: Callback<Holiday>{
                override fun onResponse(call: Call<Holiday>, response: Response<Holiday>) {
                    if (response.code() == 202){
                        refreshLayout.isRefreshing = true
                        loadData()
                        dialog.dismiss()
                    }
                }

                override fun onFailure(call: Call<Holiday>, t: Throwable) {
                    Constant.dialogError(activity!!, "Có lỗi xảy ra vui lòng thử lại.")
                }
            })
        }

        dialog.show()
    }

    private fun loadData(){
        lstData = ArrayList()
        val call = request.getListHoliday(token)
        call.enqueue(object: Callback<ListHoliday>{
            override fun onResponse(call: Call<ListHoliday>, response: Response<ListHoliday>) {
                if (response.code() == 200){
                    response.body()?.data!!.forEach {
                        lstData.add(it)
                    }
                    holidayAdapter.setData(lstData)
                    refreshLayout.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<ListHoliday>, t: Throwable) {
                refreshLayout.isRefreshing = false
                Constant.dialogError(activity!!, "Có lỗi xảy ra vui lòng thử lại.")
            }
        })
    }
}