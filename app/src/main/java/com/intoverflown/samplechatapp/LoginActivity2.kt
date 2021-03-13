package com.intoverflown.samplechatapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.intoverflown.samplechatapp.databinding.ActivityLogin2Binding

class LoginActivity2 : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {


    private var mGoogleApiClient: GoogleApiClient? = null
    //private var mGoogleSignInClient: GoogleSignInApi? = null
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private val RC_SIGN_IN: Int = 199

    private lateinit var binding: ActivityLogin2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login2)
        binding = ActivityLogin2Binding.inflate(layoutInflater)
        val view  = binding.root
        setContentView(view)

        init()
        checkAuthState()
    }

    fun init(){
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        //2
        mAuth = FirebaseAuth.getInstance()
        binding.signInButton.setOnClickListener {
            signIn()
        }
    }
    fun checkAuthState(){
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // User is signed in
                Log.d("a", "onAuthStateChanged:signed_in:" + user.uid)
                Toast.makeText(
                    this, user.uid + " Sudah login.",
                    Toast.LENGTH_SHORT
                ).show()
                updateUI(user)
            } else {
                // User is signed out
                Log.d("a", "onAuthStateChanged:signed_out")
                Toast.makeText(
                    this, "Anda Belum login.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            // ...
        }
    }

    private fun signIn() {
        val mGoogleSignInClient = Auth.GoogleSignInApi
        val signInIntent = mGoogleSignInClient?.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("LoginActivity", "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LoginActivity", "signInWithCredential:success")
                    val user = mAuth?.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("LoginActivity", "signInWithCredential:failure", task.exception)
                    //Snackbar.make(main_layout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_LONG).show()
                    updateUI(null)
                }

                // ...
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null){
            Toast.makeText(this, "Login berhasil", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            Toast.makeText(this, "Login gagak", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("LoginActivity", "Google sign in failed", e)
                // ...
            }
        }
    }

    override fun onConnectionFailed(result: ConnectionResult) {

    }

    override fun onStart() {
        super.onStart()
        mAuth?.addAuthStateListener(mAuthListener!!)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth?.removeAuthStateListener(mAuthListener!!)
        }
    }
}
