package com.intoverflown.samplechatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.intoverflown.samplechatapp.databinding.ActivityRegisterBinding
import java.util.*

class RegisterActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var reference: DatabaseReference

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_register)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view  = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            val txt_username = binding.username.text.toString()
            val txt_email = binding.email.text.toString()
            val txt_password = binding.password.text.toString()

            if (txt_username.isEmpty() || txt_email.isEmpty() || txt_password.isEmpty()){
                Toast.makeText(this, "All field are required", Toast.LENGTH_LONG).show()
            } else if (txt_password.length < 6){
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_LONG).show()
            } else {
                register(txt_username, txt_email, txt_password)
            }
        }
    }

    private fun register(txt_username: String, txt_email: String, txt_password: String) {
        auth.createUserWithEmailAndPassword(txt_email, txt_password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (BuildConfig.DEBUG && firebaseUser == null) {
                        error("Assertion failed")
                    }
                    val userId = firebaseUser?.uid.toString()

                    reference = FirebaseDatabase.getInstance().getReference("Users")
                        .child(userId)

                    val hasMap: HashMap<String, String> = hashMapOf()
                    hasMap["id"] = userId
                    hasMap["username"] = txt_username
                    hasMap["imageURL"] = "default"
                    hasMap["status"] = "offline"
                    hasMap["search"] = txt_username.toLowerCase(Locale.ROOT)
                    //input database
                    reference.setValue(hasMap).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    it.exception.toString()
                    val failedMsg = it.exception?.message

                    Toast.makeText(this, failedMsg, Toast.LENGTH_LONG).show()
                }
            }
    }
}
