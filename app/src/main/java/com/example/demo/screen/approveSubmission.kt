package com.example.demo.screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
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
//        val sub = Submission(5, "Bui Ngoc Canh 5", "08:00", "30-09-2021", "Check In", "Quên Chấm Công", false, "Từ Chối")
//        list.add(sub)
//        for (i in 0..3){
//            val sub = Submission(i, "Bui Ngoc Canh $i", "08:00", "30-09-2021", "Check In", "Quên Chấm Công", true, "Đang chờ phê duyệt")
//            list.add(sub)
//        }

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
}