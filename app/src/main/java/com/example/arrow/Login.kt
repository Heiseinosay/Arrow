package com.example.arrow

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
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
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //        this.onBackPressed ()

        // SET STATUS BAR COLOR
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.red)

        //Firebase
        auth = Firebase.auth

        //save email and password
        sharedPreferences = getSharedPreferences("rememberMe", MODE_PRIVATE)

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
    private fun performLogin(){
        val email = findViewById<EditText>(R.id.inputEmail).text.toString()
        val password = findViewById<EditText>(R.id.inputPassword).text.toString()

        // CHECK FOR NULL INPUT
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email or Password field must not be empty.", Toast.LENGTH_SHORT).show()
            return
        }

        // IF CHECKBOX IS ON
        val checkBox = findViewById<CheckBox>(R.id.checkBox)

        if (checkBox.isChecked){
            sharedPreferences.edit().putString("email", email).putString("password", password).apply()
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success")
                val user = auth.currentUser
                val isChecked = sharedPreferences.getBoolean("rememberMe", false)

                if (isChecked) {
                    // redirect to Main Activity
                    // PROCEED ON BIRDS EYE VIEW
                    // Toast.makeText(this, "You are now logged in.", Toast.LENGTH_SHORT).show()
                    val openBirdsEyeView = Intent(this@Login, BirdsEyeView::class.java)
                    startActivity(openBirdsEyeView)
                    finish()
                }
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                Toast.makeText(
                    baseContext, "Authentication failed.", Toast.LENGTH_SHORT,).show()
            }
        }
    }
}