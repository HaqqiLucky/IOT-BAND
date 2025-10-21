package com.example.smartbandiot

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.smartbandiot.databinding.FragmentAgeBinding
import com.example.smartbandiot.databinding.FragmentChooseHeightBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AgeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AgeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentAgeBinding? = null

    private val binding get() = _binding!!
    private var getAge: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAgeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.continu.isEnabled = false
        binding.year.addTextChangedListener { text->
            binding.continu.isEnabled = !text.isNullOrBlank()
        }

        val month = listOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun","Jul", "Aug", "Sep", "Oct", "Nov","Des"
        )
        val masukinMonth = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,month)
        masukinMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.month.adapter = masukinMonth

        binding.continu.setOnClickListener {
            val selectedMonthIndex = binding.month.selectedItemPosition + 1
            Log.d("MonthCheck", "Index: $selectedMonthIndex, Month: ${binding.month.selectedItem}")
            val monthNumber = selectedMonthIndex.toString().padStart(2, '0')
            val yearInput = binding.year.text.toString() // "2025"
            val yearMonthCode = "$yearInput$monthNumber" // "202504" harusnya gini



            val viewModel = ViewModelProvider(requireActivity()).get(PreferencesSharedViewModel::class.java)
            viewModel.birthYYYYmm = yearMonthCode
            Log.d("AgeFragment", "Pov Viewmodel: ${viewModel.birthYYYYmm}")
            findNavController().navigate(R.id.agetocreating)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AgeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AgeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}