package com.example.arrow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var viewModel: ViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profileImage = view.findViewById<ImageView>(R.id.iv_profile_pic)
        val changeAvatar = view.findViewById<CardView>(R.id.cv_change_avatar)
        val profileName = view.findViewById<TextView>(R.id.tv_profile_name)
        val profileRole = view.findViewById<TextView>(R.id.tv_profile_status)
        val userName = view.findViewById<TextView>(R.id.tv_name)
        val userEmail = view.findViewById<TextView>(R.id.tv_email)

        profileImage.setImageDrawable(ProfileObjects.profileImage)
        viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        viewModel.imageDrawable.observe(viewLifecycleOwner) { newDrawable ->
            profileImage.setImageDrawable(newDrawable)
        }
        val firstName = getFirstName(ProfileObjects.name!!)
        profileName.text = firstName
        profileRole.text = ProfileObjects.role
        userName.text = ProfileObjects.name
        userEmail.text = ProfileObjects.email

        changeAvatar.setOnClickListener{
            if(ProfileObjects.role!! == "Visitor"){
                Toast.makeText(requireContext(), "Visitors cannot change avatar.", Toast.LENGTH_SHORT).show()
            }else {
                (requireActivity() as BirdsEyeView).viewProfileOverlay()
            }
        }

        val logout = view.findViewById<CardView>(R.id.cv_logout)
        logout?.setOnClickListener {
            /*val sf = activity?.getSharedPreferences("rememberMe", MODE_PRIVATE)
            val editor = sf?.edit()
            editor?.clear()
            editor?.apply()*/
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireContext(), Login::class.java))
            Log.d("ProfileObjectsCheck", "${ProfileObjects.profile}")
            Log.d("ProfileObjectsCheck", "${ProfileObjects.role}")
        }
    }

    private fun getFirstName(name: String): String{
        val parts = name.split(" ")
        return parts[0]
    }
}