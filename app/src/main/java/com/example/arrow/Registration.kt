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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val login = findViewById<TextView>(R.id.tvLogin)
        login.setOnClickListener {
            val openLogin = Intent(this@Registration, Login::class.java)
            startActivity(openLogin)
        }


        val getStartedBtn = findViewById<Button>(R.id.btnSignUp)
        getStartedBtn.setOnClickListener {
            val email = findViewById<EditText>(R.id.tvEmail).text.toString().trim()
            val name = findViewById<EditText>(R.id.tvFullName).text.toString().trim()
            val pass = findViewById<EditText>(R.id.tvPassword).text.toString().trim()

            if (email.isEmpty() || name.isEmpty() || pass.isEmpty()) {
                Toast.makeText(applicationContext,"Fields cannot be empty",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: check if email is in email format

            if (!checkPass(pass)) {
                // TODO: change text
                Toast.makeText(applicationContext,"Password format is wrong",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val openReg2 = Intent(this@Registration, RegistrationSecondPart::class.java)
            openReg2.putExtra("Info", name)
            openReg2.putExtra("Email", email)
            openReg2.putExtra("Password", pass)
            startActivity(openReg2)
        }
    }

    fun checkPass(password: String): Boolean {
        var upperCount = 0
        var numCount = 0
        var charCount = 0
        password.forEach {
            if (it.isUpperCase()) {
                upperCount++
            }
            if (it.isDigit()) {
                numCount++
            }
            charCount++
        }
        if (upperCount >= 1 && numCount >= 1 && charCount >= 8) {
            return true
        }
        return false
    }
}