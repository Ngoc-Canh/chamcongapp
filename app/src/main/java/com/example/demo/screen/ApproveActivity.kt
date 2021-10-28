package com.example.demo.screen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.demo.R
import com.example.demo.adapter.ApproveActivityViewPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class ApproveActivity : AppCompatActivity() {

    lateinit var mViewPager: ViewPager
    lateinit var mBottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_approve)

        mViewPager = findViewById(R.id.view_pager_approve)
        mBottomNavigationView = findViewById(R.id.navigation_approve)

        val adapter = ApproveActivityViewPagerAdapter(
            supportFragmentManager,
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        mViewPager.adapter = adapter

        mViewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 ->
                        mBottomNavigationView.menu.findItem(R.id.dayOff).isChecked = true
                    1 ->
                        mBottomNavigationView.menu.findItem(R.id.submission).isChecked = true
                }
            }
        })

        mBottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.dayOff -> mViewPager.currentItem = 0
                R.id.submission -> mViewPager.currentItem = 1
            }
            true
        }

    }
}