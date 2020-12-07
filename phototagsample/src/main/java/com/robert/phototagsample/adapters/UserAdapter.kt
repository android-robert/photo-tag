package com.robert.phototagsample.adapters

import android.content.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.robert.phototagsample.R
import com.robert.phototagsample.interfaces.AppConstants
import com.robert.phototagsample.interfaces.UserClickListener
import com.robert.phototagsample.models.User

class UserAdapter(private val userList: List<User?>?, private val context: Context?, private val userClickListener: UserClickListener) : RecyclerView.Adapter<UserAdapter.ViewHolder>(), AppConstants {
    private val requestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.ic_default_avatar)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.ic_default_avatar)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList!![position]
        holder.user = user
        holder.txtUserName.text = user!!.userName
        holder.txtFullName.text = user.fullName
        Glide
                .with(context!!)
                .load(user.url)
                .apply(requestOptions.transforms(CircleCrop()))
                .into(holder.imgProfile)
    }

    override fun getItemCount(): Int {
        return userList?.size ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val imgProfile: ImageView
        val txtUserName: TextView
        val txtFullName: TextView
        var user: User? = null
        override fun onClick(v: View) {
            if (v.id == R.id.root_user && user!!.userName!! != AppConstants.NO_USER_FOUND) {
                userClickListener.onUserClick(user, adapterPosition)
            }
        }

        init {
            val relativeLayout = itemView.findViewById<RelativeLayout>(R.id.root_user)
            imgProfile = itemView.findViewById(R.id.img_profile)
            txtUserName = itemView.findViewById(R.id.txt_user_name)
            txtFullName = itemView.findViewById(R.id.txt_full_name)
            relativeLayout.setOnClickListener(this)
        }
    }
}