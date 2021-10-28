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
import com.example.demo.R
import com.example.demo.adapter.layout.DropDownManagerAdapter
import com.example.demo.adapter.layout.DropDownRoleAdapter
import com.example.demo.adapter.layout.eneties.RolesView
import com.example.demo.backend.entities.User

class UserAdapter(var lstUser: ArrayList<User>, val ctx: Context): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    var viewBinderHelper: ViewBinderHelper = ViewBinderHelper()
    private lateinit var dropDownManagerAdapter: DropDownManagerAdapter
    private lateinit var dropDownRoleAdapter: DropDownRoleAdapter
    private lateinit var lstManager: ArrayList<User>
    val user = User()

    fun setData(listManger :ArrayList<User>, lstRole: ArrayList<RolesView>){
        lstManager = ArrayList()
        lstManager.add(user)
        this.lstUser = listManger
        listManger.forEach {
            if (it.is_manager == true && it.is_active == true){
                lstManager.add(it)
            }
        }
        dropDownManagerAdapter = DropDownManagerAdapter(ctx, R.layout.item_selected_manager, lstManager)
        dropDownRoleAdapter = DropDownRoleAdapter(ctx, R.layout.item_selected_manager, lstRole)

        notifyDataSetChanged()
    }

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val swipeLayout = itemView.findViewById<SwipeRevealLayout>(R.id.swipeLayout)
        val userName = itemView.findViewById<TextView>(R.id.tv_user_name)
        val layoutEdit = itemView.findViewById<LinearLayout>(R.id.layout_edit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        viewBinderHelper.setOpenOnlyOne(true)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_swiper, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = lstUser[position] ?: return
        viewBinderHelper.bind(holder.swipeLayout, user.id.toString())
        holder.userName.text = user.full_name

        holder.layoutEdit.setOnClickListener {
            openDialog(user)
        }
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private fun openDialog(user: User) {
        val dialog: Dialog = Dialog(ctx)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_edit_user)

        val window = dialog.window ?: return

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttribute: WindowManager.LayoutParams = window.attributes
        windowAttribute.gravity = Gravity.CENTER
        window.attributes = windowAttribute

        dialog.setCancelable(true)

        val btn_close = dialog.findViewById<Button>(R.id.btn_close)
        val btn_send = dialog.findViewById<Button>(R.id.btn_send)
        val edt_user = dialog.findViewById<TextView>(R.id.edtUserName)
        val spnRole = dialog.findViewById<Spinner>(R.id.spnRole)
        val spnManager = dialog.findViewById<Spinner>(R.id.spnManagerOfUser)
        val edt_email = dialog.findViewById<TextView>(R.id.edtEmail)
        val edt_totalDayOff = dialog.findViewById<TextView>(R.id.edtTotalDayOff)
        val sw_status = dialog.findViewById<Switch>(R.id.swStatus)

        spnRole.adapter = dropDownRoleAdapter
        spnRole.setSelection(getIndexRole(spnRole, user))

        spnManager.adapter = dropDownManagerAdapter
        spnManager.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                println(dropDownManagerAdapter.getItem(p2)?.manager_name)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        spnManager.setSelection(getIndex(spnManager, user.manager_name))

        btn_close.setOnClickListener {
            viewBinderHelper.closeLayout(user.id.toString())
            dialog.dismiss()
        }

        edt_user.text = user.full_name
        edt_email.text = user.email
        edt_totalDayOff.text = user.dayOff.toString()
        sw_status.isChecked = user.is_active!!

        btn_send.setOnClickListener {

        }
        
        dialog.show()
    }

    private fun getIndex(spnManager: Spinner?, managerName: String?): Int {
        for (i in (0..spnManager?.count!!)){
            try {
                if((spnManager.getItemAtPosition(i) as User).full_name == managerName){
                    return i
                }
            }catch (e: Exception){
                return 0
            }
        }
        return 0
    }

    private fun getIndexRole(spinner: Spinner?, user: User): Int{
        return when {
            user.is_user == true -> {
                if (user.is_manager == true){
                    if(user.is_admin == true){
                        3
                    }else{
                        2
                    }
                }else{
                    1
                }
            }
            else -> {
                0
            }
        }
    }

    override fun getItemCount(): Int {
        return lstUser.size
    }
}