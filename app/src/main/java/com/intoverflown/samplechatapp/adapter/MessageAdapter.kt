package com.intoverflown.samplechatapp.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.intoverflown.samplechatapp.R
import com.intoverflown.samplechatapp.model.Chat

class MessageAdapter(val context: Context,
                     val mChat: List<Chat>,
                     val imageUrl: String): RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    val MSG_TYPE_LEFT = 0
    val MSG_TYPE_RIGHT = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == MSG_TYPE_RIGHT){
            val view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false)
            ViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false)
            ViewHolder(view)
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
                holder.txt_seen.text = context.getString(R.string.seen)
            } else {
                holder.txt_seen.text = context.getString(R.string.delivered)
            }
        } else {
            holder.txt_seen.visibility = View.GONE
        }

    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var show_message: TextView = itemView.findViewById(R.id.show_message)
        var profile_image: ImageView = itemView.findViewById(R.id.profile_image)
        var txt_seen: TextView = itemView.findViewById(R.id.txt_seen)

    }

    private var fuser: FirebaseUser? = null
    override fun getItemViewType(position: Int): Int {
        fuser = FirebaseAuth.getInstance().currentUser
        return if (mChat[position].sender.equals(fuser?.uid)){
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }

    }
}