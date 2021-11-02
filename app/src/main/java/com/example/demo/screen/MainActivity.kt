package com.example.demo.screen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.demo.Constant
import com.example.demo.R
import com.example.demo.adapter.ViewPagerAdapter
import com.example.demo.backend.ApiClient
import com.example.demo.backend.RestAPI
import com.example.demo.backend.SessionManager
import com.example.demo.backend.entities.Event
import com.example.demo.backend.entities.ListSubmission
import com.example.demo.backend.entities.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var mViewPager: ViewPager
    lateinit var mBottomNavigationView: BottomNavigationView
    lateinit var sessionManager: SessionManager
    lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)
        token = "token ${sessionManager.fetchAuthToken()}"

        saveTokenDevice()

        mViewPager = findViewById(R.id.view_pager)
        mBottomNavigationView = findViewById(R.id.navigation)


        val adapter = ViewPagerAdapter(
            supportFragmentManager,
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        mViewPager.adapter = adapter

        mViewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 ->
                        mBottomNavigationView.menu.findItem(R.id.home_).isChecked = true
                    1 ->
                        mBottomNavigationView.menu.findItem(R.id.checkin).isChecked = true
                    2 ->
                        mBottomNavigationView.menu.findItem(R.id.history).isChecked = true
                    3 ->
                        mBottomNavigationView.menu.findItem(R.id.profile).isChecked = true
                }
            }
        })

        mBottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_ -> {
                    mViewPager.currentItem = 0
                }
                R.id.checkin -> mViewPager.currentItem = 1
                R.id.history -> {
                    mViewPager.currentItem = 2
                    val historyFragment: historyFragment =
                        (mViewPager.adapter as ViewPagerAdapter).instantiateItem(mViewPager, 2) as historyFragment
                    historyFragment.reloadData()
                }
                R.id.profile -> mViewPager.currentItem = 3
            }
            true
        }
    }

    private fun saveTokenDevice(){
        if (sessionManager.fetchTokenDevice() != null){
            return
        }
        val request = ApiClient.getClient().create(RestAPI::class.java)
        val user = User(sessionManager.fetchUserName(), sessionManager.fetchMyEmail(), sessionManager.fetchTokenDevice())
        val call = request.saveTokenDevice(token, user)
        call.enqueue(object: Callback<Event>{
            override fun onResponse(call: Call<Event>, response: Response<Event>) {
                println("Save Token Device Success")
            }

            override fun onFailure(call: Call<Event>, t: Throwable) {
                Constant.dialogError(this@MainActivity, "Có lỗi xảy ra vui lòng thử lại.")
            }
        })
    }
}