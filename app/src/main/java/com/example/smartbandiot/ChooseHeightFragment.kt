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
import com.example.smartbandiot.databinding.FragmentChooseHeightBinding
import android.widget.Toast // Tambahkan Toast untuk feedback

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ChooseHeightFragment : Fragment() {

    private var _binding: FragmentChooseHeightBinding? = null
    private val binding get() = _binding!!
    private var getHeight: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChooseHeightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.continu.setOnClickListener {
            val viewModel = ViewModelProvider(requireActivity())[PreferencesSharedViewModel::class.java]

            // --- DIPERBARUI: Ambil dan simpan Height ke ViewModel ---
            val heightInput = binding.editTextHeight.text.toString()
            val heightDouble = heightInput.toDoubleOrNull()

            if (heightDouble != null && heightDouble > 0) {
                // Berhasil: Simpan ke ViewModel
                viewModel.height = heightDouble
                Log.d("ChooseHeight", "Height saved to ViewModel: ${viewModel.height}")
                findNavController().navigate(R.id.heighttoweight)
            } else {
                // Gagal: Input tidak valid
                Toast.makeText(requireContext(), "Masukkan tinggi badan yang valid.", Toast.LENGTH_SHORT).show()
                Log.e("ChooseHeight", "Invalid height input: $heightInput")
            }
        }

        binding.continu.isEnabled = false
        binding.editTextHeight.addTextChangedListener { text->
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
            ChooseHeightFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}