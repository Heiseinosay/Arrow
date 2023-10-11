package com.example.arrow.utils

import android.graphics.Color
import android.util.Log
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.api.matching.v5.MapboxMapMatching
import com.mapbox.api.matching.v5.models.MapMatchingResponse
import com.mapbox.common.location.Location
import com.mapbox.core.constants.Constants.PRECISION_6
import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.NavigationRouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.base.route.RouterOrigin
import com.mapbox.navigation.base.route.toNavigationRoute
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val GEO_SOURCE_ID = "DirectionSource"
val DIRECTION_LAYER_ID = "DirectionLayerSource"
val CUSTOM_ROUTE = listOf(
    Point.fromLngLat(120.988960641700885, 14.602762533392),
    Point.fromLngLat(120.988905628752349, 14.602788287603),
    Point.fromLngLat(120.988861383288324, 14.602809001034),
    Point.fromLngLat(120.988899105132418, 14.602884456064),
    Point.fromLngLat(120.988905679324418, 14.602897606421),
    Point.fromLngLat(120.988966641175153, 14.603019548390),
    Point.fromLngLat(120.989001379940888, 14.603003285518),
    Point.fromLngLat(120.98902888133081, 14.6029904108055),
    Point.fromLngLat(120.989067026830043, 14.602972553076),
    Point.fromLngLat(120.989093493821343, 14.602960162613),
    Point.fromLngLat(120.989129776047037, 14.602943177170),
    Point.fromLngLat(120.98915618911505, 14.6029308119493),
    Point.fromLngLat(120.989200292559303, 14.602910165016),
    Point.fromLngLat(120.989182636768604, 14.602874848133),
    Point.fromLngLat(120.98916345426899, 14.6028364773685),
    Point.fromLngLat(120.988943007415855, 14.602880131352),
    Point.fromLngLat(120.988979552477957, 14.602863022856),
    Point.fromLngLat(120.989024804115431, 14.602841838392),
    Point.fromLngLat(120.989063202282736, 14.602823862365),
    Point.fromLngLat(120.989101189402064, 14.602806078767),
    Point.fromLngLat(120.9891393922139, 14.60278819419309),
    Point.fromLngLat(120.989178088653034, 14.602770078526),
    Point.fromLngLat(120.989232641267677, 14.602744476106),
    Point.fromLngLat(120.989251454414955, 14.602782108071),
    Point.fromLngLat(120.989273196091688, 14.602825597970),
    Point.fromLngLat(120.989250031114949, 14.602886880015),
    Point.fromLngLat(120.989293628928507, 14.602866469788),
    Point.fromLngLat(120.989324044402423, 14.602852230845),
    Point.fromLngLat(120.98935102689822, 14.6028395990429),
    Point.fromLngLat(120.989386653727053, 14.602822920412),
    Point.fromLngLat(120.989401045020927, 14.602816183155),
    Point.fromLngLat(120.989260727614607, 14.602731327532),
    Point.fromLngLat(120.989303169890462, 14.602711458253),
    Point.fromLngLat(120.989340083170191, 14.602694241074),
    Point.fromLngLat(120.989382044414796, 14.602674596986),
    Point.fromLngLat(120.989418405571328, 14.602657574568),
    Point.fromLngLat(120.989459845921729, 14.602638174333),
    Point.fromLngLat(120.989497788865066, 14.602620411401),
    Point.fromLngLat(120.989537508295953, 14.602601816809),
    Point.fromLngLat(120.989571443537287, 14.602585930074),
    Point.fromLngLat(120.989619243272401, 14.602563552695),
    Point.fromLngLat(120.989360690828022, 14.602735462610),
    Point.fromLngLat(120.989382361051995, 14.602778809591),
    Point.fromLngLat(120.989449291864304, 14.602793596487),
    Point.fromLngLat(120.989413714602009, 14.602810251916),
    Point.fromLngLat(120.989475731328795, 14.602781218901),
    Point.fromLngLat(120.989510237099239, 14.602765065087),
    Point.fromLngLat(120.98953874531496, 14.6027517190178),
    Point.fromLngLat(120.98957604349674, 14.6027342579396),
    Point.fromLngLat(120.989611620759021, 14.602717602505),
    Point.fromLngLat(120.989652982210856, 14.602698239211),
    Point.fromLngLat(120.988928309367836, 14.602942873298),
    Point.fromLngLat(120.98894310175821, 14.6029724625117),
    Point.fromLngLat(120.988884686139826, 14.602855613746),
    Point.fromLngLat(120.98887377140629, 14.6028337809979),
    Point.fromLngLat(120.98904, 14.60272),
)

val locationObserver = object : LocationObserver {
    override fun onNewRawLocation(rawLocation: android.location.Location) {
        Log.i("LOCATIONOBSERVER", rawLocation.toString())
    }
    override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
        Log.i("LOCATIONOBSERVER", locationMatcherResult.toString())
    }
}

fun initCustomRoutes(
    access_token: String,
    mapboxNavigation: MapboxNavigation
) {
    val mapboxMapMatchingRequest = MapboxMapMatching.builder()
        .accessToken(access_token)
        // TODO: make custom route near road network for testing
        .coordinates(CUSTOM_ROUTE)
        .steps(true)
        .voiceInstructions(false)
        .bannerInstructions(true)
        .profile(DirectionsCriteria.PROFILE_WALKING)
        .build()

    mapboxMapMatchingRequest.enqueueCall(object : Callback<MapMatchingResponse> {
        override fun onResponse(call: Call<MapMatchingResponse>, response: Response<MapMatchingResponse>) {
            if (!response.isSuccessful) {
                Log.w("MAPBOXMAPMATCHINGREQUEST", "Failed")
                return
            }
            response.body()?.matchings()?.let { matchingList ->
                matchingList[0].toDirectionRoute().toNavigationRoute(
                    RouterOrigin.Custom()
                ).apply {
                    mapboxNavigation.setNavigationRoutes(listOf(this))
                }
            }
        }

        override fun onFailure(call: Call<MapMatchingResponse>, t: Throwable) {
            t.message?.let { Log.e("MAPBOXMAPMATCHINGREQUEST", it) }
        }
    })
}

fun requestRoute(
    access_token: String,
    mapboxNavigation: MapboxNavigation,
    origin: Point,
    destination: Point
): Boolean {
    val routeOptions = RouteOptions.builder()
        .coordinatesList(listOf(origin, destination))
        .overview(DirectionsCriteria.OVERVIEW_FULL)
        .profile(DirectionsCriteria.PROFILE_WALKING)
        .alternatives(false)
        .build()

    var ret = false
    mapboxNavigation.requestRoutes(
        routeOptions,
        object : NavigationRouterCallback {
            override fun onRoutesReady(
                routes: List<NavigationRoute>,
                routerOrigin: RouterOrigin
            ) {
                mapboxNavigation.setNavigationRoutes(routes)
                ret = true
            }
            override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) { }
            override fun onCanceled(routeOptions: RouteOptions, routerOrigin: RouterOrigin) { }
        }
    )
    return ret
}

fun initDirection(
//    mapboxmap: MapboxMap,
    origin: Point,
    destination: Point,
    access_token: String
): MapboxDirections {
    return MapboxDirections.builder()
        .accessToken(access_token)
        .routeOptions(
            RouteOptions.builder()
                .coordinatesList(listOf(
                    origin,
                    destination
                ))
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_WALKING)
                .build()
        )
        .build()
}

fun initDirectionLayer(loadedStyle: Style) {
    loadedStyle.addSource(GeoJsonSource.Builder(GEO_SOURCE_ID).build())
    loadedStyle.addLayer(
        LineLayer(DIRECTION_LAYER_ID, GEO_SOURCE_ID)
            .lineWidth(4.5)
            .lineColor(Color.GREEN)
    )
}

fun drawDirection(loadedStyle: Style, route: DirectionsRoute) {
    val geoSource = loadedStyle.getSourceAs<GeoJsonSource>(GEO_SOURCE_ID)
    assert(geoSource != null)
    geoSource?.feature(
        Feature.fromGeometry(
            route.geometry()?.let { LineString.fromPolyline(it, PRECISION_6) }
        )
    )
}
