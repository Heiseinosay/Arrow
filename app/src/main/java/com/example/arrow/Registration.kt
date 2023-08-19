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

class Registration : AppCompatActivity() {
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
                        db.collection("users")
                            .add(newUser)
                            .addOnSuccessListener { documentReference ->
                                Log.d("FIREBASE_LOG", "DocumentSnapshot added with ID: ${documentReference.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.w("FIREBASE_LOG", "Error adding document", e)
                                Toast.makeText(applicationContext, e.toString(),Toast.LENGTH_SHORT).show()
                            }
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
<<<<<<< Updated upstream
=======

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
        val changedName = findViewById<EditText>(R.id.tvFullName)
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
>>>>>>> Stashed changes
    }
}