package com.example.arrow

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sqlitedatabase.DataBaseHandler
import com.google.android.material.bottomsheet.BottomSheetBehavior

interface FragmentToActivitySearch {
    fun onValuePassed(txtSearch: String)
}
class ExploreFragment : Fragment(R.layout.fragment_explore), FragmentToActivitySearch{
    private lateinit var viewModel: ViewModel
    private var txtSearch: String = ""

    // FOR PASSING VALUES FROM EXPLOREFRAGMENT TO BIRDSEYEVIEW
    override fun onValuePassed(txtSearch: String) {
        this.txtSearch = txtSearch
    }
    fun passValueToActivity() {
        (activity as? FragmentToActivitySearch)?.onValuePassed(txtSearch)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var searchValue:String = ""
        val profileImage = view.findViewById<ImageView>(R.id.imageView7)
        val etSearchBar = view.findViewById<EditText>(R.id.etSearchBar)
        profileImage.setImageDrawable(ProfileObjects.profileImage)
        viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        viewModel.imageDrawable.observe(viewLifecycleOwner) { newDrawable ->
            profileImage.setImageDrawable(newDrawable)
        }

        viewModel.hasFocus.observe(viewLifecycleOwner){ clearFocus ->
            if (clearFocus == false){
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(etSearchBar.windowToken, 0)
                etSearchBar.clearFocus()
            }
        }

        etSearchBar.setOnFocusChangeListener{ view, hasFocus ->
            (requireActivity() as BirdsEyeView).focusListener(hasFocus)
        }

        // INITIALIZE SCROLL VIEW
        val svSuggestions = view?.findViewById<ScrollView>(R.id.svSuggestions)
        // INITIALIZE TOTAL SUGGESTIONS
        var totalSuggestions = 0

        etSearchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // Log the text content after it has changed
                // Log.d("TextWatcher", "Text content: $s")

                // CREATE AN INSTANCE DATABASE HANDLER
                val dbHelper = DataBaseHandler(requireContext())
                // GET READABLE DATABASE INSTANCE
                val db = dbHelper.readableDatabase

                svSuggestions?.removeAllViews()
                // DATABASE OPERATION HERE
                val entered = etSearchBar.text.toString()
                var cursor: Cursor? = null
                if (entered != "") {
                    cursor = db.rawQuery("SELECT * FROM coords WHERE RoomID LIKE '%$entered%'", null)
                }


                if (cursor != null && cursor.count > 0) {
                    val roomIdIndex = cursor.getColumnIndex("RoomID")
                    val longitudeIndex = cursor.getColumnIndex("Longitude")
                    val latitudeIndex = cursor.getColumnIndex("Latitude")

                    // Toast.makeText(context, "not null", Toast.LENGTH_SHORT).show()
                    val suggestions = mutableListOf<String>()
                    if (roomIdIndex != -1 && longitudeIndex != -1 && latitudeIndex != -1) {
                        while (cursor.moveToNext()) {
                            val roomId = cursor.getString(roomIdIndex)
                            suggestions.add(roomId)
                        }
                    } else {
                        Log.e("DB_TEST", "One or more column indices not found in the cursor.")
                    }

                    val linearLayout = LinearLayout(requireContext())
                    linearLayout.orientation = LinearLayout.VERTICAL

                    val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                    for (element in suggestions) {
                        val horizontalLayout = LinearLayout(requireContext())
                        horizontalLayout.layoutParams = layoutParams
                        horizontalLayout.orientation = LinearLayout.HORIZONTAL
                        horizontalLayout.setPadding(0, 16, 0, 16)

                        val imageView = ImageView(requireContext())
                        imageView.setImageResource(R.drawable.baseline_search_24)
                        val imageParams = LinearLayout.LayoutParams(
                            100, // Set width of the ImageView
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        imageParams.setMargins(0, 0, 16, 0) // Margin between ImageView and TextView
                        imageView.layoutParams = imageParams

                        val textView = TextView(requireContext())
                        textView.text = element
                        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black)) // Set text color
                        textView.textSize = 18f //
                        textView.layoutParams = layoutParams

                        // Add click listener if needed
                        textView.setOnClickListener {
//                            Toast.makeText(requireContext(), "Selected: $element", Toast.LENGTH_SHORT).show()
                            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(etSearchBar.windowToken, 0)
                            etSearchBar.setText(element)
                            searchValue = element
                            etSearchBar.clearFocus()
                            onValuePassed(searchValue)
                            passValueToActivity()

                            activity?.let { fragmentActivity ->
                                if (fragmentActivity is BirdsEyeView) {
                                    // Access the BottomSheetBehavior in the activity
                                    val bottomSheetBehavior = fragmentActivity.bottomSheetBehavior
                                    // Set the state to collapsed
                                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                                }
                            }
                        }

                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        textView.layoutParams = params

                        horizontalLayout.addView(imageView)
                        horizontalLayout.addView(textView)

                        linearLayout.addView(horizontalLayout)
                    }

                    view?.findViewById<ScrollView>(R.id.svSuggestions)?.addView(linearLayout)
                    totalSuggestions = suggestions.size
                } else {
                    Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show()
                    Log.e("DB_TEST", "No data in the cursor or cursor is null.")
                }

                cursor?.close()
                db.close()

            }
        })
        etSearchBar.setOnEditorActionListener { _, actionId, _ ->
            Log.d("Hello", etSearchBar.text.toString())
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO ||
                actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_SEARCH
            ) {
//                Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show()

                searchValue = etSearchBar.text.toString()
                if (searchValue == "") {
                    Toast.makeText(context, "Please Enter something.", Toast.LENGTH_SHORT).show()
                    // PASS EMPTY STRING
                    onValuePassed("")
                    passValueToActivity()
                } else {
                    if (totalSuggestions == 1) {
                        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(etSearchBar.windowToken, 0)
                        etSearchBar.clearFocus()
                        onValuePassed(searchValue)
                        passValueToActivity()

                        activity?.let { fragmentActivity ->
                            if (fragmentActivity is BirdsEyeView) {
                                // Access the BottomSheetBehavior in the activity
                                val bottomSheetBehavior = fragmentActivity.bottomSheetBehavior
                                // Set the state to collapsed
                                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            }
                        }
                    } else {
                        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(etSearchBar.windowToken, 0)
                        etSearchBar.clearFocus()
                    }

                }

                //Log.i("MYTAG", searchValue)
                true
            } else {
                // Return false if you're not consuming the event
                false
            }

        }

    }
}