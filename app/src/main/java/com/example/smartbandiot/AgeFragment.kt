package com.example.smartbandiot

import android.content.Context // <-- TAMBAHAN
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
import java.time.Year // <-- TAMBAHAN (Memerlukan minimum API 26)

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AgeFragment : Fragment() {
    private var _binding: FragmentAgeBinding? = null
    private val binding get() = _binding!!
    private var getAge: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            val monthNumber = selectedMonthIndex.toString().padStart(2, '0')
            val yearInput = binding.year.text.toString()
            val yearMonthCode = "$yearInput$monthNumber"

            val viewModel = ViewModelProvider(requireActivity())[PreferencesSharedViewModel::class.java]
            viewModel.birthYYYYmm = yearMonthCode
            Log.d("AgeFragment", "Pov Viewmodel: ${viewModel.birthYYYYmm}")

            // --- LOGIKA MENGHITUNG DAN MENYIMPAN USIA PERMANEN ---
            val currentYear = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Year.now().value
            } else {
                // Fallback untuk API di bawah 26 (Android 8.0)
                java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            }

            val birthYear = yearInput.toIntOrNull() ?: currentYear
            val age = (currentYear - birthYear).coerceAtLeast(0) // Pastikan usia tidak negatif

            val sharedPref = requireContext().getSharedPreferences("user_profile", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                // Menyimpan Usia untuk ditampilkan
                putString("age", age.toString())
                apply()
            }
            // --- AKHIR LOGIKA SIMPAN USIA ---

            findNavController().navigate(R.id.agetocreating)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
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