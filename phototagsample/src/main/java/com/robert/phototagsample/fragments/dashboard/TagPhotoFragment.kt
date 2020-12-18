package com.robert.phototagsample.fragments.dashboard

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
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
import com.robert.phototag.Tag
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
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class TagPhotoFragment : Fragment(), UserClickListener, View.OnClickListener, AppConstants {
    private val TAG = "TagPhotoFragment"
    private var photoTag: PhotoTag? = null
    private var photo: Photo? = null
    private var tagsShowHideHelper = HashSet<String?>()
    private var tagShowAnimation: Animation? = null
    private var tagHideAnimation: Animation? = null

    private var photoToBeTaggedURI: Uri? = null
    private var recyclerViewUsers: RecyclerView? = null
    private var headerUsers: LinearLayout? = null
    private var headerSearchUsers: LinearLayout? = null
    private var tapPhotoToTagUser: TextView? = null
    private var tagIndicator: AppCompatImageView? = null

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
        EventBus.getDefault().register(this)
        tagShowAnimation = AnimationUtils.loadAnimation(context, PhotoTagApplication.instance!!.tagShowAnimation)
        tagHideAnimation = AnimationUtils.loadAnimation(context, PhotoTagApplication.instance!!.tagHideAnimation)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_tag_photo, container, false)
        photoTag = rootView.findViewById(R.id.photo_tag)
        photoTag!!.setTaggedPhotoEvent(photoEvent)
        val cancelTextView = rootView.findViewById<TextView>(R.id.cancel)
        val doneImageView: SquareImageView = rootView.findViewById(R.id.done)
        val backImageView: SquareImageView = rootView.findViewById(R.id.get_back)

        tagIndicator = rootView.findViewById(R.id.tag_indicator)
        tagIndicator!!.visibility = View.GONE

        recyclerViewUsers = rootView.findViewById(R.id.rv_some_one_to_be_tagged)
        tapPhotoToTagUser = rootView.findViewById(R.id.tap_photo_to_tag_someone)
        headerUsers = rootView.findViewById(R.id.header_tag_photo)
        headerSearchUsers = rootView.findViewById(R.id.header_search_someone)
        searchForUser = rootView.findViewById(R.id.search_for_a_person)
        searchForUser!!.addTextChangedListener(textWatcher)

        tapPhotoToTagUser!!.setOnClickListener(this)
        tagIndicator!!.setOnClickListener(this)
        cancelTextView.setOnClickListener(this)
        doneImageView.setOnClickListener(this)
        backImageView.setOnClickListener(this)
        users.addAll(UsersData.users)
        userAdapter = UserAdapter(users, activity, this@TagPhotoFragment)
        recyclerViewUsers!!.adapter = userAdapter
        recyclerViewUsers!!.layoutManager = LinearLayoutManager(activity)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (tagShowAnimation != null) {
            photoTag!!.setTagShowAnimation(tagShowAnimation)
        }
        if (tagHideAnimation != null) {
            photoTag!!.setTagHideAnimation(tagHideAnimation)
        }
    }

    private fun loadImage() {
        Glide
                .with(this)
                .load(photoToBeTaggedURI)
                .apply(requestOptions)
                .into(photoTag!!.tagImageView!!)
    }

    private fun addTagViewFromTags(tags: ArrayList<Tag>) {
        photoTag!!.addTagViewFromTags(tags)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun addTagViewFromTags(photo: Photo? = null) {
        reset()
        tapPhotoToTagUser!!.text = getString(R.string.tap_photo_tag_user_drag_to_move_or_tap_to_delete)
        this.photo = photo
        photo?: kotlin.run {
            return
        }
        photo.imageUri?.let {
            photoToBeTaggedURI = Uri.parse(it)
            loadImage()
        }

        photo.tags?.let {
            photoTag!!.addTagViewFromTags(it)
            photoTag!!.showTags()
            tagsShowHideHelper.add(photo.id)
            if (it.isNotEmpty()) {
                tagIndicator!!.visibility = View.VISIBLE
            }
        }
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
            R.id.tag_indicator -> {
                if (!tagsShowHideHelper.contains(photo!!.id)) {
                    Log.e(TAG, "--->showTags()")
                    photoTag!!.showTags()
                    tagsShowHideHelper.add(photo!!.id)
                } else {
                    Log.e(TAG,"--->hideTags()")
                    photoTag!!.hideTags()
                    tagsShowHideHelper.remove(photo!!.id)
                }
            }
            R.id.done -> if (photoToBeTaggedURI != null) {
                if (photoTag!!.tags.isEmpty()) {
                    Toast.makeText(activity, ToastText.TAG_ONE_USER_AT_LEAST, Toast.LENGTH_SHORT).show()
                } else {
                    updateTags()
                    (parentFragment as ViewPagerFragmentForDashBoard?)!!.setHomeAsSelectedTab()
                    reset()
                    val intentNewPhotoIsTagged = Intent(AppConstants.Events.NEW_PHOTO_IS_TAGGED)
                    LocalBroadcastManager.getInstance(activity!!).sendBroadcast(intentNewPhotoIsTagged)
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

    private fun updateTags() {
        val photoArrayList: ArrayList<Photo> = PhotoTagApplication.instance!!.photos
        val position = exists(photoArrayList, photo)
        if (position > -1) {
            photo!!.tags = photoTag!!.tags
            photoArrayList[position] = photo!!
        } else {
            photo = Photo(Calendar.getInstance().timeInMillis.toString() + "", photoToBeTaggedURI.toString(), photoTag!!.tags)
            photoArrayList.add(photo!!)
        }
        PhotoTagApplication.instance!!.savePhotos(photoArrayList)
    }

    private fun exists(photoArrayList: ArrayList<Photo>?, photo: Photo?): Int {
        photo?: kotlin.run {
            return -1
        }
        photoArrayList?: kotlin.run {
            return -1
        }
        if (photoArrayList.isEmpty()) {
            return -1
        }
        photoArrayList.forEachIndexed { position, item ->
            if (item.id == photo.id) {
                return position
            }
        }
        return -1
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
        photo = null
        photoToBeTaggedURI = null
        tagsShowHideHelper.clear()
        loadImage()
        photoTag!!.removeTags()
        tapPhotoToTagUser!!.text = getString(R.string.tap_here_to_choose_a_photo)
    }
}