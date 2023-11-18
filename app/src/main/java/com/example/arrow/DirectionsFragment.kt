package com.example.arrow

import com.example.arrow.utils.ROOMS
import com.example.arrow.utils.distanceOf
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.example.sqlitedatabase.DataBaseHandler
import com.mapbox.geojson.Point

class DirectionsFragment(val context: BirdsEyeView) : Fragment(R.layout.fragment_directions) {
    private lateinit var etCurrLocSearchbar: EditText
    private lateinit var etDestinationSearchBar: EditText
    private lateinit var btSwitchLoc: Button
    private lateinit var svDirectionSuggestions: ScrollView
    private lateinit var svDSLinearLayout: LinearLayout
    private lateinit var dbHelper:  DataBaseHandler
    private lateinit var tvDirectionSuggestion: TextView
    private lateinit var vSuggestLine: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etCurrLocSearchbar = view.findViewById(R.id.etCurrLocSearchbar)
        etDestinationSearchBar = view.findViewById(R.id.etDestinationSearchbar)
        btSwitchLoc = view.findViewById(R.id.btSwitchLoc)
        svDirectionSuggestions = view.findViewById(R.id.svDirectionSuggestions)

        tvDirectionSuggestion = view.findViewById(R.id.tvDirectionSuggestion)
        vSuggestLine = view.findViewById(R.id.vSuggestLine)
        tvDirectionSuggestion.visibility = View.INVISIBLE
        vSuggestLine.visibility = View.INVISIBLE

        dbHelper = DataBaseHandler(requireContext())

        svDSLinearLayout = LinearLayout(requireContext())
        svDSLinearLayout.orientation = LinearLayout.VERTICAL
        svDirectionSuggestions.addView(svDSLinearLayout)

        etCurrLocSearchbar.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                suggestion(s)
//                findPath()
            }

        })
        etCurrLocSearchbar.setOnFocusChangeListener{ v: View, hasFocus: Boolean ->
            focusChangedListener(v,hasFocus)
        }
        etDestinationSearchBar.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                suggestion(s)
//                findPath()
            }
        })
        etDestinationSearchBar.setOnFocusChangeListener{ v: View, hasFocus: Boolean ->
            focusChangedListener(v,hasFocus)
        }

        btSwitchLoc.setOnClickListener{
            val t = etCurrLocSearchbar.text
            etCurrLocSearchbar.text = etDestinationSearchBar.text
            etDestinationSearchBar.text = t
        }
    }

    private fun findPath() {
        assert(false) { "TODO Implement" }
        if (etCurrLocSearchbar.text.toString().isEmpty() ||
        etDestinationSearchBar.text.toString().isEmpty()) return

        val sbCurrLoc = etCurrLocSearchbar.text.toString()
        val sbDestLoc = etDestinationSearchBar.text.toString()
        val db = dbHelper.readableDatabase
        val dbCurrLoc = db.rawQuery("SELECT * FROM MapIndex WHERE RoomID LIKE '%$sbCurrLoc%'",
            null)
        val dbDestLoc = db.rawQuery("SELECT * FROM MapIndex WHERE RoomID LIKE '%$sbDestLoc%'",
            null)

        if (dbCurrLoc == null || dbCurrLoc.count == 0 ||
                dbDestLoc == null || dbCurrLoc.count == 0) {
            dbCurrLoc?.close()
            dbDestLoc?.close()
            db.close()
            return
        }
//        toROOMSPoint(stIndexMap[i])?.let {
//            context.findRoute(it)
//        }
        dbCurrLoc.close()
        dbDestLoc.close()
        db.close()
    }
    private fun toROOMSPoint(s: String): Point? {
        val indexes = s.split(',')
        Log.i("DirectionsFragmentIndexes", "" + indexes)
        val userLoc = context.userLoc
        if (userLoc == null) {
            Log.w(
                "DirectionsFragment",
                "User Location is Null"
            )
            return null
        }
        var closest: Point? = null
        var closestDist = 1000.0
        for (i in indexes) {
            i.toIntOrNull()?.let {
                if (closest == null ||
                closestDist > distanceOf(userLoc, ROOMS[it])) {
                    closest = ROOMS[it]
                    closestDist = distanceOf(userLoc, ROOMS[it])
                }
            }
        }
        return closest
    }

    private fun focusChangedListener(v: View, hasFocus: Boolean) {
        if (hasFocus) {
            tvDirectionSuggestion.visibility = View.VISIBLE
            vSuggestLine.visibility = View.VISIBLE
        }
        else {
            tvDirectionSuggestion.visibility = View.INVISIBLE
            vSuggestLine.visibility = View.INVISIBLE
        }
    }

    fun suggestion(s: Editable?) {
        val db = dbHelper.readableDatabase
        svDSLinearLayout.removeAllViews()

        Log.i("DirectionsFragment", s.toString())
        val t = s.toString()
        if (t.isEmpty()) return
        val dbIndexMap = db.rawQuery("SELECT * FROM MapIndex WHERE RoomID LIKE '%$t%'",
            null)
        if (dbIndexMap == null || dbIndexMap.count == 0) {
            dbIndexMap?.close()
            db.close()
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
        assert(stIndexMap.size == names.size) {
            "Must be same size"
        }

//        var suggestionLayout = layoutInflater.inflate(R.layout.suggestion_layout,null) as LinearLayout
//        suggestionLayout.layoutParams = LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.WRAP_CONTENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        )
//        suggestionLayout.setPadding(0, 16, 0, 16)
//        Log.i("DirectionsFragment", "" + suggestionLayout.orientation)
//        var it = suggestionLayout.children.iterator()
//        var ivSug = it.next() as ImageView
//        val imageParams = LinearLayout.LayoutParams(
//            100,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        )
//        imageParams.setMargins(0, 0, 16, 0)
//        ivSug.layoutParams = imageParams
//
//        Log.i("DirectionsFragment", "" + ivSug.layoutParams.width + " " + ivSug.layoutParams.height)
//        var etSug = it.next() as TextView
//        etSug.setText("Hello")
//        etSug.setOnClickListener {
//            val i = it as TextView
//            Log.i("DirectionsFragment", "" + i)
//        }
//
//        svDSLinearLayout.addView(suggestionLayout)

//        suggestionLayout = layoutInflater.inflate(R.layout.suggestion_layout,null) as LinearLayout
//        it = suggestionLayout.children.iterator()
//        ivSug = it.next() as ImageView
//        etSug = it.next() as TextView
//        etSug.setText("Hello2")
//        etSug.setOnClickListener {
//            val i = it as TextView
//            Log.i("DirectionsFragment", "" + i)
//        }
//
//        svDSLinearLayout.addView(suggestionLayout)


        for (i in 0 until stIndexMap.size) {
            val hLayout = LinearLayout(requireContext())
//            hLayout.layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            )
            hLayout.orientation = LinearLayout.HORIZONTAL
            hLayout.setPadding(0, 16, 0, 16)
            val imageView = ImageView(requireContext())
            imageView.setImageResource(R.drawable.baseline_search_24)
            val imageParams = LinearLayout.LayoutParams(
                100,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            imageParams.setMargins(0, 0, 16, 0)
            imageView.layoutParams = imageParams

            val textView = TextView(requireContext())
            textView.text = names[i]
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            textView.textSize = 18f
            textView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            textView.setOnClickListener {
                toROOMSPoint(stIndexMap[i])?.let { destination: Point ->
                    context.userLoc?.let { userLoc: Point ->
                        context.findRoute(userLoc, destination)
                    }
                }
            }

            hLayout.addView(imageView)
            hLayout.addView(textView)
            svDSLinearLayout.addView(hLayout)
        }
        dbIndexMap.close()
        db.close()
    }
}