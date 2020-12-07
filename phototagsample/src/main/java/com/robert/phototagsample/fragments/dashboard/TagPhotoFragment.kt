package com.robert.phototagsample.fragments.dashboard

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.robert.phototag.PhotoTag
import com.robert.phototag.PhotoTag.PhotoEvent
import com.robert.phototag.SquareImageView
import com.robert.phototagsample.PhotoTagApplication
import com.robert.phototagsample.R
import com.robert.phototagsample.adapters.UserAdapter
import com.robert.phototagsample.interfaces.AppConstants
import com.robert.phototagsample.interfaces.AppConstants.ToastText
import com.robert.phototagsample.interfaces.UserClickListener
import com.robert.phototagsample.models.Photo
import com.robert.phototagsample.models.User
import com.robert.phototagsample.utilities.KeyBoardUtil
import com.robert.phototagsample.utilities.UsersData
import java.util.*

class TagPhotoFragment : Fragment(), UserClickListener, View.OnClickListener, AppConstants {
    private var photoTag: PhotoTag? = null
    private var photoToBeTaggedURI: Uri? = null
    private var recyclerViewUsers: RecyclerView? = null
    private var headerUsers: LinearLayout? = null
    private var headerSearchUsers: LinearLayout? = null
    private var tapPhotoToTagUser: TextView? = null
    private var addTagInX = 0
    private var addTagInY = 0
    private var searchForUser: EditText? = null
    private var userAdapter: UserAdapter? = null
    private val users = ArrayList<User?>()
    private val requestOptions = RequestOptions()
            .placeholder(0)
            .fallback(0)
            .centerCrop()
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.ALL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_tag_photo, container, false)
        photoTag = rootView.findViewById(R.id.photo_tag)
        photoTag!!.setTaggedPhotoEvent(photoEvent)
        val cancelTextView = rootView.findViewById<TextView>(R.id.cancel)
        val doneImageView: SquareImageView = rootView.findViewById(R.id.done)
        val backImageView: SquareImageView = rootView.findViewById(R.id.get_back)
        recyclerViewUsers = rootView.findViewById(R.id.rv_some_one_to_be_tagged)
        tapPhotoToTagUser = rootView.findViewById(R.id.tap_photo_to_tag_someone)
        headerUsers = rootView.findViewById(R.id.header_tag_photo)
        headerSearchUsers = rootView.findViewById(R.id.header_search_someone)
        searchForUser = rootView.findViewById(R.id.search_for_a_person)
        searchForUser!!.addTextChangedListener(textWatcher)
        tapPhotoToTagUser!!.setOnClickListener(this)
        cancelTextView.setOnClickListener(this)
        doneImageView.setOnClickListener(this)
        backImageView.setOnClickListener(this)
        users.addAll(UsersData.users)
        userAdapter = UserAdapter(users, activity, this@TagPhotoFragment)
        recyclerViewUsers!!.adapter = userAdapter
        recyclerViewUsers!!.layoutManager = LinearLayoutManager(activity)
        return rootView
    }

    private fun loadImage() {
        Glide
                .with(this)
                .load(photoToBeTaggedURI)
                .apply(requestOptions)
                .into(photoTag!!.tagImageView!!)
    }

    override fun onUserClick(user: User?, position: Int) {
        activity!!.runOnUiThread {
            KeyBoardUtil.hideKeyboard(activity)
            photoTag!!.addTag(addTagInX, addTagInY, user!!.userName!!)
            recyclerViewUsers!!.visibility = View.GONE
            tapPhotoToTagUser!!.visibility = View.VISIBLE
            headerSearchUsers!!.visibility = View.GONE
            headerUsers!!.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.cancel -> {
                KeyBoardUtil.hideKeyboard(activity)
                recyclerViewUsers!!.scrollToPosition(0)
                recyclerViewUsers!!.visibility = View.GONE
                tapPhotoToTagUser!!.visibility = View.VISIBLE
                headerSearchUsers!!.visibility = View.GONE
                headerUsers!!.visibility = View.VISIBLE
            }
            R.id.done -> if (photoToBeTaggedURI != null) {
                if (photoTag!!.tags.isEmpty()) {
                    Toast.makeText(activity, ToastText.TAG_ONE_USER_AT_LEAST, Toast.LENGTH_SHORT).show()
                } else {
                    val photoArrayList: ArrayList<Photo> = PhotoTagApplication.instance!!.photos
                    val photo = Photo(Calendar.getInstance().timeInMillis.toString() + "",
                            photoToBeTaggedURI.toString(),
                            photoTag!!.tags)
                    photoArrayList.add(photo)
                    PhotoTagApplication.instance!!.savePhotos(photoArrayList)
                    (parentFragment as ViewPagerFragmentForDashBoard?)!!.setHomeAsSelectedTab()
                    reset()
                    val intentNewPhotoIsTagged = Intent(AppConstants.Events.NEW_PHOTO_IS_TAGGED)
                    LocalBroadcastManager.getInstance(activity!!)
                            .sendBroadcast(intentNewPhotoIsTagged)
                }
            } else {
                Toast.makeText(activity, ToastText.CHOOSE_A_PHOTO, Toast.LENGTH_SHORT).show()
            }
            R.id.get_back -> {
                (parentFragment as ViewPagerFragmentForDashBoard?)!!.setHomeAsSelectedTab()
                reset()
            }
            R.id.tap_photo_to_tag_someone -> if (photoToBeTaggedURI == null) {
                val photoToBeTagged = Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(photoToBeTagged, AppConstants.CHOOSE_A_PHOTO_TO_BE_TAGGED)
            }
        }
    }

    private val photoEvent: PhotoEvent = object : PhotoEvent {
        override fun singleTapConfirmedAndRootIsInTouch(x: Int, y: Int) {
            activity!!.runOnUiThread {
                addTagInX = x
                addTagInY = y
                recyclerViewUsers!!.visibility = View.VISIBLE
                headerUsers!!.visibility = View.GONE
                tapPhotoToTagUser!!.visibility = View.GONE
                headerSearchUsers!!.visibility = View.VISIBLE
            }
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            return true
        }

        override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
            return true
        }

        override fun onLongPress(e: MotionEvent?) {}
    }
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (searchForUser!!.text.toString().trim { it <= ' ' } == "") {
                users.clear()
                users.addAll(UsersData.users)
                userAdapter!!.notifyDataSetChanged()
            } else {
                users.clear()
                users.addAll(UsersData.getFilteredUsers(searchForUser!!.text.toString().trim { it <= ' ' }))
                userAdapter!!.notifyDataSetChanged()
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.CHOOSE_A_PHOTO_TO_BE_TAGGED && resultCode == Activity.RESULT_OK && data != null) {
            photoToBeTaggedURI = data.data
            loadImage()
            tapPhotoToTagUser!!.text = getString(R.string.tap_photo_tag_user_drag_to_move_or_tap_to_delete)
        }
    }

    private fun reset() {
        photoToBeTaggedURI = null
        loadImage()
        photoTag!!.removeTags()
        tapPhotoToTagUser!!.text = getString(R.string.tap_here_to_choose_a_photo)
    }
}