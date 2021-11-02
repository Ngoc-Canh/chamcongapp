package com.example.demo.screen

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.demo.Constant
import com.example.demo.R
import com.example.demo.adapter.UserAdapter
import com.example.demo.adapter.layout.DropDownManagerAdapter
import com.example.demo.adapter.layout.DropDownRoleAdapter
import com.example.demo.adapter.layout.eneties.RolesView
import com.example.demo.backend.ApiClient
import com.example.demo.backend.RestAPI
import com.example.demo.backend.SessionManager
import com.example.demo.backend.entities.ListUser
import com.example.demo.backend.entities.User
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HrManagerUserFragment : Fragment() {
    private lateinit var rcvUser: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var btnClose: Button
    private lateinit var btnAdd: Button
    private lateinit var token: String
    private lateinit var lstRole: ArrayList<RolesView>
    private lateinit var lstManager: ArrayList<User>
    private lateinit var lst: ArrayList<User>
    private lateinit var sessionManager: SessionManager
    private lateinit var request: RestAPI
    private lateinit var dropDownManagerAdapter: DropDownManagerAdapter
    private lateinit var dropDownRoleAdapter: DropDownRoleAdapter
    private lateinit var swipeRefresh: SwipeRefreshLayout

    private val emptyRole = RolesView("", "")
    private val normalUser = RolesView("Nhân Viên", "user")
    private val managerUser = RolesView("Quản Lý", "manager")
    private val hrRole = RolesView("Nhân Sự", "admin")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_user_manager_hr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireActivity().applicationContext)
        token = "Token ${sessionManager.fetchAuthToken()}"

        rcvUser = view.findViewById(R.id.rcv_user)
        btnClose = view.findViewById(R.id.btnCloseUserManager)
        btnAdd = view.findViewById(R.id.btnAddUser)
        swipeRefresh = view.findViewById(R.id.swipeRefreshHR)

        swipeRefresh.setOnRefreshListener{
            getListUser()
        }

        lstRole = ArrayList()
        lstRole.add(emptyRole)
        lstRole.add(normalUser)
        lstRole.add(managerUser)
        lstRole.add(hrRole)
        lst = ArrayList()

        request = ApiClient.getClient().create(RestAPI::class.java)
        swipeRefresh.isRefreshing = true

        getListUser()
        val layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        rcvUser.layoutManager = layoutManager

        userAdapter = UserAdapter(lst, requireActivity().applicationContext)
        rcvUser.adapter = userAdapter

        val itemDecoration = DividerItemDecoration(requireActivity().applicationContext, DividerItemDecoration.VERTICAL)
        rcvUser.addItemDecoration(itemDecoration)

        btnClose.setOnClickListener {

        }

        btnAdd.setOnClickListener {
            openDialog()
        }
    }

    private fun getListUser() {
        lst = ArrayList()
        lstManager = ArrayList()
        val call = request.getListUser(token)
        call.enqueue(object: Callback<ListUser> {
            override fun onResponse(call: Call<ListUser>, response: Response<ListUser>) {
                if (response.code() == 200){
                    response.body()!!.list_user.forEach {
                        lst.add(it)
                    }
                    lstManager.add(User())
                    lst.forEach {
                        if (it.is_manager == true && it.is_active == true){
                            lstManager.add(it)
                        }
                    }
                    userAdapter.setData(lst, lstRole)
                    dropDownRoleAdapter = DropDownRoleAdapter(requireActivity().applicationContext, R.layout.item_selected_manager, lstRole)
                    dropDownManagerAdapter = DropDownManagerAdapter(requireActivity().applicationContext, R.layout.item_selected_manager, lstManager)
                    swipeRefresh.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<ListUser>, t: Throwable) {
                swipeRefresh.isRefreshing = false
                Constant.dialogError(requireActivity(), "Có lỗi xảy ra vui lòng thử lại.")
            }
        })

//        lst.add(User(id = 1, username = "Canh", password = "123", null, "BNC User", null, null, "manager 1", true, false, false, true, null))
//        lst.add(User(id = 2, username = "CAnh", password = "123", null, "manager 3", null, null, null, false, true, false, true, null))
//        lst.add(User(id = 3, username = "CaNH", password = "123", null, "User", null, null, "manager 2", true, false, false, true, null))
//        lst.add(User(id = 4, username = "CanHH", password = "123", null, "manager 2", null, null, null, false, true, false, true, null))
//        lst.add(User(id = 5, username = "CanHHH", password = "123", null, "manager 1", null, null, null, false, true, false, false, null))
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode", "SetTextI18n")
    private fun openDialog() {
        val dialog: Dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_edit_user)

        val window = dialog.window ?: return

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttribute: WindowManager.LayoutParams = window.attributes
        windowAttribute.gravity = Gravity.CENTER
        window.attributes = windowAttribute

        dialog.setCancelable(true)

        val btnClose = dialog.findViewById<Button>(R.id.btn_close)
        val btnSend = dialog.findViewById<Button>(R.id.btn_send)
        val edtUser = dialog.findViewById<TextView>(R.id.edtUserName)
        val spnRole = dialog.findViewById<Spinner>(R.id.spnRole)
        val spnManager = dialog.findViewById<Spinner>(R.id.spnManagerOfUser)
        val edtEmail = dialog.findViewById<TextView>(R.id.edtEmail)
        val edtTotalDayOff = dialog.findViewById<TextView>(R.id.edtTotalDayOff)
        val valid = dialog.findViewById<TextView>(R.id.validForm)
        val layout = dialog.findViewById<LinearLayout>(R.id.layoutPassword)
        val password = dialog.findViewById<EditText>(R.id.edtPassword)
        val swStatus = dialog.findViewById<Switch>(R.id.swStatus)
        val layoutRelative = dialog.findViewById<RelativeLayout>(R.id.layoutRelative)
        var role = ""
        var managerEmail = ""

        layout.visibility = View.VISIBLE
        layoutRelative.visibility = View.GONE

        spnRole.adapter = dropDownRoleAdapter
        spnRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                role = dropDownRoleAdapter.getItem(p2)?.code.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        spnManager.adapter = dropDownManagerAdapter
        spnManager.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                managerEmail = dropDownManagerAdapter.getItem(p2)?.email.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        btnSend.setOnClickListener {
            if (!isValidEmail(edtEmail.text.toString())){
                valid.text = "Email không hợp lệ"
                valid.visibility = View.VISIBLE
                return@setOnClickListener
            }else{
                valid.visibility = View.GONE
            }

            if(edtUser.text.isEmpty() || edtEmail.text.isEmpty() || edtTotalDayOff.text.isEmpty() || role.isEmpty()){
                valid.text = "Bạn chưa nhập đủ thông tin"
                valid.visibility = View.VISIBLE
                return@setOnClickListener
            }else{
                valid.visibility = View.GONE
            }
            val user = User(null, edtEmail.text.toString(), password.text.toString(), null, edtUser.text.toString(), "12".toFloat(), managerEmail, null, isUserRole(role), isManagerRole(role), isAdminRole(role), true, edtEmail.text.toString(), null)
            createUser(user, dialog)
        }

        dialog.show()
    }

    private fun isUserRole(role: String): Boolean{
        if (role == "user"){
            return true
        }
        return false
    }
    private fun isManagerRole(role: String): Boolean{
        if (role == "manager"){
            return true
        }
        return false
    }
    private fun isAdminRole(role: String): Boolean{
        if (role == "admin"){
            return true
        }
        return false
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun createUser(user: User, dialog: Dialog){
        val call = request.createUser(token, user)
        val valid = dialog.findViewById<TextView>(R.id.validForm)
        call.enqueue(object: Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.code() == 201){
                    valid.visibility = View.GONE
                    dialog.dismiss()
                    getListUser()
                    Constant.dialogSuccess(requireActivity().applicationContext, "Tạo mới thành công.")
                }else{
                    valid.visibility = View.VISIBLE
                    val jObjError = JSONObject(response.errorBody()?.string())
                    val valid = dialog.findViewById<TextView>(R.id.validForm)
                    valid.text = jObjError["msg"].toString()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Constant.dialogError(requireActivity().applicationContext, "Có lỗi xảy ra vui lòng thử lại.")
            }
        })
    }
}