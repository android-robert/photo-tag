package com.robert.phototagsample.fragments

import android.content.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.robert.phototagsample.R
import com.robert.phototagsample.activities.DashBoardActivity
import com.robert.phototagsample.fragments.dashboard.ViewPagerAdapterForDashBoard
import com.robert.phototagsample.interfaces.AppConstants
import com.robert.phototagsample.utilities.CustomViewPager

class DashBoardFragment : Fragment(), AppConstants {
    var bottomNavigationView: BottomNavigationView? = null
    var customViewPager: CustomViewPager? = null
    private var rootView: View? = null
    private var dashBoardActivityContext: DashBoardActivity? = null
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.tab_home -> customViewPager!!.setCurrentItem(0, true)
            R.id.tab_tag_photo -> customViewPager!!.setCurrentItem(1, true)
            R.id.tab_search -> customViewPager!!.setCurrentItem(2, true)
        }
        true
    }
    private val onPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
        override fun onPageSelected(position: Int) {
            bottomNavigationView!!.menu.getItem(position).isChecked = true
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_dash_board, container, false)
        bottomNavigationView = rootView!!.findViewById(R.id.bottom_navigation_view)
        customViewPager = rootView!!.findViewById(R.id.dashboard_pager)
        initView()
        return rootView
    }

    private fun initView() {
        customViewPager!!.adapter = ViewPagerAdapterForDashBoard(dashBoardActivityContext!!.supportFragmentManager, this)
        customViewPager!!.setPagingEnabled(true)
        customViewPager!!.addOnPageChangeListener(onPageChangeListener)
        customViewPager!!.offscreenPageLimit = AppConstants.OFFSCREEN_PAGE_LIMIT
        bottomNavigationView!!.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dashBoardActivityContext = context as DashBoardActivity
    }

    fun setHomeAsSelectedTab() {
        bottomNavigationView!!.selectedItemId = R.id.tab_home
    }
}