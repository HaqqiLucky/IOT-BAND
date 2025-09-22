package com.example.smartbandiot

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Dashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        // Atur padding untuk status/navigation bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboardLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ==== Referensi Views sesuai XML ====
        val etSearch = findViewById<EditText>(R.id.etSearch)
        val containerPopular = findViewById<LinearLayout>(R.id.containerPopularWorkouts)
        val containerTodayPlan = findViewById<LinearLayout>(R.id.containerTodayPlan)

        // ==== Aksi Dummy ====

        // Search bar - tekan enter untuk cari
        etSearch.setOnEditorActionListener { _, _, _ ->
            Toast.makeText(this, "Searching: ${etSearch.text}", Toast.LENGTH_SHORT).show()
            true
        }

        // Klik Card Popular (ambil anak pertama sebagai contoh)
        if (containerPopular.childCount > 0) {
            val card1 = containerPopular.getChildAt(0) as CardView
            card1.setOnClickListener {
                Toast.makeText(this, "Opening first popular workout", Toast.LENGTH_SHORT).show()
            }
        }

        // Klik Plan Item pertama (ambil anak pertama LinearLayout)
        if (containerTodayPlan.childCount > 0) {
            val planItem1 = containerTodayPlan.getChildAt(0) as LinearLayout
            planItem1.setOnClickListener {
                Toast.makeText(this, "Plan selected: Push Up", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
