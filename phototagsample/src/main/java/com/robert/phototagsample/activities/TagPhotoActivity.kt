package com.robert.phototagsample.activities

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Path
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.robert.phototag.PhotoTag
import com.robert.phototag.PhotoTag.PhotoEvent
import com.robert.phototagsample.R
import com.robert.phototagsample.adapters.UserAdapter
import com.robert.phototagsample.interfaces.AppConstants
import com.robert.phototagsample.interfaces.UserClickListener
import com.robert.phototagsample.models.Photo
import com.robert.phototagsample.models.User
import com.robert.phototagsample.utilities.KeyBoardUtil
import com.robert.phototagsample.utilities.RawJsonToStringUtil
import com.robert.phototagsample.utilities.UsersData
import java.util.*


class TagPhotoActivity: AppCompatActivity(), UserClickListener, View.OnClickListener, AppConstants {
    private val TAG = "TagPhotoActivity"
    private var photoTag: PhotoTag? = null
    private val tagsShowHideHelper = HashSet<String?>()
    private var recyclerViewUsers: RecyclerView? = null
    private var headerSearchUsers: LinearLayout? = null
    private var tapPhotoToTagUser: TextView? = null
    private var headerUsers: LinearLayout? = null
    private var tagIndicator: AppCompatImageView? = null
    private var addTagInX = 0
    private var addTagInY = 0
    private var searchForUser: EditText? = null
    private var userAdapter: UserAdapter? = null
    private val users = ArrayList<User>()
    private var photo: Photo? = null
    private val requestOptions = RequestOptions()
            .placeholder(0)
            .fallback(0)
            .centerCrop()
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.ALL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_tag_photo)

        photoTag = findViewById(R.id.photo_tag)
        photoTag!!.setTaggedPhotoEvent(photoEvent)
        val cancelTextView = findViewById<TextView>(R.id.cancel)

        tagIndicator = findViewById(R.id.tag_indicator)
        headerUsers = findViewById(R.id.header_tag_photo)
        tapPhotoToTagUser = findViewById(R.id.tap_photo_to_tag_someone)
        recyclerViewUsers = findViewById(R.id.rv_some_one_to_be_tagged)
        headerSearchUsers = findViewById(R.id.header_search_someone)
        searchForUser = findViewById(R.id.search_for_a_person)
        searchForUser!!.addTextChangedListener(textWatcher)

        tagIndicator!!.setOnClickListener(this)
        cancelTextView.setOnClickListener(this)
        tapPhotoToTagUser!!.setOnClickListener(this)

        UsersData.users.addAll(UsersData.getUsersFromJson(RawJsonToStringUtil.rawJsonToString(resources, R.raw.users)))

        userAdapter = UserAdapter(users, this, this@TagPhotoActivity)
        recyclerViewUsers!!.adapter = userAdapter
        recyclerViewUsers!!.layoutManager = LinearLayoutManager(this)
        intent?.let {
            if (it.hasExtra(MEDIA_ITEM_KEY)) {
                photo = it.getParcelableExtra(MEDIA_ITEM_KEY)
                photo?.let { media ->
                    media.imageUri?.let { it1 -> loadImage(it1) }
                    media.tags?.let { tags ->
                        if (tags.isNotEmpty()) {
                            photoTag!!.addTagViewFromTags(tags)
                            /*photoTag!!.addTag(185, 156, "Alex")
                            photoTag!!.addTag(618, 190, "Beeth")
                            photoTag!!.addTag(455, 540, "Backy")
                            photoTag!!.addTag(183, 902, "Franklin")
                            photoTag!!.addTag(914, 663, "Ian")
                            photoTag!!.addTag(670, 950, "Oscar")*/
                            tagsShowHideHelper.add(photo!!.id)
                            tagIndicator!!.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
        users.addAll(UsersData.users)
    }

    private fun loadImage(path: String) {
        Glide.with(this)
             .load(path)
             .apply(requestOptions)
             .into(photoTag!!.tagImageView!!)
    }

    override fun onUserClick(user: User?, position: Int) {
        runOnUiThread {
            KeyBoardUtil.hideKeyboard(this)
            Log.w(TAG, "===>Tag info(x,y,user)=photoTag!!.addTag(${addTagInX},${addTagInY},\"${user!!.userName!!}\")")
            photoTag!!.addTag(addTagInX, addTagInY, user.userName!!)
            recyclerViewUsers!!.visibility = View.GONE
            tapPhotoToTagUser!!.visibility = View.VISIBLE
            headerSearchUsers!!.visibility = View.GONE
            headerUsers!!.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.cancel -> {
                KeyBoardUtil.hideKeyboard(this)
                recyclerViewUsers!!.scrollToPosition(0)
                recyclerViewUsers!!.visibility = View.GONE
                headerSearchUsers!!.visibility = View.GONE
                headerUsers!!.visibility = View.VISIBLE
            }
            R.id.tag_indicator -> {

                val path = Path().apply {
                    val location = IntArray(2)
                    photoTag!!.getLocationOnScreen(location)
                    val rectf = Rect()
                    photoTag!!.getLocalVisibleRect(rectf)

                    arcTo(rectf.left.toFloat(), rectf.top.toFloat(), rectf.right.toFloat(), rectf.bottom.toFloat()/2, 270f, -180f, true)
                }
                val animator = ObjectAnimator.ofFloat(tagIndicator, View.X, View.Y, path).apply {
                    duration = 2000
                    start()
                }

                //tagIndicator!!.startAnimation(animator)

                /*TransitionManager.beginDelayedTransition(photoTag!!, ChangeBounds())

                val params = tagIndicator!!.layoutParams as RelativeLayout.LayoutParams
                //params.gravity = Gravity.RIGHT
                tagIndicator!!.layoutParams = params*/

                if (!tagsShowHideHelper.contains(photo!!.id)) {
                    Log.e(TAG, "--->showTags()")
                    photoTag!!.showTags()
                    tagsShowHideHelper.add(photo!!.id)
                } else {
                    Log.e(TAG, "--->hideTags()")
                    photoTag!!.hideTags()
                    tagsShowHideHelper.remove(photo!!.id)
                }
            }
        }
    }

    private val photoEvent: PhotoEvent = object : PhotoEvent {
        override fun singleTapConfirmedAndRootIsInTouch(x: Int, y: Int) {
            runOnUiThread {
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

    private fun reset() {
        photoTag!!.removeTags()
    }

    companion object {
        private const val MEDIA_ITEM_KEY = "media_item"
        private const val MEDIA_ITEM_POS_KEY = "media_item.position"

        fun launch(context: Context, photo: Photo?, position: Int): Intent {
            val intent = Intent(context, TagPhotoActivity::class.java)
            intent.putExtra(MEDIA_ITEM_POS_KEY, position)
            intent.putExtra(MEDIA_ITEM_KEY, photo)
            return intent
        }
    }

}