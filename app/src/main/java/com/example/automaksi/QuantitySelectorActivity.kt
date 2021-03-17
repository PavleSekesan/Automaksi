package com.example.automaksi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.ktx.Firebase

class QuantitySelectorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quantity_selector)

        val itemNameText : TextView = findViewById(R.id.textViewItemName)
        itemNameText.text = intent.getStringExtra("itemName")
        val submitBtn : Button = findViewById(R.id.buttonSubmitQuantities)
        val standard : SeekBar = findViewById(R.id.seekBarStandard)
        val critical : SeekBar = findViewById(R.id.seekBarCritical)
        submitBtn.setOnClickListener {
            Toast.makeText(this, standard.progress.toString() + "/" + critical.progress.toString(), Toast.LENGTH_LONG).show()
            val db = Firebase.firestore
            val newItem = hashMapOf(
                    "name" to itemNameText.toString(),
                    "current_quantity" to 0,
                    "optimal_quantity" to standard.progress,
                    "critical_quantity" to critical.progress
            )
            var auth = FirebaseAuth.getInstance()
            val docRef = db.collection("UserItems").document(auth.uid.toString())
            docRef.get().addOnSuccessListener { document->
                if (document.exists())
                {
                    docRef.update("items", FieldValue.arrayUnion(newItem))
                }
                else
                {
                    val data = hashMapOf(
                        "items" to arrayListOf(newItem)
                    )
                    docRef.set(data)
                }
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}