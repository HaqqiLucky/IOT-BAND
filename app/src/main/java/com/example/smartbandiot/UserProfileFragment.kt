package com.example.smartbandiot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class UserProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // hubungkan ke fragment_user_profile.xml
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }
}
