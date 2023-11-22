package com.example.arrow.utils

import android.graphics.Color
import android.util.Log
import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs

val TAG = "NAVIGATIONGRAPH"
val GEO_SOURCE_ID_01 = "DirectionSource01"
val DIRECTION_LAYER_ID_01 = "DirectionLayerSource01"
val GEO_SOURCE_ID_02 = "DirectionSource02"
val DIRECTION_LAYER_ID_02 = "DirectionLayerSource02"
//val GEO_SOURCE_ID_03 = "DirectionSource03"
//val DIRECTION_LAYER_ID_03 = "DirectionLayerSource03"

val POINTS = listOf(
Point.fromLngLat(120.98936069082802192, 14.60273546261073285), // elevator
Point.fromLngLat(120.98938236105199451, 14.60277880959188757), // elevator

Point.fromLngLat(120.98940104502092652, 14.60281618315584851),
Point.fromLngLat(120.98938665372705259, 14.60282292041240026),
Point.fromLngLat(120.98941371460200855, 14.60281025191602033), // 4

// elev2sidebot
Point.fromLngLat(120.98944929186430386, 14.60279359648773756), // 5
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
Point.fromLngLat(120.98888468613982639, 14.60285561374692165), // 53
Point.fromLngLat(120.98887377140628985, 14.60283378099789964),
Point.fromLngLat(120.98886138328832374, 14.60280900103434654),
Point.fromLngLat(120.98890562875234878, 14.60278828760344894), // 56
Point.fromLngLat(120.98896064170088493, 14.60276253339217511),
Point.fromLngLat(120.98899448041311189, 14.60274669185942642),
Point.fromLngLat(120.98904972806279545, 14.60272082776834957), // 59
)

val ROOMS = listOf(
// 801
Point.fromLngLat(120.98897628824259698, 14.60303867951060042), // 0
Point.fromLngLat(120.98901122138191511, 14.60302232839303116),
// 802
Point.fromLngLat(120.98903854730180285, 14.60300952853645384),
Point.fromLngLat(120.98907689527234766, 14.60299158366277439), // 3
// 803
Point.fromLngLat(120.98910314468635363, 14.60297928477821472),
Point.fromLngLat(120.98913922168352997, 14.60296240260303513), // 5
// 804
Point.fromLngLat(120.98916564973461618, 14.60295002566779665),
Point.fromLngLat(120.98920995000584355, 14.60292929542410256), // 7
// 805
Point.fromLngLat(120.98922725741829254, 14.60292118546547613),
Point.fromLngLat(120.98926962031819698, 14.60290136004681649), // 9
// 806
Point.fromLngLat(120.98929363237417078, 14.60289011010148208),
Point.fromLngLat(120.9893333023739217, 14.60287154658629127), // 11
// 807
Point.fromLngLat(120.98936068678133893, 14.60285871974829242),
Point.fromLngLat(120.98939673219122426, 14.60284185235428644), // 13
// 808
Point.fromLngLat(120.98942353653305304, 14.60282929692687226),
Point.fromLngLat(120.98945896113932008, 14.60281271906417366), // 15
// 809
Point.fromLngLat(120.98948569987423696, 14.6028001949241748),
Point.fromLngLat(120.98951977539392999, 14.60278424933739139), // 17
// 810
Point.fromLngLat(120.98954841808858873, 14.60277083590513669),
Point.fromLngLat(120.98958572714415993, 14.60275337719021671), // 19
// cr male - female
Point.fromLngLat(120.98962129380241493, 14.60273672187269689),
Point.fromLngLat(120.9896626788723637, 14.60271735766345635), // 21
// 811
Point.fromLngLat(120.98892312130102766, 14.60296640754689967),
// 812
Point.fromLngLat(120.98893019890223854, 14.60285476725510456),
Point.fromLngLat(120.98897773170747882, 14.60283252873598059), // 24
// 813
Point.fromLngLat(120.98901198110021937, 14.60281648008631983),
Point.fromLngLat(120.9890564003149791, 14.60279569948910172), // 26
// 814
Point.fromLngLat(120.98908836189924898, 14.60278072213567668),
Point.fromLngLat(120.98913464507965898, 14.60275906952130676), // 28
// 815
Point.fromLngLat(120.98916526415166572, 14.60274471986289058),
Point.fromLngLat(120.98921317449382684, 14.60272230715272457), // 30
// 816
Point.fromLngLat(120.9892479465624433, 14.60270601189087358),
Point.fromLngLat(120.98929037544655785, 14.60268616242812456), // 32
// 817
Point.fromLngLat(120.98932698180009027, 14.60266901370976633),
Point.fromLngLat(120.98936922112987702, 14.60264925292604943), // 34
// 818
Point.fromLngLat(120.98940557441331123, 14.60263222179350073),
Point.fromLngLat(120.98944702392901718, 14.60261283050776271), // 36
// 819
Point.fromLngLat(120.98948495622521193, 14.60259505915288969),
Point.fromLngLat(120.98952466814817797, 14.6025764807634566), // 38
// 820
Point.fromLngLat(120.9895586200888431, 14.60256057230071391),
Point.fromLngLat(120.98960743068786883, 14.60253773728676663), // 40
// elevator 1 - 2 - faculty
Point.fromLngLat(120.98938297670417796, 14.60272502366131775),
Point.fromLngLat(120.98940484926579586, 14.6027682736044504),
Point.fromLngLat(120.98889355357253805, 14.60276438426404333), // 43
// emergency 1 - 2
Point.fromLngLat(120.98895318227337725, 14.60302585432041944),
Point.fromLngLat(120.98903612286463272, 14.60269763889574257), // 45
// cr 2 female - male
Point.fromLngLat(120.98894857666996927, 14.60273861538169093),
Point.fromLngLat(120.98898263988034785, 14.60272266558838261), // 47
// stairs 1 - 2
Point.fromLngLat(120.98932931901177312, 14.60275014756102152),
Point.fromLngLat(120.98935119225413359, 14.60279339883573613), // 49
// stairs 3 - 4
Point.fromLngLat(120.98927989608382916, 14.60277328212144354),
Point.fromLngLat(120.98930176888865162, 14.60281653253094092), // 51
// escalator 1 - 2
Point.fromLngLat(120.98923219133646967, 14.60279561355931577),
Point.fromLngLat(120.98925406389875548, 14.60283886348851468), // 53
// escalator 3 - 4
Point.fromLngLat(120.98918250845456157, 14.60281887250618915),
Point.fromLngLat(120.98920438101558261, 14.60286212243142145), // 55
// deans office
Point.fromLngLat(120.98887054156791976, 14.60286244285996382),
Point.fromLngLat(120.98889162774632666, 14.60290413781859797),
// faculty room
Point.fromLngLat(120.98886011501883786, 14.60284182582272194), // 58
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
    return Math.abs((6371 * c) * 1000)
}

fun distanceOf(p1: Point, p2: Point): Double
{ return distanceOf(p1.latitude(),p1.longitude(),p2.latitude(),p2.longitude()) }

enum class Property(val value: Int) {
    Entry(1),
    Exit(2),
    Emergency(4),
    Faculty(8),
    Slow(16)
}

data class Node(val loc: Point, val property: Int = 0) {
     val neighbors: MutableList<Node> = mutableListOf()
     // distance in m
     val distance: MutableList<Double>  = mutableListOf()

     override fun toString(): String =
                  "[" + this.loc.longitude().toString() + ", " + this.loc.latitude().toString() + "]"

     fun debug(): String {
         val size = this.neighbors.size
         var l = this.neighbors.toString()
         return "{\nlocation: ${this.toString()},\nsize: $size,\n $l\n}"
     }

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

class NavigationGraph(base: Node) {
    // not the root node
    // the starting node is where the search will start
    var start = base
    var maxRouteSize = 2

    data class RouteNode(val from: Node? = null, val dist: Double = 100.0)

    fun searchFor(target: Point): Node? {
        val q: ArrayDeque<Node> = ArrayDeque<Node>()
        val visited: MutableSet<Node> = mutableSetOf<Node>()
        q.add(start)
        while (q.isNotEmpty()) {
            val curr = q.first()
            q.removeFirstOrNull()

            for (i in curr.neighbors.indices) {
                if ( visited.contains(curr.neighbors[i]) ) continue
                if (curr.neighbors[i].loc == target) {
                    return curr.neighbors[i]
                }
                q.add(curr.neighbors[i])
            }
            visited.add(curr)
        }
        return null
    }

    fun searchNearest(target: Point, priority: Int = 0, exact: Boolean = false): Node? {
        val q: ArrayDeque<Node> = ArrayDeque<Node>()
        val visited: MutableSet<Node> = mutableSetOf<Node>()
        var cDist = 10000.0
        var cLoc: Node? = null
        q.add(start)

        while (q.isNotEmpty()) {
            val curr = q.first()
            q.removeFirstOrNull()

            for (i in curr.neighbors.indices) {
                if ( visited.contains(curr.neighbors[i]) ) continue
                val nDist = distanceOf(
                    target.latitude(),
                    target.longitude(),
                    curr.neighbors[i].loc.latitude(),
                    curr.neighbors[i].loc.longitude()
                )

                if (priority != 0) {
                    if (cLoc == null && curr.neighbors[i].property and priority != 0 ) {
                        cDist = nDist
                        cLoc = curr.neighbors[i]
                    }
                    else if (curr.neighbors[i].property == priority && exact) {
                        cDist = nDist
                        cLoc = curr.neighbors[i]
                    }
                    else if (cDist > nDist && curr.neighbors[i].property and priority != 0 && cLoc != null ) {
                        if (curr.neighbors[i].property == priority ||
                        ( ( cLoc.property and priority ) != 0 && cLoc.property != priority) ) {
                            cDist = nDist
                            cLoc = curr.neighbors[i]
                        }
                    }
                }
                else if (cDist > nDist) {
                    cDist = nDist
                    cLoc = curr.neighbors[i]
                }

                q.add(curr.neighbors[i])
            }
            visited.add(curr)
        }
        return cLoc
    }

    fun findPath(destination: Point, disabled: MutableSet<Node>): Pair< Double, List<Node> > {
        val q: ArrayDeque<Node> = ArrayDeque()
        val visited: MutableSet<Node> = mutableSetOf()
        val calcNode: MutableMap<Node, RouteNode> = mutableMapOf<Node, RouteNode>()
        q.add(start)
        calcNode.put(start, RouteNode(null,0.0))

        while(q.isNotEmpty()) {
            val curr = q.first()
            q.removeFirstOrNull()
            for (i in curr.neighbors.indices) {
//                Log.i(TAG+"ROUTE", " " + i + " " + q.size)
                if ( disabled.contains(curr.neighbors[i]) ) continue
                if ( visited.contains(curr.neighbors[i]) ) continue

                assert(calcNode.get(curr) != null)
                if (calcNode.get(curr.neighbors[i]) != null) {
                    if (calcNode.get(curr.neighbors[i])!!.dist >
                        ( calcNode.get(curr)!!.dist + curr.distance[i] )) {
                        calcNode.put(
                            curr.neighbors[i],
                            RouteNode(curr, calcNode.get(curr)!!.dist + curr.distance[i])
                        )
                    }
                }
                else
                    calcNode.put(
                        curr.neighbors[i],
                        RouteNode(curr, calcNode.get(curr)!!.dist + curr.distance[i])
                    )

                if (curr.neighbors[i].loc == destination) {
                    val a = mutableListOf<Node>()
                    fun toListRNode(v: RouteNode?) {
                        if (v == null || v.from == null) return
                        a.add(v.from)
                        return toListRNode(calcNode.get(v.from))
                    }
                    a.add( curr.neighbors[i] )
                    toListRNode(calcNode.get(curr.neighbors[i]))
                    val d = calcNode.get( curr.neighbors[i] )!!.dist
                    Log.i(TAG,"Found Route: \n" + a.reversed())
                    return Pair(d, a.reversed().toMutableList() )
                }
                q.add(curr.neighbors[i])
            }

            visited.add(curr)
        }
        return Pair(0.0,listOf())
    }

    public fun requestRoute(
        origin: Point,
        destination: Point,
        // NOTE values in enum Property
        // NOTE use 'or' to apply multiple properties
        priority: Int = 0,
        customStart: Point? = null
    ): List<List<Point>> {
        if (customStart == null){
            val nStart = searchNearest(origin, priority)
            // assert(nStart != null)
            if (nStart != null)
                start = nStart
        }
        else {
            val nStart = searchFor(customStart)
            if (nStart != null)
                start = nStart
        }

        val routes: MutableList<Pair< Double,List<Node> >> = mutableListOf()
        val disabled: MutableSet<Node> = mutableSetOf()

        for (i in 1..maxRouteSize) {
            val froute = findPath(destination, disabled)
            Log.i(TAG+"froute", "" + froute)
            if (froute.second.isNotEmpty()) {
                routes.add(froute)
                if (froute.second.size >= 3) {
                    disabled.add(froute.second[2])
                }
            }
        }

        val sortedRoutes = routes.sortedWith (
            object: Comparator<Pair< Double,List<Node> >> {
            override fun compare(o1: Pair< Double,List<Node> >, o2: Pair< Double,List<Node> >) : Int {
                if (o1.first > o2.first) {
                    return 1
                }
                return -1
            }
        })

        val ret = mutableListOf<MutableList<Point>>()
        for (i in sortedRoutes) {
            val r = mutableListOf<Point>()
            r.add(origin)
            for (j in i.second) {
                r.add(j.loc)
            }
            ret.add(r)
        }
        return ret
    }
}

fun setupNavigationGraph(): NavigationGraph {
    // assert(false) { "Apply emergency property" }
    // Log.i("setupNavigationGraph","" + POINTS.size)
    val entryexit: Int = Property.Entry.value or Property.Exit.value
    val elev1 = Node(POINTS[0])
    val elev2 = Node(POINTS[1])
    elev1.add(elev2)
    elev1.add(Node(ROOMS[41], entryexit))?.let { it.add(elev1) }
    elev1.add(Node(ROOMS[48], entryexit or Property.Slow.value))?.let { it.add(elev1) }
    elev2.add(elev1)
    elev2.add(Node(ROOMS[42], entryexit))?.let { it.add(elev2) }
    elev2.add(Node(ROOMS[49], entryexit or Property.Slow.value))?.let { it.add(elev2) }
    val elev2sideMiddle = elev2.add(Node(POINTS[2]))
    elev2sideMiddle!!.add(elev2)
    val elev2SideTop = Node(POINTS[3])
    val elev2SideBot = Node(POINTS[4])
    elev2sideMiddle?.add(elev2SideTop)
    elev2sideMiddle?.add(elev2SideBot)

    elev2SideBot.add(Node(ROOMS[14]))?.let { it.add(elev2SideBot) }
    elev2SideBot.add(elev2sideMiddle!!)
    elev2SideBot.add(Node(POINTS[5]))?.let { n ->

        n.add(elev2SideBot)
        n.add(Node(ROOMS[15]))?.let { it.add(n) }
        n.add(Node(POINTS[6]))?.let { nn ->

        nn.add(n)
        nn.add(Node(ROOMS[16]))?.let { it.add(nn) }
        nn.add(Node(POINTS[7]))?.let { nnn ->

        nnn.add(nn)
        nnn.add(Node(ROOMS[17]))?.let { it.add(nnn) }
        nnn.add(Node(POINTS[8]))?.let { nnnn ->

        nnnn.add(nnn)
        nnnn.add(Node(ROOMS[18]))?.let { it.add(nnnn) }
        nnnn.add(Node(POINTS[9]))?.let { x ->

        x.add(nnnn)
        x.add(Node(ROOMS[19]))?.let { it.add(x) }
        x.add(Node(POINTS[10]))?.let { xx ->

        xx.add(x)
        xx.add(Node(ROOMS[20]))?.let { it.add(xx) }
        xx.add(Node(POINTS[11]))?.let { z ->

        z.add(xx)
        z.add(Node(ROOMS[21]))?.let { it.add(z) }
    } } } } } } }

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
        n.add(Node(ROOMS[51], entryexit or Property.Slow.value))?.let { it.add(n) }
        n.add(Node(ROOMS[53], entryexit or Property.Slow.value))?.let { it.add(n) }
        n.add(conn127)?.let { nn ->

        nn.add(n)
        nn.add(Node(ROOMS[50], entryexit or Property.Slow.value))?.let { it.add(nn) }
        nn.add(Node(ROOMS[52], entryexit or Property.Slow.value))?.let { it.add(nn) }
        nn.add(conn4)?.let { nnn -> nnn.add(nn) }
    } }
    conn2.add(conn128)?.let { n ->
        n.add(conn2)
        n.add(Node(ROOMS[55]))?.let { it.add(n) }
        n.add(conn129)?.let { nn ->

        nn.add(n)
        nn.add(Node(ROOMS[54]))?.let { it.add(nn) }
        nn.add(conn5)?.let { nnn -> nnn.add(nn) }
    } }

    conn6.add(Node(POINTS[51]))?.let { n ->
        n.add(conn6)
        n.add(Node(ROOMS[22]))?.let { it.add(n) }
        n.add(conn3)?.let { nn ->

        nn.add(Node(ROOMS[44], entryexit or Property.Emergency.value))?.let { it.add(nn) }
        nn.add(n)
    } }

    elev2SideTop.add(Node(ROOMS[13]))?.let { it.add(elev2SideTop) }
    elev2SideTop.add(elev2sideMiddle!!)
    elev2SideTop.add(Node(POINTS[12]))?.let { n ->
        n.add(elev2SideTop)
        n.add(Node(ROOMS[12]))?.let { it.add(n) }
        n.add(Node(POINTS[13]))?.let { nn ->

        nn.add(n)
        nn.add(Node(ROOMS[11]))?.let { it.add(nn) }
        nn.add(conn1)?.let { nnn ->

        nnn.add(nn)
        nnn.add(Node(POINTS[15]))?.let { nnnn ->

        nnnn.add(nnn)
        nnnn.add(Node(ROOMS[10]))?.let { it.add(nnnn) }
        nnnn.add(Node(POINTS[16]))?.let { x ->

        x.add(nnnn)
        x.add(Node(ROOMS[9]))?.let { it.add(x) }
        x.add(Node(POINTS[17]))?.let { xx->

        xx.add(x)
        xx.add(Node(ROOMS[8]))?.let { it.add(xx) }
        xx.add(conn2)?.let { xxx ->

        xxx.add(xx)
        xxx.add(Node(ROOMS[7]))?.let { it.add(xxx) }
        xxx.add(Node(POINTS[19]))?.let { xxxx ->

        xxxx.add(xxx)
        xxxx.add(Node(ROOMS[6]))?.let { it.add(xxxx) }
        xxxx.add(Node(POINTS[20]))?.let { y ->

        y.add(xxxx)
        y.add(Node(ROOMS[5]))?.let { it.add(y) }
        y.add(Node(POINTS[21]))?.let { yy ->

        yy.add(y)
        yy.add(Node(ROOMS[4]))?.let { it.add(yy) }
        yy.add(Node(POINTS[22]))?.let { yyy ->

        yyy.add(yy)
        yyy.add(Node(ROOMS[3]))?.let { it.add(yyy) }
        yyy.add(Node(POINTS[23]))?.let { yyyy ->

        yyyy.add(yyy)
        yyyy.add(Node(ROOMS[2]))?.let { it.add(yyyy) }
        yyyy.add(Node(POINTS[24]))?.let { z ->

        z.add(yyyy)
        z.add(Node(ROOMS[1]))?.let { it.add(z) }
        z.add(conn3)?.let { zz ->

        zz.add(Node(ROOMS[0]))?.let { it.add(zz) }
        zz.add(z)
    } } } } } } } } } } } } } }

    val elev1sideMiddle = elev1.add(Node(POINTS[30]))
    elev1sideMiddle!!.add(elev1)
    val elev1SideTop = Node(POINTS[31])
    val elev1SideBot = Node(POINTS[32])
    elev1sideMiddle?.add(elev1SideTop)
    elev1sideMiddle?.add(elev1SideBot)
    elev1sideMiddle?.add(Node(ROOMS[33]))?.let { it.add(elev1sideMiddle!!) }
    elev1SideTop.add(elev1sideMiddle!!)
    elev1SideBot.add(elev1sideMiddle!!)

    elev1SideBot.add(Node(ROOMS[34]))?.let { it.add(elev1SideBot) }
    elev1SideBot.add(Node(POINTS[33]))?.let { n ->

        n.add(elev1SideBot)
        n.add(Node(ROOMS[35]))?.let { it.add(n) }
        n.add(Node(POINTS[34]))?.let { nn ->

        nn.add(n)
        nn.add(Node(ROOMS[36]))?.let { it.add(nn) }
        nn.add(Node(POINTS[35]))?.let { nnn ->

        nnn.add(nn)
        nnn.add(Node(ROOMS[37]))?.let { it.add(nnn) }
        nnn.add(Node(POINTS[36]))?.let { nnnn ->

        nnnn.add(nnn)
        nnnn.add(Node(ROOMS[38]))?.let { it.add(nnnn) }
        nnnn.add(Node(POINTS[37]))?.let { z ->

        z.add(nnnn)
        z.add(Node(ROOMS[39]))?.let { it.add(z) }
        z.add(Node(POINTS[38]))?.let { zz ->

        zz.add(z)
        zz.add(Node(ROOMS[40]))?.let { it.add(zz) }
    } } } } } }

    elev1SideTop.add(Node(ROOMS[32]))?.let { it.add(elev1SideTop) }
    elev1SideTop.add(Node(POINTS[39]))?.let { n ->
        n.add(elev1SideTop)
        n.add(Node(ROOMS[31]))?.let { it.add(n) }
        n.add(conn4)?.let { nn ->

        nn.add(n)
        nn.add(Node(POINTS[41]))?.let { nnn ->

        nnn.add(nn)
        nnn.add(Node(ROOMS[30]))?.let { it.add(nnn) }
        nnn.add(Node(POINTS[42]))?.let { nnnn ->

        nnnn.add(nnn)
        nnnn.add(Node(ROOMS[29]))?.let { it.add(nnnn) }
        nnnn.add(Node(POINTS[43]))?.let { x ->

        x.add(nnnn)
        x.add(Node(ROOMS[28]))?.let { it.add(x) }
        x.add(conn5)?.let { xx ->

        xx.add(x)
        xx.add(Node(POINTS[45]))?.let { xxx ->

        xxx.add(xx)
        xxx.add(Node(ROOMS[27]))?.let { it.add(xxx) }
        xxx.add(Node(POINTS[46]))?.let { xxxx ->

        xxxx.add(xxx)
        xxxx.add(Node(ROOMS[26]))?.let { it.add(xxxx) }
        xxxx.add(Node(POINTS[47]))?.let { z ->

        z.add(xxxx)
        z.add(Node(ROOMS[25]))?.let { it.add(z) }
        z.add(Node(POINTS[48]))?.let { zz ->

        zz.add(z)
        zz.add(Node(ROOMS[24]))?.let { it.add(zz) }
        zz.add(Node(POINTS[49]))?.let { zzz ->

        zzz.add(zz)
        zzz.add(Node(ROOMS[23]))?.let { it.add(zzz) }
        zzz.add(conn6)?.let { zzzz ->

        zzzz.add(zzz)
    } } } } } } } } } } } }

    conn6.add(Node(ROOMS[57]))?.let { it.add(conn6) }
    conn6.add(Node(POINTS[52]))?.let { n ->
        n.add(conn6)
        n.add(Node(POINTS[53]))?.let { nn ->

        nn.add(n)
        nn.add(Node(ROOMS[56]))?.let { it.add(nn) }
        nn.add(Node(POINTS[54]))?.let { nnn ->

        nnn.add(nn)
        nnn.add(Node(ROOMS[58]))?.let { it.add(nnn) }
        nnn.add(Node(POINTS[55]))?.let { nnnn ->

        nnnn.add(nnn)
        nnnn.add(Node(POINTS[56]))?.let { x ->

        x.add(nnnn)
        x.add(Node(ROOMS[43], entryexit or Property.Faculty.value))?.let { it.add(x) }
        x.add(Node(POINTS[57]))?.let { xx ->

        xx.add(x)
        xx.add(Node(ROOMS[46]))?.let { it.add(xx) }
        xx.add(Node(POINTS[58]))?.let { xxx ->

        xxx.add(xx)
        xxx.add(Node(ROOMS[47]))?.let { it.add(xxx) }
        xxx.add(Node(POINTS[59]))?.let { xxxx ->

        xxxx.add(Node(ROOMS[45], entryexit or Property.Emergency.value))?.let { it.add(xxxx) }
        xxxx.add(xxx)
    } } } } } } } }

    val navGraph = NavigationGraph(elev1)

    // Log.i(TAG, navGraph.start?.debug()!!)

    return navGraph
}


fun initDirectionLayer(loadedStyle: Style) {
    loadedStyle.addSource(GeoJsonSource.Builder(GEO_SOURCE_ID_01).build())
    loadedStyle.addSource(GeoJsonSource.Builder(GEO_SOURCE_ID_02).build())
    loadedStyle.addLayer(
        LineLayer(DIRECTION_LAYER_ID_02, GEO_SOURCE_ID_02)
            .lineWidth(3.5)
            .lineColor(Color.GRAY)
            .lineOpacity(.5)
    )
    loadedStyle.addLayer(
        LineLayer(DIRECTION_LAYER_ID_01, GEO_SOURCE_ID_01)
            .lineWidth(4.5)
            .lineColor(Color.GREEN)
    )
}

fun drawDirection(loadedStyle: Style, route: List<Point>, id: String) {
    loadedStyle.getSourceAs<GeoJsonSource>(id)?.let {
        it.feature(
            Feature.fromGeometry(LineString.fromLngLats(route.toMutableList())),
        )
    }
}
