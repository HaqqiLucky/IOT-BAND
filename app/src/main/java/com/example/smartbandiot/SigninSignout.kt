package com.example.smartbandiot

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.set
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.smartbandiot.databinding.ActivitySigninSignoutBinding

class SigninSignout : AppCompatActivity() {
    private lateinit var binding: ActivitySigninSignoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninSignoutBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        if (savedInstanceState == null) {
            // Ambil "catatan" dari Intent
            val fragmentToLoad = intent.getStringExtra(FRAGMENT_TO_LOAD_KEY)
            val fragment: Fragment = when (fragmentToLoad) {
                "SIGN_IN" -> SigninFragment()
                "SIGN_UP" -> SignupFragment()
                else -> throw IllegalArgumentException("Fragment invalid/tidak ada")
            }

            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container_signin_signup, fragment)
                .commit()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun setupTextViewForNavigation(isSignInMode: Boolean) {
        val fullText: String
        val clickableText: String
        val aboutToChangeToOpenSans: String
        val destinationFragment: Fragment

        if (isSignInMode) {
            fullText = "Don't have an account? Sign up"
            clickableText = "Sign up"
            aboutToChangeToOpenSans = "Don't have an account?"
            destinationFragment = SignupFragment()
        } else {
            fullText = "Already have an account? Sign in"
            clickableText = "Sign in"
            aboutToChangeToOpenSans = "Already have an account?"
            destinationFragment = SigninFragment()
        }

        // Dapatkan referensi TextView dari layout Activity
        val textView = binding.textViewSignUpSignIn

        val poppinsFont = ResourcesCompat.getFont(this, R.font.poppins_medium)
        val poppinsSpan = poppinsFont?.let { TypefaceSpan(it) }
        val opensansFont = ResourcesCompat.getFont(this, R.font.opensans_regular)
        val opensansSpan = opensansFont?.let { TypefaceSpan(it) }

        val spannableString = SpannableString(fullText)
        val startIndex = fullText.indexOf(clickableText)

        if (startIndex != -1) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    // Lakukan transisi Fragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_signin_signup, destinationFragment)
                        .addToBackStack(null) // Penting untuk navigasi 'kembali'
                        .commit()
                }
            }
            spannableString.setSpan(clickableSpan, startIndex, startIndex + clickableText.length, 0)
            spannableString.setSpan(StyleSpan(Typeface.BOLD), startIndex, startIndex + clickableText.length, 0)
            spannableString.setSpan(ForegroundColorSpan(Color.BLACK), startIndex, startIndex + clickableText.length, 0)
            if (poppinsSpan != null) {
                spannableString.setSpan(poppinsSpan, startIndex, startIndex + clickableText.length, 0)
            }

            if (opensansSpan != null) {
                spannableString.setSpan(opensansSpan, 0, aboutToChangeToOpenSans.length, 0)
            }
        }

        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

}