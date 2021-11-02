package com.example.demo.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.demo.Constant
import com.example.demo.R
import com.example.demo.backend.ApiClient
import com.example.demo.backend.RestAPI
import com.example.demo.backend.SessionManager
import com.example.demo.backend.entities.Holiday
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class HolidayAdapter(val ctx: Context): RecyclerView.Adapter<HolidayAdapter.HolidayViewHolder>() {

    var viewBinderHelper: ViewBinderHelper = ViewBinderHelper()
    val sessionManager = SessionManager(ctx)
    private val request = ApiClient.getClient().create(RestAPI::class.java)
    private var lstHoliday = ArrayList<Holiday>()
    private val token = "token ${sessionManager.fetchAuthToken()}"

    fun setData(data: ArrayList<Holiday>){
        lstHoliday = data
        notifyDataSetChanged()
    }

    class HolidayViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val swipeLayout = itemView.findViewById<SwipeRevealLayout>(R.id.swipeLayout)
        val layoutEdit = itemView.findViewById<TextView>(R.id.btnEdit)
        val layoutDelete = itemView.findViewById<TextView>(R.id.btnDelete)
        val descHoliday = itemView.findViewById<TextView>(R.id.desc_holiday)
        val rangeDate = itemView.findViewById<TextView>(R.id.rangeDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolidayViewHolder {
        viewBinderHelper.setOpenOnlyOne(true)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_swiper_delete, parent, false)
        return HolidayViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: HolidayViewHolder, position: Int) {
        if (lstHoliday.size > 0){
            val holiday = lstHoliday[position]
            val df = SimpleDateFormat("dd/MM/yyyy")
            viewBinderHelper.bind(holder.swipeLayout, holiday.id.toString())
            holder.rangeDate.text = "Từ ${df.format(holiday.startDate!!)} - đến ${df.format(holiday.endDate!!)}"
            holder.descHoliday.text = holiday.title

            holder.layoutEdit.setOnClickListener {
                openDialog(holiday)
            }

            holder.layoutDelete.setOnClickListener {
                openDialogDelete(holiday)
            }
        }
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private fun openDialog(holiday: Holiday) {
        val dialog = Dialog(ctx)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_holiday_create)

        val window = dialog.window ?: return

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttribute: WindowManager.LayoutParams = window.attributes
        windowAttribute.gravity = Gravity.CENTER
        window.attributes = windowAttribute

        dialog.setCancelable(true)

        val btnClose = dialog.findViewById<Button>(R.id.btn_close)
        val btnSend = dialog.findViewById<Button>(R.id.btn_send)
        val edtStartDate = dialog.findViewById<TextView>(R.id.edt_dateStart)
        val edtEndDate = dialog.findViewById<TextView>(R.id.edt_dateEnd)
        val desc = dialog.findViewById<EditText>(R.id.desc)


        btnClose.setOnClickListener {
            viewBinderHelper.closeLayout(holiday.id.toString())
            dialog.dismiss()
        }

        edtStartDate.setText(holiday.startDate.toString(), TextView.BufferType.EDITABLE)
        edtEndDate.setText(holiday.endDate.toString(), TextView.BufferType.EDITABLE)
        desc.setText(holiday.title.toString(), TextView.BufferType.EDITABLE)

        btnSend.setOnClickListener {

        }
        
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun openDialogDelete(holiday: Holiday){
        val dialog = Dialog(ctx)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_confirm)

        val window = dialog.window ?: return

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttribute: WindowManager.LayoutParams = window.attributes
        windowAttribute.gravity = Gravity.CENTER
        window.attributes = windowAttribute

        dialog.setCancelable(false)

        val btnClose = dialog.findViewById<Button>(R.id.cancelConfirm)
        val btnConfirm = dialog.findViewById<Button>(R.id.approveRequest)
        val message = dialog.findViewById<TextView>(R.id.message_confirm)

        message.text = "Bạn có muốn xóa ngày nghỉ lễ này ??"

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            dialog.dismiss()
            val call = request.deleteHoliday(token, holiday.id!!)
            call.enqueue(object: Callback<Holiday>{
                override fun onResponse(call: Call<Holiday>, response: Response<Holiday>) {
                    if(response.code() == 200){
                        dialog.dismiss()
                        Constant.dialogSuccess(ctx, "Xóa bản ghi thành công")
                        lstHoliday.remove(holiday)
                        notifyDataSetChanged()
                    }else{
                        val jObjError = JSONObject(response.errorBody()?.string())
                        Constant.dialogError(ctx, jObjError["msg"].toString())
                    }
                }

                override fun onFailure(call: Call<Holiday>, t: Throwable) {
                    Constant.dialogError(ctx, "Có lỗi xảy ra vui lòng thử lại.")
                }
            })
        }
        dialog.show()
    }

    override fun getItemCount(): Int {
        return lstHoliday.size
    }
}