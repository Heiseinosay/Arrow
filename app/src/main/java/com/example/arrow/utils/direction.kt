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
import com.example.arrow.utils.distanceOf
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val GEO_SOURCE_ID = "DirectionSource"
val DIRECTION_LAYER_ID = "DirectionLayerSource"

val POINTS = listOf(
Point.fromLngLat(120.98936069082802, 14.602735462610733), // elevator
Point.fromLngLat(120.989382361052, 14.602778809591888), // elevator

Point.fromLngLat(120.98940104502092652, 14.60281618315584851),
Point.fromLngLat(120.98938665372705259, 14.60282292041240026),
Point.fromLngLat(120.98941371460200855, 14.60281025191602033),

// elev2sidebot
Point.fromLngLat(120.98944929186430386, 14.60279359648773756),
Point.fromLngLat(120.9894757313287954, 14.60278121890145897),
Point.fromLngLat(120.98951023709923902, 14.60276506508796146),
Point.fromLngLat(120.98953874531495956, 14.60275171901785463),
Point.fromLngLat(120.98957604349674, 14.60273425793967839),
Point.fromLngLat(120.98961162075902109, 14.60271760250564199),
Point.fromLngLat(120.98965298221085618, 14.60269823921165688), // 11

// elev2sidetop
Point.fromLngLat(120.98935102689821974, 14.60283959904295692),
Point.fromLngLat(120.98932404440242294, 14.60285223084534323),
Point.fromLngLat(120.98929362892850747, 14.60286646978821601), // 2RCOS
Point.fromLngLat(120.98928395652613688, 14.60287099796844856),
Point.fromLngLat(120.98925995505467768, 14.60288224105710064),
Point.fromLngLat(120.98921758061227649, 14.60290204922840118),
Point.fromLngLat(120.98920029255930331, 14.60291016501637174), // 2RCOS
Point.fromLngLat(120.98915618911505021, 14.60293081194935816),
Point.fromLngLat(120.98912977604703656, 14.60294317717012724),
Point.fromLngLat(120.98909349382134337, 14.60296016261377439),
Point.fromLngLat(120.98906702683004255, 14.6029725530765937),
Point.fromLngLat(120.98902888133081035, 14.60299041080557103),
Point.fromLngLat(120.98900137994088766, 14.60300328551837978),
Point.fromLngLat(120.98896664117515343, 14.60301954839023786), // end 25

// 2RCOS connect to 1RCOS
Point.fromLngLat(120.9892749416489437, 14.60282908961056769), // MCOS
Point.fromLngLat(120.98925327138434227, 14.60278574497410808),
Point.fromLngLat(120.98918160544094746, 14.60287278477054862),
Point.fromLngLat(120.98916006374523135, 14.60282969530113029), // 29

Point.fromLngLat(120.98934008317019106, 14.60269424107424641),
Point.fromLngLat(120.98930316989046219, 14.60271145825360861),
Point.fromLngLat(120.98938204441479627, 14.6026745969861782), // 32

// elev1sidebot
Point.fromLngLat(120.98941840557132821, 14.60265757456877012),
Point.fromLngLat(120.9894598459217292, 14.60263817433325606),
Point.fromLngLat(120.98949778886506579, 14.6026204114019027),
Point.fromLngLat(120.98953750829595322, 14.6026018168090701),
Point.fromLngLat(120.98957144353728665, 14.60258593007476868),
Point.fromLngLat(120.98961924327240069, 14.60256355269575579), // 38

// elev1sidetop
Point.fromLngLat(120.98926072761460659, 14.60273132753233583),
Point.fromLngLat(120.98923264126767663, 14.60274447610653503), // 1RCOS
Point.fromLngLat(120.98922601305943658, 14.60274764280125659),
Point.fromLngLat(120.98917808865303414, 14.60277007852616471),
Point.fromLngLat(120.98914746860305058, 14.60278441324646259),
Point.fromLngLat(120.98913939221390024, 14.60278819419309038), // 2RCOS
Point.fromLngLat(120.9891011894020636, 14.60280607876782177),
Point.fromLngLat(120.98906882854404898, 14.60282122844171049),
Point.fromLngLat(120.98902480411543081, 14.60284183839220162),
Point.fromLngLat(120.98899052533387533, 14.60285782633823004),
Point.fromLngLat(120.98894300741585539, 14.6028801313526575),
Point.fromLngLat(120.98890567932441797, 14.60289760642139356), // end 50

// end connection
Point.fromLngLat(120.98893685918412189, 14.60295997549333968),

// faculty side
Point.fromLngLat(120.9888991051324183, 14.60288445606439467),
Point.fromLngLat(120.98888468613982639, 14.60285561374692165),
Point.fromLngLat(120.98887377140628985, 14.60283378099789964),
Point.fromLngLat(120.98886138328832374, 14.60280900103434654),
Point.fromLngLat(120.98890562875234878, 14.60278828760344894),
Point.fromLngLat(120.98896064170088493, 14.60276253339217511),
Point.fromLngLat(120.98899448041311189, 14.60274669185942642),
Point.fromLngLat(120.98904972806279545, 14.60272082776834957), // 59
)

val ROOMS = listOf(
// 801
Point.fromLngLat(120.98897628824259698, 14.60303867951060042),
Point.fromLngLat(120.98901122138191511, 14.60302232839303116),

// 802
Point.fromLngLat(120.98903854730180285, 14.60300952853645384),

)

fun distanceOf(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    // calc the distance between two points
    // using haverson formula
    var lat1 = Math.toRadians(lat1);
    var lon1 = Math.toRadians(lon1);
    var lat2 = Math.toRadians(lat2);
    var lon2 = Math.toRadians(lon2);

    var dlat = lat2 - lat1;
    var dlon = lon2 - lon1;
    var a = Math.pow( Math.sin(dlat/2), 2.0 ) + Math.cos(lat1) * Math.cos(lat2) * Math.pow( Math.sin(dlon/2), 2.0 );
    var c = 2 * Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
    return (6371 * c) * 1000;
}

fun distanceOf(p1: Point, p2: Point): Double
{ return distanceOf(p1.latitude(),p1.longitude(),p2.latitude(),p2.longitude()) }

enum class Property(val value: Int) {
    Entry(1),
    Exit(2),
    Room(4)
}

data class Node(val loc: Point, val property: Int = 0) {
     val neighbors: MutableList<Node?> = mutableListOf()
     // distance in km
     val distance: MutableList<Double>  = mutableListOf()
     override fun toString(): String = neighbors.toString() + ' ' + distance.toString()
     fun add(node: Node): Node? {
         assert(neighbors.size < 4) {
             // TODO Remove assert if done testing
             "Size needs to be less than 4 to add Nodes"
         }
         if (neighbors.size >= 4) return null
         neighbors.add(node)
         distance.add(distanceOf(this.loc,node.loc))
         return node
     }
}

class NavigationGraph(base: Node?) {
    // not the root node
    // the starting node is where the search will start
    var start = base
    public fun requestRoute() {
        assert(false) { "TODO Implement" }
    }
}

fun setupNavigationTree(): NavigationGraph {
    Log.i("setupNavigationTree","" + POINTS.size)

    val elev1 = Node(POINTS[0],Property.Entry.value or Property.Exit.value)
    val elev2 = Node(POINTS[1],Property.Entry.value or Property.Exit.value)
    elev1.add(elev2)
    elev2.add(elev1)
    val elev2sideMiddle = elev2.add(Node(POINTS[2]))
    val elev2SideTop = Node(POINTS[3])
    val elev2SideBot = Node(POINTS[4])
    elev2sideMiddle?.add(elev2SideTop)
    elev2sideMiddle?.add(elev2SideBot)

    elev2SideBot.add(elev2sideMiddle!!)
    elev2SideBot.add(Node(POINTS[5]))?.let { n ->
        n.add(elev2SideBot)
        n.add(Node(POINTS[6]))?.let { nn ->
            nn.add(n)
            nn.add(Node(POINTS[7]))?.let { nnn ->
                nnn.add(nn)
                nnn.add(Node(POINTS[8]))?.let { nnnn ->
                    nnnn.add(nnn)
                    nnnn.add(Node(POINTS[9]))?.let { x ->
                        x.add(nnnn)
                        x.add(Node(POINTS[10]))?.let { xx ->
                            xx.add(x)
                            xx.add(Node(POINTS[11]))?.let { z ->
                                z.add(xx)
                            }
                        }
                    }
                }
            }
        }
    }
    val conn1 = Node(POINTS[14])
    val conn2 = Node(POINTS[18])
    val conn3 = Node(POINTS[25])
    val conn126 = Node(POINTS[26])
    val conn127 = Node(POINTS[27])
    val conn128 = Node(POINTS[28])
    val conn129 = Node(POINTS[29])
    val conn4 = Node(POINTS[40])
    val conn5 = Node(POINTS[44])
    val conn6 = Node(POINTS[50])

    conn1.add(conn126)?.let { n ->
        n.add(conn1)
            n.add(conn127)?.let { nn ->
                nn.add(n)
                nn.add(conn4)?.let { nnn ->
                    nnn.add(nn)
                }
            }
    }
    conn2.add(conn128)?.let { n ->
        n.add(conn2)
            n.add(conn129)?.let { nn ->
                nn.add(n)
                nn.add(conn5)?.let { nnn ->
                    nnn.add(nn)
                }
            }
    }

    conn6.add(Node(POINTS[51]))?.let { n ->
        n.add(conn6)
        n.add(conn3)?.let { nn ->
            nn.add(n)
        }
    }

    elev2SideTop.add(elev2sideMiddle!!)
    elev2SideTop.add(Node(POINTS[12]))?.let { n ->
        n.add(elev2SideTop)
        n.add(Node(POINTS[13]))?.let { nn ->

        nn.add(n)
        nn.add(conn1)?.let { nnn ->

        nnn.add(nn)
        nnn.add(Node(POINTS[15]))?.let { nnnn ->

        nnnn.add(nnn)
        nnnn.add(Node(POINTS[16]))?.let { x ->

        x.add(nnnn)
        x.add(Node(POINTS[17]))?.let { xx->

        xx.add(x)
        xx.add(conn2)?.let { xxx ->

        xxx.add(xx)
        xxx.add(Node(POINTS[19]))?.let { xxxx ->

        xxxx.add(xxx)
        xxxx.add(Node(POINTS[20]))?.let { y ->

        y.add(xxxx)
        y.add(Node(POINTS[21]))?.let { yy ->

        yy.add(y)
        yy.add(Node(POINTS[22]))?.let { yyy ->

        yyy.add(yy)
        yyy.add(Node(POINTS[23]))?.let { yyyy ->

        yyyy.add(yyy)
        yyyy.add(Node(POINTS[24]))?.let { z ->

        z.add(yyyy)
        z.add(conn3)?.let { zz ->
        zz.add(z)
    } } } } } } } } } } } } } }

    val elev1sideMiddle = elev1.add(Node(POINTS[30]))
    val elev1SideTop = Node(POINTS[31])
    val elev1SideBot = Node(POINTS[32])
    elev1sideMiddle?.add(elev1SideTop)
    elev1sideMiddle?.add(elev1SideBot)
    elev1SideTop.add(elev1sideMiddle!!)
    elev1SideBot.add(elev1sideMiddle!!)

    elev1SideBot.add(Node(POINTS[33]))?.let { n ->

        n.add(elev1SideBot)
        n.add(Node(POINTS[34]))?.let { nn ->

        nn.add(n)
        nn.add(Node(POINTS[35]))?.let { nnn ->

        nnn.add(nn)
        nnn.add(Node(POINTS[36]))?.let { nnnn ->

        nnnn.add(nnn)
        nnnn.add(Node(POINTS[37]))?.let { z ->

        z.add(nnnn)
        z.add(Node(POINTS[38]))?.let { zz ->
        zz.add(z)
    } } } } } }

    elev1SideTop.add(Node(POINTS[39]))?.let { n ->
        n.add(elev1SideTop)
        n.add(conn4)?.let { nn ->

        nn.add(n)
        nn.add(Node(POINTS[41]))?.let { nnn ->

        nnn.add(nn)
        nnn.add(Node(POINTS[42]))?.let { nnnn ->

        nnnn.add(nnn)
        nnnn.add(Node(POINTS[43]))?.let { x ->

        x.add(nnnn)
        x.add(conn5)?.let { xx ->

        xx.add(x)
        xx.add(Node(POINTS[45]))?.let { xxx ->

        xxx.add(xx)
        xxx.add(Node(POINTS[46]))?.let { xxxx ->

        xxxx.add(xxx)
        xxxx.add(Node(POINTS[47]))?.let { z ->

        z.add(xxxx)
        z.add(Node(POINTS[48]))?.let { zz ->

        zz.add(z)
        zz.add(Node(POINTS[49]))?.let { zzz ->

        zzz.add(zz)
        zzz.add(conn6)?.let { zzzz ->

        zzzz.add(zzz)
    } } } } } } } } } } } }

    conn6.add(Node(POINTS[52]))?.let { n ->
        n.add(conn6)
        n.add(Node(POINTS[53]))?.let { nn ->

        nn.add(n)
        nn.add(Node(POINTS[54]))?.let { nnn ->

        nnn.add(nn)
        nnn.add(Node(POINTS[55]))?.let { nnnn ->

        nnnn.add(nnn)
        nnnn.add(Node(POINTS[56]))?.let { x ->

        x.add(nnnn)
        x.add(Node(POINTS[57]))?.let { xx ->

        xx.add(x)
        xx.add(Node(POINTS[58]))?.let { xxx ->

        xxx.add(xx)
        xxx.add(Node(POINTS[59]))?.let { xxxx ->

        xxxx.add(xxx)
    } } } } } } } }

    val navGraph = NavigationGraph(elev1)

    Log.i("NAVIGATIONTREE", navGraph.start?.toString()!!)

    return navGraph
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
