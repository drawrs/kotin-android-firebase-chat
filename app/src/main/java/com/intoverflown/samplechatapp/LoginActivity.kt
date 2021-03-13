package com.intoverflown.samplechatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.intoverflown.samplechatapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    var firebaseUser: FirebaseUser? = null

    private lateinit var binding: ActivityLoginBinding

    override fun onStart() {
        super.onStart()
        // get current user
        firebaseUser = FirebaseAuth.getInstance().currentUser
        //check if user is null
        if (firebaseUser != null) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view  = binding.root
        setContentView(view)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setTitle("Login")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()

//        forgot_password.setOnClickListener {
//            startActivity(Intent(this, ))
//        }

        binding.btnLogin.setOnClickListener {
            val txt_email = binding.email.text.toString()
            val txt_password = binding.password.text.toString()

            if (txt_email.isEmpty() || txt_password.isEmpty()){
                Toast.makeText(this, "All fileds are required", Toast.LENGTH_LONG).show()
            } else {
                auth.signInWithEmailAndPassword(txt_email, txt_password)
                    .addOnCompleteListener {
                        val task = it
                        if (task.isSuccessful){
                            val intent = Intent(this, MainActivity::class.java)
                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Authentication failed!", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }
}
