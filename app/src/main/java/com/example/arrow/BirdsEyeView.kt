package com.example.arrow

import com.example.arrow.utils.*
//import android.preference.PreferenceManager

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.sqlitedatabase.DataBaseHandler
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo


class BirdsEyeView : AppCompatActivity(), FragmentToActivitySearch  {
    lateinit var reqPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var viewModel: ViewModel
    private lateinit var maleImage: ImageView
    private lateinit var femaleImage: ImageView
    private lateinit var db: FirebaseFirestore
    private var userUID: String? = null
    private lateinit var userRef: DocumentReference
    private lateinit var bottomSheet: FrameLayout
    lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private lateinit var fragmentManager: FragmentManager
    private var startValue:Float = 0.0f
    private var searchValue:String = ""

    // FRAGMENT VALUE PASS
    private val fragment: ExploreFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.fragment_explore) as ExploreFragment
    }
    override fun onValuePassed(value: String) {
        searchValue = value
        searchAnimate(searchValue)
    }

    val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET,
    )
    var mapView: MapView? = null
    var mapboxMap: MapboxMap? = null

    var lbMapLayers: MapLayer? = null

    @SuppressLint("ResourceAsColor", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_birds_eye_view)



        // SET STATUS BAR TO TRANSPARENT
        // window.statusBarColor = resources.getColor(android.R.color.transparent)
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


        val btnTest = findViewById<Button>(R.id.btnTest)

        btnTest.setOnClickListener {
            // PANORAMA TEST HERE
            Toast.makeText(this, "Panorama test", Toast.LENGTH_SHORT).show()
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

        // Getting document snapshot from User Collection
        db = Firebase.firestore
        userUID = FirebaseAuth.getInstance().currentUser?.uid

        if(userUID != null){
            userRef = db.collection("users").document(userUID!!)
            userRef.get().addOnSuccessListener { documentSnapshot->
                if (documentSnapshot.exists()){
                    val userData = documentSnapshot.data
                    ProfileObjects.name = userData?.get("name") as String?
                    ProfileObjects.role = userData?.get("role") as String?
                    ProfileObjects.email = userData?.get("email") as String?
                    ProfileObjects.profile = userData?.get("profile") as String?

                    maleImage = findViewById(R.id.iv_male)
                    femaleImage = findViewById(R.id.iv_female)

                    when (ProfileObjects.role) {
                        "Student" -> {
                            when (ProfileObjects.profile){
                                "Student_male" -> ProfileObjects.profileImage = maleImage.drawable
                                "Student_female" -> ProfileObjects.profileImage = femaleImage.drawable
                            }
                        }
                        "Employee" -> {
                            maleImage.setImageDrawable(setRawImage(resources, R.raw.employee_male))
                            femaleImage.setImageDrawable(setRawImage(resources, R.raw.employee_female))
                            when (ProfileObjects.profile){
                                "Employee_male" -> ProfileObjects.profileImage = maleImage.drawable
                                "Employee_female" -> ProfileObjects.profileImage = femaleImage.drawable
                            }
                        }
                        "Visitor" -> {
                            ProfileObjects.profileImage = setRawImage(resources, R.raw.visitor)
                        }
                    }
                    viewModel = ViewModelProvider(this).get(ViewModel::class.java)
                    viewModel.setImageDrawable(ProfileObjects.profileImage!!)
                }
            }
        } else{
            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()
        }

        // NAVIGATION START
        val cvNavExplore = findViewById<CardView>(R.id.cv_navigation_explore)
        val cvNavDirection= findViewById<CardView>(R.id.cv_navigation_direction)
        val cvNavProfile = findViewById<CardView>(R.id.cv_navigation_profile)

        val ivExplore = findViewById<ImageView>(R.id.iv_explore)
        val tvExplore = findViewById<TextView>(R.id.tv_navigation_explore)
        val ivDirection = findViewById<ImageView>(R.id.iv_direction)
        val tvDirection = findViewById<TextView>(R.id.tv_navigation_direction)
        val ivProfile = findViewById<ImageView>(R.id.iv_profile)
        val tvProfile = findViewById<TextView>(R.id.tv_navigation_profile)

        animationNavigation(cvNavExplore)
        animationNavigation(cvNavDirection)
        animationNavigation(cvNavProfile)
        // NAVIGATION END

        // DRAGGABLE SHEET START
        bottomSheet = findViewById(R.id.sheet)

        BottomSheetBehavior.from(bottomSheet).apply {
            peekHeight = 440
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        // DEFAULT
        fragmentManager = supportFragmentManager
        changeFragment(ExploreFragment(), ivExplore, tvExplore,
            listOf(ivDirection, ivProfile), listOf(tvDirection, tvProfile))

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        //Toast.makeText(applicationContext, "collapse", Toast.LENGTH_SHORT).show()

                        // Perform collapse function in the current inflated fragment
                        when(fragmentManager.findFragmentById(R.id.sheet)){
                            is ExploreFragment -> {
                                viewModel = ViewModelProvider(this@BirdsEyeView).get(ViewModel::class.java)
                                viewModel.clearFocus(false)
                            }
                            is DirectionsFragment -> {
                                changeFragment(ExploreFragment(), ivExplore, tvExplore,
                                    listOf(ivDirection, ivProfile), listOf(tvDirection, tvProfile))
                            }
                            is ProfileFragment -> {
                                changeFragment(ExploreFragment(), ivExplore, tvExplore,
                                    listOf(ivDirection, ivProfile), listOf(tvDirection, tvProfile))
                            }
                        }
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

        cvNavExplore.setOnClickListener {
            changeFragment(ExploreFragment(), ivExplore, tvExplore,
                listOf(ivDirection, ivProfile), listOf(tvDirection, tvProfile))
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        cvNavDirection.setOnClickListener{
            changeFragment(DirectionsFragment(), ivDirection, tvDirection,
                listOf(ivProfile, ivExplore), listOf(tvProfile, tvExplore))
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        cvNavProfile.setOnClickListener {
            changeFragment(ProfileFragment(), ivProfile, tvProfile, listOf(ivDirection,
                ivExplore), listOf(tvDirection, tvExplore))
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }


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
        mapboxMap?.loadStyleUri("mapbox://styles/mark-asuncion/clmvnqnd0000101pyh95u4s34" ,object: Style.OnStyleLoaded {
            override fun onStyleLoaded(style: Style) {
                initLocationComponent()
                setupGesturesListener()

                val camera = CameraOptions.Builder()
                    .center(Point.fromLngLat(120.98945,14.60195))
                    .zoom(20.0)
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

    // SEARCH ANIMATION
    @SuppressLint("Range")
    private fun searchAnimate(searchValue: String) {

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

        val dbHelper = DataBaseHandler(this)
        // Get a readable database instance
        val sqlDb = dbHelper.readableDatabase
        val cursor = sqlDb.rawQuery("SELECT * FROM coords WHERE RoomID = '$searchValue'", null)

        if (cursor.moveToFirst()) {
            val longitude = cursor.getDouble(cursor.getColumnIndex("Longitude"))
            val latitude = cursor.getDouble(cursor.getColumnIndex("Latitude"))
            val floor = cursor.getInt(cursor.getColumnIndex("Floor"))
            when(floor) {
                1 -> {
                    tvGroundFloor.performClick()
                    scrollView.smoothScrollTo(0, 400)
                }
                2 -> {
                    tvSecondFloor.performClick()
                    scrollView.smoothScrollTo(0, 400)
                }
                3 -> {
                    tvThridFloor.performClick()
                    scrollView.smoothScrollTo(0, 400)
                }
                4 -> {
                    tvFourthFloor.performClick()
                    scrollView.smoothScrollTo(0, 350)
                }
                5 -> {
                    tvFifthFloor.performClick()
                    scrollView.smoothScrollTo(0, 250)
                }
                6 -> {
                    tvSixthFloor.performClick()
                    scrollView.smoothScrollTo(0, 180)
                }
                7 -> {
                    tvSeventhFloor.performClick()
                    scrollView.smoothScrollTo(0, 80)
                }
                8 -> {
                    tvEightFloor.performClick()
                    scrollView.smoothScrollTo(0, 0)
                }
                9 -> {
                    tvNinethFloor.performClick()
                    scrollView.smoothScrollTo(0, 0)
                }
            }

            val camera = CameraOptions.Builder()
                .center(Point.fromLngLat(longitude,latitude))
                .zoom(22.0)
                .bearing(0.0)
                .build()
            val animationOptions = MapAnimationOptions.mapAnimationOptions {
                duration(3000) // Duration in milliseconds for the animation
            }
            mapboxMap?.flyTo(camera, animationOptions)

            // Toast.makeText(this, "$longitude $latitude $floor", Toast.LENGTH_SHORT).show()
        } else {
            // Handle the case when no matching data is found
            Toast.makeText(this, "No data found for RoomID: $searchValue", Toast.LENGTH_SHORT).show()
        }

        cursor.close()
        sqlDb.close()


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

    private fun addAnnotationToMap(longitude: Double, latitude: Double) {
        bitmapFromDrawableRes(
            this@BirdsEyeView,
            R.drawable.arrowvector
        )?.let {
            val annotationApi = mapView?.annotations
            val pointAnnotationManager = annotationApi?.createPointAnnotationManager()
            val pointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(longitude, latitude))
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

    fun viewProfileOverlay(){
        // Select Avatar Overlay
        val profileOverlay = findViewById<ConstraintLayout>(R.id.profile_overlay)
        ProfileObjects.profileOverlay = profileOverlay
        val male = findViewById<CardView>(R.id.cv_male)
        val female = findViewById<CardView>(R.id.cv_female)
        val confirm = findViewById<Button>(R.id.btn_profile_confirm)

        profileOverlay.visibility = VISIBLE
        val bgSelected = ContextCompat.getColor(this, R.color.green)
        val bgUnselected = ContextCompat.getColor(this, R.color.offWhite)
        var avatarSelected: Drawable? = null
        var avatarString: String? = null

        male.setOnClickListener {
            male.setCardBackgroundColor(bgSelected)
            female.setCardBackgroundColor(bgUnselected)
            confirm.setBackgroundColor(ContextCompat.getColor(this, R.color.blue))
            when(ProfileObjects.role){
                "Student" -> {
                    avatarString = "Student_male"
                }
                "Employee" -> {
                    avatarString = "Employee_male"
                }
            }
            avatarSelected = maleImage.drawable

        }
        female.setOnClickListener {
            male.setCardBackgroundColor(bgUnselected)
            female.setCardBackgroundColor(bgSelected)
            confirm.setBackgroundColor(ContextCompat.getColor(this, R.color.blue))
            when(ProfileObjects.role){
                "Student" -> {
                    avatarString = "Student_female"
                }
                "Employee" -> {
                    avatarString = "Employee_female"
                }
            }
            avatarSelected = femaleImage.drawable
        }
        confirm.setOnClickListener {
            if(avatarSelected !=null) {
                viewModel = ViewModelProvider(this).get(ViewModel::class.java)
                viewModel.setImageDrawable(avatarSelected!!)

                userRefUpdate(avatarString)
                ProfileObjects.profileImage = avatarSelected
                ProfileObjects.profile = avatarString

                avatarSelected = null
                avatarString = null

                male.setCardBackgroundColor(bgUnselected)
                female.setCardBackgroundColor(bgUnselected)
                confirm.setBackgroundColor(ContextCompat.getColor(this, R.color.darkGrey))
                profileOverlay.visibility = GONE
            }else{
                Toast.makeText(this,"Select an avatar first.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun userRefUpdate(value: String?){
        userRef.update("profile", value)
            .addOnFailureListener {
                Toast.makeText(this, "Change avatar failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
    }

    fun focusListener(hasFocus: Boolean){
        if (hasFocus) {
            BottomSheetBehavior.from(bottomSheet).apply {
                this.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun changeFragment(fragment: Fragment, selectedIv: ImageView, selectedTv: TextView,
                               unselectedIv: List<ImageView>, unselectedTv : List<TextView>){
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.sheet, fragment)
        fragmentTransaction.commit()

        selectedIv.setColorFilter(ContextCompat.getColor(this, R.color.green))
        selectedTv.setTextColor(ContextCompat.getColor(this, R.color.green))
        for (view in unselectedIv) {
            view.setColorFilter(ContextCompat.getColor(this, R.color.medGrey))
        }
        for (view in unselectedTv){
            view.setTextColor(ContextCompat.getColor(this, R.color.medGrey))
        }
    }

    fun askPermissions() {
        reqPermissionLauncher.launch(PERMISSIONS)
    }
}
