package com.example.arrow

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    val logout = view?.findViewById<CardView>(R.id.cv_logout)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = Firebase.firestore
        val userUID = FirebaseAuth.getInstance().currentUser?.uid
        val profileName = view.findViewById<TextView>(R.id.tv_profile_name)
        val profileRole = view.findViewById<TextView>(R.id.tv_profile_status)
        val userName = view.findViewById<TextView>(R.id.tv_name)
        val userEmail = view.findViewById<TextView>(R.id.tv_email)

        if(userUID != null){
            val userRef = db.collection("users").document(userUID)
            userRef.get().addOnSuccessListener { documentSnapshot->
                if (documentSnapshot.exists()){
                    val userData = documentSnapshot.data
                    val name = userData?.get("name") as String?
                    val role = userData?.get("role") as String?
                    val email = userData?.get("email") as String?

                    if (name != null){
                        val firstName = getFirstName(name)
                        profileName.text  = firstName
                        profileRole.text = role
                        userName.text = name
                        userEmail.text = email

                    } else{
                        profileName.text = "Anonymous"
                        userName.text = "User is Anonymous"
                    }
                }
            }
        }

        logout?.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(requireContext(), Login::class.java)

            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun getFirstName(name: String): String{
        val parts = name.split(" ")
        return parts[0]
    }
}