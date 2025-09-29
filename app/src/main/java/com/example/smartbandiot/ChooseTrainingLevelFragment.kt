package com.example.smartbandiot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.smartbandiot.databinding.FragmentChooseTrainingLevelBinding
import com.example.smartbandiot.databinding.FragmentSigninBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChooseTrainingLevelFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChooseTrainingLevelFragment : Fragment() {

    private var _binding: FragmentChooseTrainingLevelBinding? = null
    private val binding get() = _binding!!
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChooseTrainingLevelBinding.inflate(inflater, container, false)


//       ini untuk semua material card view

        val cardo_sengko = listOf(
            binding.beginer,
            binding.irregularTraining,
            binding.medium,
            binding.advance
        )

        cardo_sengko.forEach { kartu ->
            kartu.setOnClickListener {
                cardo_sengko.forEach { it.strokeColor = ContextCompat.getColor(requireContext(),R.color.stroke_abu_abu); it.isChecked = false }
                kartu.isChecked = true
                kartu.strokeColor = ContextCompat.getColor(requireContext(),R.color.black)
            }

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.continu.setOnClickListener {
            findNavController().navigate(R.id.trainingtocreating)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChooseTrainingLevelFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChooseTrainingLevelFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}