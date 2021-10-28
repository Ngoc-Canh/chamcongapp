package com.example.demo.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.demo.screen.approve_dayOff
import com.example.demo.screen.approve_submission

class ApproveActivityViewPagerAdapter(fragmentManager: FragmentManager, behavior: Int): FragmentStatePagerAdapter(fragmentManager) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> approve_dayOff()
            1 -> approve_submission()

            else -> {
                approve_dayOff()
            }
        }
    }

}