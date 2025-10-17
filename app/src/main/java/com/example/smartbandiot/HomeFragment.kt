package com.example.smartbandiot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartbandiot.databinding.FragmentChooseTrainingLevelBinding
import com.example.smartbandiot.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalTime
import kotlin.random.Random

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
        val myDataList = generateDummyList(15) // Menggunakan fungsi dummy

        // 2. Inisialisasi Adapter
        // Pastikan Anda telah mendefinisikan MyAdapter dan ItemData
        val adapter = ActivitiesHomeAdapter(myDataList)
        binding.resaikelviewHome.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
            setHasFixedSize(true)
        }

        bonjourHuman()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun generateDummyList(size: Int): List<HistoryActivityItemData> {
        val list = ArrayList<HistoryActivityItemData>()
        val activityTypes = listOf("Lari Pagi", "Bersepeda Santai", "Jalan Kaki Cepat", "Hiking", "Sprint Interval")
        val random = Random.Default

        for (i in 0 until size) {
            // 1. Tentukan Jarak Acak (antara 1.0 km hingga 15.0 km)
            val distance = random.nextDouble(1.0, 15.0)

            // 2. Tentukan Waktu Acak (antara 20 menit hingga 2 jam)
            // Kita hitung dalam detik (Int)
            // Min 20 menit = 1200 detik. Max 120 menit = 7200 detik.
            val minTimeSec = 1200
            val maxTimeSec = 7200
            val time = random.nextInt(minTimeSec, maxTimeSec)

            // 3. Tentukan Judul Acak
            // Mengambil judul acak dari daftar activityTypes
            val title = activityTypes.random()

            // 4. Buat objek data baru dan tambahkan ke daftar
            // Kita tidak perlu mengisi 'date' karena ia memiliki default = System.currentTimeMillis()
            list.add(
                HistoryActivityItemData(
                    title = title,
                    timeInSec = time,
                    distanceKm = distance
                    // date akan menggunakan nilai default (waktu saat ini)
                )
            )
        }
        return list
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

