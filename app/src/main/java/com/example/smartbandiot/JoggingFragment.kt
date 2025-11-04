package com.example.smartbandiot

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.smartbandiot.databinding.FragmentJoggingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.views.CustomZoomButtonsController

class JoggingFragment : Fragment() {

    private var _binding: FragmentJoggingBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: MapView
    private lateinit var controller: IMapController

    private lateinit var database: FirebaseDatabase
    private lateinit var heartRateRef: DatabaseReference
    private lateinit var stepsRef: DatabaseReference
    private lateinit var historyRef: DatabaseReference

    private var pathOverlay: Polyline? = null
    private var lastLocation: GeoPoint? = null
    private var totalDistance = 0.0
    private var joggingActive = false

    private lateinit var panelStats: LinearLayout
    private lateinit var heartRateText: TextView
    private lateinit var stepsText: TextView
    private lateinit var btnStartRun: Button
    private lateinit var btnStopRun: Button

    private var lastSteps = 0
    private var lastHeartRate = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJoggingBinding.inflate(inflater, container, false)
        val view = binding.root

        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))

        map = binding.mapView
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.setBuiltInZoomControls(false)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

        controller = map.controller
        controller.setZoom(17.0)

        database = FirebaseDatabase.getInstance("https://smartbandforteens-default-rtdb.asia-southeast1.firebasedatabase.app/")
        heartRateRef = database.getReference("data_iot/device_001/heart_rate")
        stepsRef = database.getReference("data_iot/device_001/steps")
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        historyRef = database.getReference("history").child(uid)

        panelStats = view.findViewById(R.id.panelStats)
        panelStats.visibility = View.GONE

        heartRateText = view.findViewById(R.id.txtHeart)
        stepsText = view.findViewById(R.id.txtStep)
        btnStartRun = view.findViewById(R.id.btnStartRun)
        btnStopRun = view.findViewById(R.id.btnStopRun)

        btnStartRun.setOnClickListener {
            // reset realtime data agar starting clean
            stepsRef.setValue(0)
            heartRateRef.setValue(0)

            totalDistance = 0.0
            lastLocation = null

            btnStartRun.visibility = View.GONE
            panelStats.visibility = View.VISIBLE
            joggingActive = true
        }


        btnStopRun.setOnClickListener {
            joggingActive = false

            // SAVE HISTORY
            val historyId = historyRef.push().key ?: System.currentTimeMillis().toString()

            val dataSave = mapOf(
                "heart_rate" to lastHeartRate,
                "steps" to lastSteps,
                "distance_km" to totalDistance,
                "timestamp" to System.currentTimeMillis()
            )

            historyRef.child(historyId).setValue(dataSave)

            // RESET step di firebase realtime
            stepsRef.setValue(0)

            // UI reset
            panelStats.visibility = View.GONE
            btnStartRun.visibility = View.VISIBLE
        }

        addFirebaseListeners()
        setupUserLocationTracking()

        return view
    }

    private fun addFirebaseListeners() {
        heartRateRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hr = snapshot.getValue(Int::class.java)
                hr?.let {
                    lastHeartRate = it
                    heartRateText.text = "Heart Rate: $it bpm"
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        stepsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val steps = snapshot.getValue(Int::class.java)
                steps?.let {
                    lastSteps = it
                    stepsText.text = "Steps: $it"
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setupUserLocationTracking() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val overlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), map)
            overlay.enableMyLocation()
            overlay.enableFollowLocation()
            map.overlays.add(overlay)

            pathOverlay = Polyline()
            pathOverlay?.outlinePaint?.color = Color.BLUE
            pathOverlay?.outlinePaint?.strokeWidth = 8f
            map.overlays.add(pathOverlay)

            overlay.runOnFirstFix {
                requireActivity().runOnUiThread {
                    val l = overlay.myLocation
                    if (l != null) {
                        val point = GeoPoint(l.latitude, l.longitude)
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

    override fun onResume() { super.onResume(); map.onResume() }
    override fun onPause() { super.onPause(); map.onPause() }
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
