package com.example.demo.screen

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.Constant
import com.example.demo.R
import com.example.demo.adapter.DayOffListener
import com.example.demo.adapter.RecyclerViewDayOffAdapter
import com.example.demo.backend.ApiClient
import com.example.demo.backend.RestAPI
import com.example.demo.backend.SessionManager
import com.example.demo.backend.entities.DayOffEntities
import com.example.demo.backend.entities.EventEntities
import com.example.demo.backend.entities.ListDayOffEntities
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [approve_dayOff.newInstance] factory method to
 * create an instance of this fragment.
 */
class approve_dayOff : Fragment(), DayOffListener{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var rcv_dayOff: RecyclerView
    lateinit var list: ArrayList<DayOffEntities>
    lateinit var adapter: RecyclerViewDayOffAdapter
    lateinit var checkAll: CheckBox
    lateinit var btnApprove: Button
    lateinit var btnDecline: Button
    lateinit var sessionManager: SessionManager
    lateinit var listPostData: ArrayList<DayOffEntities>
    lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_approve_day_off, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(activity?.applicationContext!!)
        token = "Token ${sessionManager.fetchAuthToken()}"

        rcv_dayOff = view.findViewById(R.id.rcv_dayOff)
        checkAll = view.findViewById(R.id.btn_checkedAll)
        btnApprove = view.findViewById(R.id.btn_approve_manager)
        btnDecline = view.findViewById(R.id.btn_decline_manager)
        btnApprove.isEnabled = false
        btnDecline.isEnabled = false
        list = ArrayList()

        setRecyclerView(null)

        getQuantityData()

        checkAll.setOnClickListener {
            if (checkAll.isChecked){
                setRecyclerView(true)
            }else{
                setRecyclerView(false)
            }
        }

        btnApprove.setOnClickListener {
            openDialog(listPostData.size, true)
        }

        btnDecline.setOnClickListener {
            openDialog(listPostData.size, false)
        }

    }

    private fun getQuantityData() {
        list = ArrayList()
        val request = ApiClient.getClient().create(RestAPI::class.java)
        val call = request.getApproveDayOff(token)
        call.enqueue(object: Callback<ListDayOffEntities>{
            override fun onResponse(
                call: Call<ListDayOffEntities>,
                response: Response<ListDayOffEntities>
            ) {
                if (response.code() == 200){
                    response.body()?.data!!.forEach {
                        list.add(it)
                    }
                }
                adapter.setData(list)
            }

            override fun onFailure(call: Call<ListDayOffEntities>, t: Throwable) {
                Constant.dialogError(activity!!, "Có lỗi xảy ra vui lòng thử lại.")
            }
        })

    }

    private fun setRecyclerView(flag: Boolean?) {
        rcv_dayOff.setHasFixedSize(true)
        rcv_dayOff.layoutManager = LinearLayoutManager(activity?.applicationContext)

        adapter = RecyclerViewDayOffAdapter(list, flag,this)
        rcv_dayOff.adapter = adapter
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            approve_dayOff().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDayOffChange(list: ArrayList<DayOffEntities>) {
        listPostData = ArrayList()
        listPostData = list
        if (listPostData.size > 0) {
            btnApprove.isEnabled = true
            btnDecline.isEnabled = true
        }else{
            btnApprove.isEnabled = false
            btnDecline.isEnabled = false
        }
        Log.i("12345678", listPostData.toString())
    }

    override fun onDayCheckAll() {
        checkAll.isChecked = false
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun openDialog(qualityRequest: Int, approve: Boolean){
        val dialog: Dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_confirm)

        val window = dialog.window ?: return

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttribute: WindowManager.LayoutParams = window.attributes
        windowAttribute.gravity = Gravity.CENTER
        window.attributes = windowAttribute

        dialog.setCancelable(false)

        val accept =  if(approve){"Chấp Thuận"}else{"Từ Chối"}

        val btn_close = dialog.findViewById<Button>(R.id.cancelConfirm)
        val btn_confirm = dialog.findViewById<Button>(R.id.approveRequest)
        val message = dialog.findViewById<TextView>(R.id.message_confirm)

        message.text = "Bạn chọn $qualityRequest cùng trạng thái $accept. Việc xét duyệt của bạn sẽ không thể sửa đổi khi bạn nhấn đồng ý !!"

        btn_close.setOnClickListener {
            dialog.dismiss()
        }
        btn_confirm.setOnClickListener {
            dialog.dismiss()
            val request = ApiClient.getClient().create(RestAPI::class.java)
            val call = request.postApproveDayOff(token, ListDayOffEntities(listPostData), accept = approve)
            call.enqueue(object: Callback<ListDayOffEntities>{
                override fun onResponse(call: Call<ListDayOffEntities>, response: Response<ListDayOffEntities>) {
                    println(response.body())
                    getQuantityData()
                    btnApprove.isEnabled = false
                    btnDecline.isEnabled = false
                }

                override fun onFailure(call: Call<ListDayOffEntities>, t: Throwable) {
                    Constant.dialogError(activity!!, "Có lỗi xảy ra vui lòng thử lại.")
                }
            })
        }
        dialog.show()
    }
}