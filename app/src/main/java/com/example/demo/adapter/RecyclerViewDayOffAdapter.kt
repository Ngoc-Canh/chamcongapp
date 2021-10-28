package com.example.demo.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.adapter.RecyclerViewDayOffAdapter.ViewHolder
import com.example.demo.backend.entities.DayOffEntities

class RecyclerViewDayOffAdapter(private var lst: ArrayList<DayOffEntities>,
                                private var checkedAll: Boolean?,
                                private var dayOffListener: DayOffListener): RecyclerView.Adapter<ViewHolder>() {

    private var TYPE_CHECKED = 1
    private val TYPE_UNCHECKED = 2
    private val WAITING = "waiting"
    private val ACCPET = "accept"
    private val DECLINE = "decline"

    private val lstChecked = ArrayList<DayOffEntities>()

    fun setData(list: ArrayList<DayOffEntities>){
        this.lst = list
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var checkBox: CheckBox = itemView.findViewById(R.id.checkedBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (TYPE_CHECKED == viewType){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.rcv_layout, parent, false)
            ViewHolder(view)
        }else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.rcv_layout_unchecked, parent, false)
            ViewHolder(view)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(lst.size > 0){
            holder.checkBox.text = "Yêu cầu từ: ${lst[position].created_by} " +
                    "\nHình thức: ${lst[position].type} " +
                    "\nThời gian: ${lst[position].start_date} - ${lst[position].end_date}" +
                    "\nSố ngày nghỉ: ${lst[position].total_dayOff} ngày" +
                    "\nTrạng thái: ${lst[position].manager_confirm}"
            holder.checkBox.setOnClickListener {
                if (lst[position].manager_confirm_code == true){
                    if(holder.checkBox.isChecked){
                        lstChecked.add(lst[position])
                    }else{
                        lstChecked.remove(lst[position])
                    }
                    dayOffListener.onDayOffChange(lstChecked)

                    if(checkedAll != null){
                        dayOffListener.onDayCheckAll()
                    }
                }
            }

            if (lst[position].manager_confirm_code == true){
                if(checkedAll == true){
                    holder.checkBox.isChecked = true
                    dayOffListener.onDayOffChange(lst)
                }else{
                    lstChecked.remove(lst[position])
                    holder.checkBox.isChecked = false
                    dayOffListener.onDayOffChange(lstChecked)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(lst[position].manager_confirm_code == true){
            TYPE_CHECKED
        }else{
            TYPE_UNCHECKED
        }
    }

    override fun getItemCount(): Int {
        return lst.size
    }
}