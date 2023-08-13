package com.example.arrow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // TODO: connect database and add functional query

        auth = Firebase.auth

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
        loginButton.setOnClickListener {
            performLogin()
        }

    }

    // getting input from user
    private fun performLogin(){
        val email = findViewById<EditText>(R.id.inputEmail)
        val password = findViewById<EditText>(R.id.inputPassword)

        // check for null input
        if (email.text.isEmpty() || password.text.isEmpty()){
            Toast.makeText(this, "Email or Passowrd field must not be empty.", Toast.LENGTH_SHORT)
                .show()
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
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
    }
}