package com.example.demo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.backend.SessionManager
import com.example.demo.backend.entities.EventDetail
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class RecyclerViewChamBu(private var lstData: ArrayList<EventDetail>,
                         val deleteItem: DeleteItem,
                         var context: Context,
                         var isWaiting: Boolean?,
                         var isApprove: Boolean?,
                         var isDecline: Boolean?):
    RecyclerView.Adapter<RecyclerViewChamBu.ChamBuViewHolder>() {

    private val IS_WAITING = 1
    private val IS_APPROVE = 2
    private val IS_DECLINE = 3

    private val STATUS_WAITING = "waiting"
    private val STATUS_APPROVE = "accept"
    private val STATUS_DECLINE = "decline"

    private val sessionManager = SessionManager(this.context)

    fun setData(lst: ArrayList<EventDetail>, waiting: Boolean, accept: Boolean, decline: Boolean){
        this.lstData = lst
        this.isWaiting = waiting
        this.isApprove = accept
        this.isDecline = decline
        notifyDataSetChanged()
    }

    class ChamBuViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tv_title_waiting = itemView.findViewById<TextView>(R.id.tv_title_cham_bu_waiting)
        val imvDelete = itemView.findViewById<ImageView>(R.id.trashWaiting)
        val tv_title_approve = itemView.findViewById<TextView>(R.id.tv_title_cham_bu_approve)
        val tv_title_decline = itemView.findViewById<TextView>(R.id.tv_title_cham_bu_decline)

        val tv_time_waiting = itemView.findViewById<TextView>(R.id.tv_time_waiting)
        val tv_time_approve = itemView.findViewById<TextView>(R.id.tv_time_approve)
        val tv_time_decline = itemView.findViewById<TextView>(R.id.tv_time_decline)

        val tv_inOut_waiting = itemView.findViewById<TextView>(R.id.tv_inOut_waiting)
        val tv_inOut_approve = itemView.findViewById<TextView>(R.id.tv_inOut_approve)
        val tv_inOut_decline = itemView.findViewById<TextView>(R.id.tv_inOut_decline)

        val tv_date_waiting = itemView.findViewById<TextView>(R.id.tv_date_waiting)
        val tv_date_approve = itemView.findViewById<TextView>(R.id.tv_date_approve)
        val tv_date_decline = itemView.findViewById<TextView>(R.id.tv_date_decline)

        val tv_manager_waiting = itemView.findViewById<TextView>(R.id.tv_manager_waiting)
        val tv_manager_approve = itemView.findViewById<TextView>(R.id.tv_manager_approve)
        val tv_manager_decline = itemView.findViewById<TextView>(R.id.tv_manager_decline)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChamBuViewHolder {
        return when (viewType) {
            IS_WAITING -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.rcv_chambu_waiting, parent, false)
                ChamBuViewHolder(view)
            }
            IS_APPROVE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.rcv_chambu_approve, parent, false)
                ChamBuViewHolder(view)
            }
            IS_DECLINE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.rcv_chambu_decline, parent, false)
                ChamBuViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.empty_request, parent, false)
                ChamBuViewHolder(view)
            }
        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: ChamBuViewHolder, position: Int) {
        if (lstData.size <= 0){
            return
        }

        val event = lstData[position]
        val df = SimpleDateFormat("yyyy-MM-dd")

        when (holder.itemViewType) {
            IS_WAITING -> {
                holder.tv_title_waiting.text = "Yêu cầu ${event.reason}"
                holder.tv_time_waiting.text = sessionManager.convertTimeStampToTime((event.created_at!!), "HH:ss")
                holder.tv_inOut_waiting.text = event.event_type
                holder.tv_date_waiting.text = df.format(event.created_date!!)
                holder.tv_manager_waiting.text = sessionManager.fetchManagerName()
                holder.imvDelete.setOnClickListener {
                    deleteItem.onClickItem(event.id!!)
                }
            }
            IS_APPROVE -> {
                holder.tv_title_approve.text = "Yêu cầu ${event.reason}"
                holder.tv_time_approve.text = sessionManager.convertTimeStampToTime((event.created_at!!), "HH:ss")
                holder.tv_inOut_approve.text = event.event_type
                holder.tv_date_approve.text = df.format(event.created_date!!)
                holder.tv_manager_approve.text = sessionManager.fetchManagerName()
            }
            IS_DECLINE -> {
                holder.tv_title_decline.text = "Yêu cầu ${event.reason}"
                holder.tv_time_decline.text = sessionManager.convertTimeStampToTime((event.created_at!!), "HH:ss")
                holder.tv_inOut_decline.text = event.event_type
                holder.tv_date_decline.text = df.format(event.created_date!!)
                holder.tv_manager_decline.text = sessionManager.fetchManagerName()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (lstData.size != 0){
            val event = lstData[position]
            return when(event.manager_confirm){
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
}