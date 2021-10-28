package com.example.demo.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.demo.screen.checkinFragment
import com.example.demo.screen.historyFragment
import com.example.demo.screen.homeFragment
import com.example.demo.screen.profileFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, behavior: Int) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        return 4
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> homeFragment()
            1 -> checkinFragment()
            2 -> historyFragment()
            3 -> profileFragment()

            else -> {
                homeFragment()
            }
        }
    }
}