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
    // INITIALIZE GLOBAL VARIABLES

    // FOR DATABASE
    private lateinit var auth: FirebaseAuth
    // FOR REMEMBER ME SHARED PREFERENCES
    private lateinit var emailText:EditText
    private lateinit var pwdText:EditText
    private lateinit var sf: SharedPreferences
    private lateinit var editor:SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // SET STATUS BAR COLOR
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.red)

        //Firebase
        auth = Firebase.auth

        //save email and password
        emailText = findViewById(R.id.inputEmail)
        pwdText = findViewById(R.id.inputPassword)
        sf = getSharedPreferences("rememberMe", MODE_PRIVATE)
        editor = sf.edit()

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
        val email = findViewById<EditText>(R.id.inputEmail)
        val password = findViewById<EditText>(R.id.inputPassword)

        // CHECK FOR NULL INPUT
        if (email.text.isEmpty() || password.text.isEmpty()) {
            Toast.makeText(this, "Email or Password field must not be empty.", Toast.LENGTH_SHORT).show()
            return
        }
        val emailCheck = email.text.toString()
        val passwordCheck = password.text.toString()

        // IF CHECKBOX IS ON

        auth.signInWithEmailAndPassword(emailCheck, passwordCheck)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser

                    // redirect to Main Activity
                    // PROCEED ON BIRDS EYE VIEW
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

    // FOR REMEMBER ME CHECK BOX
    override fun onPause() {
        super.onPause()

        val remember = findViewById<CheckBox>(R.id.checkBox)

        val mail = emailText.text.toString()
        val pwd = pwdText.text.toString()
        val status = ""
        editor.apply {
            putString("sf_email", mail)
            putString("sf_pwd", pwd)
            putString("sf_remember", "${remember.isChecked}")
            commit()
        }
    }

    override fun onResume() {
        super.onResume()

        val status = sf.getString("sf_remember", null)
        if (status == "true") {
            val remember = findViewById<CheckBox>(R.id.checkBox)
            remember.isChecked = true

            val mail = sf.getString("sf_email", null)
            val pwd = sf.getString("sf_pwd", null)
            emailText.setText(mail)
            pwdText.setText(pwd)
        }
    }
}