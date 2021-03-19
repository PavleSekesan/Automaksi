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

class OrderItemsAdapter(private var stockHandler: StockHandler) :
        RecyclerView.Adapter<OrderItemsAdapter.ViewHolder>()  {
        private val itemIds = mutableListOf<String>()

        fun addItem(product : Map<String, Any>) {
            if (product.containsKey("item_id"))
            {
                stockHandler.addItem(product)
                val id = product["item_id"].toString()
                itemIds.add(id)
                stockHandler.addOnItemCountChangeListener(id) {
                    super.notifyDataSetChanged()
                }
                super.notifyDataSetChanged()
            }
        }

        fun deleteItem(itemId : String) {
            stockHandler.deleteItem(itemId)
        }

        fun decreaseItem(pos: Int) {
            stockHandler.modifyItemCount(itemIds[pos],-1)
        }

        fun increaseItem(pos: Int) {
            stockHandler.modifyItemCount(itemIds[pos]!!,1)
        }

        fun clearItems() {
            itemIds.clear()
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
            val item = stockHandler.getItem(itemIds[position])
            if (item != null)
            {
                viewHolder.itemName.text = item["name"].toString()
                viewHolder.quantity.text = item["current_quantity"].toString()
                viewHolder.add.setOnClickListener {
                    increaseItem(position)
                }
                viewHolder.subtract.setOnClickListener {
                    decreaseItem(position)
                }
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = itemIds.size

    }