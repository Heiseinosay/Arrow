package com.example.arrow

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.material.card.MaterialCardView

class RegistrationSecondPart : AppCompatActivity() {
    private var classStatus = -1
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_second_part)

        // DATA FROM REGISTRATION PART 1
        val userName = intent.getStringExtra("Info")
        val eMail = intent.getStringExtra("Email")
        val id = intent.getStringExtra("id")

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
            } else {
                // TODO: ADD STUDENT/EMPLOYEE/VISITOR ON DB
                Toast.makeText(this, "$classStatus", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    fun setStatus(status: Int, student: MaterialCardView, employee: MaterialCardView, visitor: MaterialCardView) {
        // Toast.makeText(this, "$status hello", Toast.LENGTH_SHORT).show()
        classStatus = status
        when (status) {
            0 -> {
                student.setCardBackgroundColor(resources.getColor(R.color.darkGrey))
                employee.setCardBackgroundColor(resources.getColor(R.color.white))
                visitor.setCardBackgroundColor(resources.getColor(R.color.white))
            } 1 -> {
                employee.setCardBackgroundColor(resources.getColor(R.color.darkGrey))
                student.setCardBackgroundColor(resources.getColor(R.color.white))
                visitor.setCardBackgroundColor(resources.getColor(R.color.white))
            } 2 -> {
                visitor.setCardBackgroundColor(resources.getColor(R.color.darkGrey))
                student.setCardBackgroundColor(resources.getColor(R.color.white))
                employee.setCardBackgroundColor(resources.getColor(R.color.white))
            }
            else -> {
                Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show()
            }
        }
    }
}