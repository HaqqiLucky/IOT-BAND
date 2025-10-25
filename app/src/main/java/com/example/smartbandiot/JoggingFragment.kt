package com.example.smartbandiot

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.smartbandiot.databinding.FragmentJoggingBinding
import com.google.firebase.database.*
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class JoggingFragment : Fragment() {

    private var _binding: FragmentJoggingBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: MapView
    private lateinit var controller: IMapController

    // 🔥 Tambahkan Firebase Reference
    private lateinit var database: FirebaseDatabase
    private lateinit var heartRateRef: DatabaseReference
    private lateinit var stepsRef: DatabaseReference
    private lateinit var timestampRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoggingBinding.inflate(inflater, container, false)
        val view = binding.root

        // ✅ Setup konfigurasi OSM
        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )

        map = binding.mapView
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.setBuiltInZoomControls(true)

        controller = map.controller
        controller.setZoom(17.0)

        // ✅ Setup Firebase
        database = FirebaseDatabase.getInstance("https://smartbandforteens-default-rtdb.firebaseio.com/")
        heartRateRef = database.getReference("heartRate")
        stepsRef = database.getReference("steps")
        timestampRef = database.getReference("timestamp")

        // 🔁 Tambahkan listener Firebase
        addFirebaseListeners()

        // ✅ Lokasi pengguna
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), map)
            locationOverlay.enableMyLocation()
            map.overlays.add(locationOverlay)

            locationOverlay.runOnFirstFix {
                requireActivity().runOnUiThread {
                    val myLoc = locationOverlay.myLocation
                    if (myLoc != null) {
                        controller.setCenter(GeoPoint(myLoc.latitude, myLoc.longitude))
                    }
                }
            }
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }

        return view
    }

    // 🔥 Fungsi ambil data Firebase real-time
    private fun addFirebaseListeners() {
        // Heart Rate
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

        // Steps
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

        // Timestamp
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
