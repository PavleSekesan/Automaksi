package com.example.automaksi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.Manifest
import android.content.pm.PackageManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
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
import kotlinx.android.synthetic.main.activity_barcode_scanner.*
import java.io.File
import java.security.KeyStore
import java.util.concurrent.ExecutorService
typealias BarCodeListener = (barcode: String) -> Unit

private class BarCodeAnalyzer(private val listener: BarCodeListener) : ImageAnalysis.Analyzer {

    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_EAN_8)
        .build()
    val scanner = BarcodeScanning.getClient(options)

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null)
        {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->

                    for (barcode in barcodes) {
                        val rawValue = barcode.rawValue
                        listener(rawValue)
                    }
                    imageProxy.close()
                }
                .addOnFailureListener {
                    imageProxy.close()
                }
        }
        else
        {
            imageProxy.close()
        }
    }
}

class BarcodeScannerActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var barcodeCountMap = mutableMapOf<String,Int>()
    private val minimumBarcodeCount = 5
    private lateinit var webView: WebView
    private lateinit var textView: TextView
    private var waitingForResponse = false
    private lateinit var chosenBarcode: String

    private fun scanConfirmed()
    {
        barcodeCountMap.clear()
        waitingForResponse = false
        textView.text = "SKENIRAJ PICKO"
        // Add the scanned barcode
    }
    private fun scanDeclined()
    {
        barcodeCountMap.clear()
        waitingForResponse = false
        textView.text = "SKENIRAJ PICKO"
        webView.loadUrl("https://www.maxi.rs/online/")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scanner)

        textView = findViewById(R.id.textView)
        webView = findViewById(R.id.webView)
        webView.settings.setJavaScriptEnabled(true)

        // Wtf?
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        var okButton = findViewById<Button>(R.id.scan_ok_button)
        var wrongButton = findViewById<Button>(R.id.scan_wrong_button)
        okButton.setOnClickListener{ scanConfirmed() }
        wrongButton.setOnClickListener{ scanDeclined() }

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun onFoundBarcode(barcode: String)
    {
        val oldValue = barcodeCountMap[barcode] ?: 0
        barcodeCountMap[barcode] = oldValue+1
        if (barcodeCountMap[barcode]!! >= minimumBarcodeCount && !waitingForResponse)
        {
            webView.loadUrl("https://www.maxi.rs/online/search?q=$barcode")
            waitingForResponse = true
            chosenBarcode = barcode


            var okButton = findViewById<Button>(R.id.scan_ok_button)
            var wrongButton = findViewById<Button>(R.id.scan_wrong_button)
            okButton.visibility = Button.VISIBLE
            wrongButton.visibility = Button.VISIBLE
            textView.text = "Sken dobar?"

            barcodeCountMap.clear()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.createSurfaceProvider())
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, BarCodeAnalyzer { barcode ->
                        Log.d(TAG, "Found barcode: $barcode")
                        onFoundBarcode(barcode)
                    })
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "BarcodeScanner"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

}