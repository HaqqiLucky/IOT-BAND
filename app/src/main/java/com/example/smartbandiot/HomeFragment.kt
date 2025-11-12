package com.example.smartbandiot

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartbandiot.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.LocalTime

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val user = FirebaseAuth.getInstance().currentUser

    private lateinit var db: FirebaseDatabase
    private lateinit var todayRef: DatabaseReference
    private lateinit var historyRef: DatabaseReference
    private var liveHRListener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        if (user != null) binding.namaUser.text = user.displayName
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bonjourHuman()

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db = FirebaseDatabase.getInstance("https://smartbandforteens-default-rtdb.asia-southeast1.firebasedatabase.app/")
        todayRef = db.getReference("users").child(uid).child("today_challenge")
        historyRef = db.getReference("history").child(uid)

        setupLiveHeartRate(uid)
        loadChallengeAndHistory(uid)
    }

    /**
     * 🔴 LIVE UPDATE HEART RATE dari Firebase
     */
    private fun setupLiveHeartRate(uid: String) {
        liveHRListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var latestHR = 0.0
                for (child in snapshot.children) {
                    latestHR = (child.child("heart_rate").getValue(Double::class.java)) ?: 0.0
                }
                binding.txtLiveHeartRateHome.text = "${latestHR.toInt()} bpm"
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Error ambil heart rate live: ${error.message}")
            }
        }

        // dengarkan setiap perubahan heart_rate user ini (real-time)
        historyRef.orderByKey().limitToLast(1)
            .addValueEventListener(liveHRListener as ValueEventListener)
    }

    /**
     * 🔵 Ambil data challenge hari ini dan progress terakhir
     */
    private fun loadChallengeAndHistory(uid: String) {
        todayRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(todaySnap: DataSnapshot) {
                if (!isAdded || _binding == null) return

                val rpe = todaySnap.child("rpe").getValue(String::class.java) ?: "Normal"
                val targetDistance = todaySnap.child("targetDistance").getValue(Double::class.java) ?: 0.0
                val completedAt = todaySnap.child("completedAt").getValue(Long::class.java) ?: 0L

                historyRef.orderByKey().limitToLast(1)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(historySnap: DataSnapshot) {
                            var latestStep = 0.0
                            var latestHR = 0.0
                            var latestDist = 0.0

                            for (child in historySnap.children) {
                                latestStep = (child.child("steps").getValue(Double::class.java)) ?: 0.0
                                latestHR = (child.child("heart_rate").getValue(Double::class.java)) ?: 0.0
                                latestDist = (child.child("distance_km").getValue(Double::class.java)) ?: 0.0
                            }

                            Log.d(
                                "HomeFragment",
                                "Fetched → HR=$latestHR | Step=$latestStep | Dist=$latestDist | Target=$targetDistance"
                            )

                            if (targetDistance > 0 && latestDist >= targetDistance) {
                                if (completedAt == 0L)
                                    todayRef.child("completedAt").setValue(System.currentTimeMillis())
                                showCompletedChallengeUI(targetDistance, latestStep, latestHR)
                            } else {
                                updateChallengeUI(rpe, targetDistance, latestStep, latestHR)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("HomeFragment", "Error ambil history: ${error.message}")
                        }
                    })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Error ambil today_challenge: ${error.message}")
            }
        })
    }

    private fun updateChallengeUI(rpe: String, distance: Double, step: Double, hr: Double) {
        val challenge = ChallengeItemData(
            title = when (rpe) {
                "Easy" -> "Recovery Run"
                "Normal" -> "Light Jog"
                else -> "Tempo Challenge"
            },
            timeInSec = when (rpe) {
                "Easy" -> 1800
                "Normal" -> 2500
                else -> 3000
            },
            distanceKm = distance,
            step = step,
            heartRate = hr
        )

        binding.resaikelviewHome.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ActivitiesHomeAdapter(listOf(challenge))
        }
    }

    private fun showCompletedChallengeUI(distance: Double, step: Double, hr: Double) {
        val challengeCompleted = ChallengeItemData(
            title = "✅ Challenge Completed",
            timeInSec = 0,
            distanceKm = distance,
            step = step,
            heartRate = hr
        )

        binding.resaikelviewHome.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ActivitiesHomeAdapter(listOf(challengeCompleted))
        }
    }

    private fun bonjourHuman() {
        val hour = LocalTime.now().hour
        binding.greeting.text = when (hour) {
            in 4..10 -> "Good Morning 🔥"
            in 11..15 -> "Good Afternoon 🔥"
            else -> "Good Evening 🔥"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // hapus listener biar gak leak
        liveHRListener?.let {
            historyRef.removeEventListener(it)
        }
        _binding = null
    }
}
