package com.example.arrow
import com.example.arrow.utils.*

import android.Manifest
import android.R.attr.button
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.health.connect.datatypes.units.Length
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.core.constants.Constants.PRECISION_6
import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.attribution.attribution
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.logo.logo
import com.mapbox.maps.plugin.scalebar.scalebar
import com.mapbox.navigation.base.options.DeviceProfile
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.base.options.RerouteOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import org.checkerframework.common.returnsreceiver.qual.This
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
    var mapboxNavigationApp: MapboxNavigationApp? = null

    var lbMapLayers: MapLayer? = null

    @SuppressLint("ResourceAsColor", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_birds_eye_view)

        // SET STATUS BAR TO TRANSPARENT
//        window.statusBarColor = resources.getColor(android.R.color.transparent)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // FLOOR SWITCHING START
        // FLOOR ID'S
        val tvGroundFloor = findViewById<TextView>(R.id.groundFloor)
        val tvSecondFloor = findViewById<TextView>(R.id.secondFloor)
        val tvThridFloor = findViewById<TextView>(R.id.thirdFloor)
        val tvFourthFloor = findViewById<TextView>(R.id.fourthFloor)
        val tvFifthFloor = findViewById<TextView>(R.id.fifthFloor)
        val tvSixthFloor = findViewById<TextView>(R.id.sixthFloor)
        val tvSeventhFloor = findViewById<TextView>(R.id.seventhFloor)
        val tvEightFloor = findViewById<TextView>(R.id.eightFloor)
        val tvNinethFloor = findViewById<TextView>(R.id.ninethFloor)
        val roofDeck = findViewById<TextView>(R.id.roofDeck)
        val scrollView = findViewById<ScrollView>(R.id.myScroll)
        // SCROLL TO BOTTOM BY DEFAULT
        scrollView.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }

        val allTextViews = listOf(tvGroundFloor, tvSecondFloor, tvThridFloor, tvFourthFloor, tvFifthFloor, tvSixthFloor, tvSeventhFloor, tvEightFloor, tvNinethFloor, roofDeck)

        for (i in allTextViews.indices) {
            val textView = allTextViews[i]
            textView.setOnClickListener { view ->
                // Reset all TextViews to their original state
                for (tv in allTextViews) {
                    tv.setTextColor(ContextCompat.getColor(this, R.color.black))
                    tv.setTypeface(Typeface.DEFAULT)
                    val resetScaleXAnimator = ObjectAnimator.ofFloat(tv, "scaleX", 1.0f)
                    val resetScaleYAnimator = ObjectAnimator.ofFloat(tv, "scaleY", 1.0f)
                    resetScaleXAnimator.duration = 200
                    resetScaleYAnimator.duration = 200

                  // Create an AnimatorSet to run both animations simultaneously
                    val resetAnimatorSet = AnimatorSet()
                    resetAnimatorSet.playTogether(resetScaleXAnimator, resetScaleYAnimator)
                    resetAnimatorSet.start()
                }

                // Set the clicked TextView to be red, bold, and larger
                view?.let {
                    val clickedTextView = it as TextView
                    clickedTextView.setTextColor(ContextCompat.getColor(this, R.color.red))
                    clickedTextView.setTypeface(Typeface.DEFAULT_BOLD)
                    val scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 1.5f)
                    val scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 1.5f)
                    scaleXAnimator.duration = 200
                    scaleYAnimator.duration = 200

                    // Create an AnimatorSet to run both animations simultaneously
                    val animatorSet = AnimatorSet()
                    animatorSet.playTogether(scaleXAnimator, scaleYAnimator)
                    animatorSet.start()
                }
                lbMapLayers?.setCurrFloor(i+1) { ctx, floor ->
                    val key = if (floor == 3) "lb-rooms-3f-label" else "lb-rooms-upw-label"
                    ctx.symbolLayers[key]?.textField(
                        Expression.match(
                            Expression.get("name"),
                            Expression.array(
                                Expression.literal("Faculty"),
                                Expression.literal("Dean's Office"),
                                Expression.literal("Library")
                            ),
                            Expression.get("name"),
                            Expression.concat(
                                Expression.literal("" + floor),
                                Expression.get("name")
                            )
                        )
                    )
                }
            }
        }
        // SET GROUND FLOOR
        tvGroundFloor.performClick()
        // FLOOR SWITCHING END

        // NAVIGATION START
        val cvNavExplore = findViewById<CardView>(R.id.cv_navigation_explore)
        val cvNavDirection= findViewById<CardView>(R.id.cv_navigation_direction)
        val cvNavProfile = findViewById<CardView>(R.id.cv_navigation_profile)

        animationNavigation(cvNavExplore)
        animationNavigation(cvNavDirection)
        animationNavigation(cvNavProfile)
        // NAVIGATION END

        // DRAGGABLE SHEET START
        val bottomSheet = findViewById<FrameLayout>(R.id.sheet)

        val etSearchBar = findViewById<EditText>(R.id.etSearchBar)
        BottomSheetBehavior.from(bottomSheet).apply {
            peekHeight = 440
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }


        etSearchBar.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                BottomSheetBehavior.from(bottomSheet).apply {
                    this.state = BottomSheetBehavior.STATE_EXPANDED
                }
            } else {
                BottomSheetBehavior.from(bottomSheet).apply {
                    this.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        }
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        //Toast.makeText(applicationContext, "collapse", Toast.LENGTH_SHORT).show()
                        etSearchBar.clearFocus()
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        //Toast.makeText(applicationContext, "Expanded", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // DON'T REMOVE
                // This method is called when the bottom sheet is being dragged or settled
                // You can use it to perform actions based on the slide offset if needed
            }
        })
        // DRAGGABLE SHEET END

        reqPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            )
            { permissions ->
                val allPermissionsGranted = permissions.all { it.value }
                if (allPermissionsGranted) {
                    Log.w( "PERMISSIONS", "Permission Granted")
                }
                else {
                    Log.w( "PERMISSIONS", "One of Permission is Denied")
                }
            }
        reqPermissionLauncher.launch(PERMISSIONS)

        mapView = findViewById(R.id.mapView)
        mapboxMap = mapView?.getMapboxMap()

        onMapReady()
    }

    private fun removeExtraUI() {
        mapView?.logo?.updateSettings {
            enabled = false
        }
        mapView?.attribution?.updateSettings {
            enabled = false
        }
        mapView?.scalebar?.updateSettings {
            enabled = false
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestDirection() {
        mapboxNavigationApp?.current()?.startTripSession()
        mapboxNavigationApp?.current()?.registerLocationObserver(locationObserver)
    }

    private fun onMapReady() {
        mapboxMap?.loadStyleUri("mapbox://styles/mark-asuncion/clmvnqnd0000101pyh95u4s34" ,object: Style.OnStyleLoaded {
            override fun onStyleLoaded(style: Style) {
                initLocationComponent()
                setupGesturesListener()

                val southwest = Point.fromLngLat(120.98452,14.59990)
                val northeast = Point.fromLngLat(120.99466,14.60415)

                // set bounds
                val coordBound = CoordinateBounds(southwest,northeast)
                val cmBounds: CameraBoundsOptions.Builder = CameraBoundsOptions.Builder()
                cmBounds.bounds(coordBound)
                mapboxMap?.setBounds(cmBounds.build())

                val camera = CameraOptions.Builder()
                    .center(Point.fromLngLat(120.98945,14.60195))
                    .zoom(15.0)
                    .build()
                mapboxMap?.setCamera(camera)

                addAnnotationToMap(southwest.longitude(),southwest.latitude())
                addAnnotationToMap(northeast.longitude(), northeast.latitude())
                addAnnotationToMap(camera.center!!.longitude(),camera.center!!.latitude())

                // init MapLayers
                lbMapLayers = MapLayer(
                    "lb-building",
                    mapboxMap,
                    LB_BUILDING_LAYER_IDS,
                    LB_BUILDING_SYMBOL_IDS,
                    LB_BUILDING_LINE_IDS,
                    7
                )
                // set floors
                 setLBFloors(lbMapLayers!!)
                lbMapLayers?.setCurrFloor(1)
            }
        })
    }

    // NAVIGATION ANIMATION START
    private fun animationNavigation(cardView: CardView) {
        cardView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    animateBackgroundColorChange(cardView, resources.getColor(R.color.white), resources.getColor(R.color.lightGrey))
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    animateBackgroundColorChange(cardView, resources.getColor(R.color.lightGrey) ,resources.getColor(R.color.white))
                }
            }
            false
        }
    }
    private fun animateBackgroundColorChange(view: CardView, fromColor: Int, toColor: Int) {
        val animator = ObjectAnimator.ofObject(
            view,
            "cardBackgroundColor",
            ArgbEvaluator(),
            fromColor,
            toColor
        )
        animator.duration = 500 // Adjust the duration as needed
        animator.interpolator = AccelerateDecelerateInterpolator() // Optional
        animator.start()
    }
    // NAVIGATION ANIMATION START

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

    private fun addAnnotationToMap(longtitude: Double, latitude: Double) {
        bitmapFromDrawableRes(
            this@BirdsEyeView,
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
}
