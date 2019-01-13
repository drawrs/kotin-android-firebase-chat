package com.khilman.www.sampleappchat.fragments


import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask

import com.khilman.www.sampleappchat.R
import com.khilman.www.sampleappchat.model.User
import kotlinx.android.synthetic.main.fragment_profile.view.*

/**
 * A simple [Fragment] subclass.
 *
 */
class ProfileFragment : Fragment() {

    lateinit var refrence: DatabaseReference
    var fuser: FirebaseUser? = null

    lateinit var storageReference: StorageReference
    val IMAGE_REQUEST = 1
    lateinit var imageUri: Uri
    private var uploadTask: StorageTask<*>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storageReference = FirebaseStorage.getInstance().getReference("uploads")

        fuser = FirebaseAuth.getInstance().currentUser

        refrence = FirebaseDatabase.getInstance().getReference("Users").child(fuser?.uid!!)
        refrence.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var user = dataSnapshot.getValue(User::class.java)

                activity?.runOnUiThread {
                    view.username.text = user?.username
                    if (user?.imageURL.equals("default")){
                        view.profile_image.setImageResource(R.mipmap.ic_launcher)
                    } else {
                        Glide.with(context!!).load(user?.imageURL).into(view.profile_image)
                    }
                }


            }
        })



    }

}
