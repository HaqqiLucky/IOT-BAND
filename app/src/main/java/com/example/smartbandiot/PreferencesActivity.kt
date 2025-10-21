package com.example.smartbandiot

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.smartbandiot.databinding.ActivityPreferencesBinding
import com.example.smartbandiot.model.PreferencesViewModel

class PreferencesActivity : AppCompatActivity() {

    lateinit var preferencesViewModel: PreferencesViewModel
    private lateinit var binding: ActivityPreferencesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesViewModel = ViewModelProvider(this)[PreferencesViewModel::class.java]
//        val prefs = requireCo

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.host_fragment_container_preferences) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.Step1Gender -> {
                    binding.step.text = "Step 1 of 5"
                    binding.buttonback.visibility = View.GONE
                    binding.skip.visibility = View.VISIBLE
                }
                R.id.Step2MainGoal -> {
                    binding.step.text = "Step 2 of 5"
                    binding.buttonback.visibility = View.VISIBLE
                    binding.skip.visibility = View.VISIBLE
                }
                R.id.Step3Height -> {
                    binding.step.text = "Step 3 of 5"
                    binding.buttonback.visibility = View.VISIBLE
                    binding.skip.visibility = View.VISIBLE
                }
                R.id.Step4Weight -> {
                    binding.step.text = "Step 4 of 5"
                    binding.buttonback.visibility = View.VISIBLE
                    binding.skip.visibility = View.VISIBLE
                }
                R.id.Step5age -> {
                    binding.step.text = "Step 5 of 5"
                    binding.step.visibility = View.VISIBLE
                    binding.buttonback.visibility = View.VISIBLE
                    binding.skip.visibility = View.VISIBLE
                }
                R.id.CreatingPlan -> {
                    binding.step.visibility = View.GONE
                    binding.skip.visibility = View.GONE
                    binding.buttonback.visibility = View.VISIBLE
                }
            }
        }


//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            when (destination.id) {
//                R.id.Step1Gender -> binding.step.text = "Step 1 of 5"
//                R.id.Step2MainGoal -> binding.step.text = "Step 2 of 5"
//                R.id.Step3Height -> binding.step.text = "Step 3 of 5"
//                R.id.Step4Weight -> binding.step.text = "Step 4 of 5"
//                R.id.Step5traininglevel -> binding.step.text = "Step 5 of 5"
//            }
//        }
//
        binding.buttonback.setOnClickListener {
            navController.navigateUp()
        }
//
        binding.skip.setOnClickListener {
            when (navController.currentDestination?.id) {
                R.id.Step1Gender -> navController.navigate(R.id.Step2MainGoal)
                R.id.Step2MainGoal -> navController.navigate(R.id.Step3Height)
                R.id.Step3Height -> navController.navigate(R.id.Step4Weight)
                R.id.Step4Weight -> navController.navigate(R.id.Step5age)
                R.id.Step5age -> navController.navigate(R.id.CreatingPlan)
            }
        }
//
//        if (navController.currentDestination?.id == R.id.CreatingPlan) {
//            binding.skip.visibility = View.GONE
//        }
//
//        if (navController.currentDestination?.id == R.id.Step1Gender) {
//            binding.buttonback.visibility = View.GONE
//        }

        if (savedInstanceState == null) {
//            val myFragment = ChooseGenderFragment()
//
//            supportFragmentManager.beginTransaction()
//                .add(R.id.host_fragment_container_preferences, myFragment)
//                .commit()

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.host_fragment_container_preferences).navigateUp() || super.onSupportNavigateUp()
    }
}