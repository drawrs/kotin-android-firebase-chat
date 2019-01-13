package com.khilman.www.sampleappchat

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    var firebaseUser: FirebaseUser? = null

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
        setContentView(R.layout.activity_login)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setTitle("Login")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()

//        forgot_password.setOnClickListener {
//            startActivity(Intent(this, ))
//        }
        btn_login.setOnClickListener {
            val txt_email = email.text.toString()
            val txt_password = password.text.toString()

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
        btn_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }
}
