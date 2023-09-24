package com.example.arrow

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment

class ExploreFragment : Fragment(R.layout.fragment_explore) {
    val etSearchBar = view?.findViewById<EditText>(R.id.etSearchBar)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}