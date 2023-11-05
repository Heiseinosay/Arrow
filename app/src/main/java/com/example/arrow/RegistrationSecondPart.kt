package com.example.arrow

// <<<<<<< registration-2
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
// >>>>>>> master

class RegistrationSecondPart : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private var classStatus = -1
    private lateinit var role: String
    private lateinit var profile: String

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_second_part)

        // DETECT BACK EXECUTION
        val rootView = findViewById<View>(android.R.id.content)
        rootView.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
                Log.i("MYTAG", "BACK PRESSED")
                finish()
                true // Return true to indicate that the event was handled
            } else {
                false // Return false to pass the event to other handlers
            }
        }

        // initialize database and authentication
        db = Firebase.firestore
        auth = Firebase.auth

        // ID'S
        val btnStudent = findViewById<MaterialCardView>(R.id.mtStudent)
        val btnEmployee = findViewById<MaterialCardView>(R.id.mtEmployee)
        val btnVisitor = findViewById<MaterialCardView>(R.id.mtVisitor)
        val btnConfirm = findViewById<Button>(R.id.btnConfirm)

        btnStudent.setOnClickListener { setStatus(0, btnStudent, btnEmployee, btnVisitor) }
        btnEmployee.setOnClickListener { setStatus(1, btnStudent, btnEmployee,btnVisitor ) }
        btnVisitor.setOnClickListener { setStatus(2, btnStudent, btnEmployee, btnVisitor) }
        btnConfirm.setOnClickListener {
            if (classStatus == -1) {
                Toast.makeText(this, "Please select one", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Toast.makeText(this, "$classStatus", Toast.LENGTH_SHORT).show()
            // DATA FROM REGISTRATION PART 1
            val userName = intent.getStringExtra("Info")
            val eMail = intent.getStringExtra("Email")
            val password = intent.getStringExtra("Password")

            // TIME AND DATE FOR DATABASE
            val current = LocalDateTime.now()
            val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val dateFormatted = current.format(dateFormat)
            val timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
            val timeFormatted = current.format(timeFormat)

            val newUser = hashMapOf<String, String>(
                "name" to userName!!,
                "email" to eMail!!,
                "date" to dateFormatted!!,
                "time" to timeFormatted!!,
                "role" to role,
                "profile" to profile
            )
            createAccount(newUser, password)
        }
    }

    @SuppressLint("ResourceAsColor")
    fun setStatus(status: Int, student: MaterialCardView, employee: MaterialCardView, visitor: MaterialCardView) {
        // Toast.makeText(this, "$status hello", Toast.LENGTH_SHORT).show()
        val txtStudent = findViewById<TextView>(R.id.tvStudent)
        val txtEmployee = findViewById<TextView>(R.id.tvEmployee)
        val txtVisitor = findViewById<TextView>(R.id.tvVisitor)
        classStatus = status
        when (status) {
            0 -> {
                role = "Student"
                profile = "Student_male"
                student.setCardBackgroundColor(resources.getColor(R.color.darkGrey))
                employee.setCardBackgroundColor(resources.getColor(R.color.white))
                visitor.setCardBackgroundColor(resources.getColor(R.color.white))
                txtStudent.setTextColor(resources.getColor(R.color.white))
                txtEmployee.setTextColor(resources.getColor(R.color.black))
                txtVisitor.setTextColor(resources.getColor(R.color.black))

            } 1 -> {
                role = "Employee"
                profile = "Employee_male"
                employee.setCardBackgroundColor(resources.getColor(R.color.darkGrey))
                student.setCardBackgroundColor(resources.getColor(R.color.white))
                visitor.setCardBackgroundColor(resources.getColor(R.color.white))
                txtStudent.setTextColor(resources.getColor(R.color.black))
                txtEmployee.setTextColor(resources.getColor(R.color.white))
                txtVisitor.setTextColor(resources.getColor(R.color.black))
            } 2 -> {
                role = "Visitor"
                profile = "Visitor"
                visitor.setCardBackgroundColor(resources.getColor(R.color.darkGrey))
                student.setCardBackgroundColor(resources.getColor(R.color.white))
                employee.setCardBackgroundColor(resources.getColor(R.color.white))
                txtStudent.setTextColor(resources.getColor(R.color.black))
                txtEmployee.setTextColor(resources.getColor(R.color.black))
                txtVisitor.setTextColor(resources.getColor(R.color.white))
            }
            else -> {
                Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun createAccount(newUser: HashMap<String, String>, pass: String?) {
        val email = newUser["email"]
        auth.createUserWithEmailAndPassword(email ?: "", pass ?: "")
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("FIREBASE_AUTH_LOG", "createUserWithEmail:success")
                    user = auth.currentUser

                    val uid = user?.uid ?:""
                    if (uid == "") {
                        Log.w("FIREBASE_DB_LOG", "Adding User to DB:failure no UID")
                        Toast.makeText(
                            baseContext,
                            "Adding to database failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        return@addOnCompleteListener
                    }

                    newUser.set("uid",uid)
                    db.collection("users").document(uid)
                        .set(newUser)
                        .addOnSuccessListener {
                            Log.w("FIREBASE_DB_LOG", "Adding User to DB:Success")
                            val openLogin = Intent(this@RegistrationSecondPart, Login::class.java)
                            startActivity(openLogin)
                            finish()
                        }
                        .addOnFailureListener {
                            Log.w("FIREBASE_DB_LOG", "Adding User to DB:failure", it)
                            Toast.makeText(
                                baseContext,
                                "Adding to database failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
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
}
