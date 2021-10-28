package com.example.demo.screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.demo.R

class SigUpActivity : AppCompatActivity() {

    private lateinit var btnLogin: Button
    private lateinit var btnSigUp: Button
    private lateinit var editUserName: EditText
    private lateinit var editAddress: EditText
    private lateinit var editFullName: EditText
    private lateinit var editPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sig_up)

        initUi()

        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnSigUp.setOnClickListener {
            register()
        }
    }

    private fun register() {
        val strUserName = editUserName.text.toString().trim()
        val strAddress = editAddress.text.toString().trim()
        val strFullName = editFullName.text.toString().trim()
        val strPassword = editPassword.text.toString().trim()

        if (TextUtils.isEmpty(strUserName) || TextUtils.isEmpty(strAddress) ||
            TextUtils.isEmpty(strFullName) || TextUtils.isEmpty(strPassword)){
            Toast.makeText(applicationContext, "Enter your info begin login", Toast.LENGTH_SHORT).show()
            return
        }
    }

    private fun initUi(){
        btnLogin = findViewById(R.id.buttonLogin)
        btnSigUp = findViewById(R.id.buttonConfirmSigUp)
        editUserName = findViewById(R.id.editTextTextPersonName)
        editAddress = findViewById(R.id.editTextTextPersonName2)
        editFullName = findViewById(R.id.editTextTextPersonName3)
        editPassword = findViewById(R.id.editTextTextPersonName4)
    }
}