package com.example.automaksi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddItemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)
        
        val searchRecycler : RecyclerView = findViewById(R.id.searchResultList)
        val adapter = SearchItemsAdapter(emptyArray())
        searchRecycler.adapter = adapter
        searchRecycler.layoutManager = LinearLayoutManager(this)

        val productSearch: SearchView = findViewById(R.id.productSearchView)
        productSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val db = Firebase.firestore
                val docRef = db.collection("Products")
                    .whereGreaterThanOrEqualTo("name", query)
                    .whereLessThanOrEqualTo("name", query + "\uf8ff")
                docRef.get()
                    .addOnSuccessListener { documents ->
                        adapter.clearItems()
                        for (document in documents) {
                            val nameIdPair = Pair(document.data["name"] as String, (document.data["id"] as String).toLong())
                            adapter.addItem(nameIdPair)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("Kurac", "Crko")
                    }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                /*val db = Firebase.firestore
                val docRef = db.collection("Products")
                    .whereGreaterThanOrEqualTo("name", newText)
                    .whereLessThanOrEqualTo("name", newText + "\uf8ff")
                docRef.get()
                    .addOnSuccessListener { documents ->
                        adapter.clearItems()
                        for (document in documents) {
                            adapter.addItem(document.data["name"] as String)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("Kurac", "Crko")
                    }*/
                return true
            }
        })
    }
}