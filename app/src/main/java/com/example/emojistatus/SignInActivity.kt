package com.example.emojistatus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.emojistatus.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private companion object{
        private const val TAG = "SignInActivity"
        private const val RC_GOOGLE_SIG_IN = 49
    }
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

// Initialize Firebase Auth
        auth = Firebase.auth

        val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val client: GoogleSignInClient = GoogleSignIn.getClient(this,gso)
        binding.btnSignIn.setOnClickListener {
            val signInIntent = client.signInIntent
            startActivityForResult(signInIntent, RC_GOOGLE_SIG_IN)
        }
    }
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        //Navigate to main activity
        if (user == null){
            Log.w(TAG,"User is null, not going to navigate")
            return
        }
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Request returned from launching intent from GoogleSignIn api
        if(requestCode == RC_GOOGLE_SIG_IN){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
             try {
                 //Google Sign In Was successfully
                 val account :GoogleSignInAccount = task .getResult(ApiException::class.java)!!
                 Log.d(TAG,"firebaseAuthwithGoogle:" +account.id)
                 firebaseAuthWithGoogle(account.idToken)
             }catch (e:ApiException){
                 Log.w(TAG,"Google signIn Failed")
             }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken,null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this){task ->
                if (task.isSuccessful){
                    //Sign in Success, update UI with the signed-in user's information
                    Log.d(TAG,"signInwithcredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                }else{
                    //If sign infails display a message to the user
                    Log.w(TAG,"signInwithCredential:failure",task.exception)
                    //..
                    Toast.makeText(this,"Authentication Failed.",Toast.LENGTH_SHORT).show()
                    updateUI(null)

                }
            }
    }
}