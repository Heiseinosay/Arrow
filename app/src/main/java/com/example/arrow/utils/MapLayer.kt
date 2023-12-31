package com.example.arrow.utils

import android.util.Log
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.extension.style.layers.generated.FillLayer
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.layers.getLayerAs
import com.mapbox.maps.extension.style.layers.properties.generated.Visibility

class MapLayer(
    name: String,
    mapboxMap: MapboxMap?,
    fillID: List<String>,
    symbolID: List<String>,
    strokeID: List<String>,
    startingFloor: Int = 1
) {
    var name = name
    val fillLayers = mutableMapOf<String, FillLayer?>()
    val symbolLayers = mutableMapOf<String, SymbolLayer?>()
    val lineLayers = mutableMapOf<String, LineLayer?>()

    val ID_FILLLAYERS = fillID.toList()
    val ID_SYMBOLLAYERS = symbolID.toList()
    val ID_LINELAYERS = strokeID.toList()

    private val floorsFill = mutableListOf< List<Int> >()
    private val floorsSymbol = mutableListOf< List<Int> >()
    private val floorsLine = mutableListOf< List<Int> >()

    var maxFloor: Int = 0
        get() = field

    var currFloor: Int = startingFloor
        get() = field

    fun setCurrFloor(floor: Int, callable: (ctx: MapLayer,floor: Int) -> Unit = { _,_ -> }) {
        if (floor >= maxFloor || floor <= 0) { return Unit }
        setFloorVisibility(currFloor,Visibility.NONE)
        setFloorVisibility(floor,Visibility.VISIBLE)
        callable(this,floor)
        currFloor = floor
    }

    init {
        ID_FILLLAYERS.forEach {
            fillLayers[it] = mapboxMap?.getStyle()?.getLayerAs<FillLayer>(it)
        }
        Log.i("MAPLAYERS","fill: ${fillLayers}")
        ID_SYMBOLLAYERS.forEach {
            symbolLayers[it] = mapboxMap?.getStyle()?.getLayerAs<SymbolLayer>(it)
        }
        Log.i("MAPLAYERS","symbol: ${symbolLayers}")

        ID_LINELAYERS.forEach {
            lineLayers[it] = mapboxMap?.getStyle()?.getLayerAs<LineLayer>(it)
        }
        Log.i("MAPLAYERS","line: ${lineLayers}")
    }

    fun setFloorOpacity(floor: Int, opacity: Double) {
        floorsFill[floor-1].forEach {
            fillLayers[ID_FILLLAYERS[it]]?.fillOpacity(opacity)
        }
        floorsSymbol[floor-1].forEach {
            symbolLayers[ID_SYMBOLLAYERS[it]]?.iconOpacity(opacity)
            symbolLayers[ID_SYMBOLLAYERS[it]]?.textOpacity(opacity)
        }
        floorsLine[floor-1].forEach {
            lineLayers[ID_LINELAYERS[it]]?.lineOpacity(opacity)
        }
    }

    fun setFloorVisibility(floor: Int, visibility: Visibility) {
        floorsFill[floor-1].forEach {
            fillLayers[ID_FILLLAYERS[it]]?.visibility(visibility)
        }
        floorsSymbol[floor-1].forEach {
            symbolLayers[ID_SYMBOLLAYERS[it]]?.visibility(visibility)
        }
        floorsLine[floor-1].forEach {
            lineLayers[ID_LINELAYERS[it]]?.visibility(visibility)
        }
    }

    // push ids to list by floor 
    fun addFloor(fill: List<String>?,symbol: List<String>?, line: List<String>?): MapLayer {
        val a = mutableListOf<Int>()
        val b = mutableListOf<Int>()
        val c = mutableListOf<Int>()

        if (fill != null) {
            for (layer_id in ID_FILLLAYERS.indices) {
                for (id in fill!!) {
                    if (id == ID_FILLLAYERS[layer_id]) {
                        a.add(layer_id)
                    }
                }
            }
        }

        if (symbol != null) {
            for (layer_id in ID_SYMBOLLAYERS.indices) {
                for (id in symbol!!) {
                    if (id == ID_SYMBOLLAYERS[layer_id]) {
                        b.add(layer_id)
                    }
                }
            }
        }

        if (line != null) {
            for (layer_id in ID_LINELAYERS.indices) {
                for (id in line!!) {
                    if (id == ID_LINELAYERS[layer_id]) {
                        c.add(layer_id)
                    }
                }
            }
        }

        floorsFill.add(a)
        floorsSymbol.add(b)
        floorsLine.add(c)
        maxFloor++
        return this
    }
}

val LB_BUILDING_LAYER_IDS: List<String> = listOf(
        "lb-rooms-upw",
        "lb-ground-2f-upw",
        "lb-stairs",
        "lb-stairs-2f-upw",
        "lb-rooms-3f",
        "lb-escalator",
        "lb-escalator-oddf",
        "lb-rooms",
        "lb-ground",
        "lb-gate",
        "lb-emergency",
        "lb-elevator",
        "lb-cr",
        "lb-cr-2f-upw"
        )

val LB_BUILDING_SYMBOL_IDS: List<String> = listOf(
        "lb-rooms-upw-label",
        "lb-stairs-label",
        "lb-stairs-2f-upw-label",
        "lb-rooms-3f-label",
        "lb-escalator-label",
        "lb-escalator-oddf-label",
        "lb-rooms-label",
        "lb-gate-label",
        "lb-emergency-label",
        "lb-elevator-label",
        "lb-cr-label",
        "lb-cr-2f-upw-label"
        )

val LB_BUILDING_LINE_IDS: List<String> = listOf(
        "lb-ground-2f-upw-stroke"
        )

public fun setLBFloors(maplayer: MapLayer) {
    maplayer.addFloor(
            listOf(
                "lb-escalator",
                "lb-stairs",
                "lb-rooms",
                "lb-ground",
                "lb-gate",
                "lb-elevator",
                "lb-cr"
                ),
            listOf(
                "lb-stairs-label",
                "lb-escalator-label",
                "lb-rooms-label",
                "lb-gate-label",
                "lb-elevator-label",
                "lb-cr-label"
                ),
            null
            )
    for (i in 2..10) {
        maplayer.addFloor(
            listOf(
                if (i!=3) "lb-rooms-upw" else "",
                "lb-ground-2f-upw",
                "lb-stairs-2f-upw",
                if (i==3) "lb-rooms-3f" else "",
                if (i%2 == 1) "lb-escalator" else "",
                if (i%2 == 1) "lb-escalator-oddf" else "",
                "lb-emergency",
                "lb-elevator",
                "lb-cr-2f-upw"
                ),
            listOf(
                if (i!=3) "lb-rooms-upw-label" else "",
                "lb-stairs-2f-upw-label",
                if (i==3) "lb-rooms-3f-label" else "",
                if (i%2 == 1) "lb-escalator-label" else "",
                if (i%2 == 1) "lb-escalator-oddf-label" else "",
                "lb-emergency-label",
                "lb-elevator-label",
                "lb-cr-2f-upw-label"
                ),
            LB_BUILDING_LINE_IDS
        )
    }
}
