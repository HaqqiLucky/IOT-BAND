package com.example.smartbandiot

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.smartbandiot.databinding.FragmentChooseGenderBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChooseGenderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChooseGenderFragment : Fragment() {

    private var _binding: FragmentChooseGenderBinding? = null

    private val binding get() = _binding!!
    private var selectedGender : String? = null

    // TODO: Rename and change types of parameters
    private var param1: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

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
            val viewModel = ViewModelProvider(requireActivity()).get(PreferencesSharedViewModel::class.java)
            viewModel.gender = selectedGender.toString()
            Log.d("Gender", "Pov Viewmodel: ${viewModel.gender}, pov choosegenderfragment.kt: $selectedGender")
            findNavController().navigate(R.id.gendertomain)
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChooseGenderFragment.
         */
        // TODO: Rename and change types and number of parameters
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