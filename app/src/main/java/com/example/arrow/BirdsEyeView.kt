package com.example.arrow

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.util.Log
//import android.preference.PreferenceManager
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.DrawableRes

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
// LineLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.extension.style.utils.ColorUtils
import com.mapbox.maps.plugin.gestures.addOnMapClickListener

class BirdsEyeView : AppCompatActivity() {
    lateinit var reqPermissionLauncher: ActivityResultLauncher<Array<String>>

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
    }
    private fun onMapReady() {
        mapboxMap?.loadStyleUri("mapbox://styles/mark-asuncion/clluwyesj006501rabdba3vi7" ,object: Style.OnStyleLoaded {
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

                explorationView(style)
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

    fun explorationView(style: Style){
        // Line Coordinates
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

        val lineString = LineString.fromLngLats(coordinates)
        val feature = Feature.fromGeometry(lineString)
        val featureCollection = FeatureCollection.fromFeature(feature)
        val geoJson = featureCollection.toJson()
        val geoJsonSourceId = "explorationViewLinesSource"
        val geoJsonSource = GeoJsonSource.Builder(geoJsonSourceId)
            .data(geoJson)
            .build()

        style.addSource(geoJsonSource)

        /*Line geoJson Log Check
        if (style.getSource(geoJsonSourceId) != null) {
            Log.d("LineLayer", geoJson)
        } else {
            Log.e("LineLayer", "GeoJson source not added.")
        }*/

        style.addLayer(
            LineLayer("explorationViewLinesLayer", "explorationViewLinesSource").apply {
                    lineCap(LineCap.ROUND)
                    lineJoin(LineJoin.ROUND)
                    lineOpacity(0.7)
                    lineWidth(3.0)
                    lineColor(ContextCompat.getColor(this@BirdsEyeView, R.color.blue))
            }
        )
        val layersIdToQuery = listOf("explorationViewLinesLayer")

        mapboxMap?.addOnMapClickListener { point ->
            val screenPoint = mapboxMap.getProjection().toScreenLocation(point)
            val features = mapboxMap.queryRenderedFeatures(screenPoint, layersIdToQuery)

            if (!features.isNullOrEmpty()) {
                // Handle the clicked feature(s) here
                for (feature in features) {
                    // You can access properties of the clicked feature like this:
                    val title = feature.getStringProperty("title")
                    // Do something with the title or other properties
                    Toast.makeText(this, "You selected $title", Toast.LENGTH_SHORT).show()
                }
            }
        }

        /*LineLayer Log Check
        if (style.getLayer("userDrawnLinesLayer") != null) {
            Log.d("LineLayer", LineLayer.toString())
        } else {
            Log.e("LineLayer", "LineLayer not added.")
        }*/

        /* Clickable Symbol Layer deins gumagana
        for (i in 0 until coordinates.size - 1) {
            val startCoord = coordinates[i]
            val endCoord = coordinates[i + 1]

            // Unique symbol ID for each segment
            val symbolId = "lineSegment_$i"


            val symbolFeature = Feature.fromGeometry(LineString.fromLngLats(listOf(startCoord, endCoord)))
            val symbolFeatureCollection = FeatureCollection.fromFeature(symbolFeature)
            val symbolGeoJson = symbolFeatureCollection.toJson()
            val symbolGeoJsonSourceId = "explorationViewSymbolSource"
            val symbolGeoJsonSource = GeoJsonSource.Builder(symbolGeoJsonSourceId)
                .data(symbolGeoJson)
                .build()

            style.addSource(symbolGeoJsonSource)

            style.addLayer(
                SymbolLayer(symbolId, "explorationViewSymbolSource").apply {
                    iconImage("invisible-icon")
                    iconOpacity(0.0)
                    iconAllowOverlap(true)
                }
            )
            if (style.getLayer("explorationViewSymbolSource") != null) {
                Log.d("SymbolLayer", SymbolLayer.toString())
            } else {
                Log.e("SymbolLayer", "LineLayer not added.")
            }

            //When a line segment is clicked
            mapView.addOnMapClickListener { point ->
                val screenPoint = mapboxMap.getProjection().toScreenLocation(point)
                val features = mapboxMap.queryRenderedFeatures(screenPoint,"explorationViewSymbolSource")

                if (features.isNotEmpty()) {
                    val clickedFeature = features[0]
                    val segmentId = clickedFeature.getStringProperty("segmentId")
                }

                true
            }

        }*/
    }
}



/* Annotations
    fun addCircleAnnotationToMap(point: Point, locationColor: String , locationName: String) {
        val annotationApi = mapView?.annotations
        val circleAnnotationManager =
            mapView?.let { annotationApi?.createCircleAnnotationManager(it) }
        val circleAnnotationOptions: CircleAnnotationOptions = CircleAnnotationOptions()
            .withPoint(point)
            .withCircleRadius(8.0)
            .withCircleColor("#ee4e8b")
            .withCircleStrokeWidth(  2.0)
            .withCircleStrokeColor(locationColor)
            //.withTextField(locationName)

        circleAnnotationManager?.create(circleAnnotationOptions)
    }
fun annotations(){

    val gastambideGate = Point.fromLngLat(120.9906056915854,14.60198483117427)
    val gastambideParking = Point.fromLngLat(120.99022946810736,14.601813550412315)
    val podcitBldg = Point.fromLngLat(120.99029863211052, 14.60169542025284)

    addAnnotationToMap(gastambideParking.longitude(), gastambideParking.latitude())
    addAnnotationToMap(podcitBldg.longitude(), podcitBldg.latitude())
    addCircleAnnotationToMap(gastambideGate, )
}
data class CircleAnnotationOptions(
    val latLng: Point, // The position of the symbol annotation
    val textAnchor: String, // The text anchor for the symbol ("bottom", "top", "left", "right", "center", etc.)
    val textField: String, // The text content of the symbol
    val textSize: Float, // The text size
    val textColor: String // The text color
)

data class MarkerOptions(
    val position: Point, // The position of the marker
    val icon: Drawable? = null // The icon for the marker (default is null)
)*/