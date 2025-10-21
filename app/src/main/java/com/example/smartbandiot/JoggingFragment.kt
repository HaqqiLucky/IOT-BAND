package com.example.smartbandiot

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.smartbandiot.databinding.FragmentJoggingBinding
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.OnMapReadyCallback
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.Point
import org.maplibre.android.style.layers.PropertyFactory.*
import org.maplibre.android.style.layers.PropertyFactory.iconImage

class JoggingFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentJoggingBinding? = null
    private val binding get() = _binding!!

    private lateinit var mapView: MapView
    private var mapLibreMap: MapLibreMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoggingBinding.inflate(inflater, container, false)
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        return binding.root
    }

    override fun onMapReady(maplibreMap: MapLibreMap) {
        mapLibreMap = maplibreMap

        try {
            maplibreMap.setStyle("https://demotiles.maplibre.org/style.json") { style ->
                val location = LatLng(-6.200000, 106.816666) // Jakarta

                // Tambahkan ikon marker ke dalam style
                style.addImage(
                    "marker-icon-id",
                    BitmapFactory.decodeResource(resources, R.drawable.maplibre_marker_icon_default)
                )

                // Tambahkan marker dengan GeoJSON source
                val geoJsonSource = GeoJsonSource(
                    "marker-source",
                    Feature.fromGeometry(Point.fromLngLat(location.longitude, location.latitude))
                )
                style.addSource(geoJsonSource)

                // Buat layer untuk marker
                val symbolLayer = SymbolLayer("marker-layer", "marker-source").withProperties(
                    iconImage("marker-icon-id"),
                    iconSize(1.2f),
                    iconAllowOverlap(true),
                    iconIgnorePlacement(true)
                )
                style.addLayer(symbolLayer)

                // Posisikan kamera ke Jakarta
                maplibreMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(location, 12.0)
                )

                Log.d("JoggingFragment", "MapLibre berhasil dimuat dengan marker.")
            }

        } catch (e: Exception) {
            Log.e("JoggingFragment", "Error initializing MapLibre: ${e.message}", e)
        }
    }

    // Lifecycle MapView
    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
        _binding = null
    }
}
