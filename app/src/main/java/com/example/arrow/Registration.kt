package com.example.arrow

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import org.w3c.dom.Text

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

            // To check password is valid
            contChange()

            /* val openReg2 = Intent(this@Registration, RegistrationSecondPart::class.java)
            openReg2.putExtra("Info", name)
            openReg2.putExtra("Email", email)
            openReg2.putExtra("Password", pass)
            startActivity(openReg2) */
        }

    }

    // To validate input password
    @SuppressLint("ResourceAsColor")
    private fun passwordValidation(){
        val passwordCheck = findViewById<EditText>(R.id.tvPassword).text.toString()
        val passValidation1 = findViewById<TextView>(R.id.tvValidation1)
        val passValidation2 = findViewById<TextView>(R.id.tvValidation2)
        val passValidation3 = findViewById<TextView>(R.id.tvValidation3)

        if (passwordCheck.length >= 8){
            passValidation1.setTextColor(Color.parseColor(R.color.blue.toString()))
        } else {
            passValidation1.setTextColor(Color.parseColor(R.color.lightGrey.toString()))
        }

        if (Regex(".*[A-Z]*.").containsMatchIn(passwordCheck)){
            passValidation2.setTextColor(Color.parseColor(R.color.blue.toString()))
        } else {
            passValidation2.setTextColor(Color.parseColor(R.color.lightGrey.toString()))
        }

        if(Regex(".*[0-9]*.").containsMatchIn(passwordCheck)){
            passValidation3.setTextColor(Color.parseColor(R.color.blue.toString()))
        } else {
            passValidation3.setTextColor(Color.parseColor(R.color.lightGrey.toString()))
        }
    }

    // To constantly check the changes in password edit
    private fun contChange(){
        val changedPassword = findViewById<EditText>(R.id.tvPassword)
        changedPassword.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                passwordValidation()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }
}