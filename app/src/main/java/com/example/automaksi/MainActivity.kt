package com.example.automaksi

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var stockHandler: StockHandler

    fun continueAfterUserSetup(deliveryDays: ArrayList<Int>)
    {
        stockHandler = StockHandler(db, auth.currentUser!!)
        val addItemButton: View = findViewById(R.id.addItemFloatingActionButton)
        addItemButton.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }

        // Sends order to database for the server to execute
        val sendOrderButton : View = findViewById(R.id.sendOrderButton)
        sendOrderButton.setOnClickListener {
            db.collection("UserItems").document(auth.uid.toString()).collection("items").get().addOnSuccessListener { documents ->
                val userId = auth.uid.toString()
                val productsArray = arrayListOf<Map<String, Any>>()
                for (document in documents) {
                    val itemId = document.data["item_id"].toString()
                    val quantity = document.data["current_quantity"] as Long
                    productsArray.add(hashMapOf(
                        "product_id" to itemId,
                        "quantity" to quantity
                    ))
                }

                val orderData = hashMapOf(
                    "products" to productsArray,
                    "user_id" to userId
                )

                db.collection("Orders")
                    .add(orderData)
                    .addOnSuccessListener { documentReference ->
                        Log.d("Jaje", "DocumentSnapshot written with ID: ${documentReference.id}")
                    }
            }
        }

        val scanBarcodeButton : View = findViewById(R.id.scanBarcodeButton)
        scanBarcodeButton.setOnClickListener {
            val intent = Intent(this, BarcodeScannerActivity::class.java)
            startActivity(intent)
        }

        val orderRecycler : RecyclerView = findViewById(R.id.ordersRecyclerView)
        val adapter = OrderItemsAdapter(stockHandler)
        orderRecycler.adapter = adapter
        orderRecycler.layoutManager = LinearLayoutManager(this)

        stockHandler.addOnDataLoadedListener { allItems->
            for (item in allItems)
            {
                adapter.addItem(item)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if user is already logged in
        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        if (auth.currentUser == null)
        {
            // Choose authentication providers
            val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN)
        }

        if (auth.currentUser != null)
        {
            db.collection("UserData").document(auth.uid.toString()).get().addOnSuccessListener { document->
                if (!document.exists())
                {
                    val intent = Intent(this, WeekdaySelectorActivity::class.java)
                    startActivity(intent)
                }
                else
                {
                    continueAfterUserSetup(document.data?.get("delivery_days") as ArrayList<Int>)
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                Log.d(TAG, "Login successful: $user")
                // ...
            } else {
                Log.d(TAG, "Login failed: ${response?.error?.errorCode.toString()}")

                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
    companion object {
        private const val TAG = "MainActivity"
        private const val RC_SIGN_IN = 123
    }
}