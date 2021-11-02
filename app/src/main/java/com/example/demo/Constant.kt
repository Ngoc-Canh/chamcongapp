package com.example.demo

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

interface Constant {
    companion object {
        const val BASE_URL = "http://b1d9-113-168-165-30.ngrok.io"
        const val CHECK_IN = "check_in"
        const val CHECK_OUT = "check_out"

        // Dialog Message Success
        fun dialogSuccess(context: Context, msg: String){
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_success)

            val window = dialog.window ?: return

            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val windowAttribute: WindowManager.LayoutParams = window.attributes
            windowAttribute.gravity = Gravity.CENTER
            window.attributes = windowAttribute

            dialog.setCancelable(true)

            val msgError = dialog.findViewById<TextView>(R.id.msgError)
            val btnOk = dialog.findViewById<Button>(R.id.btnOK)

            msgError.text = msg

            btnOk.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        // Dialog Message Error
        fun dialogError(context: Context, msg: String){
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_error)

            val window = dialog.window ?: return

            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val windowAttribute: WindowManager.LayoutParams = window.attributes
            windowAttribute.gravity = Gravity.CENTER
            window.attributes = windowAttribute

            dialog.setCancelable(true)

            val msgError = dialog.findViewById<TextView>(R.id.msgError)
            val btnOk = dialog.findViewById<Button>(R.id.btnOK)

            msgError.text = msg

            btnOk.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }

}