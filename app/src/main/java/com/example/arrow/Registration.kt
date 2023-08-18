package com.example.arrow

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import org.w3c.dom.Text

open class Registration : AppCompatActivity() {

    var passCheck1: Boolean = false; var passCheck2: Boolean = false
    var passCheck3: Boolean = false; var canContinue: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val login = findViewById<TextView>(R.id.tvLogin)
        login.setOnClickListener {
            val openLogin = Intent(this@Registration, Login::class.java)
            startActivity(openLogin)

        }

        val email = findViewById<EditText>(R.id.tvEmail).text.toString().trim()
        val name = findViewById<EditText>(R.id.tvFullName).text.toString().trim()
        val pass = findViewById<EditText>(R.id.tvPassword).text.toString().trim()


        val getStartedBtn = findViewById<Button>(R.id.btnSignUp)
        getStartedBtn.setOnClickListener {
            if(email.isNotEmpty() && name.isNotEmpty() && pass.isNotEmpty()){
                if(canContinue){
                    Toast.makeText(this,"Testing", Toast.LENGTH_SHORT).show()
                    /* val openReg2 = Intent(this@Registration, RegistrationSecondPart::class.java)
                    openReg2.putExtra("Info", name)
                    openReg2.putExtra("Email", email)
                    openReg2.putExtra("Password", pass)
                    startActivity(openReg2)*/
                }
            } else{
                if (name.isEmpty()){
                    Toast.makeText(this, "This field must not be empty.", Toast.LENGTH_SHORT).show()
                }
                if (email.isEmpty()){
                    Toast.makeText(this, "This field must not be empty.", Toast.LENGTH_SHORT).show()
                }
                if (pass.isEmpty()){
                    Toast.makeText(this, "This field must not be empty.", Toast.LENGTH_SHORT).show()
                }
            }

            // To check password if valid
            passwordValidation(pass, email)
        }
    }

    // To validate input password
    @SuppressLint("ResourceAsColor")
    private fun passwordValidation(passwordCheck: String, email: String) {
        val passValidation1 = findViewById<CardView>(R.id.valid1)
        val passValidation2 = findViewById<CardView>(R.id.valid2)
        val passValidation3 = findViewById<CardView>(R.id.valid3)

        if (passwordCheck.length >= 8){
            passValidation1.setCardBackgroundColor(Color.parseColor(R.color.blue.toString()))
            passCheck1 = true
        } else {
            passValidation1.setCardBackgroundColor(Color.parseColor(R.color.red.toString()))
            passCheck1 = false

        }

        if ((".*[A-Z]*.").toRegex().matches(passwordCheck)){
            passValidation2.setCardBackgroundColor(Color.parseColor(R.color.blue.toString()))
            passCheck2 = true
        } else {
            passValidation2.setCardBackgroundColor(Color.parseColor(R.color.red.toString()))
            passCheck2 = false
        }

        if((".*[0-9]*.").toRegex().matches(passwordCheck)){
            passValidation3.setCardBackgroundColor(Color.parseColor(R.color.blue.toString()))
            passCheck3 = true
        } else {
            passValidation3.setCardBackgroundColor(Color.parseColor(R.color.red.toString()))
            passCheck3 = false
        }

        checkAll(email)
    }

    private fun checkAll(email: String){
        if (passCheck1 && passCheck2 && passCheck3 && email.length >= 8){
            canContinue = true
        }
    }

    /* To constantly check the changes in password edit
    private fun contChange(){
        val changedPassword = findViewById<EditText>(R.id.tvPassword)
        changedPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                passwordValidation()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    } */
}