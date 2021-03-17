package com.example.automaksi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast

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
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("name", itemNameText.text as String?)
            intent.putExtra("standard", standard.progress)
            intent.putExtra("critical", critical.progress)
            startActivity(intent)
        }
    }
}