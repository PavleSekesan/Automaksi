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
        val itemId = intent.getLongExtra("itemId", -1)
        val submitBtn : Button = findViewById(R.id.buttonSubmitQuantities)
        val standard : SeekBar = findViewById(R.id.seekBarStandard)
        val critical : SeekBar = findViewById(R.id.seekBarCritical)
        submitBtn.setOnClickListener {
            Toast.makeText(this, standard.progress.toString() + "/" + critical.progress.toString(), Toast.LENGTH_LONG).show()
            val db = Firebase.firestore
            val newItem = hashMapOf(
                    "name" to itemNameText.text.toString(),
                    "current_quantity" to 0,
                    "optimal_quantity" to standard.progress,
                    "critical_quantity" to critical.progress,
                    "item_id" to itemId
            )
            var auth = FirebaseAuth.getInstance()
            val docRef = db.collection("UserItems").document(auth.uid.toString()).collection("items").whereEqualTo("item_id",itemId)
            docRef.get().addOnSuccessListener { documents->
                if (documents.isEmpty)
                {
                    db.collection("UserItems").document(auth.uid.toString()).collection("items").add(newItem).addOnCompleteListener {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
                else
                {
                    Toast.makeText(this, "Item already added", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}