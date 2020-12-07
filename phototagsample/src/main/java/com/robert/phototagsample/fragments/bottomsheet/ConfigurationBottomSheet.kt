package com.robert.phototagsample.fragments.bottomsheet

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.FrameLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.robert.phototagsample.PhotoTagApplication
import com.robert.phototagsample.R
import com.robert.phototagsample.adapters.AnimationSpinnerAdapter
import com.robert.phototagsample.interfaces.AppConstants
import com.robert.phototagsample.interfaces.AppConstants.Animations.Hide
import com.robert.phototagsample.interfaces.AppConstants.Animations.Show
import com.robert.phototagsample.models.Asset

class ConfigurationBottomSheet : BottomSheetDialogFragment(), AppConstants, View.OnClickListener {
    private var rootView: View? = null
    var spinnerTagShowAnimation: Spinner? = null
    var spinnerTagHideAnimation: Spinner? = null
    lateinit var tagShowAssets: Array<Asset>
    lateinit var tagHideAssets: Array<Asset>
    var tagShowAnimationSpinnerAdapter: AnimationSpinnerAdapter? = null
    var tagHideAnimationSpinnerAdapter: AnimationSpinnerAdapter? = null
    var carrotTopColorDialogPicker: AlertDialog? = null
    var tagBackgroundColorDialogPicker: AlertDialog? = null
    var tagTextColorDialogPicker: AlertDialog? = null
    var likeColorDialogPicker: AlertDialog? = null
    var textViewCarrotTopColor: TextView? = null
    var textViewTagBackgroundColor: TextView? = null
    var textViewTagTextColor: TextView? = null
    var textViewLikeColor: TextView? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheetDialog1 = dialog as BottomSheetDialog
            val bottomSheet = bottomSheetDialog1.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
            BottomSheetBehavior.from(bottomSheet).skipCollapsed = true
            BottomSheetBehavior.from(bottomSheet).isHideable = true
        }
        return bottomSheetDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.bottom_sheet_configuration, container)
        spinnerTagShowAnimation = rootView!!.findViewById(R.id.spinner_tag_show_animation)
        spinnerTagHideAnimation = rootView!!.findViewById(R.id.spinner_tag_hide_animation)
        textViewCarrotTopColor = rootView!!.findViewById(R.id.tv_carrot_top_color)
        textViewTagBackgroundColor = rootView!!.findViewById(R.id.tv_tag_background_color)
        textViewTagTextColor = rootView!!.findViewById(R.id.tv_tag_text_color)
        textViewLikeColor = rootView!!.findViewById(R.id.tv_like_color)
        textViewCarrotTopColor!!.setOnClickListener(this)
        textViewTagBackgroundColor!!.setOnClickListener(this)
        textViewTagTextColor!!.setOnClickListener(this)
        textViewLikeColor!!.setOnClickListener(this)
        rootView!!.findViewById<View>(R.id.btn_apply).setOnClickListener(this)
        textViewCarrotTopColor!!.setBackgroundColor(PhotoTagApplication.instance!!.carrotTopColor)
        textViewTagBackgroundColor!!.setBackgroundColor(PhotoTagApplication.instance!!.tagBackgroundColor)
        textViewTagTextColor!!.setBackgroundColor(PhotoTagApplication.instance!!.tagTextColor)
        textViewLikeColor!!.setBackgroundColor(PhotoTagApplication.instance!!.likeColor)
        initSpinners()
        initColorPickers()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initSpinners() {
        tagShowAssets = arrayOf(
                Asset(Show.BOUNCE_DOWN, R.anim.bounce_down),
                Asset(Show.FADE_IN, R.anim.fade_in),
                Asset(Show.SLIDE_DOWN, R.anim.slide_down),
                Asset(Show.ZOOM_IN, R.anim.zoom_in))
        tagHideAssets = arrayOf(
                Asset(Hide.BOUNCE_UP, R.anim.bounce_up),
                Asset(Hide.FADE_OUT, R.anim.fade_out),
                Asset(Hide.SLIDE_UP, R.anim.slide_up),
                Asset(Hide.ZOOM_OUT, R.anim.zoom_out))
        tagShowAnimationSpinnerAdapter = AnimationSpinnerAdapter(activity!!, android.R.layout.simple_spinner_dropdown_item, tagShowAssets)
        tagHideAnimationSpinnerAdapter = AnimationSpinnerAdapter(activity!!, android.R.layout.simple_spinner_dropdown_item, tagHideAssets)
        spinnerTagShowAnimation!!.adapter = tagShowAnimationSpinnerAdapter
        spinnerTagHideAnimation!!.adapter = tagHideAnimationSpinnerAdapter
        spinnerTagShowAnimation!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, position: Int, id: Long) {
                val asset = tagShowAnimationSpinnerAdapter!!.getItem(position)
                PhotoTagApplication.instance!!.tagShowAnimation = asset.id
            }

            override fun onNothingSelected(adapter: AdapterView<*>?) {}
        }
        spinnerTagHideAnimation!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, position: Int, id: Long) {
                val asset = tagHideAnimationSpinnerAdapter!!.getItem(position)
                PhotoTagApplication.instance!!.tagHideAnimation = asset.id
            }

            override fun onNothingSelected(adapter: AdapterView<*>?) {}
        }
    }

    private fun initColorPickers() {
        carrotTopColorDialogPicker = ColorPickerDialogBuilder
                .with(activity)
                .setTitle(getString(R.string.carrot_top_color))
                .initialColor(ContextCompat.getColor(activity!!, R.color.colorPrimaryDark))
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .setOnColorSelectedListener {
                    //                        Log.d("Selected Color", "onColorSelected: 0x" + Integer.toHexString(selectedColor));
                }
                .setPositiveButton(R.string.ok) { dialog, selectedColor, allColors ->
                    textViewCarrotTopColor!!.setBackgroundColor(selectedColor)
                    PhotoTagApplication.instance!!.carrotTopColor = selectedColor
                }
                .setNegativeButton(R.string.cancel) { dialog, which -> }
                .build()
        tagBackgroundColorDialogPicker = ColorPickerDialogBuilder
                .with(activity)
                .setTitle(getString(R.string.tag_background_color))
                .initialColor(ContextCompat.getColor(activity!!,R.color.colorPrimaryDark))
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .setOnColorSelectedListener {
                    //                        Log.d("Selected Color", "onColorSelected: 0x" + Integer.toHexString(selectedColor));
                }
                .setPositiveButton(R.string.ok) { dialog, selectedColor, allColors ->
                    textViewTagBackgroundColor!!.setBackgroundColor(selectedColor)
                    PhotoTagApplication.instance!!.tagBackgroundColor = selectedColor
                }
                .setNegativeButton(R.string.cancel) { dialog, which -> }
                .build()
        tagTextColorDialogPicker = ColorPickerDialogBuilder
                .with(activity)
                .setTitle(getString(R.string.tag_text_color))
                .initialColor(ContextCompat.getColor(activity!!,android.R.color.white))
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .setOnColorSelectedListener {
                    //Log.d("Selected Color", "onColorSelected: 0x" + Integer.toHexString(selectedColor));
                }
                .setPositiveButton(R.string.ok) { dialog, selectedColor, allColors ->
                    textViewTagTextColor!!.setBackgroundColor(selectedColor)
                    PhotoTagApplication.instance!!.tagTextColor = selectedColor
                }
                .setNegativeButton(R.string.cancel) { dialog, which -> }
                .build()
        likeColorDialogPicker = ColorPickerDialogBuilder
                .with(activity)
                .setTitle(getString(R.string.like_color))
                .initialColor(ContextCompat.getColor(activity!!,android.R.color.white))
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .setOnColorSelectedListener {
                    //Log.d("Selected Color", "onColorSelected: 0x" + Integer.toHexString(selectedColor));
                }
                .setPositiveButton(R.string.ok) { dialog, selectedColor, allColors ->
                    textViewLikeColor!!.setBackgroundColor(selectedColor)
                    PhotoTagApplication.instance!!.likeColor = selectedColor
                }
                .setNegativeButton(R.string.cancel) { dialog, which -> }
                .build()
    }

    @SuppressLint("NonConstantResourceId")
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_apply -> {
                dismiss()
                val configurationSaved = Intent(AppConstants.Events.NEW_CONFIGURATION_SAVED)
                LocalBroadcastManager.getInstance(activity!!).sendBroadcast(configurationSaved)
            }
            R.id.tv_carrot_top_color -> carrotTopColorDialogPicker!!.show()
            R.id.tv_tag_background_color -> tagBackgroundColorDialogPicker!!.show()
            R.id.tv_tag_text_color -> tagTextColorDialogPicker!!.show()
            R.id.tv_like_color -> likeColorDialogPicker!!.show()
        }
    }
}