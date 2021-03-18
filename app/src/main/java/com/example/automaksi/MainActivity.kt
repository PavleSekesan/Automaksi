package com.example.automaksi

import android.app.Activity
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if user is already logged in
        var auth = FirebaseAuth.getInstance()
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
        val db = Firebase.firestore

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
        val adapter = OrderItemsAdapter(ArrayList(0))
        orderRecycler.adapter = adapter
        orderRecycler.layoutManager = LinearLayoutManager(this)

        db.collection("UserItems").document(auth.uid.toString()).collection("items").get().addOnSuccessListener { documents ->
            for (document in documents)
            {
                val data = document.data
                val name = data["name"]
                val currentQuantity = data["current_quantity"] as Long
                val itemId = data["item_id"] as Long
                val newItem = Triple(name.toString(), currentQuantity.toInt(), itemId.toInt())
                adapter.addItem(newItem)
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
                Log.d("kurac", user.toString())
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
    companion object {

        private const val RC_SIGN_IN = 123
    }
}