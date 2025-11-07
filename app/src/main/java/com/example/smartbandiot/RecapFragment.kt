package com.example.smartbandiot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smartbandiot.databinding.FragmentRecapBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecapFragment : Fragment() {

    private var _binding: FragmentRecapBinding? = null
    private val binding get() = _binding!!
    private val realtimeDatabase = Firebase.database
    private val userRPE = realtimeDatabase.getReference("history")

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
        _binding = FragmentRecapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.RPEEasy.setOnClickListener {
//
//        }

        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: return

        userRPE.limitToLast(1).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        val data = child.value as Map<*, *>
                        val distance = (data["distance_km"] as? Double) ?: 0.0
                        Log.e("RecapFragment","history skrng $distance")
                        binding.RPEEasy.setOnClickListener {
                            GlobalData.distanceTargetNextSession = distance + (distance * 0.015)
                            Log.i("RecapFragment","easy next sesion adalah ${GlobalData.distanceTargetNextSession}")
                            parentFragmentManager.beginTransaction()
                                val intent = Intent(requireContext(), RpeFinishingRequestActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                        }
                        binding.RPENormal.setOnClickListener {
                            GlobalData.distanceTargetNextSession = distance
                            Log.i("RecapFragment","normal next sesion adalah ${GlobalData.distanceTargetNextSession}")
                            parentFragmentManager.beginTransaction()
                                val intent = Intent(requireContext(), RpeFinishingRequestActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                        }
                        binding.RPEHard.setOnClickListener {
                            GlobalData.distanceTargetNextSession = distance - (distance * 0.005)
                            Log.i("RecapFragment","hard next sesion adalah ${GlobalData.distanceTargetNextSession}")
                            parentFragmentManager.beginTransaction()
                                val intent = Intent(requireContext(), RpeFinishingRequestActivity::class.java)
                                startActivity(intent)
                            requireActivity().finish()
                        }
                        Log.d("FirebaseLatest", "Dist: $distance")
                    }
                } else {
                    Log.d("FirebaseLatest", "Data kosong.")
                }
            }
            .addOnFailureListener {
                Log.e("FirebaseLatest", "Error: ${it.message}")
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecapFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}