// --- FULL UPDATED JoggingFragment.kt ---

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
import kotlin.math.max

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

    // baseline
    private var baseSteps: Int? = null
    private var rawStepsLatest: Int = 0

    // ➕ NEW: untuk average HR
    private var latestHeartRate: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentJoggingBinding.inflate(inflater, container, false)
        val view = binding.root

        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )

        database = FirebaseDatabase.getInstance(
            "https://smartbandforteens-default-rtdb.asia-southeast1.firebasedatabase.app/"
        )

        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        heartRateRef = database.getReference("data_iot/device_001/heart_rate")
        stepsRef = database.getReference("data_iot/device_001/steps")

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

        // START
        btnStartRun.setOnClickListener {
            if (joggingActive) {
                Toast.makeText(requireContext(), "Jogging sedang berjalan!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            stepsRef.get().addOnSuccessListener { snap ->
                val raw = snap.getValue(Int::class.java) ?: 0
                baseSteps = raw

                totalDistance = 0.0
                lastLocation = null
                pathOverlay?.points?.clear()
                map.invalidate()

                btnStartRun.visibility = View.GONE
                panelStats.visibility = View.VISIBLE
                joggingActive = true

                updateStepsDisplay()

                Toast.makeText(requireContext(), "🏃‍♂️ Jogging dimulai!", Toast.LENGTH_SHORT).show()
            }
        }

        // STOP
        val btnStopRun = view.findViewById<Button>(R.id.btnStopRun)
        btnStopRun.setOnClickListener {
            if (!joggingActive) {
                Toast.makeText(requireContext(), "Belum mulai jogging!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            joggingActive = false

            val uidStop = FirebaseAuth.getInstance().currentUser!!.uid
            val ref = database.getReference("history").child(uidStop).push()

            val displaySteps = if (baseSteps != null) max(0, rawStepsLatest - baseSteps!!) else rawStepsLatest

            // Ambil HR terakhir → dijadikan average HR
            val avgHR = latestHeartRate

            var distance = totalDistance
            if (distance <= 0.0 && displaySteps > 0) {
                distance = (displaySteps * 0.78) / 1000.0
            }

            val data = mapOf(
                "timestamp" to System.currentTimeMillis(),
                "average_hr" to avgHR,
                "heart_rate" to avgHR,
                "steps" to displaySteps,
                "distance_km" to distance,
                "rpe_status" to "pending"
            )

            ref.setValue(data)

            Toast.makeText(requireContext(), "Aktivitas disimpan!", Toast.LENGTH_SHORT).show()

            baseSteps = null

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

    private fun updateStepsDisplay() {
        val display = if (baseSteps != null) max(0, rawStepsLatest - baseSteps!!) else rawStepsLatest
        stepsText.text = "Steps: $display"
    }

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
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun addFirebaseListeners() {

        // HR Listener
        heartRateRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hr = snapshot.getValue(Int::class.java) ?: 0
                latestHeartRate = hr
                heartRateText.text = "Heart Rate: $hr bpm"
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        // Steps Listener
        stepsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rawStepsLatest = snapshot.getValue(Int::class.java) ?: 0
                updateStepsDisplay()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

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

            overlay.myLocationProvider.startLocationProvider { location, _ ->
                if (location != null && joggingActive) {
                    requireActivity().runOnUiThread {
                        val newPoint = GeoPoint(location.latitude, location.longitude)
                        if (lastLocation != null) {
                            totalDistance += newPoint.distanceToAsDouble(lastLocation) / 1000.0
                        }
                        lastLocation = newPoint
                        pathOverlay?.addPoint(newPoint)
                        map.invalidate()
                    }
                }
            }
        }
    }

    override fun onResume() { super.onResume(); map.onResume() }
    override fun onPause() { super.onPause(); map.onPause() }
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
