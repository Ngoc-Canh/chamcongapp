package com.example.demo.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.demo.Constant
import com.example.demo.R
import com.example.demo.backend.ApiClient
import com.example.demo.backend.RestAPI
import com.example.demo.backend.SessionManager
import com.example.demo.backend.entities.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {
    private lateinit var btnLogOut: Button
    private lateinit var tvUserName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvEmailManager: TextView
    private lateinit var tvDayOff: TextView
    private lateinit var sessionManager: SessionManager
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var token: String
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var notify1: Switch
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var notify2: Switch

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnLogOut = view.findViewById(R.id.buttonLogOut)
        tvUserName = view.findViewById(R.id.textView9)
        tvEmail = view.findViewById(R.id.textView10)
        tvEmailManager = view.findViewById(R.id.textView13)
        tvDayOff = view.findViewById(R.id.textView15)
        refresh = view.findViewById(R.id.swipeRefresh)
        notify1 = view.findViewById(R.id.switch1)
        notify2 = view.findViewById(R.id.switch2)
        refresh.isRefreshing = true

        sessionManager = SessionManager(context?.applicationContext!!)
        token = "token ${sessionManager.fetchAuthToken()}"

        refresh.setOnRefreshListener { fetchInfo() }

        btnLogOut.setOnClickListener {
            sessionManager.refreshAll()
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        notify1.setOnClickListener {
            refresh.isRefreshing = true
            sessionManager.saveNotify1(notify1.isChecked)
            refresh.isRefreshing = false
        }

        notify2.setOnClickListener {
            refresh.isRefreshing = true
            sessionManager.saveNotify2(notify2.isChecked)
            refresh.isRefreshing = false
        }

        fetchInfo()
    }

    private fun fetchInfo(){
        val request = ApiClient.getClient().create(RestAPI::class.java)
        val call = request.info(token)
        call.enqueue(object: Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                sessionManager.saveUserName(response.body()?.full_name.toString())
                sessionManager.saveDayOff(response.body()?.dayOff.toString())
                sessionManager.saveManagerEmail(response.body()?.manager_email.toString())
                sessionManager.saveManagerName(response.body()?.manager_name.toString())
                sessionManager.saveMyEmail(response.body()?.email.toString())
                sessionManager.isUser(response.body()?.is_user!!)
                sessionManager.isManager(response.body()?.is_manager!!)
                sessionManager.isHR(response.body()?.is_admin!!)
                sessionManager.saveAuthToken(response.body()?.token.toString())
                sessionManager.saveNotify1(notify1.isChecked)
                sessionManager.saveNotify2(notify2.isChecked)

                tvUserName.text = sessionManager.fetchUserName()
                tvEmail.text = sessionManager.fetchMyEmail()
                tvEmailManager.text = sessionManager.fetchManagerEmail()
                tvDayOff.text = sessionManager.fetchDayOff()
                notify1.isChecked = sessionManager.fetchNotify1()
                notify2.isChecked = sessionManager.fetchNotify2()
                refresh.isRefreshing = false
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                refresh.isRefreshing = false
                Constant.dialogError(activity!!, "Có lỗi xảy ra vui lòng thử lại.")
            }
        })
    }

}