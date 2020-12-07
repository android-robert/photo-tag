package com.robert.phototagsample.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.robert.phototagsample.R
import com.robert.phototagsample.fragments.DashBoardFragment
import com.robert.phototagsample.fragments.bottomsheet.ConfigurationBottomSheet
import com.robert.phototagsample.fragments.dashboard.ViewPagerFragmentForDashBoard.DashBoardFragments

class DashBoardActivity : AppCompatActivity() {
    private var configurationBottomSheet: ConfigurationBottomSheet? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment: Fragment = DashBoardFragment()
        fragmentTransaction.add(R.id.dash_board_content,
                fragment,
                DashBoardFragments.HOME)
        fragmentTransaction.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun showConfigurationBottomSheet() {
        configurationBottomSheet = ConfigurationBottomSheet()
        configurationBottomSheet!!.show(supportFragmentManager,
                ConfigurationBottomSheet::class.java.simpleName)
    }

    override fun onPause() {
        super.onPause()
        if (configurationBottomSheet != null && configurationBottomSheet!!.isVisible) {
            configurationBottomSheet!!.dismiss()
        }
    }
}