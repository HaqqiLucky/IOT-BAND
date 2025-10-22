package com.example.smartbandiot

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class EditProfileFragment : Fragment() {

    private val currentUser = FirebaseAuth.getInstance().currentUser

    private lateinit var btnBack: ImageButton
    private lateinit var btnChangePhoto: ImageButton
    private lateinit var profileImage: CircleImageView
    private lateinit var etFullName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var etWeight: EditText
    private lateinit var etHeight: EditText
    private lateinit var spinnerGender: Spinner
    private lateinit var etAge: EditText
    private lateinit var btnSave: Button

    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                profileImage.setImageURI(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        hideBottomNavBar()
        initViews(view)
        setupGenderSpinner()
        setupClickListeners()
        loadUserData() // Panggil untuk memuat data awal

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showBottomNavBar()
    }

    private fun hideBottomNavBar() {
        activity?.findViewById<View>(R.id.navigation_main)?.visibility = View.GONE
    }

    private fun showBottomNavBar() {
        activity?.findViewById<View>(R.id.navigation_main)?.visibility = View.VISIBLE
    }

    private fun initViews(view: View) {
        btnBack = view.findViewById(R.id.btnBack)
        btnChangePhoto = view.findViewById(R.id.btnChangePhoto)
        profileImage = view.findViewById(R.id.profileImage)
        etFullName = view.findViewById(R.id.etFullName)
        etPhone = view.findViewById(R.id.etPhone)
        etEmail = view.findViewById(R.id.etEmail)
        etWeight = view.findViewById(R.id.etWeight)
        etHeight = view.findViewById(R.id.etHeight)
        spinnerGender = view.findViewById(R.id.spinnerGender)
        etAge = view.findViewById(R.id.etAge)
        btnSave = view.findViewById(R.id.btnSave)
    }

    private fun setupGenderSpinner() {
        val genderOptions = arrayOf("Male", "Female", "Other")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            genderOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = adapter
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnChangePhoto.setOnClickListener {
            openImagePicker()
        }

        btnSave.setOnClickListener {
            saveProfile()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun loadUserData() {
        val sharedPref = requireContext().getSharedPreferences("user_profile", Context.MODE_PRIVATE)

        // --- Muat Data Google Auth ---
        if (currentUser != null) {
            etFullName.setText(currentUser.displayName ?: sharedPref.getString("full_name", ""))
            etEmail.setText(currentUser.email ?: sharedPref.getString("email", ""))
            etEmail.isEnabled = false // Email dari Google tidak bisa diedit

            val photoUrl = currentUser.photoUrl
            if (photoUrl != null) {
                Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(profileImage)
            } else {
                profileImage.setImageResource(R.drawable.ic_launcher_foreground)
            }
        } else {
            // Muat data jika tidak ada user Google
            etFullName.setText(sharedPref.getString("full_name", ""))
            etEmail.setText(sharedPref.getString("email", ""))
            etEmail.isEnabled = true
        }

        // --- MUAT DATA Weight, Height, Age, Gender, Phone dari SharedPreferences ---
        // Nilai default "0" atau "0.0" memastikan field tidak kosong saat pertama dibuka.
        etPhone.setText(sharedPref.getString("phone", ""))
        etWeight.setText(sharedPref.getString("weight", "0.0")) // <-- Memuat Weight
        etHeight.setText(sharedPref.getString("height", "0.0")) // <-- Memuat Height
        etAge.setText(sharedPref.getString("age", "0"))         // <-- Memuat Age

        // Set Gender Spinner
        val savedGender = sharedPref.getString("gender", "Male")
        val genderOptions = arrayOf("Male", "Female", "Other")
        val genderIndex = genderOptions.indexOf(savedGender)
        if (genderIndex >= 0) {
            spinnerGender.setSelection(genderIndex)
        }
    }

    private fun saveProfile() {
        // Ambil input
        val fullName = etFullName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val weight = etWeight.text.toString().trim()
        val height = etHeight.text.toString().trim()
        val gender = spinnerGender.selectedItem.toString()
        val age = etAge.text.toString().trim()

        // Validasi sederhana
        if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty() || weight.isEmpty() || height.isEmpty() || age.isEmpty()) {
            Toast.makeText(context, "Semua kolom harus diisi.", Toast.LENGTH_SHORT).show()
            return
        }

        // --- SIMPAN DATA BARU KE SharedPreferences ---
        val sharedPref = requireContext().getSharedPreferences("user_profile", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("full_name", fullName)
            putString("phone", phone)

            // Simpan semua data yang diedit
            putString("weight", weight)
            putString("height", height)
            putString("gender", gender)
            putString("age", age)

            apply()
        }
        // --- AKHIR SIMPAN DATA ---

        Toast.makeText(context, "Profile berhasil disimpan!", Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
    }
}