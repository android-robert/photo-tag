package com.robert.phototag

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.view.View.OnTouchListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.robert.phototag.TagOnTouchListener.OnDragActionListener
import java.util.*

class PhotoTag: RelativeLayout {
    var rootWidth = 0
    var rootHeight = 0
    private var mContext: Context? = null
    var tagTextColor = 0
    var tagBackgroundColor = 0
    var carrotTopBackGroundColor = 0
    var tagTextDrawable: Drawable? = null
    var carrotTopDrawable: Drawable? = null
    private var tagsAreAdded = false
    private var canWeAddTags = false
    private var mIsRootIsInTouch = true
    private var mShowAnimation: Animation? = null
    private var mHideAnimation: Animation? = null
    private var mGestureDetector: GestureDetector? = null
    private var mPhotoEvent: PhotoEvent? = null
    private var mRoot: ViewGroup? = null
    private var mLikeImage: ImageView? = null
    var tagImageView: SquareImageView? = null
    private val mTagList = ArrayList<View>()
    private val mSetRootHeightWidth: Runnable = Runnable {
        rootWidth = mRoot!!.width
        rootHeight = mRoot!!.height
    }

    @SuppressLint("ClickableViewAccessibility")
    private val mOnTouchListener = OnTouchListener { v, event -> mGestureDetector!!.onTouchEvent(event) }
    private val mGestureListener: GestureListener = object : GestureListener {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (canWeAddTags) {
                if (mIsRootIsInTouch) {
                    val x = e.x.toInt()
                    val y = e.y.toInt()
                    when (e.action) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                        }
                    }
                    if (mPhotoEvent != null) {
                        mPhotoEvent!!.singleTapConfirmedAndRootIsInTouch(x, y)
                    }
                } else {
                    hideRemoveButtonFromAllTagView()
                    mIsRootIsInTouch = true
                }
            }
            return false
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return true
        }

        override fun onShowPress(e: MotionEvent) {}
        override fun onDoubleTap(e: MotionEvent): Boolean {
            return if (mPhotoEvent != null) {
                mPhotoEvent!!.onDoubleTap(e)
            } else {
                true
            }
        }

        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            return if (mPhotoEvent != null) {
                mPhotoEvent!!.onDoubleTapEvent(e)
            } else {
                true
            }
        }

        override fun onLongPress(e: MotionEvent) {
            if (mPhotoEvent != null) {
                mPhotoEvent!!.onLongPress(e)
            }
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            return true
        }
    }

    interface Constants {
        companion object {
            const val DEFAULT_COLOR = Color.CYAN
            const val TAG_TEXT_COLOR = Color.WHITE
        }
    }

    interface PhotoEvent {
        fun singleTapConfirmedAndRootIsInTouch(x: Int, y: Int)
        fun onDoubleTap(e: MotionEvent?): Boolean
        fun onDoubleTapEvent(e: MotionEvent?): Boolean
        fun onLongPress(e: MotionEvent?)
    }

    constructor(context: Context) : super(context) {
        initViewWithId(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        if (!isInEditMode) {
            initView(attrs, context)
        } else {
            initView(attrs, context)
        }
    }

    private fun initViewWithId(context: Context, obtainStyledAttributes: TypedArray?) {
        mContext = context
        val inflater = mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.tag_root, this)
        mRoot = findViewById(R.id.tag_root)
        tagImageView = findViewById(R.id.tag_image_view)
        mLikeImage = ImageView(context)
        val likeColor: Int
        val likeSrc: Int
        val likeSize: Int
        if (obtainStyledAttributes != null) {
            likeColor = obtainStyledAttributes.getColor(R.styleable.PhotoTag_likeColor, ContextCompat.getColor(context, R.color.colorAccent))
            likeSrc = obtainStyledAttributes.getResourceId(R.styleable.PhotoTag_likeSrc, R.drawable.ic_like)
            likeSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.PhotoTag_likeSize, resources.getDimensionPixelSize(R.dimen.dp150))
        } else {
            likeColor = ContextCompat.getColor(context, R.color.colorAccent)
            likeSrc = R.drawable.ic_like
            likeSize = resources.getDimensionPixelSize(R.dimen.dp150)
        }
        val heartParams = LayoutParams(likeSize, likeSize)
        heartParams.addRule(CENTER_IN_PARENT, TRUE)
        mLikeImage!!.layoutParams = heartParams
        mLikeImage!!.visibility = GONE
        mLikeImage!!.setImageResource(likeSrc)
        mLikeImage!!.setColorFilter(likeColor)
        setRootLayoutParams(mContext)
        mRoot!!.post(mSetRootHeightWidth)
        mRoot!!.setOnTouchListener(mOnTouchListener)
        mGestureDetector = GestureDetector(mRoot!!.context, mGestureListener)
    }

    @SuppressLint("CustomViewStyleable")
    private fun initView(attrs: AttributeSet, context: Context) {
        mContext = context
        val obtainStyledAttributes = context.obtainStyledAttributes(attrs, R.styleable.PhotoTag, 0, 0)
        tagTextDrawable = obtainStyledAttributes.getDrawable(R.styleable.PhotoTag_tagTextBackground)
        carrotTopDrawable = obtainStyledAttributes.getDrawable(R.styleable.PhotoTag_carrotTopBackground)
        canWeAddTags = obtainStyledAttributes.getBoolean(R.styleable.PhotoTag_canWeAddTags, false)
        tagTextColor = obtainStyledAttributes.getColor(R.styleable.PhotoTag_instaTextColor, Constants.TAG_TEXT_COLOR)
        val overrideDefaultColor = obtainStyledAttributes.getColor(R.styleable.PhotoTag_overrideDefaultColor, Constants.DEFAULT_COLOR)
        if (overrideDefaultColor == Constants.DEFAULT_COLOR) {
            carrotTopBackGroundColor = obtainStyledAttributes.getColor(R.styleable.PhotoTag_carrotTopColor, Constants.DEFAULT_COLOR)
            tagBackgroundColor = obtainStyledAttributes.getColor(R.styleable.PhotoTag_instaBackgroundColor, Constants.DEFAULT_COLOR)
        } else {
            tagBackgroundColor = overrideDefaultColor
            carrotTopBackGroundColor = overrideDefaultColor
        }
        mHideAnimation = AnimationUtils.loadAnimation(context, obtainStyledAttributes.getResourceId(R.styleable.PhotoTag_hideAnimation, R.anim.zoom_out))
        mShowAnimation = AnimationUtils.loadAnimation(context, obtainStyledAttributes.getResourceId(R.styleable.PhotoTag_showAnimation, R.anim.zoom_in))
        initViewWithId(context, obtainStyledAttributes)
        obtainStyledAttributes.recycle()
    }

    private fun setColorForTag(tagView: View) {
        (tagView.findViewById<View>(R.id.tag_text_view) as TextView).setTextColor(tagTextColor)
        if (carrotTopDrawable == null) {
            setColor(tagView.findViewById<View>(R.id.carrot_top).background, carrotTopBackGroundColor)
        }
        if (tagTextDrawable == null) {
            setColor(tagView.findViewById<View>(R.id.tag_text_container).background, tagBackgroundColor)
        }
    }

    private fun setColor(drawable: Drawable?, color: Int) {
        when (drawable) {
            is ShapeDrawable -> {
                drawable.paint.color = color
            }
            is GradientDrawable -> {
                drawable.setColor(color)
            }
            is ColorDrawable -> {
                drawable.color = color
            }
            is LayerDrawable -> {
                val rotateDrawable = drawable.findDrawableByLayerId(R.id.carrot_shape_top) as RotateDrawable
                setColor(rotateDrawable.drawable, color)
            }
            is RotateDrawable -> {
                setColor(drawable.drawable, color)
            }
        }
    }

    private fun hideRemoveButtonFromAllTagView() {
        if (mTagList.isNotEmpty()) {
            for (view in mTagList) {
                view.findViewById<View>(R.id.remove_tag_image_view).visibility = GONE
            }
        }
    }

    private fun isTagged(tagName: String): Boolean {
        var tagFound = true
        if (mTagList.isNotEmpty()) {
            for (tagView in mTagList) {
                if ((tagView.findViewById<View>(R.id.tag_text_view) as TextView).text.toString() == tagName) {
                    tagFound = false
                    break
                }
            }
        } else {
            tagFound = true
        }
        return tagFound
    }

    private fun setRootLayoutParams(context: Context?) {
        val displayMetrics = context!!.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        val dpHeight = displayMetrics.heightPixels / displayMetrics.density
        val rootLayoutHeightWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpWidth, resources.displayMetrics).toInt()
        val params = mRoot!!.layoutParams
        params.height = rootLayoutHeightWidth
        params.width = rootLayoutHeightWidth
        mRoot!!.layoutParams = params
    }

    fun addTag(x: Int, y: Int, tagText: String) {
        if (isTagged(tagText)) {
            val layoutInflater = LayoutInflater.from(mContext)
            val tagView = layoutInflater.inflate(R.layout.view_for_tag, mRoot, false)
            val tagTextView = tagView.findViewById<TextView>(R.id.tag_text_view)
            val carrotTopContainer = tagView.findViewById<LinearLayout>(R.id.carrot_top)
            val removeTagImageView = tagView.findViewById<ImageView>(R.id.remove_tag_image_view)
            val textContainer = tagView.findViewById<LinearLayout>(R.id.tag_text_container)
            if (tagTextDrawable != null) {
                ViewCompat.setBackground(textContainer, tagTextDrawable)
            }
            if (carrotTopDrawable != null) {
                ViewCompat.setBackground(carrotTopContainer, carrotTopDrawable)
            }
            tagTextView.text = tagText
            setColorForTag(tagView)
            val layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(x - tagTextView.length() * 8,
                    y - tagTextView.length() * 2,
                    0,
                    0)
            tagView.layoutParams = layoutParams
            mTagList.add(tagView)
            mRoot!!.addView(tagView)
            removeTagImageView.setOnClickListener {
                mTagList.remove(tagView)
                mRoot!!.removeView(tagView)
            }
            val tagOnTouchListener = TagOnTouchListener(tagView)
            tagOnTouchListener.setOnDragActionListener(object : OnDragActionListener {
                override fun onDragStart(view: View?) {
                    if (canWeAddTags) {
                        mIsRootIsInTouch = false
                        removeTagImageView.visibility = VISIBLE
                    }
                }

                override fun onDragEnd(view: View?) {}
            })
            if (canWeAddTags) tagView.setOnTouchListener(tagOnTouchListener)
        } else {
            Toast.makeText(mContext, "This user is already tagged", Toast.LENGTH_SHORT).show()
        }
    }

    fun addTagViewFromTags(tags: ArrayList<Tag>) {
        if (!tagsAreAdded) {
            for (tag in tags) {
                addTag(tag)
            }
            tagsAreAdded = true
        }
    }

    private fun addTag(tag: Tag) {
        val layoutInflater = LayoutInflater.from(mContext)
        val x = getXCoOrdForTag(tag.x_co_ord)
        val y = getYCoOrdForTag(tag.y_co_ord)
        val tagView = layoutInflater.inflate(R.layout.view_for_tag, mRoot, false)
        val tagTextView = tagView.findViewById<TextView>(R.id.tag_text_view)
        val carrotTopContainer = tagView.findViewById<LinearLayout>(R.id.carrot_top)
        val textContainer = tagView.findViewById<LinearLayout>(R.id.tag_text_container)
        if (tagTextDrawable != null) {
            ViewCompat.setBackground(textContainer, tagTextDrawable)
        }
        if (carrotTopDrawable != null) {
            ViewCompat.setBackground(carrotTopContainer, carrotTopDrawable)
        }
        tagTextView.text = tag.unique_tag_id
        setColorForTag(tagView)
        tagView.x = x
        tagView.y = y
        mTagList.add(tagView)
        mRoot!!.addView(tagView)
    }

    val tags: ArrayList<Tag>
        get() {
            val tags = ArrayList<Tag>()
            if (mTagList.isNotEmpty()) {
                for (i in mTagList.indices) {
                    val view = mTagList[i]
                    var x = view.x
                    x = x / rootWidth * 100
                    var y = view.y
                    y = y / rootHeight * 100
                    tags.add(Tag((view.findViewById<View>(R.id.tag_text_view) as TextView).text.toString(), x, y))
                }
            }
            return tags
        }

    fun showTags() {
        if (mTagList.isNotEmpty()) {
            for (tagView in mTagList) {
                tagView.visibility = VISIBLE
                tagView.startAnimation(mShowAnimation)
            }
        }
    }

    fun hideTags() {
        if (mTagList.isNotEmpty()) {
            for (tagView in mTagList) {
                tagView.startAnimation(mHideAnimation)
                tagView.visibility = GONE
            }
        }
    }

    fun removeTags() {
        if (mTagList.isNotEmpty()) {
            for (tagView in mTagList) {
                mRoot!!.removeView(tagView)
            }
            mTagList.clear()
        }
    }

    fun animateLike() {
        try {
            mRoot!!.addView(mLikeImage)
        } catch (ignored: Exception) {
            // Illegal Exception is being thrown here while adding mLikeImage
        }
        mLikeImage!!.visibility = VISIBLE
        mLikeImage!!.scaleY = 0f
        mLikeImage!!.scaleX = 0f
        val animatorSet = AnimatorSet()
        val likeScaleUpYAnimator = ObjectAnimator
                .ofFloat(mLikeImage, SCALE_Y, 0f, 1f)
        likeScaleUpYAnimator.duration = 400
        likeScaleUpYAnimator.interpolator = OvershootInterpolator()
        val likeScaleUpXAnimator = ObjectAnimator
                .ofFloat(mLikeImage, SCALE_X, 0f, 1f)
        likeScaleUpXAnimator.duration = 400
        likeScaleUpXAnimator.interpolator = OvershootInterpolator()
        val likeScaleDownYAnimator = ObjectAnimator
                .ofFloat(mLikeImage, SCALE_Y, 1f, 0f)
        likeScaleDownYAnimator.duration = 100
        val likeScaleDownXAnimator = ObjectAnimator
                .ofFloat(mLikeImage, SCALE_X, 1f, 0f)
        likeScaleDownXAnimator.duration = 100
        animatorSet.playTogether(likeScaleUpXAnimator,
                likeScaleUpYAnimator)
        animatorSet.play(likeScaleDownXAnimator).with(likeScaleDownYAnimator).after(800)
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mLikeImage!!.visibility = GONE
                mRoot!!.removeView(mLikeImage)
            }
        })
        animatorSet.start()
    }

    private fun getXCoOrdForTag(x: Float): Float {
        return rootWidth * x / 100
    }

    private fun getYCoOrdForTag(y: Float): Float {
        return rootHeight * y / 100
    }

    fun setTaggedPhotoEvent(photoEvent: PhotoEvent?) {
        if (mPhotoEvent == null) {
            mPhotoEvent = photoEvent
        }
    }

    fun setTagShowAnimation(mShowAnimation: Animation?) {
        this.mShowAnimation = mShowAnimation
    }

    fun setTagHideAnimation(mHideAnimation: Animation?) {
        this.mHideAnimation = mHideAnimation
    }

    fun canWeAddTags(): Boolean {
        return canWeAddTags
    }

    fun setCanWeAddTags(mCanWeAddTags: Boolean) {
        canWeAddTags = mCanWeAddTags
    }

    fun setLikeResource(@DrawableRes resource: Int) {
        mLikeImage!!.setImageResource(resource)
    }

    fun setLikeDrawable(drawable: Drawable?) {
        mLikeImage!!.setImageDrawable(drawable)
    }

    fun setLikeColor(@ColorInt color: Int) {
        mLikeImage!!.setColorFilter(color)
    }
}