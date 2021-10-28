package com.example.demo.adapter.layout

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.demo.R
import com.example.demo.backend.entities.User

class DropDownManagerAdapter(
    context: Context,
    resource: Int,
    objects: ArrayList<User>
) : ArrayAdapter<User>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val convertView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_selected_manager, parent, false)
        val tvSelected = convertView.findViewById<TextView>(R.id.tv_selected_manager)

        val user = this.getItem(position)

        if (user != null){
            tvSelected.text = user.full_name
        }
        return convertView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val convertView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_manager, parent, false)
        val tvManager = convertView.findViewById<TextView>(R.id.tvManager)

        val user = this.getItem(position)

        if (user != null){
            tvManager.text = user.full_name
        }
        return convertView
    }
}