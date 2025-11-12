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
    private lateinit var liveHeartRateRef: DatabaseReference
    private lateinit var challengesRef: DatabaseReference

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

        challengesRef = db.getReference("users").child(uid).child("challenges")
        liveHeartRateRef = db.getReference("data_iot").child("device_001").child("heart_rate")

        setupLiveHeartRateListener()
        loadAllChallenges()
    }

    private fun setupLiveHeartRateListener() {
        liveHeartRateRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isAdded || _binding == null) return
                val bpm = snapshot.getValue(Int::class.java) ?: -1

                if (bpm > 0) {
                    binding.txtLiveHeartRateHome.text = "$bpm bpm"
                    binding.txtLiveHeartRateHome.setTextColor(requireContext().getColor(R.color.no_device_red))
                } else {
                    binding.txtLiveHeartRateHome.text = "-- bpm"
                    binding.txtLiveHeartRateHome.setTextColor(requireContext().getColor(R.color.abu_abu))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Error HR: ${error.message}")
            }
        })
    }

    private fun loadAllChallenges() {
        challengesRef.orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!isAdded || _binding == null) return
                    val challengeList = mutableListOf<ChallengeItemData>()

                    for (child in snapshot.children) {
                        val rpe = child.child("rpe").getValue(String::class.java) ?: "Normal"
                        val target = child.child("targetDistance").getValue(Double::class.java) ?: 0.0
                        val completedAt = child.child("completedAt").getValue(Long::class.java) ?: 0L

                        val hr = child.child("lastHeartRate").getValue(Double::class.java) ?: 0.0
                        val step = child.child("lastStep").getValue(Double::class.java) ?: 0.0
                        val distance = child.child("lastDistance").getValue(Double::class.java) ?: 0.0

                        var title = when (rpe) {
                            "Easy" -> "Recovery Run"
                            "Normal" -> "Light Jog"
                            else -> "Tempo Challenge"
                        }

                        if (completedAt > 0L) title = "✅ Challenge Completed"

                        val challenge = ChallengeItemData(
                            title = title,
                            timeInSec = when (rpe) {
                                "Easy" -> 1800
                                "Normal" -> 2500
                                else -> 3000
                            },
                            distanceKm = target,
                            step = step,
                            heartRate = hr
                        )

                        challengeList.add(challenge)
                    }

                    // hapus challenge lama > 3
                    if (snapshot.childrenCount > 3) {
                        val extra = snapshot.childrenCount - 3
                        snapshot.children.take(extra.toInt()).forEach { it.ref.removeValue() }
                        Log.d("HomeFragment", "🧹 Hapus $extra challenge lama.")
                    }

                    challengeList.reverse()
                    binding.resaikelviewHome.apply {
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = ActivitiesHomeAdapter(challengeList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("HomeFragment", "Error ambil challenge: ${error.message}")
                }
            })
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
        _binding = null
    }
}
