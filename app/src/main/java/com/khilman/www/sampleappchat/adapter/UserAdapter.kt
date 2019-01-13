package com.khilman.www.sampleappchat.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.khilman.www.sampleappchat.MessageActivity
import com.khilman.www.sampleappchat.R
import com.khilman.www.sampleappchat.model.Chat
import com.khilman.www.sampleappchat.model.User
import kotlinx.android.synthetic.main.user_item.view.*

class UserAdapter(val context: Context, val mUsers: MutableList<User>, val isChat: Boolean): RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    var theLastMessage: String? = null

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): UserAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = mUsers.size

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val user = mUsers.get(position)
        holder.itemView.username.text = user.username

        // foto profil
        if (user.imageURL.equals("default")){
            holder.itemView.profile_image.setImageResource(R.mipmap.ic_launcher)
        } else {
            Glide.with(context).load(user.imageURL).into(holder.itemView.profile_image)
        }

        // apakah tampilan chat / bukan
        if (isChat){
            lastMessage(user.id, holder.itemView.last_msg)
        } else {
            holder.itemView.last_msg.visibility = View.GONE
        }
        // apakah tampilan chat / bukan
        if (isChat){
            if (user.status.equals("online")){
                holder.itemView.img_on.visibility = View.VISIBLE
                holder.itemView.img_off.visibility = View.GONE
            } else {
                holder.itemView.img_on.visibility = View.GONE
                holder.itemView.img_off.visibility = View.VISIBLE
            }
        } else {
            holder.itemView.img_on.visibility = View.GONE
            holder.itemView.img_off.visibility = View.GONE
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