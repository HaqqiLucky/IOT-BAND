package com.example.smartbandiot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartbandiot.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalTime
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError


private val user = FirebaseAuth.getInstance().currentUser

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        // ini buat ngambil data nama
        if (user != null) {
            binding.namaUser.text = user.displayName
        }



        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef = com.google.firebase.database.FirebaseDatabase
            .getInstance("https://smartbandforteens-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("users").child(uid).child("today_challenge")

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val rpe = snapshot.child("rpe").getValue(String::class.java) ?: ""

                    val challengeList = ArrayList<ChallengeItemData>()

                    val challenge = when(rpe){
                        "Very Tired" -> ChallengeItemData("Recovery Run", 1800, 2.0)
                        "Tired" -> ChallengeItemData("Light Jog", 2500, 4.0)
                        else -> ChallengeItemData("Tempo Challenge", 3000, 6.0)
                    }

                    challengeList.add(challenge)

                    val adapter = ActivitiesHomeAdapter(challengeList)
                    binding.resaikelviewHome.apply {
                        layoutManager = LinearLayoutManager(requireContext())
                        this.adapter = adapter
                    }
                } else {
                    binding.resaikelviewHome.adapter = ActivitiesHomeAdapter(ArrayList())
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })


        bonjourHuman()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bonjourHuman(){
        val currentTime : LocalTime = LocalTime.now()
        val hour : Int = currentTime.hour

        val greetings :  String = when (hour){
            in 4..10 -> "Good Morning \uD83D\uDD25"
            in 11..15 -> "Good Afternoon \uD83D\uDD25"
            else -> "Good Evening \uD83D\uDD25"
        }

        binding.greeting.text = greetings
    }



        companion object {
            /**
             * Use this factory method to create a new instance of
             * this fragment using the provided parameters.
             *
             * @param param1 Parameter 1.
             * @param param2 Parameter 2.
             * @return A new instance of fragment HomeFragment.
             */
            // TODO: Rename and change types and number of parameters
            @JvmStatic
            fun newInstance(param1: String, param2: String) =
                HomeFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
        }

}

