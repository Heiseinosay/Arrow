package com.example.arrow

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.OnPolylineAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager

var polylineAnnotationManager: PolylineAnnotationManager? = null

fun explorationView(context: Context, mapView: MapView?){
    polylineAnnotationManager = mapView?.annotations?.createPolylineAnnotationManager()
    polylineAnnotationManager?.lineCap = (LineCap.ROUND)

    val blue = ContextCompat.getColor(context, R.color.blue)
    val segmentOptionsList = mutableListOf<PolylineAnnotationOptions>()

    val coordinateSize = Coordinates.gastamToLualhati.size - 1

    for (i in 0 until coordinateSize){
        val polylineAnnotationOptions: PolylineAnnotationOptions = PolylineAnnotationOptions()
            .withPoints(listOf(Coordinates.gastamToLualhati[i], Coordinates.gastamToLualhati[i + 1]))
            .withLineJoin(LineJoin.ROUND)
            .withLineColor(blue)
            .withLineWidth(5.0)
        segmentOptionsList.add(polylineAnnotationOptions)
    }

    polylineAnnotationManager?.create(segmentOptionsList)

    //Polyline Click Listener
    val clickListener = OnPolylineAnnotationClickListener { polylineId ->
        //Handle the 360 view here. Make the id of image == to the ID of
        Toast.makeText(context, "Polyline with ID $polylineId clicked", Toast.LENGTH_SHORT).show()
        true
    }
    polylineAnnotationManager?.addClickListener(clickListener)
}