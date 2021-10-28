package com.example.demo.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.se.omapi.Session
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.backend.SessionManager
import com.example.demo.backend.entities.DayOffEntities
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterRequestDayOff(private var lstData: ArrayList<DayOffEntities>,
                           var deleteItem: DeleteItem,
                           var ctx: Context?,
                           var isWaiting: Boolean?,
                           var isApprove: Boolean?,
                           var isDecline: Boolean?):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val IS_WAITING = 1
    private val IS_APPROVE = 2
    private val IS_DECLINE = 3

    private val STATUS_WAITING = "waiting"
    private val STATUS_APPROVE = "accept"
    private val STATUS_DECLINE = "decline"

    private lateinit var sessionManager: SessionManager

    fun setData(lst: ArrayList<DayOffEntities>){
        this.lstData = lst
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            IS_WAITING -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.rcv_dayoff_waiting, parent, false)
                DayOffWaitingStatus(view)
            }
            IS_APPROVE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.rcv_dayoff_approve, parent, false)
                DayOffApproveStatus(view)
            }
            IS_DECLINE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.rcv_dayoff_decline, parent, false)
                DayOffDeclineStatus(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.rcv_dayoff_decline, parent, false)
                DayOffDeclineStatus(view)
            }
        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (lstData.size <= 0){
            return
        }
        sessionManager = SessionManager(this.ctx!!)
        val dayOff = lstData[position]
        val df = SimpleDateFormat("yyyy-MM-dd")
        when (holder.itemViewType) {
            IS_WAITING -> {
                val dayOffStatus: DayOffWaitingStatus = holder as DayOffWaitingStatus
                dayOffStatus.tvTitleDayOffWaiting.text = "Yêu cầu ${dayOff.type}"
                dayOffStatus.tvStartDateWaiting.text = df.format(dayOff.start_date!!)
                dayOffStatus.tvEndDateWaiting.text = df.format(dayOff.end_date!!)
                dayOffStatus.tvTypeDayOffWaiting.text = dayOff.type
                dayOffStatus.tvNumberDayOffWaiting.text = dayOff.total_dayOff.toString()
                dayOffStatus.tvManagerWaiting.text = sessionManager.fetchManagerName()
                dayOffStatus.imvTrash.setOnClickListener {
                    deleteItem.onClickItem(dayOff.id!!)
                }
            }
            IS_APPROVE -> {
                val dayOffStatus: DayOffApproveStatus = holder as DayOffApproveStatus
                dayOffStatus.tvTitleDayOffApprove.text = "Yêu cầu ${dayOff.type}"
                dayOffStatus.tvStartDateApprove.text = df.format(dayOff.start_date!!)
                dayOffStatus.tvEndDateApprove.text = df.format(dayOff.end_date!!)
                dayOffStatus.tvTypeDayOffApprove.text = dayOff.type
                dayOffStatus.tvNumberDayOffApprove.text = dayOff.total_dayOff.toString()
                dayOffStatus.tvManagerApprove.text = sessionManager.fetchManagerName()
            }
            IS_DECLINE -> {
                val dayOffStatus: DayOffDeclineStatus = holder as DayOffDeclineStatus
                dayOffStatus.tvTitleDayOffDecline.text = "Yêu cầu ${dayOff.type}"
                dayOffStatus.tvStartDateApprove.text = df.format(dayOff.start_date!!)
                dayOffStatus.tvEndDateApprove.text = df.format(dayOff.end_date!!)
                dayOffStatus.tvTypeDayOffApprove.text = dayOff.type
                dayOffStatus.tvNumberDayOffApprove.text = dayOff.total_dayOff.toString()
                dayOffStatus.tvNumberDayOffApprove.text = sessionManager.fetchManagerName()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (lstData.size != 0){
            val dayOff = lstData[position]
            return when(dayOff.manager_confirm){
                STATUS_WAITING -> {
                    IS_WAITING
                }
                STATUS_APPROVE -> {
                    IS_APPROVE
                }
                STATUS_DECLINE -> {
                    IS_DECLINE
                }
                else -> {
                    0
                }
            }
        }else{
            return 0
        }
    }

    override fun getItemCount(): Int {
        return if (lstData.size == 0){
            1
        }else{
            lstData.size
        }
    }

    class DayOffWaitingStatus(itemView: View): RecyclerView.ViewHolder(itemView){
        val tvTitleDayOffWaiting: TextView = itemView.findViewById(R.id.tv_title_dayOff_waiting)
        val tvStartDateWaiting: TextView = itemView.findViewById(R.id.tv_start_date_waiting)
        val tvEndDateWaiting: TextView = itemView.findViewById(R.id.tv_end_date_waiting)
        val tvTypeDayOffWaiting: TextView = itemView.findViewById(R.id.tv_type_dayOff_waiting)
        val tvNumberDayOffWaiting: TextView = itemView.findViewById(R.id.tv_number_dayOff_waiting)
        val tvManagerWaiting: TextView = itemView.findViewById(R.id.tv_manager_dayOff_waiting)
        val imvTrash: ImageView = itemView.findViewById(R.id.trashWaiting)
    }

    class DayOffApproveStatus(itemView: View): RecyclerView.ViewHolder(itemView){
        val tvTitleDayOffApprove: TextView = itemView.findViewById(R.id.tv_title_dayOff_approve)
        val tvStartDateApprove: TextView = itemView.findViewById(R.id.tv_start_date_approve)
        val tvEndDateApprove: TextView = itemView.findViewById(R.id.tv_end_date_approve)
        val tvTypeDayOffApprove: TextView = itemView.findViewById(R.id.tv_type_dayOff_approve)
        val tvManagerApprove: TextView = itemView.findViewById(R.id.tv_manager_dayOff_approve)
        val tvNumberDayOffApprove: TextView = itemView.findViewById(R.id.tv_number_dayOff_approve)
    }

    class DayOffDeclineStatus(itemView: View): RecyclerView.ViewHolder(itemView){
        val tvTitleDayOffDecline: TextView = itemView.findViewById(R.id.tv_title_dayOff_decline)
        val tvStartDateApprove: TextView = itemView.findViewById(R.id.tv_start_date_approve)
        val tvEndDateApprove: TextView = itemView.findViewById(R.id.tv_end_date_approve)
        val tvTypeDayOffApprove: TextView = itemView.findViewById(R.id.tv_type_dayOff_approve)
        val tvNumberDayOffApprove: TextView = itemView.findViewById(R.id.tv_number_dayOff_approve)
    }
}