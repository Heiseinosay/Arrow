package com.example.arrow

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
//import android.preference.PreferenceManager
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.DrawableRes

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.MapboxMap
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
// Polyline
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.OnPolylineAnnotationClickListener


class BirdsEyeView : AppCompatActivity() {
    lateinit var reqPermissionLauncher: ActivityResultLauncher<Array<String>>

    //Layer Button Animation
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_bottom_layer)}
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_layer)}
    private var clicked = false

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
        val layerButton = findViewById<FloatingActionButton>(R.id.layerButton)
        val streetView = findViewById<FloatingActionButton>(R.id.streetView)

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
        layerButtonListener(layerButton, streetView)
    }

    private fun onMapReady() {
        mapboxMap?.loadStyleUri("mapbox://styles/mark-asuncion/clluwyesj006501rabdba3vi7" ,object: Style.OnStyleLoaded { //clm20kpkw00fp01raf9yaar2u
            override fun onStyleLoaded(style: Style) {
                initLocationComponent()
                setupGesturesListener()
                explorationView()

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
            val pointAnnotationManager = annotationApi?.createPointAnnotationManager(mapView!!)
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

    fun explorationView(){
        // Line Coordinates From Gastambide to Lualhati
        val coordinates = listOf(
            Point.fromLngLat(120.99062060085, 14.6019655071744),
            Point.fromLngLat(120.990585732133, 14.6019525292989),
            Point.fromLngLat(120.990550192865, 14.6019408492104),
            Point.fromLngLat(120.990520688567, 14.6020076852644),
            Point.fromLngLat(120.990522029671, 14.6020070363707),
            Point.fromLngLat(120.99050660697, 14.6020401299434),
            Point.fromLngLat(120.990470397149, 14.6020479166657),
            Point.fromLngLat(120.99043485788, 14.6020414277305),
            Point.fromLngLat(120.990395965851, 14.6020362365822),
            Point.fromLngLat(120.990361217374, 14.6020284339487),
            Point.fromLngLat(120.990322995897, 14.602018700545),
            Point.fromLngLat(120.990285444972, 14.6020083182472),
            Point.fromLngLat(120.99025191736, 14.6019998826299),
            Point.fromLngLat(120.990241188524, 14.6020284339487),
            Point.fromLngLat(120.990223754166, 14.6020647719855),
            Point.fromLngLat(120.990209002017, 14.6020972166562),
            Point.fromLngLat(120.990198943733, 14.6021329057884),
            Point.fromLngLat(120.990160722256, 14.6021179812429),
            Point.fromLngLat(120.990123841883, 14.6021056522698),
            Point.fromLngLat(120.990088302614, 14.6020946210827),
            Point.fromLngLat(120.990056786659, 14.6020816432149),
            Point.fromLngLat(120.9900239296, 14.6020680164528),
            Point.fromLngLat(120.989985708122, 14.6020517941159),
            Point.fromLngLat(120.989951509958, 14.6020595808378),
            Point.fromLngLat(120.98994011057, 14.6020887810423),
            Point.fromLngLat(120.989924017317, 14.6021238212826),
            Point.fromLngLat(120.989910178848, 14.6021566472101),
            Point.fromLngLat(120.989897438355, 14.6021897407603),
            Point.fromLngLat(120.989882015653, 14.6022228343062),
            Point.fromLngLat(120.989867934056, 14.6022552789536),
            Point.fromLngLat(120.989854523011, 14.602288372489),
            Point.fromLngLat(120.989841782518, 14.6023234126988),
            Point.fromLngLat(120.989827700921, 14.6023565062239),
            Point.fromLngLat(120.989814960428, 14.602390248638),
            Point.fromLngLat(120.989800878831, 14.602423342153),
            Point.fromLngLat(120.989786126682, 14.6024570845554),
            Point.fromLngLat(120.989768692323, 14.6024882313838),
        )

        val polylineAnnotationManager = mapView?.annotations?.createPolylineAnnotationManager()
        polylineAnnotationManager?.lineCap = (LineCap.ROUND)

        val blue = ContextCompat.getColor(this@BirdsEyeView, R.color.blue)

        val segmentOptionsList = mutableListOf<PolylineAnnotationOptions>()
        val coordinateSize = coordinates.size - 1

        for (i in 0 until coordinateSize){
            val polylineAnnotationOptions: PolylineAnnotationOptions = PolylineAnnotationOptions()
                .withPoints(listOf(coordinates[i], coordinates[i + 1]))
                .withLineJoin(LineJoin.ROUND)
                .withLineColor(blue)
                .withLineWidth(5.0)
            segmentOptionsList.add(polylineAnnotationOptions)
        }

        polylineAnnotationManager?.create(segmentOptionsList)

        //Polyline Click Listener
        val clickListener = OnPolylineAnnotationClickListener { polylineId ->
            //Handle the 360 view here. Make the id of image == to the ID of
            Toast.makeText(this, "Polyline with ID $polylineId clicked", Toast.LENGTH_SHORT).show()
            true
        }
        polylineAnnotationManager?.addClickListener(clickListener)
    }

    //Layer Floating Button
    private fun layerButtonListener(layerButton: FloatingActionButton, streetView: FloatingActionButton) {
        layerButton.setOnClickListener{
            onAddButtonClicked(layerButton, streetView)
        }
        streetView.setOnClickListener{

        }
    }

    private fun onAddButtonClicked(layerButton: FloatingActionButton, streetView: FloatingActionButton){
        setButtonVisibility(clicked, layerButton, streetView)
        setButtonAnimation(clicked, streetView)
        setClickableButton(clicked, streetView)
        clicked = !clicked
    }

    private fun setButtonVisibility(clicked: Boolean, layerButton: FloatingActionButton, streetView: FloatingActionButton){
        if (!clicked){
            streetView.visibility = View.VISIBLE
            //layerButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.medGrey))
        } else{
            streetView.visibility = View.INVISIBLE
            //layerButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.lightGrey))
        }
    }

    private fun setButtonAnimation(clicked: Boolean, streetView: FloatingActionButton){
        if (!clicked){
            streetView.startAnimation(fromBottom)
        } else{
            streetView.startAnimation(toBottom)

        }
    }

    private fun setClickableButton(clicked: Boolean, streetView: FloatingActionButton){
        if (!clicked){
            streetView.isClickable = true
        } else{
            streetView.isClickable = false
        }
    }


}