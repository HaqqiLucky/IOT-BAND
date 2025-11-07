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

        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )

        database = FirebaseDatabase.getInstance("https://smartbandforteens-default-rtdb.asia-southeast1.firebasedatabase.app/")

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        heartRateRef = database.getReference("data_iot").child("device_001").child("heart_rate")
        stepsRef = database.getReference("data_iot").child("device_001").child("steps")

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

        btnStartRun.setOnClickListener {
            btnStartRun.visibility = View.GONE
            panelStats.visibility = View.VISIBLE
            joggingActive = true
        }

        val btnStopRun = view.findViewById<Button>(R.id.btnStopRun)
        btnStopRun.setOnClickListener {
            joggingActive = false

            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val ref = database.getReference("history").child(uid).push()

            val heart = heartRateText.text.toString()
                .replace("Heart Rate: ","")
                .replace(" bpm","")
                .toIntOrNull() ?: 0

            val step = stepsText.text.toString()
                .replace("Steps: ","")
                .toIntOrNull() ?: 0

            val data = mapOf(
                "timestamp" to System.currentTimeMillis(),
                "heart_rate" to heart,
                "steps" to step,
                "distance_km" to totalDistance,
                "rpe_status" to "pending"
            )


            ref.setValue(data)

            Toast.makeText(requireContext(),"Aktivitas disimpan!",Toast.LENGTH_SHORT).show()

            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, HistoryFragment())
                    .addToBackStack(null)
                    .commit()
            },400)
        }


            loadUserHRLimit(uid)
        addFirebaseListeners()
        setupUserLocationTracking()

        return view
    }

    private fun loadUserHRLimit(uid:String) {
        val prefRef = database.getReference("users_personal_preferences")
            .child(uid).child("hasilRulebase").child("HrTarget")

        prefRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val range = snapshot.getValue(String::class.java) ?: return
                val parts = range.split("..")
                if(parts.size == 2){
                    hrMinLimit = parts[0].toDouble()
                    hrMaxLimit = parts[1].toDouble()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun addFirebaseListeners() {

        heartRateRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val heartRate = snapshot.getValue(Int::class.java)
                heartRate?.let {
                    heartRateText.text = "Heart Rate: $it bpm"

                    if(joggingActive && hrMaxLimit > 0 && it > hrMaxLimit){
                        val now = System.currentTimeMillis()
                        if(now - lastWarningTime > 7000){ // 7s anti spam
                            lastWarningTime = now
                            Toast.makeText(requireContext(), "⚠️ Heart Rate kamu melewati batas aman! Turunkan tempo sedikit.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("JoggingFirebase", "heart error ${error.message}")
            }
        })

        stepsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val steps = snapshot.getValue(Int::class.java)
                steps?.let { stepsText.text = "Steps: $it" }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("JoggingFirebase", "steps error ${error.message}")
            }
        })
    }

    private fun setupUserLocationTracking() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
