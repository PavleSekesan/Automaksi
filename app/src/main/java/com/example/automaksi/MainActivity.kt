package com.example.automaksi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, BarcodeScannerActivity::class.java)
        startActivity(intent)
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