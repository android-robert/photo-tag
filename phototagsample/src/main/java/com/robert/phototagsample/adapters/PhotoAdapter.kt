package com.robert.phototagsample.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.robert.phototag.PhotoTag
import com.robert.phototag.PhotoTag.PhotoEvent
import com.robert.phototagsample.R
import com.robert.phototagsample.interfaces.PhotoClickListener
import com.robert.phototagsample.models.Photo
import java.util.*

class PhotoAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private val context: Context?
    private val photos: ArrayList<Photo>
    private val tagsShowHideHelper: HashSet<String?>
    private val photoClickListener: PhotoClickListener
    private var tagShowAnimation: Animation? = null
    private var tagHideAnimation: Animation? = null
    private var carrotTopColor = 0
    private var tagBackgroundColor = 0
    private var tagTextColor = 0
    private var likeColor = 0
    private val requestOptions = RequestOptions()
            .placeholder(0)
            .fallback(0)
            .centerCrop()
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.ALL)

    constructor(photos: ArrayList<Photo>, context: Context?, photoClickListener: PhotoClickListener) {
        this.photos = photos
        this.context = context
        this.photoClickListener = photoClickListener
        tagsShowHideHelper = HashSet()
    }

    constructor(photos: ArrayList<Photo>, context: Context?, photoClickListener: PhotoClickListener,
                showAnim: Int, hideAnim: Int,
                carrotTopColor: Int, tagBackgroundColor: Int, tagTextColor: Int, likeColor: Int) {
        this.photos = photos
        this.context = context
        this.photoClickListener = photoClickListener
        tagsShowHideHelper = HashSet()
        tagShowAnimation = AnimationUtils.loadAnimation(context, showAnim)
        tagHideAnimation = AnimationUtils.loadAnimation(context, hideAnim)
        this.carrotTopColor = carrotTopColor
        this.tagBackgroundColor = tagBackgroundColor
        this.tagTextColor = tagTextColor
        this.likeColor = likeColor
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val inflater = LayoutInflater.from(viewGroup.context)
        val defaultView = inflater.inflate(R.layout.item_photo, viewGroup, false)
        viewHolder = TaggedPhotoViewHolder(defaultView)
        return viewHolder
    }

    override fun getItemViewType(position: Int): Int {
        return -1
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val defaultViewHolder = viewHolder as TaggedPhotoViewHolder
        defaultViewHolder.photoTag.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        defaultViewHolder.photoTag.rootWidth = defaultViewHolder.photoTag.measuredWidth
        defaultViewHolder.photoTag.rootHeight = defaultViewHolder.photoTag.measuredHeight
        configureTaggedPhotoViewHolder(defaultViewHolder, position)
    }

    private fun configureTaggedPhotoViewHolder(taggedPhotoViewHolder: TaggedPhotoViewHolder,
                                               position: Int) {
        val photo = photos[position]
        Glide
                .with(context!!)
                .load(Uri.parse(photo.imageUri))
                .apply(requestOptions)
                .into(taggedPhotoViewHolder.photoTag.tagImageView!!)
        if (tagShowAnimation != null) {
            taggedPhotoViewHolder.photoTag.setTagShowAnimation(tagShowAnimation)
        }
        if (tagHideAnimation != null) {
            taggedPhotoViewHolder.photoTag.setTagHideAnimation(tagHideAnimation)
        }
        if (carrotTopColor != 0) {
            taggedPhotoViewHolder.photoTag.carrotTopBackGroundColor = carrotTopColor
        }
        if (tagBackgroundColor != 0) {
            taggedPhotoViewHolder.photoTag.tagBackgroundColor = tagBackgroundColor
        }
        if (tagTextColor != 0) {
            taggedPhotoViewHolder.photoTag.tagTextColor = tagTextColor
        }
        if (likeColor != 0) {
            taggedPhotoViewHolder.photoTag.setLikeColor(likeColor)
        }
        taggedPhotoViewHolder.photoTag.addTagViewFromTags(photo.tags!!)
        if (tagsShowHideHelper.contains(photos[taggedPhotoViewHolder.adapterPosition].id)) {
            taggedPhotoViewHolder.photoTag.showTags()
        } else {
            taggedPhotoViewHolder.photoTag.hideTags()
        }
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    private inner class TaggedPhotoViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val photoTag: PhotoTag = view.findViewById(R.id.tag_photo)
        val tagHeart: ImageView = view.findViewById(R.id.tag_heart)
        val tagIndicator: ImageView = view.findViewById(R.id.tag_indicator)

        private val photoEvent: PhotoEvent = object : PhotoEvent {
            override fun singleTapConfirmedAndRootIsInTouch(x: Int, y: Int) {}
            override fun onDoubleTap(e: MotionEvent?): Boolean {
                photoTag.animateLike()
                return true
            }

            override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
                photoTag.animateLike()
                return true
            }

            override fun onLongPress(e: MotionEvent?) {
                photoTag.animateLike()
            }
        }

        override fun onClick(view: View) {
            val photo = photos[adapterPosition]
            Log.e("PhotoAdapter","view.id=${view.id},R.id.tag_photo=${R.id.tag_photo}")
            when (view.id) {
                R.id.tag_photo -> {
                    Log.e("PhotoAdapter","onPhotoClick.....")
                    photoClickListener.onPhotoClick(photo, adapterPosition)
                }
                R.id.tag_heart -> photoTag.animateLike()
                R.id.tag_indicator -> {
                    if (!tagsShowHideHelper.contains(photo.id)) {
                        photoTag.showTags()
                        tagsShowHideHelper.add(photo.id)
                    } else {
                        photoTag.hideTags()
                        tagsShowHideHelper.remove(photo.id)
                    }
                }
            }
        }

        init {
            tagHeart.setOnClickListener(this)
            tagIndicator.setOnClickListener(this)
            photoTag.setOnClickListener(this)
            photoTag.setTaggedPhotoEvent(photoEvent)
        }
    }
}