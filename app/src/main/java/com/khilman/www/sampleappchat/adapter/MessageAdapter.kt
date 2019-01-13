package com.khilman.www.sampleappchat.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.khilman.www.sampleappchat.R
import com.khilman.www.sampleappchat.model.Chat

class MessageAdapter(val context: Context,
                     val mChat: List<Chat>,
                     val imageUrl: String): RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    val MSG_TYPE_LEFT = 0
    val MSG_TYPE_RIGHT = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == MSG_TYPE_RIGHT){
            val view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false)
            return ViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false)
            return ViewHolder(view)
        }
    }

    override fun getItemCount(): Int = mChat.size

    override fun onBindViewHolder(holder: MessageAdapter.ViewHolder, position: Int) {
        val chat = mChat[position]

        holder.show_message.text = chat.message
        if (imageUrl.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher)
        } else {
            Glide.with(context).load(imageUrl).into(holder.profile_image)
        }

        if (position == (mChat.size - 1)){
            if (chat.isseen!!){
                holder.txt_seen.text = "Seen"
            } else {
                holder.txt_seen.text = "Delivered"
            }
        } else {
            holder.txt_seen.visibility = View.GONE
        }

    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var show_message: TextView
        var profile_image: ImageView
        var txt_seen: TextView

        init {
            show_message = itemView.findViewById(R.id.show_message)
            profile_image = itemView.findViewById(R.id.profile_image)
            txt_seen = itemView.findViewById(R.id.txt_seen)
        }
    }

    private var fuser: FirebaseUser? = null
    override fun getItemViewType(position: Int): Int {
        fuser = FirebaseAuth.getInstance().currentUser
        if (mChat[position].sender.equals(fuser?.uid)){
            return MSG_TYPE_RIGHT
        } else {
            return MSG_TYPE_LEFT
        }

    }
}