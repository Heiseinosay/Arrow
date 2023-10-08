package com.example.arrow

import android.annotation.SuppressLint
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.Bearing
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxMap

public fun initDirection(
    mapboxmap: MapboxMap,
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
                    Point.fromLngLat(120.988960641700885, 14.602762533392),
                    Point.fromLngLat(120.988905628752349, 14.602788287603),
//                    Point.fromLngLat(120.988861383288324, 14.602809001034),
//                    Point.fromLngLat(120.988899105132418, 14.602884456064),
//                    Point.fromLngLat(120.988905679324418, 14.602897606421),
//                    Point.fromLngLat(120.988966641175153, 14.603019548390),
//                    Point.fromLngLat(120.989001379940888, 14.603003285518),
//                    Point.fromLngLat(120.98902888133081, 14.6029904108055),
//                    Point.fromLngLat(120.989067026830043, 14.602972553076),
//                    Point.fromLngLat(120.989093493821343, 14.602960162613),
//                    Point.fromLngLat(120.989129776047037, 14.602943177170),
//                    Point.fromLngLat(120.98915618911505, 14.6029308119493),
//                    Point.fromLngLat(120.989200292559303, 14.602910165016),
//                    Point.fromLngLat(120.989182636768604, 14.602874848133),
//                    Point.fromLngLat(120.98916345426899, 14.6028364773685),
//                    Point.fromLngLat(120.988943007415855, 14.602880131352),
//                    Point.fromLngLat(120.988979552477957, 14.602863022856),
//                    Point.fromLngLat(120.989024804115431, 14.602841838392),
//                    Point.fromLngLat(120.989063202282736, 14.602823862365),
//                    Point.fromLngLat(120.989101189402064, 14.602806078767),
//                    Point.fromLngLat(120.9891393922139, 14.60278819419309),
//                    Point.fromLngLat(120.989178088653034, 14.602770078526),
//                    Point.fromLngLat(120.989232641267677, 14.602744476106),
//                    Point.fromLngLat(120.989251454414955, 14.602782108071),
//                    Point.fromLngLat(120.989273196091688, 14.602825597970),
//                    Point.fromLngLat(120.989250031114949, 14.602886880015),
//                    Point.fromLngLat(120.989293628928507, 14.602866469788),
//                    Point.fromLngLat(120.989324044402423, 14.602852230845),
//                    Point.fromLngLat(120.98935102689822, 14.6028395990429),
//                    Point.fromLngLat(120.989386653727053, 14.602822920412),
//                    Point.fromLngLat(120.989401045020927, 14.602816183155),
//                    Point.fromLngLat(120.989260727614607, 14.602731327532),
//                    Point.fromLngLat(120.989303169890462, 14.602711458253),
//                    Point.fromLngLat(120.989340083170191, 14.602694241074),
//                    Point.fromLngLat(120.989382044414796, 14.602674596986),
//                    Point.fromLngLat(120.989418405571328, 14.602657574568),
//                    Point.fromLngLat(120.989459845921729, 14.602638174333),
//                    Point.fromLngLat(120.989497788865066, 14.602620411401),
//                    Point.fromLngLat(120.989537508295953, 14.602601816809),
//                    Point.fromLngLat(120.989571443537287, 14.602585930074),
//                    Point.fromLngLat(120.989619243272401, 14.602563552695),
//                    Point.fromLngLat(120.989360690828022, 14.602735462610),
//                    Point.fromLngLat(120.989382361051995, 14.602778809591),
//                    Point.fromLngLat(120.989449291864304, 14.602793596487),
//                    Point.fromLngLat(120.989413714602009, 14.602810251916),
//                    Point.fromLngLat(120.989475731328795, 14.602781218901),
//                    Point.fromLngLat(120.989510237099239, 14.602765065087),
//                    Point.fromLngLat(120.98953874531496, 14.6027517190178),
//                    Point.fromLngLat(120.98957604349674, 14.6027342579396),
//                    Point.fromLngLat(120.989611620759021, 14.602717602505),
//                    Point.fromLngLat(120.989652982210856, 14.602698239211),
//                    Point.fromLngLat(120.988928309367836, 14.602942873298),
//                    Point.fromLngLat(120.98894310175821, 14.6029724625117),
//                    Point.fromLngLat(120.988884686139826, 14.602855613746),
//                    Point.fromLngLat(120.98887377140629, 14.6028337809979),
//                    Point.fromLngLat(120.98904, 14.60272),
                    destination
                ))
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_WALKING)
                .build()
        )
        .build()
}