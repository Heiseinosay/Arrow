package com.example.arrow

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore

open class Registration : AppCompatActivity() {

    var passCheck1: Boolean = false; var passCheck2: Boolean = false
    var passCheck3: Boolean = false; var canContinue: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // SET PHONE BACK FUNCTION
        val login = findViewById<TextView>(R.id.tvLogin)
        login.setOnClickListener {
            //val openLogin = Intent(this@Registration, Login::class.java)
            //startActivity(openLogin)
            this.onBackPressed ()
            finish()
        }

        val getStartedBtn = findViewById<Button>(R.id.btnSignUp)
        getStartedBtn.setOnClickListener {
            val email = findViewById<EditText>(R.id.tvEmail).text.toString().trim()
            val name = findViewById<EditText>(R.id.inputEmail).text.toString().trim()
            val pass = findViewById<EditText>(R.id.tvPassword).text.toString().trim()
            if(email.isNotEmpty() && name.isNotEmpty() && pass.isNotEmpty()){
                if(canContinue){
                    // CHECK IF EMAIL EXIST ON DB
                    checkIfEmailExists(email) { emailExists ->
                        if (emailExists) {
                            Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show()
                        } else {
                            val openReg2 = Intent(this@Registration, RegistrationSecondPart::class.java)
                            openReg2.putExtra("Info", name)
                            openReg2.putExtra("Email", email)
                            openReg2.putExtra("Password", pass)
                            startActivity(openReg2)
                        }
                    }
                } else if (!passCheck1 || !passCheck2 || !passCheck3){
                    Toast.makeText(this,"The entered password is invalid.", Toast.LENGTH_SHORT).show()
                }
            } else{
                Toast.makeText(this, "All fields must not be empty.", Toast.LENGTH_SHORT).show()
            }
        }

        // To check password if valid
        contChange()
    }

    // To validate input password
    @SuppressLint("ResourceAsColor")
    private fun passwordValidation(name: String, email: String, passwordCheck: String  ) {
        val passValidation1 = findViewById<CardView>(R.id.valid1)
        val passValidation2 = findViewById<CardView>(R.id.valid2)
        val passValidation3 = findViewById<CardView>(R.id.valid3)
        val passText1 = findViewById<TextView>(R.id.tvValidation1)
        val passText2 = findViewById<TextView>(R.id.tvValidation2)
        val passText3 = findViewById<TextView>(R.id.tvValidation3)

        if (passwordCheck.length >= 8){
            passValidation1.setCardBackgroundColor(ContextCompat.getColor(this, R.color.blue))
            passText1.setTextColor(ContextCompat.getColor(this,R.color.blue))
            passCheck1 = true
        } else {
            passValidation1.setCardBackgroundColor(ContextCompat.getColor(this, R.color.darkGrey))
            passText1.setTextColor(ContextCompat.getColor(this,R.color.darkGrey))
            passCheck1 = false

        }

        if (("^.*[A-Z].*$").toRegex().matches(passwordCheck)){
            passValidation2.setCardBackgroundColor(ContextCompat.getColor(this, R.color.blue))
            passText2.setTextColor(ContextCompat.getColor(this,R.color.blue))
            passCheck2 = true
        } else {
            passValidation2.setCardBackgroundColor(ContextCompat.getColor(this, R.color.darkGrey))
            passText2.setTextColor(ContextCompat.getColor(this,R.color.darkGrey))
            passCheck2 = false
        }

        if(("^.*[0-9].*$").toRegex().matches(passwordCheck)){
            passValidation3.setCardBackgroundColor(ContextCompat.getColor(this, R.color.blue))
            passText3.setTextColor(ContextCompat.getColor(this,R.color.blue))
            passCheck3 = true
        } else {
            passValidation3.setCardBackgroundColor(ContextCompat.getColor(this, R.color.darkGrey))
            passText3.setTextColor(ContextCompat.getColor(this,R.color.darkGrey))
            passCheck3 = false
        }

        checkAll(name, email)
    }

    private fun checkAll(name: String, email: String){
        if (passCheck1 && passCheck2 && passCheck3 && email.isNotEmpty()  && name.isNotEmpty()){
            canContinue = true
        } else{
            canContinue = false
        }
    }

    // To constantly check the changes in password edit
    private fun contChange(){
        val changedName = findViewById<EditText>(R.id.inputEmail)
        val changedEmail = findViewById<EditText>(R.id.tvEmail)
        val changedPassword = findViewById<EditText>(R.id.tvPassword)

        changedName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("TextWatcher", "Password text changed: $s")
                passwordValidation(changedName.text.toString(), changedEmail.text.toString(), changedPassword.text.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        changedEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("TextWatcher", "Password text changed: $s")
                passwordValidation(changedName.text.toString(), changedEmail.text.toString(), changedPassword.text.toString())
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        changedPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("TextWatcher", "Password text changed: $s")
                passwordValidation(changedName.text.toString(), changedEmail.text.toString(), changedPassword.text.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }
    private fun checkIfEmailExists(email: String, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val existing = !querySnapshot.isEmpty
                callback(existing)
            }
            .addOnFailureListener { exception ->
                // Handle the failure
                Log.w("FIREBASE_DB_LOG", "Adding User to DB:failure")
                Toast.makeText(
                    baseContext,
                    "Connection failed.",
                    Toast.LENGTH_SHORT
                ).show()
                callback(false) // Indicate failure
            }
    }


}