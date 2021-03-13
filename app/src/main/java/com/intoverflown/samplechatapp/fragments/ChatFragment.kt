package com.intoverflown.samplechatapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.intoverflown.samplechatapp.adapter.UserAdapter
import com.intoverflown.samplechatapp.databinding.FragmentChatBinding
import com.intoverflown.samplechatapp.model.Chatlist
import com.intoverflown.samplechatapp.model.User
import com.intoverflown.samplechatapp.notifications.Token

class ChatFragment : Fragment() {
    lateinit var userAdapter: UserAdapter
    lateinit var mUsers: ArrayList<User>

    var firebaseUser: FirebaseUser? = null
    lateinit var reference: DatabaseReference

    lateinit var usersList: ArrayList<Chatlist>

    private lateinit var binding: FragmentChatBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_chat, container, false)
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // setup the view
        this.binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        // get users
        firebaseUser = FirebaseAuth.getInstance().currentUser
        usersList = arrayListOf()

        // get chats
        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser?.uid!!)
        reference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usersList.clear()
                // put chats data to variable
                for (snapshot in dataSnapshot.children){
                    val chatlist = snapshot.getValue(Chatlist::class.java)
                    usersList.add(chatlist!!)
                }

                chatList()
            }

        })

        updateToken(FirebaseInstanceId.getInstance().token.toString())
    }

    private fun updateToken(token: String) {
        val reference = FirebaseDatabase.getInstance().getReference("Tokens")
        val token1 = Token(token)
        reference.child(firebaseUser?.uid!!).setValue(token1)
    }

    // get data chats
    private fun chatList(){
        mUsers = arrayListOf()
        reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUsers.clear()
                // ambil data semua chat
                for (snapshot in dataSnapshot.children){
                    val user = snapshot.getValue(User::class.java)

                    // mencari data chat yang ada uid current user
                    for (chatlist in usersList){
                        if (user?.id.equals(chatlist.id)){
                            mUsers.add(user!!)
                        }
                    }

                }
                userAdapter = UserAdapter(context!!, mUsers, true)
                binding.recyclerView.adapter = userAdapter
            }

        })
    }
}
