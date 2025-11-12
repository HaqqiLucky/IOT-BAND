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

class RecapFragment : Fragment() {

    private var _binding: FragmentRecapBinding? = null
    private val binding get() = _binding!!
    private val realtimeDatabase = Firebase.database
    private val userRPE = realtimeDatabase.getReference("history")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                            val target = distance + (distance * 0.015)
                            GlobalData.distanceTargetNextSession = target

                            Firebase.database.getReference("users").child(uid).child("today_challenge")
                                .setValue(
                                    mapOf(
                                        "rpe" to "Easy",
                                        "targetDistance" to target
                                    )
                                )
                            gotoFinish()
                        }

                        binding.RPENormal.setOnClickListener {
                            val target = distance
                            GlobalData.distanceTargetNextSession = target
                            Firebase.database.getReference("users").child(uid).child("today_challenge")
                                .setValue(
                                    mapOf(
                                        "rpe" to "Normal",
                                        "targetDistance" to target
                                    )
                                )
                            gotoFinish()
                        }

                        binding.RPEHard.setOnClickListener {
                            val target = distance - (distance * 0.005)
                            GlobalData.distanceTargetNextSession = target
                            Firebase.database.getReference("users").child(uid).child("today_challenge")
                                .setValue(
                                    mapOf(
                                        "rpe" to "Tired",
                                        "targetDistance" to target
                                    )
                                )
                            gotoFinish()
                        }

                    }
                } else {
                    Log.d("FirebaseLatest", "Data kosong.")
                }
            }
            .addOnFailureListener {
                Log.e("FirebaseLatest", "Error: ${it.message}")
            }
    }

    private fun gotoFinish(){
        val intent = Intent(requireContext(), RpeFinishingRequestActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}
