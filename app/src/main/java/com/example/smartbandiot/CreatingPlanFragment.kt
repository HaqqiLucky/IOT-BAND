package com.example.smartbandiot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.smartbandiot.databinding.FragmentCreatingPlanBinding
import com.example.smartbandiot.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import java.io.File
import java.time.YearMonth
import java.time.temporal.ChronoUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreatingPlanFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreatingPlanFragment : Fragment() {

    private var _binding: FragmentCreatingPlanBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private val realtimeDatabase = Firebase.database
    private val userRef = realtimeDatabase.getReference("users_personal_preferences")


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        saveUserDataToFirebase()
        Log.d("CreatingPLanFragment","Sampe sini harusnya udah masuk ke database")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatingPlanBinding.inflate(inflater, container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.circularProgressBar.apply {
            setProgressWithAnimation(100f, 10000)
        }

        binding.continu.isEnabled = false
        binding.circularProgressBar.onProgressChangeListener = { progress ->
            binding.persenanloading.text = "${progress.toInt()}%"
            binding.continu.isEnabled = (binding.persenanloading.text == "100%")
        }

        binding.continu.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // g bs balik lagi blee
        }
    }

    private fun saveUserDataToFirebase() {
        val viewModel = ViewModelProvider(requireActivity())[PreferencesSharedViewModel::class.java]
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: return

        val user = User(
            uid = uid,
            name = auth.currentUser?.displayName ?: "",
            weight = viewModel.weight,
            height = viewModel.height,
            age = viewModel.age,
            profileImagePath = auth.currentUser?.photoUrl.toString(),
            email = auth.currentUser?.email ?: "",
            gender = viewModel.gender,
            goal = viewModel.goal
        )

//        val database = Firebase.database
//        val myRef = database.getReference("users_personal_preferences")
        userRef.child(uid).setValue(user)
            .addOnSuccessListener {
                Log.d("user_personal_preferences","mi sukses")
            }
            .addOnFailureListener { e ->
                Log.e("user_personal_preferences","ya elah ga masuk")
            }
    }

    private fun rulebase(){
//        val viewModel = ViewModelProvider(requireActivity())[PreferencesSharedViewModel::class.java]
//        val dfRef = Firebase.
//        val auth = FirebaseAuth.getInstance()

        userRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()){
                val userPreferencesfromFirebasehehe = snapshot.getValue(User::class.java)
                if (userPreferencesfromFirebasehehe != null){

                    val hrMax = 200 - userPreferencesfromFirebasehehe.age
//                    val bmi = userPreferencesfromFirebasehehe.weight / userPreferencesfromFirebasehehe.height * userPreferencesfromFirebasehehe.height

                    fun bmi(): String {
                        val age = userPreferencesfromFirebasehehe.age
                        val weight = userPreferencesfromFirebasehehe.weight
                        val height = userPreferencesfromFirebasehehe.height
                        val sex = userPreferencesfromFirebasehehe.gender

                        if (age >= 20 ){
                            val kalkulasi = weight / (height * height)
                            val kategori = when (kalkulasi) {
                                in Double.NEGATIVE_INFINITY..18.4 -> "Underweight"
                                in 18.5..24.9 -> "Normal"
                                in 25.0..29.9 -> "Overweight"
                                else -> "Obese"
                            }
                            return kategori
                        } else {
                            val kalkulasiawal = weight / (height * height)
                            // LMS EQUATION
                            if (sex == "Male"){
                                val dataForBoy = "bmi-boys-z-who-2007-exp.csv"
                                File(dataForBoy).forEachLine { line ->
                                    val columns = line.split(",")

                                    val month = columns[0]
                                    val L = columns[1]
                                    val M = columns[2]
                                    val S = columns[3]

                                    val bulanuserlahir = YearMonth.of(2005,11)
                                    val bulanSekarang = YearMonth.now()
                                    val kalkulasiBulanUserDariLahir = ChronoUnit.MONTHS.between(bulanuserlahir, bulanSekarang)
                                    Log.d("CreatingPlan","User male dengan umur sekarang dengan satuan bulan $kalkulasiBulanUserDariLahir")

                                    
                                }
                            }
                        }
//                        return bmi()
                    }


                    if (userPreferencesfromFirebasehehe.goal == "Keep Fit"){
                        if (userPreferencesfromFirebasehehe.age < 19)
                            Log.d("Age","Childen and adoselent")

                    }

                }
            }
        }
//        val hrMax = 200 - viewModel.age
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreatingPlanFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreatingPlanFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}


// rulebase dr draw io




//Underweight
//Less than 18.5

//Healthy Weight
//18.5 to less than 25

//Overweight
//25 to less than 30

//Obesity
//30 or greater

//Class 1 Obesity
//30 to less than 35

//Class 2 Obesity
//35 to less than 40

//Class 3 Obesity
//(Severe Obesity)
//40 or greater

// source =  https://www.cdc.gov/bmi/adult-calculator/bmi-categories.html



// gender : gender was found to be an important factor influencing endurance performance
//source : https://dl.acm.org/doi/10.1145/3732299.3732334#sec-5


//height & weight source : https://dl.acm.org/doi/10.1145/3732299.3732334#sec-5
//The influence of height, weight and BMI: Height and weight, as basic indicators of body shape,
//have a direct impact on endurance performance. Higher BMI is negatively correlated with endurance
//performance, indicating that overweight and obese individuals typically have poorer endurance
//performance. This highlights the importance of weight control in improving endurance performance.

//makin gede bmi kalo besarannya itu isinya lemak semua maka bakal cpt capek juga orgnya kalo lari
//        kalo gedenya karena muscle beda lagi mesti kuat kuat aja larinya
