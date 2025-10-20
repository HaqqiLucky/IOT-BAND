package com.example.smartbandiot

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.smartbandiot.databinding.ActivityLoginBinding
import com.example.smartbandiot.databinding.ActivityPreferencesBinding
import com.example.smartbandiot.databinding.FragmentChooseHeightBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // ambil dari google-services.json
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.login.setOnClickListener {
            signInWithGoogle()
        }
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        if (task.isSuccessful) {
            val account = task.result
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Welcome ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, PreferencesActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Firebase Sign-in Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Google Sign-in Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }
}
