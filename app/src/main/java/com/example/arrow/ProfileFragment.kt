package com.example.arrow

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val profileName = view.findViewById<TextView>(R.id.tv_profile_name)

        currentUser?.let {
            val firstname = it.displayName.toString()
            profileName.text = firstname
            Log.d("ProfileFragment", "First Name: $profileName")
        }
    }


}