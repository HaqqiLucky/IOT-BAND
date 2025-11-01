package com.example.smartbandiot

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.smartbandiot.databinding.FragmentChooseGenderBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ChooseGenderFragment : Fragment() {

    private var _binding: FragmentChooseGenderBinding? = null
    private val binding get() = _binding!!
    private var selectedGender : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChooseGenderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val genders = listOf(binding.man, binding.woman)
        binding.continu.isEnabled = false

        genders.forEach { gender ->
            gender.setOnClickListener {
                genders.forEach {
                    it.isChecked = false
                    it.isSelected = false
                }
                gender.isChecked = true
                gender.isSelected = true
                binding.continu.isEnabled = true
                selectedGender = when (gender.id) {
                    binding.man.id -> "Male"
                    binding.woman.id -> "Female"
                    else -> ""
                }
            }
        }

        binding.continu.setOnClickListener {
            val viewModel = ViewModelProvider(requireActivity())[PreferencesSharedViewModel::class.java]
            viewModel.gender = selectedGender.toString()
            val sharedPref = requireContext().getSharedPreferences("user_profile", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                // 1. Simpan Gender
                putString("gender", viewModel.gender)

                // 2. Simpan Weight (Ambil dari ViewModel, jika null/belum ada, gunakan "0.0")
                val weight = viewModel.weight?.toString() ?: "0.0"
                putString("weight", weight)
                Log.d("SaveData", "Final Saving Weight: $weight")

                // 3. Simpan Height (Ambil dari ViewModel, jika null/belum ada, gunakan "0.0")
                val height = viewModel.height?.toString() ?: "0.0"
                putString("height", height)
                Log.d("SaveData", "Final Saving Height: $height")

                apply()
            }
            // --- AKHIR LOGIKA PENYIMPANAN ---

            findNavController().navigate(R.id.gendertomain)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChooseGenderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}