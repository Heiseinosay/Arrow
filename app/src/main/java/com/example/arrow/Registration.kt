package com.example.arrow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore

abstract class Registration : AppCompatActivity() {
    lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val login = findViewById<TextView>(R.id.tvLogin)
        login.setOnClickListener {
            val openLogin = Intent(this@Registration, Login::class.java)
            startActivity(openLogin)
        }

        db = Firebase.firestore

        val getStartedBtn = findViewById<Button>(R.id.btnSignUp)
        getStartedBtn.setOnClickListener {
            val email: String = findViewById<EditText>(R.id.tvEmail).text.toString().trim()
            val name = findViewById<EditText>(R.id.tvFullName).text.toString().trim()
            val pass = findViewById<EditText>(R.id.tvPassword).text.toString().trim()
//            val phone = findViewById<EditText>(R.id.tvPhone).text.toString().trim()

            /*
            TODO: give temp permission to add user
            change firestore rules to only allow verified user
             */
            if (email.isEmpty() || name.isEmpty() || pass.isEmpty()) {
                Toast.makeText(applicationContext,"Fields must not be empty",Toast.LENGTH_SHORT)
                return@setOnClickListener
            }
            val newUser = hashMapOf(
                "email" to email,
                "name" to name,
                "password" to pass,
//                "phone" to phone,
            )

            db.collection("users")
                .add(newUser)
                .addOnSuccessListener { documentReference ->
                    Log.d("FIREBASE_LOG", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("FIREBASE_LOG", "Error adding document", e)
                }
        }

    }
}