package com.example.demo.screen

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.demo.Constant
import com.example.demo.R
import com.example.demo.backend.ApiClient
import com.example.demo.backend.RestAPI
import com.example.demo.backend.SessionManager
import com.example.demo.backend.entities.User
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var editUserName: EditText
    private lateinit var editPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonRegister: Button
    private lateinit var prefs: SharedPreferences
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initUi()

        buttonRegister.setOnClickListener {
            val intent = Intent(this, SigUpActivity::class.java)
            startActivity(intent)
        }

        buttonLogin.setOnClickListener {
            login()
        }

        if (prefs.contains("tokenUser")){
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login() {
        val strUserName = editUserName.text.toString().trim()
        val strPassword = editPassword.text.toString().trim()

        if (TextUtils.isEmpty(strUserName) || TextUtils.isEmpty(strPassword)){
            Toast.makeText(applicationContext, "Enter your info begin login", Toast.LENGTH_SHORT).show()
            return
        }

        val request = ApiClient.getClient().create(RestAPI::class.java)
        val user = User(username=strUserName, password=strPassword)

        val call = request.login(user)
        var is_active = false
        sessionManager = SessionManager(applicationContext)
        val token = "Token ${sessionManager.fetchAuthToken()}"

        call.enqueue(object: Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful && response.code() == 200){
                    val info = request.info("Token ${response.body()?.token.toString()}")
                    info.enqueue(object: Callback<User>{
                        override fun onResponse(callInfo: Call<User>, responseInfo: Response<User>) {
                            if (responseInfo.isSuccessful && responseInfo.code() == 200){
                                sessionManager.saveUserName(responseInfo.body()?.full_name.toString())
                                sessionManager.saveDayOff(responseInfo.body()?.dayOff.toString())
                                sessionManager.saveManagerEmail(responseInfo.body()?.manager_email.toString())
                                sessionManager.saveManagerName(responseInfo.body()?.manager_name.toString())
                                sessionManager.saveMyEmail(responseInfo.body()?.email.toString())
                                sessionManager.isUser(responseInfo.body()?.is_user!!)
                                sessionManager.isManager(responseInfo.body()?.is_manager!!)
                                sessionManager.isHR(responseInfo.body()?.is_admin!!)
                                sessionManager.saveAuthToken(responseInfo.body()?.token.toString())
                                is_active = responseInfo.body()?.is_active!!
                                retrieveTokenDevice()
                            }

                            if (is_active){
                                val intent = Intent(applicationContext, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }else{
                                Constant.dialogError(applicationContext, "Tài khoản chưa được kích hoạt.")
                            }
                        }
                        override fun onFailure(callInfo: Call<User>, t: Throwable) {
                            Constant.dialogError(applicationContext, "Có lỗi xảy ra vui lòng thử lại.")
                        }
                    })

                }else if(response.code() == 400){
                    Constant.dialogError(applicationContext, "Tài khoản mật khẩu không chính xác")
                }else{
                    Constant.dialogError(applicationContext, "Có lỗi xảy ra vui lòng thử lại.")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("Error", t.message.toString())
            }
        })
    }

    private fun initUi(){
        editUserName = findViewById(R.id.editUserName)
        editPassword = findViewById(R.id.editPassword)
        buttonLogin = findViewById(R.id.login)
        buttonRegister = findViewById(R.id.buttonRegister)
        prefs = getSharedPreferences("PREF", MODE_PRIVATE)
    }

    private fun retrieveTokenDevice(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if(task.isSuccessful){
                sessionManager.saveTokenDevice(task.result.toString())
            }
        }
    }
}