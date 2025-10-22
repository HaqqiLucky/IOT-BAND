package com.example.smartbandiot

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.smartbandiot.databinding.FragmentChooseWeightBinding
import android.widget.Toast // Tambahkan Toast untuk feedback

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ChooseWeightFragment : Fragment() {

    private var _binding: FragmentChooseWeightBinding? = null
    private val binding get() = _binding!!
    private var getWeight: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChooseWeightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.continu.setOnClickListener {
            val viewModel = ViewModelProvider(requireActivity())[PreferencesSharedViewModel::class.java]

            // --- DIPERBARUI: Ambil dan simpan Weight ke ViewModel ---
            val weightInput = binding.editTextWeight.text.toString()
            val weightDouble = weightInput.toDoubleOrNull()

            if (weightDouble != null && weightDouble > 0) {
                // Berhasil: Simpan ke ViewModel
                viewModel.weight = weightDouble
                Log.d("ChooseWeight", "Weight saved to ViewModel: ${viewModel.weight}")
                findNavController().navigate(R.id.weightToAge)
            } else {
                // Gagal: Input tidak valid
                Toast.makeText(requireContext(), "Masukkan berat badan yang valid.", Toast.LENGTH_SHORT).show()
                Log.e("ChooseWeight", "Invalid weight input: $weightInput")
            }
        }

        binding.continu.isEnabled = false
        binding.editTextWeight.addTextChangedListener { text->
            binding.continu.isEnabled = !text.isNullOrBlank()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChooseWeightFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}