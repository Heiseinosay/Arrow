package com.example.arrow

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class ExploreFragment : Fragment(R.layout.fragment_explore) {
    private lateinit var viewModel: ViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profileImage = view.findViewById<ImageView>(R.id.imageView7)

        profileImage.setImageDrawable(ProfileObjects.profileImage)
        viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        viewModel.imageDrawable.observe(viewLifecycleOwner) { newDrawable ->
            profileImage.setImageDrawable(newDrawable)
        }

        val etSearchBar = view.findViewById<EditText>(R.id.etSearchBar)
        etSearchBar.setOnFocusChangeListener{ view, hasFocus ->
            (requireActivity() as BirdsEyeView).focusListener(hasFocus)
        }

    }
}