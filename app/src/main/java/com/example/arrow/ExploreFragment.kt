package com.example.arrow

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class ExploreFragment : Fragment(R.layout.fragment_explore) {
    private lateinit var viewModel: ViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
    }
}