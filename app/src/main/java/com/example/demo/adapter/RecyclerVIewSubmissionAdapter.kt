package com.example.demo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.backend.SessionManager
import com.example.demo.backend.entities.Submission

class RecyclerVIewSubmissionAdapter(
    private var submissionListener: SubmissionListener,
    private var checkedAll: Boolean?,
    private var ctx: Context,
): RecyclerView.Adapter<RecyclerVIewSubmissionAdapter.ViewHolderSubmission>(){

    private lateinit var listData: ArrayList<Submission>
    private val lstChecked = ArrayList<Submission>()
    private var TYPE_CHECKED = 1
    private val TYPE_UNCHECKED = 2
    private val sessionManager = SessionManager(ctx)

    fun setData(lst: ArrayList<Submission>){
        listData = lst
        notifyDataSetChanged()
    }

    class ViewHolderSubmission(itemView: View) : RecyclerView.ViewHolder(itemView){
        var checkBox: CheckBox = itemView.findViewById(R.id.checkedBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderSubmission {
        return if (TYPE_CHECKED == viewType){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.rcv_layout, parent, false)
            ViewHolderSubmission(view)
        }else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.rcv_layout_unchecked, parent, false)
            ViewHolderSubmission(view)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolderSubmission, position: Int) {
        if(listData.size > 0){
            holder.checkBox.text = "Yêu cầu ${listData[position].reason}" +
                    "\nNgười tạo Y/C: ${listData[position].created_by} " +
                    "\nHình thức: ${listData[position].type} " +
                    "\nThời gian: ${sessionManager.convertTimeStampToTime(listData[position].created_at!!.toLong(), "HH:mm")}" +
                    "\nNgày: ${listData[position].created_date}" +
                    "\nTrạng thái: ${listData[position].manager_confirm}"
            holder.checkBox.setOnClickListener {
                if (listData[position].status == true){
                    if(holder.checkBox.isChecked){
                        lstChecked.add(listData[position])
                    }else{
                        lstChecked.remove(listData[position])
                    }
                    submissionListener.onSubmissionChange(lstChecked)

                    if(checkedAll != null){
                        submissionListener.onSubmissionCheckAll()
                    }
                }
            }

            if (listData[position].status == true){
                if(checkedAll == true){
                    holder.checkBox.isChecked = true
                    submissionListener.onSubmissionChange(listData)
                }else{
                    lstChecked.remove(listData[position])
                    holder.checkBox.isChecked = false
                    submissionListener.onSubmissionChange(lstChecked)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(listData[position].status == true){
            TYPE_CHECKED
        }else{
            TYPE_UNCHECKED
        }
    }
}