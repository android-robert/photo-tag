package com.robert.phototagsample.fragments.dashboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.robert.phototagsample.fragments.DashBoardFragment
import com.robert.phototagsample.fragments.dashboard.ViewPagerFragmentForDashBoard.DashBoardFragments
import java.util.*

class ViewPagerAdapterForDashBoard(fm: FragmentManager?, dashBoardFragment: DashBoardFragment) : FragmentStatePagerAdapter(fm!!) {
    var dashBoardFragments = ArrayList<String>()
    private val dashBoardFragment: DashBoardFragment
    override fun getItem(position: Int): Fragment {
        return ViewPagerFragmentForDashBoard.newInstance(dashBoardFragments[position], dashBoardFragment)
    }

    override fun getCount(): Int {
        return dashBoardFragments.size
    }

    init {
        dashBoardFragments.add(DashBoardFragments.HOME)
        dashBoardFragments.add(DashBoardFragments.TAG_PHOTO)
        dashBoardFragments.add(DashBoardFragments.SEARCH)
        this.dashBoardFragment = dashBoardFragment
    }
}