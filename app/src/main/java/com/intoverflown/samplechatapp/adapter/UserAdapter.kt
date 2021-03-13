package com.intoverflown.samplechatapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.intoverflown.samplechatapp.MessageActivity
import com.intoverflown.samplechatapp.R
import com.intoverflown.samplechatapp.databinding.UserItemBinding
import com.intoverflown.samplechatapp.model.Chat
import com.intoverflown.samplechatapp.model.User

class UserAdapter(val context: Context, val mUsers: MutableList<User>, val isChat: Boolean): RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    var theLastMessage: String? = null
    lateinit var binding: UserItemBinding

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): UserAdapter.ViewHolder {
//        val view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false)
//        return ViewHolder(view)
        binding = UserItemBinding.inflate(LayoutInflater.from(context))
        val view = binding.root
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = mUsers.size

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val user = mUsers[position]

        binding.username.text = user.username

        // foto profil
        if (user.imageURL.equals("default")){
            binding.profileImage.setImageResource(R.mipmap.ic_launcher)
        } else {
            Glide.with(context).load(user.imageURL).into(binding.profileImage)
        }

        // apakah tampilan chat / bukan
        if (isChat){
            lastMessage(user.id, binding.lastMsg)
        } else {
            binding.lastMsg.visibility = View.GONE
        }
        // apakah tampilan chat / bukan
        if (isChat){
            if (user.status.equals("online")){
                binding.imgOn.visibility = View.VISIBLE
                binding.imgOff.visibility = View.GONE
            } else {
                binding.imgOn.visibility = View.GONE
                binding.imgOff.visibility = View.VISIBLE
            }
        } else {
            binding.imgOn.visibility = View.GONE
            binding.imgOff.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, MessageActivity::class.java)
            intent.putExtra("USER_ID", user.id)
            context.startActivity(intent)
        }
    }

    private fun lastMessage(userid: String, last_msg: TextView?) {
        theLastMessage = "default"

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.getChildren()) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (firebaseUser != null && chat != null){
                        if (chat.receiver?.equals(firebaseUser.uid)!! && chat.sender.equals(userid)){
                            theLastMessage = chat.message
                        }
                    }
                }

                when (theLastMessage){
                    "default" -> last_msg?.text = "No Message"
                    else -> {
                        last_msg?.text = theLastMessage
                    }
                }

                theLastMessage = "default"
            }

        })
    }

}