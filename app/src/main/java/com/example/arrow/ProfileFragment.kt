package com.example.arrow

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
            val sf = activity?.getSharedPreferences("rememberMe", MODE_PRIVATE)
            val editor = sf?.edit()
            editor?.clear()
            editor?.apply()
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireContext(), Login::class.java))
            // FINISH THE MAIN ACTIVITY
            getActivity()?.finish();
        }

        // TERMS LINK
        val terms = view.findViewById<TextView>(R.id.tv_terms)
        terms.setOnClickListener { redirect(1) }
        val helpImg = view.findViewById<ImageView>(R.id.iv_help)
        helpImg.setOnClickListener { redirect(1) }

        // HELP LINK
        val tv_help = view.findViewById<TextView>(R.id.tv_terms)
        tv_help.setOnClickListener { redirect(0) }
        val termsImg = view.findViewById<ImageView>(R.id.iv_terms)
        termsImg.setOnClickListener { redirect(0) }
    }

    private fun redirect(num:Int) {
        // Specify the URL of the website
        var websiteUrl:String = ""
        if (num == 0) {
            websiteUrl = "https://aaronmanlogon.github.io/arrow/support.html"
        } else {
            websiteUrl = "https://aaronmanlogon.github.io/arrow/tc.html"
        }
        // Create an intent to open the website
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl))

        // Check if there's an app available to handle the intent
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            startActivity(intent)
        }
    }

    private fun getFirstName(name: String): String{
        val parts = name.split(" ")
        return parts[0]
    }
}