package com.robert.phototagsample.fragments.dashboard

import android.app.ProgressDialog
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.robert.phototag.SquareImageView
import com.robert.phototagsample.PhotoTagApplication
import com.robert.phototagsample.R
import com.robert.phototagsample.activities.DashBoardActivity
import com.robert.phototagsample.adapters.PhotoAdapter
import com.robert.phototagsample.interfaces.AppConstants
import com.robert.phototagsample.interfaces.AppConstants.ProgressText
import com.robert.phototagsample.interfaces.AppConstants.ToastText
import com.robert.phototagsample.interfaces.PhotoClickListener
import com.robert.phototagsample.models.Photo
import java.util.*

class HomeFragment : Fragment(), PhotoClickListener, AppConstants, View.OnClickListener {
    private val newPhotoTaggedIntentFilter: IntentFilter = IntentFilter(AppConstants.Events.NEW_PHOTO_IS_TAGGED)
    private val newConfigurationSavedIntentFilter: IntentFilter = IntentFilter(AppConstants.Events.NEW_CONFIGURATION_SAVED)
    private var photos = ArrayList<Photo>()
    private var recyclerViewPhotos: RecyclerView? = null
    private var emptyContainer: LinearLayout? = null
    private var photoAdapter: PhotoAdapter? = null
    private var handler: Handler? = null
    private var progressDialog: ProgressDialog? = null
    var configuration: SquareImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        handler = Handler()
        configuration = rootView.findViewById(R.id.iv_configuration)
        configuration!!.setOnClickListener(this)
        emptyContainer = rootView.findViewById(R.id.empty_container)
        rootView.findViewById<View>(R.id.iv_delete_all_photos).setOnClickListener(this)
        photos.addAll(PhotoTagApplication.instance!!.photos)
        recyclerViewPhotos = rootView.findViewById(R.id.rv_photos)
        photoAdapter = PhotoAdapter(photos, context, this)
        recyclerViewPhotos!!.adapter = photoAdapter
        recyclerViewPhotos!!.layoutManager = LinearLayoutManager(context)
        showEmptyContainer()
        initProgressDialog(ProgressText.PROGRESS_MSG)
        return rootView
    }

    override fun onPhotoClick(photo: Photo?, position: Int) {}
    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(activity!!).registerReceiver(newPhotoIsTagged,
                newPhotoTaggedIntentFilter)
        LocalBroadcastManager.getInstance(activity!!).registerReceiver(newConfigurationSaved,
                newConfigurationSavedIntentFilter)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(newPhotoIsTagged)
        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(newConfigurationSaved)
    }

    private fun showEmptyContainer() {
        if (PhotoTagApplication.instance!!.photos.isEmpty()) {
            recyclerViewPhotos!!.visibility = View.GONE
            emptyContainer!!.visibility = View.VISIBLE
        } else {
            recyclerViewPhotos!!.visibility = View.VISIBLE
            emptyContainer!!.visibility = View.GONE
        }
    }

    private val newPhotoIsTagged: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            showProgress(ProgressText.PROGRESS_MSG)
            handler!!.postDelayed({
                photos = PhotoTagApplication.instance!!.photos
                photoAdapter = PhotoAdapter(photos, getContext(),this@HomeFragment)
                recyclerViewPhotos!!.adapter = photoAdapter
                showEmptyContainer()
                dismissProgress()
                recyclerViewPhotos!!.scrollToPosition(photos.size - 1)
                Toast.makeText(activity, ToastText.PHOTO_TAGGED_SUCCESSFULLY, Toast.LENGTH_SHORT).show()
            }, AppConstants.ADD_TAG_DELAY_MILLIS.toLong())
        }
    }
    private val newConfigurationSaved: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            showProgress(ProgressText.RELOADING)
            handler!!.postDelayed({
                photoAdapter = PhotoAdapter(photos, getContext(),
                        this@HomeFragment,
                        PhotoTagApplication.instance!!.tagShowAnimation,
                        PhotoTagApplication.instance!!.tagHideAnimation,
                        PhotoTagApplication.instance!!.carrotTopColor,
                        PhotoTagApplication.instance!!.tagBackgroundColor,
                        PhotoTagApplication.instance!!.tagTextColor,
                        PhotoTagApplication.instance!!.likeColor
                )
                recyclerViewPhotos!!.adapter = photoAdapter
                showEmptyContainer()
                dismissProgress()
            }, AppConstants.CONFIGURATION_DELAY_MILLIS.toLong())
        }
    }

    private fun initProgressDialog(msg: String) {
        progressDialog = ProgressDialog(activity)
        progressDialog!!.isIndeterminate = true
        progressDialog!!.setCanceledOnTouchOutside(false)
        progressDialog!!.setMessage(msg)
    }

    fun showProgress(msg: String?) {
        progressDialog!!.setMessage(msg)
        progressDialog!!.show()
    }

    fun dismissProgress() {
        progressDialog!!.dismiss()
    }

    override fun onClick(view: View) {
        if (PhotoTagApplication.instance!!.photos.isNotEmpty()) {
            when (view.id) {
                R.id.iv_configuration -> (activity as DashBoardActivity?)!!.showConfigurationBottomSheet()
                R.id.iv_delete_all_photos -> showDeleteAllPhotosAlertDialog()
            }
        } else {
            Toast.makeText(activity, getString(R.string.no_posts_yet), Toast.LENGTH_SHORT).show()
        }
    }

    fun showDeleteAllPhotosAlertDialog() {
        val alert = AlertDialog.Builder(activity!!)
        alert.setTitle(getString(R.string.remove_all_tagged_posts))
        alert.setMessage(getString(R.string.are_you_sure_you_want_to_delete_all_posts))
        alert.setPositiveButton(getString(R.string.ok)) { dialog, which ->
            PhotoTagApplication.instance!!.savePhotos(ArrayList<Photo>())
            photos.clear()
            photoAdapter!!.notifyDataSetChanged()
            showEmptyContainer()
        }
        alert.setNegativeButton(getString(R.string.cancel), null)
        alert.show()
    }
}