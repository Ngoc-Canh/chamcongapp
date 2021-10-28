package com.example.demo.screen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.demo.R
import com.example.demo.adapter.ViewPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    lateinit var mViewPager: ViewPager
    lateinit var mBottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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


}