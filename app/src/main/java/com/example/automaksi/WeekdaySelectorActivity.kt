package com.example.automaksi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class WeekdaySelectorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekday_selector)
        val okButton = findViewById<Button>(R.id.weekday_choice_ok)
        okButton.setOnClickListener {
            val dayPicker = findViewById<ca.antonious.materialdaypicker.MaterialDayPicker>(R.id.day_picker)
            val daysOrdinal = mutableListOf<Int>()
            for (day in dayPicker.selectedDays)
                daysOrdinal.add(day.ordinal)

            val db = FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()
            val data = mapOf(
                "delivery_days" to daysOrdinal
            )
            db.collection("UserData").document(auth.uid.toString()).set(data)
            finish()
        }
    }
}