package com.example.gr3

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment
import com.arcgismaps.data.Feature
import com.arcgismaps.data.ServiceFeatureTable
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.BasemapStyle
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.layers.FeatureLayer
import com.arcgismaps.mapping.view.MapView
import com.arcgismaps.mapping.view.ScreenCoordinate
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MapActivity : AppCompatActivity() {
    val serviceFeatureTable = ServiceFeatureTable("https://services9.arcgis.com/UVxdrlZq3S3gqt7w/arcgis/rest/services/Messwerte/FeatureServer")
    val featureLayer = FeatureLayer.createWithFeatureTable(serviceFeatureTable)
    lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_map)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ArcGISEnvironment.apiKey = ApiKey.create(getString(R.string.arcgis_api_key))
        mapView = findViewById<MapView>(R.id.mapView)
        lifecycle.addObserver(mapView)

        val map = ArcGISMap(BasemapStyle.ArcGISLightGray)
        map.operationalLayers.add(featureLayer)
        mapView.map = map

        mapView.setViewpoint(Viewpoint(53.143, 8.2, 5000.0))

        requestPermissions()
        val locationDisplay = mapView.locationDisplay
        lifecycleScope.launch {
            locationDisplay.dataSource.start()
            mapView.onSingleTapConfirmed.collect { tapEvent ->
                val screenCoordinate = tapEvent.screenCoordinate
                val location = mapView.screenToLocation(screenCoordinate)
                getSelectedFeatureLayer(screenCoordinate)
            }
        }
    }

    private suspend fun getSelectedFeatureLayer(screenCoordinate: ScreenCoordinate) {
        // clear the previous selection
        featureLayer.clearSelection()
        // set a tolerance for accuracy of returned selections from point tapped
        val tolerance = 25.0
        // create a IdentifyLayerResult using the screen coordinate
        val identifyLayerResult =
            mapView.identifyLayer(featureLayer, screenCoordinate, tolerance, false, 1)
        // handle the result's onSuccess and onFailure
        identifyLayerResult.apply {
            onSuccess { identifyLayerResult ->
                // get the elements in the selection that are features
                val features = identifyLayerResult.geoElements.filterIsInstance<Feature>()
                // add the features to the current feature layer selection
                featureLayer.selectFeatures(features)
                if (features.size > 0) {
                }
                Snackbar.make(mapView, "${features.size} features selected", Snackbar.LENGTH_SHORT)
                    .show()
            }
            onFailure {
                val errorMessage = "Select feature failed: " + it.message
                Snackbar.make(mapView, errorMessage, Snackbar.LENGTH_SHORT).show()
            }
        }
    }
    private fun requestPermissions() {
        // coarse location permission
        val permissionCheckCoarseLocation =
            ContextCompat.checkSelfPermission(this@MapActivity, ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
        // fine location permission
        val permissionCheckFineLocation =
            ContextCompat.checkSelfPermission(this@MapActivity, ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED

        // if permissions are not already granted, request permission from the user
        if (!(permissionCheckCoarseLocation && permissionCheckFineLocation)) {
            ActivityCompat.requestPermissions(
                this@MapActivity,
                arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION),
                2
            )
        }
    }
}