package com.example.demo.adapter

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import com.example.demo.R

class LoadingDialog(private var activity: Activity) {

    private lateinit var dialog: Dialog

    fun start(){
        val builder:AlertDialog.Builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.dialog_loadding, null))
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
    }

    fun cancerLoading(){
        dialog.dismiss()
    }
}