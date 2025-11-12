package com.example.smartbandiot

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.smartbandiot.databinding.FragmentJoggingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class JoggingFragment : Fragment() {

    private var _binding: FragmentJoggingBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: MapView
    private lateinit var controller: IMapController

    private lateinit var database: FirebaseDatabase
    private lateinit var heartRateRef: DatabaseReference
    private lateinit var stepsRef: DatabaseReference

    private var pathOverlay: Polyline? = null
    private var lastLocation: GeoPoint? = null
    private var totalDistance = 0.0
    private var joggingActive = false

    private lateinit var panelStats: LinearLayout
    private lateinit var heartRateText: TextView
    private lateinit var stepsText: TextView
    private lateinit var btnStartRun: Button

    private var hrMaxLimit = 0.0
    private var hrMinLimit = 0.0
    private var lastWarningTime = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoggingBinding.inflate(inflater, container, false)
        val view = binding.root

        // Load peta OSM
        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )

        database = FirebaseDatabase.getInstance(
            "https://smartbandforteens-default-rtdb.asia-southeast1.firebasedatabase.app/"
        )

        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        // 🔹 Pastikan reference ke node yang benar
        heartRateRef = database.getReference("data_iot/device_001/heart_rate")
        stepsRef = database.getReference("data_iot/device_001/steps")

        // Setup map
        map = binding.mapView
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.setBuiltInZoomControls(false)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

        controller = map.controller
        controller.setZoom(17.0)

        panelStats = view.findViewById(R.id.panelStats)
        panelStats.visibility = View.GONE

        heartRateText = view.findViewById(R.id.txtHeart)
        stepsText = view.findViewById(R.id.txtStep)
        btnStartRun = view.findViewById(R.id.btnStartRun)

        // Tombol Mulai
        btnStartRun.setOnClickListener {
            if (joggingActive) {
                Toast.makeText(requireContext(), "Jogging sedang berjalan!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            resetStepsCounterOnly() // ✅ hanya reset langkah

            totalDistance = 0.0
            lastLocation = null
            pathOverlay?.points?.clear()
            map.invalidate()

            btnStartRun.visibility = View.GONE
            panelStats.visibility = View.VISIBLE
            joggingActive = true

            Toast.makeText(requireContext(), "🏃‍♂️ Jogging dimulai! Langkah direset.", Toast.LENGTH_SHORT).show()
        }

        // Tombol Stop
        val btnStopRun = view.findViewById<Button>(R.id.btnStopRun)
        btnStopRun.setOnClickListener {
            if (!joggingActive) {
                Toast.makeText(requireContext(), "Belum mulai jogging!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            joggingActive = false
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val ref = database.getReference("history").child(uid).push()

            val heart = heartRateText.text.toString()
                .replace("Heart Rate: ", "")
                .replace(" bpm", "")
                .toIntOrNull() ?: 0

            val step = stepsText.text.toString()
                .replace("Steps: ", "")
                .toIntOrNull() ?: 0

            // Jika GPS tidak aktif, hitung dari langkah
            var distance = totalDistance
            if (distance <= 0.0 && step > 0) {
                distance = (step * 0.78) / 1000.0 // fallback ke jarak langkah
            }

            val data = mapOf(
                "timestamp" to System.currentTimeMillis(),
                "heart_rate" to heart,
                "steps" to step,
                "distance_km" to distance,
                "rpe_status" to "pending"
            )

            ref.setValue(data)
            Log.d("JoggingFragment", "Saved: HR=$heart | Step=$step | Distance=$distance")

            Toast.makeText(requireContext(), "Aktivitas disimpan!", Toast.LENGTH_SHORT).show()

            // Pindah ke recap setelah simpan
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, RecapFragment())
                    .addToBackStack(null)
                    .commit()
            }, 400)
        }

        loadUserHRLimit(uid)
        addFirebaseListeners()
        setupUserLocationTracking()

        return view
    }

    /** ✅ Reset hanya langkah, bukan heart rate */
    private fun resetStepsCounterOnly() {
        database.getReference("data_iot/device_001/steps")
            .setValue(0)
            .addOnSuccessListener {
                Log.d("JoggingFragment", "✅ Steps direset ke 0 (heart_rate tidak disentuh).")
            }
            .addOnFailureListener {
                Log.e("JoggingFragment", "❌ Gagal reset langkah: ${it.message}")
            }
    }

    /** 🔹 Ambil batas HR user dari hasil rulebase */
    private fun loadUserHRLimit(uid: String) {
        val prefRef = database.getReference("users_personal_preferences")
            .child(uid).child("hasilRulebase").child("HrTarget")

        prefRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val range = snapshot.getValue(String::class.java) ?: return
                val parts = range.split("..")
                if (parts.size == 2) {
                    hrMinLimit = parts[0].toDouble()
                    hrMaxLimit = parts[1].toDouble()
                    Log.d("JoggingFragment", "HR Range Loaded: $hrMinLimit..$hrMaxLimit")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("JoggingFragment", "Gagal load HR limit: ${error.message}")
            }
        })
    }

    /** 🔹 Listener real-time IoT */
    private fun addFirebaseListeners() {
        heartRateRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val heartRate = snapshot.getValue(Int::class.java)
                heartRate?.let {
                    heartRateText.text = "Heart Rate: $it bpm"

                    if (joggingActive && hrMaxLimit > 0 && it > hrMaxLimit) {
                        val now = System.currentTimeMillis()
                        if (now - lastWarningTime > 7000) {
                            lastWarningTime = now
                            Toast.makeText(
                                requireContext(),
                                "⚠️ Heart Rate melewati batas aman!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("JoggingFirebase", "Heart rate error: ${error.message}")
            }
        })

        stepsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val steps = snapshot.getValue(Int::class.java)
                steps?.let {
                    stepsText.text = "Steps: $it"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("JoggingFirebase", "Steps error: ${error.message}")
            }
        })
    }

    /** 🔹 GPS Tracking */
    private fun setupUserLocationTracking() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val overlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), map)
            overlay.enableMyLocation()
            overlay.enableFollowLocation()
            map.overlays.add(overlay)

            pathOverlay = Polyline().apply {
                outlinePaint.color = Color.BLUE
                outlinePaint.strokeWidth = 8f
            }
            map.overlays.add(pathOverlay)

            overlay.runOnFirstFix {
                requireActivity().runOnUiThread {
                    val loc = overlay.myLocation
                    loc?.let {
                        val point = GeoPoint(it.latitude, it.longitude)
                        controller.setCenter(point)
                        lastLocation = point
                        pathOverlay?.addPoint(point)
                    }
                }
            }

            overlay.myLocationProvider.startLocationProvider { location, _ ->
                if (location != null && joggingActive) {
                    requireActivity().runOnUiThread {
                        val newPoint = GeoPoint(location.latitude, location.longitude)
                        if (lastLocation != null) {
                            val distance = newPoint.distanceToAsDouble(lastLocation)
                            totalDistance += (distance / 1000.0)
                        }
                        lastLocation = newPoint
                        pathOverlay?.addPoint(newPoint)
                        map.invalidate()
                    }
                }
            }
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
