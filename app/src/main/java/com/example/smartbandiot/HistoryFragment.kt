package com.example.smartbandiot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView

class HistoryFragment : Fragment() {

    private lateinit var btnBack: ImageButton
    private lateinit var tvViewAll: TextView
    private lateinit var cardYesterday: MaterialCardView
    private lateinit var tvYesterdayBpm: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

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
        tvViewAll = view.findViewById(R.id.tvViewAll)
        cardYesterday = view.findViewById(R.id.cardYesterday)
        tvYesterdayBpm = view.findViewById(R.id.tvYesterdayBpm)
    }

    private fun setupClickListeners() {
        // Back button
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // View All button
        tvViewAll.setOnClickListener {
            navigateToDetail()
        }

        // Yesterday Card - klik untuk detail
        cardYesterday.setOnClickListener {
            navigateToDetail()
        }
    }

    private fun navigateToDetail() {
        // Navigasi ke HeartRateDetailFragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, HeartRateDetailFragment())
            .addToBackStack("analytics")
            .commit()
    }

    private fun loadData() {
        // TODO: Load data dari database atau API
        // Untuk sementara gunakan data dummy
        tvYesterdayBpm.text = "102"
    }
}