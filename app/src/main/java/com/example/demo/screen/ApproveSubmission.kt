package com.example.demo.screen

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.Constant
import com.example.demo.R
import com.example.demo.adapter.RecyclerVIewSubmissionAdapter
import com.example.demo.adapter.SubmissionListener
import com.example.demo.backend.ApiClient
import com.example.demo.backend.RestAPI
import com.example.demo.backend.SessionManager
import com.example.demo.backend.entities.ListDayOffEntities
import com.example.demo.backend.entities.ListSubmission
import com.example.demo.backend.entities.Submission
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class approve_submission : Fragment(), SubmissionListener {
    private lateinit var sessionManager: SessionManager
    private lateinit var token: String
    private lateinit var rcvSubmission: RecyclerView
    private lateinit var request: RestAPI
    private lateinit var checkAll: CheckBox
    private lateinit var btnApprove: Button
    private lateinit var btnDecline: Button
    private lateinit var list: ArrayList<Submission>
    private lateinit var listPostData: ArrayList<Submission>
    private lateinit var rcvAdapter: RecyclerVIewSubmissionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_approve_submission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(activity?.applicationContext!!)
        token = "Token ${sessionManager.fetchAuthToken()}"
        rcvSubmission = view.findViewById(R.id.rcv_submission)
        checkAll = view.findViewById(R.id.btn_checkedAll)
        btnApprove = view.findViewById(R.id.btn_approve_manager)
        btnDecline = view.findViewById(R.id.btn_decline_manager)

        btnApprove.isEnabled = false
        btnDecline.isEnabled = false

        request = ApiClient.getClient().create(RestAPI::class.java)

        list = ArrayList()

        setRecyclerView(null)
        getData()

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

    private fun getData(){
        list = ArrayList()
        val call = request.getApproveSubmission(token)
        call.enqueue(object: Callback<ListSubmission>{
            override fun onResponse(
                call: Call<ListSubmission>,
                response: Response<ListSubmission>
            ) {
                if (response.code() == 200){
                    response.body()?.submission_list!!.forEach {
                        list.add(it)
                    }
                    rcvAdapter.setData(list)
                }else{
                    println("Failed")
                }
            }

            override fun onFailure(call: Call<ListSubmission>, t: Throwable) {
                Constant.dialogError(activity!!, "Có lỗi xảy ra vui lòng thử lại.")
            }
        })
    }

    private fun setRecyclerView(flag: Boolean?) {
        rcvSubmission.setHasFixedSize(true)
        rcvSubmission.layoutManager = LinearLayoutManager(activity?.applicationContext)

        rcvAdapter = RecyclerVIewSubmissionAdapter(this, flag, activity?.applicationContext!!)
        rcvAdapter.setData(list)
        rcvSubmission.adapter = rcvAdapter
    }

    override fun onSubmissionChange(list: ArrayList<Submission>) {
        listPostData = ArrayList()
        listPostData = list
        if (listPostData.size > 0) {
            btnApprove.isEnabled = true
            btnDecline.isEnabled = true
        }else{
            btnApprove.isEnabled = false
            btnDecline.isEnabled = false
        }
    }

    override fun onSubmissionCheckAll() {
        checkAll.isChecked = false
    }

    @SuppressLint("SetTextI18n")
    private fun openDialog(qualityRequest: Int, approve: Boolean){
        val dialog = Dialog(requireActivity())
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

        val btnClose = dialog.findViewById<Button>(R.id.cancelConfirm)
        val btnConfirm = dialog.findViewById<Button>(R.id.approveRequest)
        val message = dialog.findViewById<TextView>(R.id.message_confirm)

        message.text = "Bạn chọn $qualityRequest cùng trạng thái $accept. Việc xét duyệt của bạn sẽ không thể sửa đổi khi bạn nhấn đồng ý !!"

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            dialog.dismiss()
            val request = ApiClient.getClient().create(RestAPI::class.java)
            val call = request.postApproveSubmission(token, ListSubmission(listPostData), accept = approve)
            call.enqueue(object: Callback<ListSubmission>{
                override fun onResponse(call: Call<ListSubmission>, response: Response<ListSubmission>) {
                    println(response.body())
                    getData()
                    btnApprove.isEnabled = false
                    btnDecline.isEnabled = false
                }

                override fun onFailure(call: Call<ListSubmission>, t: Throwable) {
                    Constant.dialogError(activity!!, "Có lỗi xảy ra vui lòng thử lại.")
                }
            })
        }
        dialog.show()
    }
}