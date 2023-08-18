package com.example.arrow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.preference.PreferenceManager
import androidx.activity.result.ActivityResultLauncher

import org.osmdroid.config.Configuration.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

class Map : AppCompatActivity() {
    lateinit var reqPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var map : MapView
//    private lateinit var mapController : MapController
    val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )
    var permissionOK: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        reqPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            )
            { permissions ->
                val allPermissionsGranted = permissions.all { it.value }
                if (allPermissionsGranted) {
                    Toast.makeText( applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
                    permissionOK = true
                }
                else {
                    Toast.makeText( applicationContext, "One of Permission is Denied", Toast.LENGTH_SHORT).show()
                    permissionOK = false
                }
            }
        askPermissions()

        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        map = findViewById<MapView>(R.id.map)

        // default view
        val mapController = map.controller
        mapController.setZoom(20)
        mapController.setCenter(GeoPoint(14.60195,120.98945))

        // add rotation capabilities
        val rotationGestureOverlay = RotationGestureOverlay(map)
        rotationGestureOverlay.isEnabled
        map.setMultiTouchControls(true)
        map.overlays.add(rotationGestureOverlay)
    }

     override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    fun askPermissions() {
        // TODO: do a check if permission is denied

        reqPermissionLauncher.launch(permissions)
    }
}