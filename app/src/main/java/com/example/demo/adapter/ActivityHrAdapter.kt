package com.example.demo.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.demo.screen.HolidayFragment
import com.example.demo.screen.HrManagerUserFragment

class ActivityHrAdapter(fragmentManager: FragmentManager, behavior: Int): FragmentStatePagerAdapter(fragmentManager) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> HrManagerUserFragment()
            1 -> HolidayFragment()

            else -> {
                HrManagerUserFragment()
            }
        }
    }

}