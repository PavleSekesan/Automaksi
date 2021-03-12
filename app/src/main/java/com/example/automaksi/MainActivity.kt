package com.example.automaksi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    fun kupiMaksi(view: View) {
        val db = Firebase.firestore
        val docRef = db.collection("Products").document("008lcmgIqmsAyuU1ADF3")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("kurac", "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d("kurac", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("kurac", "get failed with ", exception)
            }
    }
}