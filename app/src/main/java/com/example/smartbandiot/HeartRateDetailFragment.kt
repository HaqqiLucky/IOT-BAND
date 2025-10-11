package com.example.smartbandiot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class HeartRateDetailFragment : Fragment() {

    private lateinit var btnBack: ImageButton
    private lateinit var tvAverageBpm: TextView
    private lateinit var tvMaximumBpm: TextView
    private lateinit var btnMoreOptions: ImageView
    private lateinit var chartHeartRate: HeartRateChartView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_heart_rate_detail, container, false)

        // Inisialisasi views
        initViews(view)

        // Setup click listeners
        setupClickListeners()

        // Load data
        loadData()

        return view
    }

    private fun initViews(view: View) {
        btnBack = view.findViewById(R.id.btnBack)
        tvAverageBpm = view.findViewById(R.id.tvAverageBpm)
        tvMaximumBpm = view.findViewById(R.id.tvMaximumBpm)
        btnMoreOptions = view.findViewById(R.id.btnMoreOptions)
        chartHeartRate = view.findViewById(R.id.chartHeartRate)
    }

    private fun setupClickListeners() {
        // Back button
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // More options button
        btnMoreOptions.setOnClickListener {
            Toast.makeText(context, "More options", Toast.LENGTH_SHORT).show()
            // TODO: Tampilkan menu atau dialog
        }
    }

    private fun loadData() {
        // TODO: Load data dari database atau API
        // Untuk sementara gunakan data dummy
        tvAverageBpm.text = "78"
        tvMaximumBpm.text = "98"

        // Set data untuk chart (bisa diubah sesuai data real)
        val chartData = listOf(
            HeartRateChartView.ChartBar("Mar", 55f),  // 55% dari maksimal
            HeartRateChartView.ChartBar("Apr", 72f),  // 72% dari maksimal
            HeartRateChartView.ChartBar("May", 67f),
            HeartRateChartView.ChartBar("Jun", 50f)
        )
        chartHeartRate.setData(chartData)
    }

    // Fungsi untuk update chart dengan data baru
    fun updateChartData(newData: List<HeartRateChartView.ChartBar>) {
        chartHeartRate.setData(newData)
    }
}

