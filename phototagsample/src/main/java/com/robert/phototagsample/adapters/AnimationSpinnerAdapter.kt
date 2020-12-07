package com.robert.phototagsample.adapters

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.robert.phototagsample.models.Asset

class AnimationSpinnerAdapter(private val mContext: Context, textViewResourceId: Int,
                              private val assets: Array<Asset>) : ArrayAdapter<Asset>(mContext, textViewResourceId, assets) {
    override fun getCount(): Int {
        return assets.size
    }

    override fun getItem(position: Int): Asset {
        return assets[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getView(position, convertView, parent) as TextView
        label.setTextColor(Color.BLACK)
        label.text = assets[position].name
        return label
    }

    override fun getDropDownView(position: Int, convertView: View, parent: ViewGroup): View {
        val label = super.getDropDownView(position, convertView, parent) as TextView
        label.setTextColor(Color.BLACK)
        label.text = assets[position].name
        return label
    }
}