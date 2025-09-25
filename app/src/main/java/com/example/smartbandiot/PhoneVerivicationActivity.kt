package com.example.smartbandiot

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.smartbandiot.databinding.ActivityPhoneVerivicationBinding

class PhoneVerivicationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhoneVerivicationBinding

    // Konstanta
    companion object {
        const val FRAGMENT_TO_LOAD_KEY = "fragment_to_load"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Perbaikan: Hanya panggil setContentView() satu kali
        binding = ActivityPhoneVerivicationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val poppinsFont = ResourcesCompat.getFont(this, R.font.poppins_medium)
        val poppinsSpan = poppinsFont?.let { TypefaceSpan(it) }
        val opensansFont = ResourcesCompat.getFont(this, R.font.opensans_regular)
        val opensansSpan = opensansFont?.let { TypefaceSpan(it) }


        val changeText = SpannableString(binding.change.text)
        changeText.setSpan(UnderlineSpan(), 0, changeText.length, 0)
        binding.change.text = changeText

        binding.change.setOnClickListener {
            val intent = Intent(this, PreferencesActivity::class.java)
            startActivity(intent)
            finish() // biar g bs balik ke verif
        }


        val resendFullText = "Don't receive your code? Resend"
        val spannableString = SpannableString(resendFullText)
        val resend = "Resend"
        val resendStartIndex = resendFullText.indexOf(resend)
        val resendEndIndex = resendStartIndex + resend.length

        // Perbaikan: Terapi semua Span secara berurutan pada spannableString
        spannableString.setSpan(UnderlineSpan(), resendStartIndex, resendEndIndex, 0)
        spannableString.setSpan(poppinsSpan, resendStartIndex, resendEndIndex, 0)


        val blueColor = ContextCompat.getColor(this, R.color.biru_resend)
        spannableString.setSpan(ForegroundColorSpan(blueColor), resendStartIndex, resendEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(widget.context, "Udah di resend tpi boong", Toast.LENGTH_SHORT).show()
            }
        }
        spannableString.setSpan(clickableSpan, resendStartIndex, resendEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)


        val dontReciv = "Don't receive your code?"
        val dontStartIndex = resendFullText.indexOf(dontReciv)
        val dontEndIndex = dontStartIndex + dontReciv.length
        spannableString.setSpan(opensansSpan, dontStartIndex, dontEndIndex, 0)

        // Perbaikan: Tetapkan semua atribut Span pada TextView
        binding.textViewDontReceiveCode.text = spannableString
        binding.textViewDontReceiveCode.movementMethod = LinkMovementMethod.getInstance()

    }
}