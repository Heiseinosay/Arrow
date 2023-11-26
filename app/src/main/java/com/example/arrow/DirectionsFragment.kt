package com.example.arrow

import android.database.sqlite.SQLiteDatabase
import com.example.arrow.utils.getROOMS
import com.example.arrow.utils.distanceOf
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.example.sqlitedatabase.DataBaseHandler
import com.mapbox.geojson.Point

const val YOUR_LOCATION = "Your Location"
const val CURR_LOC = "CurrLoc"
const val DEST_LOC = "DestLoc"
const val IS_PATH_ENABLE =  "IsPathEnable"

class DirectionsFragment(val context: BirdsEyeView) : Fragment(R.layout.fragment_directions) {
    private lateinit var etInputs: List<EditText>
    private lateinit var svDSLinearLayout: LinearLayout

    private lateinit var dbHelper:  DataBaseHandler

    private lateinit var tvDirectionSuggestion: TextView
    private lateinit var vSuggestLine: View

    private lateinit var btSwitchLoc: Button
    private lateinit var tvCancel: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etInputs = listOf(
            view.findViewById(R.id.etCurrLocSearchbar),
            view.findViewById(R.id.etDestinationSearchbar)
        )

        btSwitchLoc = view.findViewById(R.id.btSwitchLoc)
        tvCancel = view.findViewById(R.id.tvCancel)
        val svDirectionSuggestions: ScrollView = view.findViewById(R.id.svDirectionSuggestions)

        tvDirectionSuggestion = view.findViewById(R.id.tvDirectionSuggestion)
        vSuggestLine = view.findViewById(R.id.vSuggestLine)
        tvDirectionSuggestion.visibility = View.INVISIBLE
        vSuggestLine.visibility = View.INVISIBLE

        dbHelper = DataBaseHandler(requireContext())

        svDSLinearLayout = LinearLayout(requireContext())
        svDSLinearLayout.orientation = LinearLayout.VERTICAL
        svDirectionSuggestions.addView(svDSLinearLayout)

        fun setListeners(id: Int) {
            val ev = etInputs[id]
            ev.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
                override fun afterTextChanged(s: Editable?) {
                    val db = dbHelper.readableDatabase
                    suggestion(db, s, id)
                    db.close()
                    context.updateDirFragBundle(if (id == 0) CURR_LOC else DEST_LOC, s.toString())
                }

            })
            ev.setOnFocusChangeListener{ v: View, hasFocus: Boolean ->
                focusChangedListener(v,hasFocus)
                val db = dbHelper.readableDatabase
                suggestion(db, ev.text, id)
                db.close()
            }
            ev.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    findPath()
                }
                false
            }
        }
        setListeners(0)
        setListeners(1)

        Log.i("DirectionFragmentBundle", "$arguments")
        if (arguments != null) {
            etInputs[0].setText(requireArguments().getString(CURR_LOC,""))
            etInputs[1].setText(requireArguments().getString(DEST_LOC,""))
            svDSLinearLayout.removeAllViews()
            if (requireArguments().getString(IS_PATH_ENABLE).toBoolean()) {
                tvCancel.visibility = View.VISIBLE
            }
        }

        tvCancel.setOnClickListener{
            tvCancel.visibility = View.INVISIBLE
            etInputs[0].setText("")
            etInputs[1].setText("")
            context.disablePath()
            Log.i("DirectionFragmentButton", "cancel ${context.isPathingEnabled}")
            context.updateDirFragBundle(IS_PATH_ENABLE,context.isPathingEnabled.toString())
        }

        btSwitchLoc.setOnClickListener{
            Log.i("DirectionFragmentButton", "Switch")
            if (etInputs[0].text.toString() == YOUR_LOCATION) {
                Toast.makeText(requireContext(), "Cannot Switch Location", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val t = etInputs[0].text
            etInputs[0].text = etInputs[1].text
            etInputs[1].text = t
            svDSLinearLayout.removeAllViews()
            findPath()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (arguments != null) {
            requireArguments().putString(CURR_LOC,etInputs[0].text.toString())
            requireArguments().putString(DEST_LOC,etInputs[1].text.toString())
        }
    }

    private fun findPath() {
        val sbCurrLoc = etInputs[0].text.toString()
        val sbDestLoc = etInputs[1].text.toString()
        if (sbCurrLoc.isEmpty() || sbDestLoc.isEmpty()) return

        var findFirst: Point? = null
        var destination: Point? = null
        val db = dbHelper.readableDatabase
        val userLoc = context.checkLocationEnabled()

        fun getStringFrom(query: String): Pair<String, Int> {
            val res = db.rawQuery(query, null)
            if (res == null || res.count == 0) {
                res?.close()
                return Pair("",-1)
            }
            res.moveToNext()
            val columnIndex = res.getColumnIndex("stIndex")
            val columnFloor = res.getColumnIndex("Floor")
            val ret = res.getString(columnIndex)
            val floor = res.getInt(columnFloor)
            res.close()
            return Pair(ret, floor)
        }

        var originFloor = -1
        if (sbCurrLoc != YOUR_LOCATION) {
            Log.i("DestinationFragmentFindPath", "sbCurrLoc: $sbCurrLoc")
            val pret = getStringFrom("SELECT * FROM MapIndex join coords on MapIndex.RoomID = coords.RoomID WHERE MapIndex.RoomID LIKE '$sbCurrLoc'")
            val ret = pret.first
            originFloor = pret.second
            if (ret.isEmpty()) {
                db.close()
                return
            }
            findFirst = toROOMSPoint(ret,userLoc)
        }

        Log.i("DestinationFragmentFindPath", "sbDestLoc: $sbDestLoc")
        val (ret, floor) = getStringFrom("SELECT * FROM MapIndex join coords on MapIndex.RoomID = coords.RoomID WHERE MapIndex.RoomID LIKE '$sbDestLoc'")
        if (ret.isEmpty()) {
            db.close()
            return
        }
        if (findFirst != null) destination = toROOMSPoint(ret,userLoc,findFirst)
        else destination = toROOMSPoint(ret,userLoc)
        if (originFloor != floor && originFloor >= 1 && originFloor <= 10) context.allTextViews[originFloor-1].performClick()

        db.close()
        if (destination == null) return
        Log.i("DirectionsFragmentFindPath", "$findFirst $destination")

        val handler = Handler(requireContext().mainLooper)
        context.findRoute(findFirst, destination, floor) { isPathEnabled ->
            Log.i("FindRoute", "Callable")
            handler.post {
                context.updateDirFragBundle(IS_PATH_ENABLE,isPathEnabled.toString())
                if (isPathEnabled) {
                    Log.i("FindRoute", "Callable show cancel")
                    tvCancel.visibility = View.VISIBLE
                } else {
                    Log.i("FindRoute", "Callable hide cancel")
                    tvCancel.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun toROOMSPoint(s: String,userLoc: Point?, cmp: Point? = null): Point? {
        val indexes = s.split(',')
        Log.i("DirectionsFragmentIndexes", "" + indexes)
        if (userLoc == null) {
            Log.w(
                "DirectionsFragment",
                "User Location is Null"
            )
            return null
        }
        var closest: Point? = null
        var closestDist = 1000.0
        val ROOMS = getROOMS()
        for (i in indexes) {
            i.toIntOrNull()?.let {
                Log.i("DirectionsFragment", "New dist ${ distanceOf(userLoc, ROOMS[it]) }")
                if (closest == null ||
                closestDist > distanceOf(if( cmp != null ) cmp else userLoc, ROOMS[it])) {
                    Log.i("DirectionsFragment", "New Closest ${ distanceOf(userLoc, ROOMS[it]) }")
                    closest = ROOMS[it]
                    closestDist = distanceOf(userLoc, ROOMS[it])
                }
            }
        }
        Log.w(
            "DirectionsFragment",
            "closest is $closest"
        )
        return closest
    }

    private fun focusChangedListener(_v: View, hasFocus: Boolean) {
        if (hasFocus) {
            tvDirectionSuggestion.visibility = View.VISIBLE
            vSuggestLine.visibility = View.VISIBLE
        }
        else {
            tvDirectionSuggestion.visibility = View.INVISIBLE
            vSuggestLine.visibility = View.INVISIBLE
        }
        svDSLinearLayout.removeAllViews()
    }

    fun suggestion(db: SQLiteDatabase,s: Editable?, evId: Int) {
        fun createSuggestion(name: String, image: Int = -1): Pair<LinearLayout, TextView> {
            val suggestionLayout = layoutInflater.inflate(R.layout.suggestion_layout,null)
                    as LinearLayout
            suggestionLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val it = suggestionLayout.children.iterator()

            val ivSug = it.next() as ImageView
            if (image != -1) ivSug.setImageResource(image)
            val imageParams = LinearLayout.LayoutParams(
                100,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            imageParams.setMargins(0, 0, 16, 0)
            ivSug.layoutParams = imageParams

            val tvSug = it.next() as TextView
            tvSug.setText(name)
            return Pair(suggestionLayout, tvSug)
        }
        svDSLinearLayout.removeAllViews()

        if (etInputs[0].text.toString() != YOUR_LOCATION && evId == 0)  {
            val (suggestionLayout, tvSug) = createSuggestion(YOUR_LOCATION, R.drawable.my_location_24px)
            tvSug.setOnClickListener {
                Log.i("DirectionsFragment", "" + it)
                etInputs[evId].setText(YOUR_LOCATION)
                Log.i("DirectionsFragment", "ID: " + evId)
                findPath()
            }
            svDSLinearLayout.addView(suggestionLayout)
        }

        val t = s.toString()
        if (t.isEmpty()) return
        val dbIndexMap = db.rawQuery("SELECT * FROM MapIndex WHERE RoomID LIKE '%$t%'",
            null)
        if (dbIndexMap == null || dbIndexMap.count == 0) {
            dbIndexMap?.close()
            return
        }

        val columnRoomID = dbIndexMap.getColumnIndex("RoomID")
        val columnstIndex = dbIndexMap.getColumnIndex("stIndex")

        assert(columnstIndex != -1 || columnRoomID != -1) {
            "Database Column Does not Exist"
        }

        val stIndexMap = mutableListOf<String>()
        val names = mutableListOf<String>()
        while (dbIndexMap.moveToNext()) {
            stIndexMap.add( dbIndexMap.getString(columnstIndex) )
            names.add( dbIndexMap.getString(columnRoomID) )
        }

        for (i in 0 until stIndexMap.size) {
            val (suggestionLayout, tvSug) = createSuggestion(names[i])

            tvSug.setOnClickListener {
                Log.i("DirectionsFragment", "" + it)
                etInputs[evId].setText(names[i])
                Log.i("DirectionsFragment", "ID: " + evId)
                findPath()
            }

            svDSLinearLayout.addView(suggestionLayout)
        }
        dbIndexMap.close()
    }
}
