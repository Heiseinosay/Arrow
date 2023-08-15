package com.example.arrow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore

open class Registration : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val login = findViewById<TextView>(R.id.tvLogin)
        login.setOnClickListener {
            val openLogin = Intent(this@Registration, Login::class.java)
            startActivity(openLogin)
        }

        db = Firebase.firestore
        auth = Firebase.auth

        val getStartedBtn = findViewById<Button>(R.id.btnSignUp)
        getStartedBtn.setOnClickListener {
            val email: String = findViewById<EditText>(R.id.tvEmail).text.toString().trim()
            val name = findViewById<EditText>(R.id.tvFullName).text.toString().trim()
            val pass = findViewById<EditText>(R.id.tvPassword).text.toString().trim()

            if (email.isEmpty() || name.isEmpty() || pass.isEmpty()) {
                Toast.makeText(applicationContext,"Fields cannot be empty",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("FIREBASE_AUTH_LOG", "createUserWithEmail:success")
                        val user = auth.currentUser
                        val newUser = hashMapOf(
                            "email" to email,
                            "name" to name,
                            "id" to user?.uid,
                        )

                        val openReg2 = Intent(this@Registration, RegistrationSecondPart::class.java)
                        openReg2.putExtra("Info", newUser["name"])
                        openReg2.putExtra("Email", newUser["email"])
                        openReg2.putExtra("id", newUser["id"])
                        startActivity(openReg2)

                        /*


                        db.collection("users")
                            .add(newUser)
                            .addOnSuccessListener { documentReference ->
                                Log.d("FIREBASE_LOG", "DocumentSnapshot added with ID: ${documentReference.id}")
                                Toast.makeText(applicationContext,"Successfully Created account.",Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Log.w("FIREBASE_LOG", "Error adding document", e)
                                Toast.makeText(applicationContext, e.toString(),Toast.LENGTH_SHORT).show()
                            }

                         */
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("FIREBASE_AUTH_LOG", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }
    }
}