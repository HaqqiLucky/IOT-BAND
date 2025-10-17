package com.example.smartbandiot

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartbandiot.databinding.LandingPageBinding
import com.google.firebase.auth.FirebaseAuth

//const val FRAGMENT_TO_LOAD_KEY = "fragment_to_load"
class LandingPage : AppCompatActivity() {
    private lateinit var binding: LandingPageBinding

    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LandingPageBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        // deklarasi buat custom string
        val ngeBold = StyleSpan(Typeface.BOLD)
        val underlineSpan = UnderlineSpan()
        // popins
        val poppinsFont = ResourcesCompat.getFont(this, R.font.poppins_medium)
        val poppinsSpan = poppinsFont?.let { TypefaceSpan(it) }
        val opensansFont = ResourcesCompat.getFont(this, R.font.opensans_regular)
        val opensansSpan = opensansFont?.let { TypefaceSpan(it) }

        //sign in custom bold, popins, clickaable
        val alreadyHaveAcc = "Already have account? Sign in"
        val spannableString = SpannableString(alreadyHaveAcc)
        val signIn = "Sign in"
        val boldStartIndex = alreadyHaveAcc.indexOf(signIn)
        val boldEndIndex = boldStartIndex + signIn.length
        if (poppinsSpan != null) {
            spannableString.setSpan(poppinsSpan, boldStartIndex, boldEndIndex, 0)
        }


//        val clickableSpan = object : ClickableSpan() {
//            override fun onClick(widget: View) {
//                val intent = Intent(this@LandingPage, SigninSignout::class.java)
//                intent.putExtra(FRAGMENT_TO_LOAD_KEY, "SIGN_IN")
//                startActivity(intent)
//            }
//        }


        spannableString.setSpan(ngeBold,boldStartIndex, boldEndIndex, 0)
        spannableString.setSpan(underlineSpan, boldStartIndex, boldEndIndex, 0)
//        spannableString.setSpan(clickableSpan, boldStartIndex, boldEndIndex, 0)


        // already have acc cuma oppen sans aja, font size kalo bs
        val wordOpenSans = "Already have account?"
        val openSansStartIndex = wordOpenSans.indexOf("Already have account?")
        val openSansEndIndex = openSansStartIndex + wordOpenSans.length
        spannableString.setSpan(opensansSpan, openSansStartIndex, openSansEndIndex, 0)


        binding.getstartedorsignup.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}