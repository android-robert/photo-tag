package com.robert.phototagsample.fragments.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.robert.phototagsample.R
import com.robert.phototagsample.fragments.DashBoardFragment

class ViewPagerFragmentForDashBoard : Fragment() {
    private var mFragmentManager: FragmentManager? = null
    private var dashBoardFragment: DashBoardFragment? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_view_pager_for_dashboard, container, false)
        mFragmentManager = childFragmentManager
        val extras = arguments
        var type: String? = null
        type = extras!!.getString(TYPE, "")
        val fragment: Fragment
        when (type) {
            DashBoardFragments.TAG_PHOTO -> {
                fragment = TagPhotoFragment()
                switchFragment(fragment)
            }
            DashBoardFragments.SEARCH -> {
                fragment = SearchFragment()
                switchFragment(fragment)
            }
            DashBoardFragments.HOME -> {
                fragment = HomeFragment()
                switchFragment(fragment)
            }
            else -> {
                fragment = HomeFragment()
                switchFragment(fragment)
            }
        }
        return view
    }

    fun switchFragment(fragment: Fragment?) {
        mFragmentManager!!.beginTransaction().replace(R.id.dashboard, fragment!!).commit()
    }

    interface DashBoardFragments {
        companion object {
            const val HOME = "HOME"
            const val TAG_PHOTO = "MY_TEAM"
            const val SEARCH = "SEARCH"
        }
    }

    fun setHomeAsSelectedTab() {
        dashBoardFragment!!.setHomeAsSelectedTab()
    }

    companion object {
        private const val TYPE = "TYPE"
        fun newInstance(type: String?,
                        dashBoardFragment: DashBoardFragment?): ViewPagerFragmentForDashBoard {
            val viewPagerFragmentForDashBoard = ViewPagerFragmentForDashBoard()
            val extras = Bundle()
            extras.putString(TYPE, type)
            viewPagerFragmentForDashBoard.arguments = extras
            viewPagerFragmentForDashBoard.dashBoardFragment = dashBoardFragment
            return viewPagerFragmentForDashBoard
        }
    }
}