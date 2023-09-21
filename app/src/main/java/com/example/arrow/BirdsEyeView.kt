package com.example.arrow

//import android.preference.PreferenceManager

// Polyline
//Google Location

//Coordinates Object
import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location

//Local File imports


class BirdsEyeView : AppCompatActivity() {
    lateinit var reqPermissionLauncher: ActivityResultLauncher<Array<String>>

    //Layer Button Animation
    private val fromTop: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_top_layer)}
    private val toTop: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_top_layer)}




    val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET,
    )

    var mapView: MapView? = null
    var mapboxMap: MapboxMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_birds_eye_view)

        reqPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            )
            { permissions ->
                val allPermissionsGranted = permissions.all { it.value }
                if (allPermissionsGranted) {
                    Toast.makeText( applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText( applicationContext, "One of Permission is Denied", Toast.LENGTH_SHORT).show()
                }
            }
        reqPermissionLauncher.launch(PERMISSIONS)

        mapView = findViewById(R.id.mapView)
        mapboxMap = mapView?.getMapboxMap()

        onMapReady()
        layerButtonListener()
    }

    private fun onMapReady() {
        mapboxMap?.loadStyleUri("mapbox://styles/mark-asuncion/clmlyake001u801r8f0nhgal3" ,object: Style.OnStyleLoaded { //clm20kpkw00fp01raf9yaar2u
            override fun onStyleLoaded(style: Style) {
                initLocationComponent()
                setupGesturesListener()

                val camera = CameraOptions.Builder()
                    .center(Point.fromLngLat(120.98945,14.60195))
                    .zoom(15.0)
                    .build()
                mapboxMap?.setCamera(camera)

                val southwest = Point.fromLngLat(120.98452,14.59990)
                val northeast = Point.fromLngLat(120.99466,14.60415)

                // set bounds
                val coordBound = CoordinateBounds(southwest,northeast)
                val cmBounds: CameraBoundsOptions.Builder = CameraBoundsOptions.Builder()
                cmBounds.bounds(coordBound)
                mapboxMap?.setBounds(cmBounds.build())

                addAnnotationToMap(southwest.longitude(),southwest.latitude())
                addAnnotationToMap(northeast.longitude(), northeast.latitude())
                addAnnotationToMap(camera.center!!.longitude(),camera.center!!.latitude())
            }
        })
    }

    private fun setupGesturesListener() {
        mapView?.gestures?.addOnMoveListener(onMoveListener)
    }

    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapboxMap?.setCamera(CameraOptions.Builder().bearing(it).build())
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapboxMap?.setCamera(CameraOptions.Builder().center(it).build())
        mapView?.gestures?.focalPoint = mapboxMap?.pixelForCoordinate(it)
    }


    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }
    private fun onCameraTrackingDismissed() {
        Toast.makeText(this, "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show()
        mapView?.location?.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView?.location?.removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView?.gestures?.removeOnMoveListener(onMoveListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.location?.removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView?.location?.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView?.gestures?.removeOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = mapView?.location

        locationComponentPlugin?.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    this@BirdsEyeView,
                    R.drawable.arrowvector,
                ),
                shadowImage = AppCompatResources.getDrawable(
                    this@BirdsEyeView,
                    R.drawable.arrowvector,
                ),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        locationComponentPlugin?.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        locationComponentPlugin?.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
    }

    fun addAnnotationToMap(longtitude: Double, latitude: Double) {
        bitmapFromDrawableRes(
            this,
            R.drawable.arrowvector
        )?.let {
            val annotationApi = mapView?.annotations
            val pointAnnotationManager = annotationApi?.createPointAnnotationManager()
            val pointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(longtitude, latitude))
                .withIconImage(it)

            pointAnnotationManager?.create(pointAnnotationOptions)
        }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    // Converting Drawable To Bitmap
    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    fun askPermissions() {
        reqPermissionLauncher.launch(PERMISSIONS)
    }

    private fun layerButtonListener() {
        var clicked = false
        var streetViewClicked = false
        val focusLocation = findViewById<FloatingActionButton>(R.id.focusLocation)
        val layerButton = findViewById<FloatingActionButton>(R.id.layerButton)
        val streetView = findViewById<FloatingActionButton>(R.id.streetView)
        val arView = findViewById<FloatingActionButton>(R.id.arView)
        val birdsView = findViewById<FloatingActionButton>(R.id.birdsView)

        val streetViewText = findViewById<TextView>(R.id.streetViewText)
        val arViewText = findViewById<TextView>(R.id.arViewText)
        val birdsViewText = findViewById<TextView>(R.id.birdsViewText)

        focusLocation.setOnClickListener {
        }
        layerButton.setOnClickListener{
            setButton(clicked, streetView)
            setButton(clicked, streetViewText)

            setButton(clicked, arView)
            setButton(clicked, arViewText)

            setButton(clicked, birdsView)
            setButton(clicked, birdsViewText)

            clicked = !clicked
        }
        streetView.setOnClickListener{
            streetViewClicked = if(!streetViewClicked){
                explorationView(this, mapView)
                true
            } else {
                polylineAnnotationManager?.deleteAll()
                // polylineAnnotationManager?.update("polyline_$i", PropertyFactory.lineOpacity(opacity))
                false
            }
        }
        arView.setOnClickListener {
            Toast.makeText(this, "AR View", Toast.LENGTH_SHORT).show()
        }
        birdsView.setOnClickListener {

        }
    }

    private fun setButton(clicked: Boolean, FAB: View){
        if (!clicked){
            FAB.visibility = View.VISIBLE
            FAB.startAnimation(fromTop)
            FAB.isClickable = true

        } else{
            FAB.visibility = View.INVISIBLE
            FAB.startAnimation(toTop)
            FAB.isClickable = false
        }
    }
}