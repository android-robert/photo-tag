package com.robert.phototagsample.fragments.dashboard

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.robert.phototagsample.R
import com.robert.phototagsample.adapters.UserAdapter
import com.robert.phototagsample.interfaces.UserClickListener
import com.robert.phototagsample.models.User
import com.robert.phototagsample.utilities.UsersData
import java.util.*

class SearchFragment : Fragment(), UserClickListener {
    private var recyclerViewUsers: RecyclerView? = null
    private var searchForUser: EditText? = null
    private var userAdapter: UserAdapter? = null
    private val users = ArrayList<User?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_search, container, false)
        recyclerViewUsers = rootView.findViewById(R.id.rv_users)
        searchForUser = rootView.findViewById(R.id.search_for_a_person)
        searchForUser!!.addTextChangedListener(textWatcher)
        users.addAll(UsersData.users)
        userAdapter = UserAdapter(users, activity, this)
        recyclerViewUsers!!.adapter = userAdapter
        recyclerViewUsers!!.layoutManager = LinearLayoutManager(activity)
        return rootView
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

    override fun onUserClick(user: User?, position: Int) {
        activity!!.runOnUiThread { Toast.makeText(activity, user!!.fullName, Toast.LENGTH_SHORT).show() }
    }
}