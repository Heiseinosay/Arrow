package com.example.arrow

import android.content.ContentValues.TAG
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
import com.google.firebase.ktx.Firebase

import android.view.Window
import android.view.WindowManager
import android.widget.CheckBox
import androidx.core.content.ContextCompat

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // SET STATUS BAR COLOR
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.red)

        //Firebase
        auth = Firebase.auth
        // var firebaseAuth = FirebaseAuth.getInstance()
        //var firebaseUser = auth.currentUser

        //save email and password
        val checkBox = findViewById<CheckBox>(R.id.checkBox)

        /* val FILE_EMAIL = "rememberMe"
        SharedPreferences sharedPreferences = getSharedPreferences(FILE_EMAIL, MODE_PRIVATE) */

        // ID'S
        val forgotPassword = findViewById<TextView>(R.id.tvForgotPassword)
        val register = findViewById<TextView>(R.id.tvRegister)
        val loginButton = findViewById<Button>(R.id.btnLogin)

        forgotPassword.setOnClickListener {
            Toast.makeText(this, "Nothing here for now!" , Toast.LENGTH_SHORT).show()
        }
        register.setOnClickListener {
            val openRegister = Intent(this@Login, Registration::class.java)
            startActivity(openRegister)
        }
        loginButton.setOnClickListener { performLogin() }
    }
    // GETTING INPUT FROM USE
    private fun performLogin() {
        val email = findViewById<EditText>(R.id.tvFullName)
        val password = findViewById<EditText>(R.id.inputPassword)

        // CHECK FOR NULL INPUT
        if (email.text.isEmpty() || password.text.isEmpty()) {
            Toast.makeText(this, "Email or Password field must not be empty.", Toast.LENGTH_SHORT).show()
            return
        }
        val emailCheck = email.text.toString()
        val passwordCheck = password.text.toString()

        auth.signInWithEmailAndPassword(emailCheck, passwordCheck)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser

                    // redirect to Main Activity
                    // PROCEED ON BIRDS EYE VIEW
                    // Toast.makeText(this, "You are now logged in.", Toast.LENGTH_SHORT).show()
                    val openBirdsEyeView = Intent(this@Login, BirdsEyeView::class.java)
                    startActivity(openBirdsEyeView)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    // Toast.makeText(baseContext,"Authentication failed.", Toast.LENGTH_SHORT,).show()
                    val info = findViewById<TextView>(R.id.tvInfo)
                    info.text = "Invalid username or password"

                }
            }
    }
}