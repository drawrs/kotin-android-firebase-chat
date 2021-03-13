package com.intoverflown.samplechatapp.fragments


import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
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
import com.intoverflown.samplechatapp.model.User
import com.intoverflown.samplechatapp.R
import com.intoverflown.samplechatapp.databinding.FragmentChatBinding
import com.intoverflown.samplechatapp.databinding.FragmentProfileBinding

/**
 * A simple [Fragment] subclass.
 *
 */
class ProfileFragment : Fragment() {

    lateinit var refrence: DatabaseReference
    var fuser: FirebaseUser? = null

    private lateinit var storageReference: StorageReference
    val IMAGE_REQUEST = 1
    lateinit var imageUri: Uri
    private var uploadTask: StorageTask<*>? = null

    lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_profile, container, false)
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
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
                val user = dataSnapshot.getValue(User::class.java)

                activity?.runOnUiThread {
                    binding.username.text = user?.username
                    if (user?.imageURL.equals("default")){
                        binding.profileImage.setImageResource(R.mipmap.ic_launcher)
                    } else {
                        Glide.with(context!!).load(user?.imageURL).into(binding.profileImage)
                    }
                }


            }
        })
    }

}
