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
import java.time.Year
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import kotlin.math.pow

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
            birthYYYYmm = viewModel.birthYYYYmm,
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
                rulebase()
                Log.d("CreatingPLanFragment","Rulebase masuk")
            }
            .addOnFailureListener { e ->
                Log.e("user_personal_preferences","ya elah ga masuk")
            }
    }

    private fun rulebase(){
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: return


        userRef.child(uid).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()){
                val userPreferencesfromFirebasehehe = snapshot.getValue(User::class.java)
                if (userPreferencesfromFirebasehehe != null) {

                    val ageBelumDipisah =
                        userPreferencesfromFirebasehehe.birthYYYYmm // misal 200511
                    val birthYear = ageBelumDipisah.substring(0, 4).toInt() // 2005
                    val birthMonth = ageBelumDipisah.substring(4, 6).toInt() // 11
                    val bulanuserlahir = YearMonth.of(birthYear, birthMonth)
                    val bulanSekarang = YearMonth.now()
                    val kalkulasiBulanUserDariLahir =
                        ChronoUnit.MONTHS.between(bulanuserlahir, bulanSekarang)
                    Log.d(
                        "CreatingPlan",
                        "umur sekarang dengan satuan bulan $kalkulasiBulanUserDariLahir dengan bulan di hitung saat ini $bulanSekarang"
                    )
                    val bulanLahir = YearMonth.of(birthYear, birthMonth)
                    val totalBulan = ChronoUnit.MONTHS.between(bulanLahir, bulanSekarang)

                    val tahunSekarang = Year.now()
                    val birthYear2 = Year.of(birthYear)
                    val age = ChronoUnit.YEARS.between(birthYear2, tahunSekarang)
                    Log.d("CreatingPlan", "umur sekarang dengan satuan tahun $age")

                    fun bmi(): String {
                        Log.d("CreatingPlan", "Ini adalah bmi fungsi init")
                        val weight = userPreferencesfromFirebasehehe.weight
                        val height = userPreferencesfromFirebasehehe.height
                        val sex = userPreferencesfromFirebasehehe.gender

                        if (age >= 19) {
                            val kalkulasibmi = weight / (height * height)
                            val kalkulasi = kalkulasibmi * 10000
                            Log.d("CreatingPlan", "Hasil kalkulasi BMI :  $kalkulasibmi dan yang sudah di kali 10.000 $kalkulasi")
                            val kategori = when (kalkulasi) {
                                in Double.NEGATIVE_INFINITY..18.4 -> "Underweight"
                                in 18.5..24.9 -> "Normal"
                                in 25.0..29.9 -> "Overweight"
                                else -> "Obese"
                            }
                            Log.d("CreatingPlan", "ini age 20 > dapet kategori $kategori")
                            return kategori
                        } else {
                            val bmibocilblmFix = weight / (height * height)
                            // ngambil el em es
                            if (sex == "Male") {
                                val L = 1
                                val M = 2
                                val S = 3
//                                val dataForBoy = File("app/src/main/assets/bmi-boys-z-who-2007-exp.csv")
//                                    .readLines()
//                                    .find {
//                                        it.split(',').first()
//                                            .trim() == kalkulasiBulanUserDariLahir.toString()
//                                    }
//                                    ?.split(',')
//                                    ?.slice(listOf(L, M, S))
                                val inputStream = requireContext().assets.open("bmi-boys-z-who-2007-exp.csv")
                                val lines = inputStream.bufferedReader().readLines()

                                val dataForBoy = lines.find {
                                    it.split(',').first().trim() == kalkulasiBulanUserDariLahir.toString()
                                }?.split(',')?.slice(listOf(L, M, S))

                                // LMS Equation
                                val z_score = ((bmibocilblmFix / M).pow(L) - 1) / (L * S)
                                Log.d("CreatingPlan", "Hasil z score = $z_score")
                                val kategoriBocil = when (z_score) {
                                    in Double.NEGATIVE_INFINITY..-2.0 -> "Underweight"
                                    in -2.0..1.0 -> "Normal"
                                    in 1.0..2.0 -> "Overweight"
                                    else -> "Obese"
                                }
                                Log.d("CreatingPlan", "ini age < 20 boy dapet kategori $kategoriBocil")
                                return kategoriBocil
                            } else { // ini pricess
                                val L = 1
                                val M = 2
                                val S = 3
//                                val dataForGirl = File("app/src/main/assets/bmi-girls-z-who-2007-exp.csv")
//                                    .readLines()
//                                    .find {
//                                        it.split(',').first()
//                                            .trim() == kalkulasiBulanUserDariLahir.toString()
//                                    }
//                                    ?.split(',')
//                                    ?.slice(listOf(L, M, S))
                                val inputStream = requireContext().assets.open("bmi-girls-z-who-2007-exp.csv")
                                val lines = inputStream.bufferedReader().readLines()

                                val dataForGirl = lines.find {
                                    it.split(',').first().trim() == kalkulasiBulanUserDariLahir.toString()
                                }?.split(',')?.slice(listOf(L, M, S))


                                // LMS Equation
                                val z_score = ((bmibocilblmFix / M).pow(L) - 1) / (L * S)
                                Log.d("CreatingPlan", "Hasil z score = $z_score")
                                val kategoriBocil = when (z_score) {
                                    in Double.NEGATIVE_INFINITY..-2.0 -> "Underweight"
                                    in -2.0..1.0 -> "Normal"
                                    in 1.0..2.0 -> "Overweight"
                                    else -> "Obese"
                                }
                                Log.d("CreatingPlan", "ini age < 20 girl dapet kategori $kategoriBocil")
                                return kategoriBocil
                            }
                        }
                    }
                    val hrMax = 200 - age
                    val bmi = bmi()
                    Log.d("Creating Plan","BMI yg didapet $bmi")

                    // jadwal umur
                    val jadwal = when {
                        age in 5 .. 19 -> "Minimal 3 jam seminggu"
                        age in 20 .. 64 -> "Minimal 7.5 jam seminggu"
                        age >= 65 -> "2.5 jam seminggu"
                        else -> "Tidak ada rekomendasi, usia terlalu dini"
                    }
                    // rekomendasi hari = senen, rabu, sabtu

                    // bmi
                    val olahragaUser = if (bmi == "Underweight" || bmi == "Obese"){
                        "Jalan santai"
                    }else{
                        "Jogging"
                    }

                    // HrMax
                    val HrTarget = if (bmi == "Normal") {
                        (0.75 * hrMax)..(0.85 * hrMax)
                    } else { // obese, underweight sama overweight
                        (0.75 * hrMax)
                    }

                    val rulebaseUser = mapOf(
                        "jenisOlahragaUser" to olahragaUser,
                        "HrTarget" to HrTarget.toString(),
                        "jadwal" to jadwal
                    )
                    userRef.child(uid).child("hasilRulebase").setValue(rulebaseUser)
                        .addOnSuccessListener {
                            Log.d("Creating plan","Rulebase udah masuk")
                            Log.d("Creating plan","jenis olahraga $olahragaUser")
                            Log.d("Creating plan","Hr target $HrTarget")
                            Log.d("Creating plan","jadwal $jadwal")
                        } .addOnFailureListener {
                            Log.d("Creating plan", "life bloom like flower..")
                        }
                }
            }

        }

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
