package com.example.automaksi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.order_item.view.*

class OrderItemsAdapter(private var products: ArrayList<Triple<String, Int, Int>>) :
        RecyclerView.Adapter<OrderItemsAdapter.ViewHolder>()  {

        fun addItem(product : Triple<String, Int, Int>) {
            products.add(product)
            super.notifyItemInserted(products.size - 1)
        }

        fun deleteItem(name : String) {
            for (product in products) {
                if (product.first == name)
                    products.remove(product)
            }
        }

        fun decreaseItem(pos: Int) {
            val quantity = products[pos].second
            if (quantity > 0)
            {
                // Update count in database
                var auth = FirebaseAuth.getInstance()
                val db = Firebase.firestore
                val docRef = db.collection("UserItems").document(auth.uid.toString()).collection("items").whereEqualTo("item_id", products[pos].third)
                docRef.get().addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val currentQuantity = documents.first().data["current_quantity"] as Long
                        val queryDocument = db.collection("UserItems").document(auth.uid.toString()).collection("items").document(documents.first().id)
                        queryDocument.update(mapOf("current_quantity" to currentQuantity - 1 )).addOnSuccessListener {
                            products[pos] = Triple(products[pos].first, products[pos].second - 1, products[pos].third)
                            super.notifyDataSetChanged()
                        }
                    }
                }
            }
        }

        fun increaseItem(pos: Int) {
            // Update count in database
            var auth = FirebaseAuth.getInstance()
            val db = Firebase.firestore
            val docRef = db.collection("UserItems").document(auth.uid.toString()).collection("items").whereEqualTo("item_id", products[pos].third)
            docRef.get().addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val currentQuantity = documents.first().data["current_quantity"] as Long
                    val queryDocument = db.collection("UserItems").document(auth.uid.toString()).collection("items").document(documents.first().id)
                    queryDocument.update(mapOf("current_quantity" to currentQuantity + 1 )).addOnSuccessListener {
                        products[pos] = Triple(products[pos].first, products[pos].second + 1, products[pos].third)
                        super.notifyDataSetChanged()
                    }
                }
            }
        }

        fun clearItems() {
            products.clear()
            super.notifyDataSetChanged()
        }
        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val itemName: TextView
            val quantity: TextView
            val add: Button
            val subtract: Button

            init {
                // Define click listener for the ViewHolder's View.
                itemName = view.findViewById(R.id.itemNameTextView)
                quantity = view.findViewById(R.id.quantityTextView)
                add = view.findViewById(R.id.addButton)
                subtract = view.findViewById(R.id.subtractButton)
            }
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            // Create a new view, which defines the UI of the list item
            val view = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.order_item, viewGroup, false)

            return ViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            val context = viewHolder.itemView.context
            viewHolder.itemName.text = products[position].first
            viewHolder.quantity.text = products[position].second.toString()
            viewHolder.add.setOnClickListener {
                increaseItem(position)
            }
            viewHolder.subtract.setOnClickListener {
                decreaseItem(position)
            }
            viewHolder.quantity.text = products[position].second.toString()
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = products.size

    }