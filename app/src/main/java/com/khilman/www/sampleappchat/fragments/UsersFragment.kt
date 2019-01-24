package com.khilman.www.sampleappchat.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.khilman.www.sampleappchat.R
import com.khilman.www.sampleappchat.adapter.UserAdapter
import com.khilman.www.sampleappchat.model.User
import kotlinx.android.synthetic.main.fragment_users.view.*


class UsersFragment : Fragment() {
    private var userAdapter: UserAdapter? = null
    private var mUsers: MutableList<User>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    lateinit var searchUsers: EditText
    lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchUsers = view.search_users
        recyclerView = view.recycler_view

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        mUsers = arrayListOf()

        readUsers()

        searchUsers.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchUsers(s.toString().toLowerCase())
            }

        })
    }

    private fun searchUsers(search: String) {
        val fuser = FirebaseAuth.getInstance().currentUser
        val query = FirebaseDatabase.getInstance().getReference("Users")
            .orderByChild("search")
            .startAt(search)
            .endAt("$search\uf8ff")

        query.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUsers?.clear()

                for (snapshot: DataSnapshot in dataSnapshot.children){
                    val user = snapshot.getValue(User::class.java)!!

                    assert(user != null)
                    assert(fuser != null)

                    if (!user?.id.equals(fuser?.uid)){
                        mUsers?.add(user)
                    }

                    userAdapter = UserAdapter(context!!, mUsers!!, false)
                    recyclerView.adapter = userAdapter
                }
            }

        })
    }

    private fun readUsers() {
        val fuser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Users")

        //Log.d("G", FirebaseDatabase.getInstance().reference.toString())

        reference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (searchUsers.text.toString().equals("")){
                    mUsers?.clear()
                    for (snapshot: DataSnapshot in dataSnapshot.children){
                        val user = snapshot.getValue(User::class.java)

                        if (!user?.id.equals(fuser?.uid)){
                            mUsers?.add(user!!)
                        }

                        userAdapter = UserAdapter(context!!, mUsers!!, false)
                        recyclerView.adapter = userAdapter
                    }
                }

            }
        })
    }
}
