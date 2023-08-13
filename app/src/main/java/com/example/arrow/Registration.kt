package com.example.arrow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore

class Registration : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val login = findViewById<TextView>(R.id.tvLogin)
        login.setOnClickListener {
            val openLogin = Intent(this@Registration, Login::class.java)
            startActivity(openLogin)
        }

        val db = Firebase.firestore

        val getStartedBtn = findViewById<Button>(R.id.btnSignUp)
        getStartedBtn.setOnClickListener {
            val email: String = findViewById<EditText>(R.id.tvEmail).text.toString().trim()
            val name = findViewById<EditText>(R.id.tvFullName).text.toString().trim()
            val pass = findViewById<EditText>(R.id.tvPassword).text.toString().trim()

            if (email.isEmpty() || name.isEmpty() || pass.isEmpty()) {
                Toast.makeText(applicationContext,"Fields cannot be empty",Toast.LENGTH_SHORT)
                return@setOnClickListener
            }
            // TODO: check for empty inputs
            val newUser = hashMapOf(
                "email" to email,
                "name" to name,
                "password" to pass,
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