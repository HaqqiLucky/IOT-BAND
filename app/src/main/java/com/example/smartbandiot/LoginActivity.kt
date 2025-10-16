package com.example.smartbandiot

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class LoginActivity : AppCompatActivity() { // Menggunakan LoginActivity sesuai nama class

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    // Kode yang akan dieksekusi setelah berhasil atau gagal mengambil akun Google
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Mendapatkan tugas (task) dari intent hasil
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            // Berhasil mendapatkan akun Google
            val account = task.getResult(ApiException::class.java)
            // Lanjutkan dengan autentikasi ke Firebase
            firebaseAuthWithGoogle(account.idToken)
        } catch (e: ApiException) {
            // Gagal mendapatkan akun Google
            Toast.makeText(this, "Login Google Gagal: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Menggunakan R.layout.activity_signin_signout sesuai layout Anda
        setContentView(R.layout.activity_signin_signout)

        // Inisialisasi Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Konfigurasi Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // Web Client ID ini didapat otomatis (atau dari strings.xml jika manual)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Bangun GoogleSignInClient
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Atur OnClickListener untuk ImageButton Google
        // ID di XML Anda adalah imageButtonGoogle
        findViewById<ImageButton>(R.id.imageButtonGoogle).setOnClickListener {
            signInWithGoogle()
        }
    }

    // Fungsi untuk memulai proses Google Sign-In
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    // Fungsi untuk mengautentikasi pengguna di Firebase menggunakan kredensial Google
    private fun firebaseAuthWithGoogle(idToken: String?) {
        // Buat kredensial Firebase dari ID Token Google
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        // Autentikasi ke Firebase
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in sukses
                    val user = auth.currentUser
                    Toast.makeText(this, "Selamat datang ${user?.displayName}!", Toast.LENGTH_SHORT).show()

                    // ✨ NAVIGASI KE ACTIVITY UTAMA
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Tutup Activity login
                } else {
                    // Sign in gagal
                    Toast.makeText(this, "Autentikasi Firebase Gagal: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    // ✅ Implementasi Persistence: Periksa apakah pengguna sudah login saat Activity dimulai
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Pengguna sudah login, langsung navigasi ke Activity utama
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Tutup Activity login agar pengguna tidak bisa kembali
        }
    }
}