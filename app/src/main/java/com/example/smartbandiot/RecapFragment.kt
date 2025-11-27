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
import com.google.firebase.database.*

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
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        userRPE.child(uid).limitToLast(1).get()
            .addOnSuccessListener { snapshot ->
                var distance = 0.0
                var step = 0.0
                var hr = 0.0

                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        val data = child.value as? Map<*, *>
                        distance = (data?.get("distance_km") as? Number)?.toDouble() ?: 0.0
                        step = (data?.get("steps") as? Number)?.toDouble() ?: 0.0
                        hr = (data?.get("heart_rate") as? Number)?.toDouble() ?: 0.0
                    }
                }

                Log.d("RecapFragment", "Latest data: $distance km | $step step | $hr bpm")

                binding.RPEEasy.setOnClickListener {
                    val target = if (distance > 0) distance + (distance * 0.015) else 0.1
                    saveChallenge(uid, "Easy", target, hr, step, distance)
                }

                binding.RPENormal.setOnClickListener {
                    val target = if (distance > 0) distance else 0.1
                    saveChallenge(uid, "Normal", target, hr, step, distance)
                }

                binding.RPEHard.setOnClickListener {
                    val target = if (distance > 0) distance - (distance * 0.005) else 0.1
                    saveChallenge(uid, "Tired", target, hr, step, distance)
                }
            }
            .addOnFailureListener {
                Log.e("RecapFragment", "Error ambil history: ${it.message}")
            }
    }

    /** 🔹 Simpan challenge baru lengkap dengan HR & Step dari history terakhir */
    private fun saveChallenge(uid: String, rpe: String, target: Double, hr: Double, step: Double, distance: Double) {
        val db = Firebase.database
        val userChallengesRef = db.getReference("users").child(uid).child("challenges")

        val challengeData = mapOf(
            "rpe" to rpe,
            "targetDistance" to target,
            "completedAt" to 0L,
            "timestamp" to System.currentTimeMillis(),
            "lastHeartRate" to hr,
            "lastStep" to step,
            "lastDistance" to distance
        )

        val newRef = userChallengesRef.push()
        newRef.setValue(challengeData).addOnSuccessListener {
            db.getReference("users").child(uid).child("today_challenge").setValue(challengeData)
            trimOldChallenges(uid)
            gotoFinish()
        }
    }

    /** 🔹 Batasi maksimal 3 challenge */
    private fun trimOldChallenges(uid: String) {
        val userChallengesRef = Firebase.database.getReference("users").child(uid).child("challenges")
        userChallengesRef.orderByChild("timestamp")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.childrenCount > 3) {
                        val extra = snapshot.childrenCount - 3
                        snapshot.children.take(extra.toInt()).forEach { it.ref.removeValue() }
                        Log.d("RecapFragment", "🧹 Hapus $extra challenge lama.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun gotoFinish() {
        val intent = Intent(requireContext(), RpeFinishingRequestActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}
