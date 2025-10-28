package com.example.smartbandiot

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.smartbandiot.databinding.FragmentJoggingBinding
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
    private lateinit var timestampRef: DatabaseReference

    private var pathOverlay: Polyline? = null
    private var lastLocation: GeoPoint? = null
    private var totalDistance = 0.0
    private var joggingActive = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoggingBinding.inflate(inflater, container, false)
        val view = binding.root

        // ✅ Konfigurasi OSM
        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )

        map = binding.mapView
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.setBuiltInZoomControls(false) // kita bikin tombol custom sendiri
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

        controller = map.controller
        controller.setZoom(17.0)

        // ✅ Firebase setup
        database =
            FirebaseDatabase.getInstance("https://smartbandforteens-default-rtdb.firebaseio.com/")
        heartRateRef = database.getReference("heartRate")
        stepsRef = database.getReference("steps")
        timestampRef = database.getReference("timestamp")

        addFirebaseListeners()
        setupUserLocationTracking()
        addCustomZoomButtons(view)

        return view
    }

    // ------------------ 🔥 Firebase Listener ------------------
    private fun addFirebaseListeners() {
        heartRateRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val heartRate = snapshot.getValue(Int::class.java)
                heartRate?.let {
                    Log.d("JoggingFirebase", "❤️ Heart Rate: $it bpm")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("JoggingFirebase", "Error reading heartRate: ${error.message}")
            }
        })

        stepsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val steps = snapshot.getValue(Int::class.java)
                steps?.let {
                    Log.d("JoggingFirebase", "👣 Steps: $it")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("JoggingFirebase", "Error reading steps: ${error.message}")
            }
        })

        timestampRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val time = snapshot.getValue(String::class.java)
                time?.let {
                    Log.d("JoggingFirebase", "⏱ Timestamp: $it")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("JoggingFirebase", "Error reading timestamp: ${error.message}")
            }
        })
    }

    // ------------------ 🗺️ Lokasi & Tracking ------------------
    private fun setupUserLocationTracking() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), map)
            locationOverlay.enableMyLocation()
            locationOverlay.enableFollowLocation()
            map.overlays.add(locationOverlay)

            pathOverlay = Polyline()
            pathOverlay?.outlinePaint?.color = Color.BLUE
            pathOverlay?.outlinePaint?.strokeWidth = 8f
            map.overlays.add(pathOverlay)

            locationOverlay.runOnFirstFix {
                requireActivity().runOnUiThread {
                    val myLoc = locationOverlay.myLocation
                    if (myLoc != null) {
                        val point = GeoPoint(myLoc.latitude, myLoc.longitude)
                        controller.setCenter(point)
                        lastLocation = point
                        pathOverlay?.addPoint(point)
                    }
                }
            }

            locationOverlay.myLocationProvider.startLocationProvider { location, _ ->
                if (location != null && joggingActive) {
                    requireActivity().runOnUiThread {
                        val newPoint = GeoPoint(location.latitude, location.longitude)
                        if (lastLocation != null) {
                            val distance = newPoint.distanceToAsDouble(lastLocation)
                            totalDistance += distance / 1000.0
                            Log.d("JoggingTrack", "Total distance: %.3f km".format(totalDistance))
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

    // ------------------ 🔍 Tombol Zoom Custom ------------------
    private fun addCustomZoomButtons(rootView: View) {
        val context = requireContext()

        val zoomIn = ImageButton(context).apply {
            setImageResource(android.R.drawable.ic_input_add)
            setBackgroundResource(android.R.drawable.btn_default)
        }

        val zoomOut = ImageButton(context).apply {
            setImageResource(android.R.drawable.ic_input_delete)
            setBackgroundResource(android.R.drawable.btn_default)
        }

        zoomIn.setOnClickListener { map.controller.zoomIn() }
        zoomOut.setOnClickListener { map.controller.zoomOut() }

        val layout = FrameLayout(context)
        layout.addView(zoomIn)
        layout.addView(zoomOut)

        val paramsIn = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.END or Gravity.BOTTOM
            setMargins(0, 0, 30, 200) // kanan, atas, bawah
        }

        val paramsOut = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.END or Gravity.BOTTOM
            setMargins(0, 0, 30, 120)
        }

        layout.removeAllViews()
        (rootView as FrameLayout).addView(zoomIn, paramsIn)
        (rootView as FrameLayout).addView(zoomOut, paramsOut)
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
