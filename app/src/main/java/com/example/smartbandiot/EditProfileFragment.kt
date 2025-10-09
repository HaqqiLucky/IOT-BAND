package com.example.smartbandiot

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import de.hdodenhof.circleimageview.CircleImageView

class EditProfileFragment : Fragment() {

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

    // Image picker launcher
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

        // Sembunyikan bottom navbar
        hideBottomNavBar()

        // Inisialisasi views
        initViews(view)

        // Setup Gender Spinner
        setupGenderSpinner()

        // Setup click listeners
        setupClickListeners()

        // Load existing data (jika ada)
        loadUserData()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Tampilkan kembali bottom navbar saat fragment dihancurkan
        showBottomNavBar()
    }

    private fun hideBottomNavBar() {
        // Sembunyikan container navbar (MaterialCardView)
        activity?.findViewById<View>(R.id.bottom_navbar_main_activity)?.visibility = View.GONE
    }

    private fun showBottomNavBar() {
        // Tampilkan container navbar (MaterialCardView)
        activity?.findViewById<View>(R.id.bottom_navbar_main_activity)?.visibility = View.VISIBLE
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
        // Back button - kembali ke SettingsFragment
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Change photo button
        btnChangePhoto.setOnClickListener {
            openImagePicker()
        }

        // Save button
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
        // TODO: Load data dari SharedPreferences atau Database
        // Contoh data dummy:
        etFullName.setText("CROG Robotic")
        etPhone.setText("9876543210")
        etEmail.setText("loremipsum@gmail.com")
        etWeight.setText("55")
        etHeight.setText("165")
        spinnerGender.setSelection(0) // Male
        etAge.setText("20")
    }

    private fun saveProfile() {
        // Validasi input
        val fullName = etFullName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val weight = etWeight.text.toString().trim()
        val height = etHeight.text.toString().trim()
        val gender = spinnerGender.selectedItem.toString()
        val age = etAge.text.toString().trim()

        // Validasi sederhana
        if (fullName.isEmpty()) {
            etFullName.error = "Full name is required"
            etFullName.requestFocus()
            return
        }

        if (phone.isEmpty()) {
            etPhone.error = "Phone number is required"
            etPhone.requestFocus()
            return
        }

        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            etEmail.requestFocus()
            return
        }

        if (weight.isEmpty()) {
            etWeight.error = "Weight is required"
            etWeight.requestFocus()
            return
        }

        if (height.isEmpty()) {
            etHeight.error = "Height is required"
            etHeight.requestFocus()
            return
        }

        if (age.isEmpty()) {
            etAge.error = "Age is required"
            etAge.requestFocus()
            return
        }

        // TODO: Simpan data ke SharedPreferences atau Database
        // Contoh:
        // val sharedPref = requireContext().getSharedPreferences("user_profile", Context.MODE_PRIVATE)
        // with(sharedPref.edit()) {
        //     putString("full_name", fullName)
        //     putString("phone", phone)
        //     putString("email", email)
        //     putString("weight", weight)
        //     putString("height", height)
        //     putString("gender", gender)
        //     putString("age", age)
        //     apply()
        // }

        Toast.makeText(context, "Profile saved successfully!", Toast.LENGTH_SHORT).show()

        // Kembali ke Settings Fragment
        parentFragmentManager.popBackStack()
    }
}