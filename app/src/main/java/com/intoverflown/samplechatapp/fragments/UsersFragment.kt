package com.intoverflown.samplechatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.intoverflown.samplechatapp.BuildConfig
import com.intoverflown.samplechatapp.R
import com.intoverflown.samplechatapp.adapter.UserAdapter
import com.intoverflown.samplechatapp.databinding.FragmentChatBinding
import com.intoverflown.samplechatapp.databinding.FragmentUsersBinding
import com.intoverflown.samplechatapp.model.User
import java.util.*


class UsersFragment : Fragment() {
    private var userAdapter: UserAdapter? = null
    private var mUsers: MutableList<User>? = null

    private lateinit var binding : FragmentUsersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_users, container, false)
        binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    lateinit var searchUsers: EditText
    lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchUsers = binding.searchUsers
        recyclerView = binding.recyclerView

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            LinearLayoutManager(context)

        mUsers = arrayListOf()

        readUsers()

        searchUsers.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchUsers(s.toString().toLowerCase(Locale.ROOT))
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

                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)!!

                    if (BuildConfig.DEBUG && user == null) {
                        error("Assertion failed")
                    }
                    if (BuildConfig.DEBUG && fuser == null) {
                        error("Assertion failed")
                    }

                    if (!user.id.equals(fuser?.uid)) {
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
