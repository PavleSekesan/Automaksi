package com.example.automaksi

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchItemsAdapter(private var dataSet: Array<Pair<String,Long>>) :
    RecyclerView.Adapter<SearchItemsAdapter.ViewHolder>() {

    fun addItem(nameIdPair: Pair<String,Long>) {
        dataSet = dataSet.plus(nameIdPair)
        super.notifyItemInserted(dataSet.size - 1)
    }

    fun clearItems() {
        dataSet = emptyArray()
        super.notifyDataSetChanged()
    }
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            // Define click listener for the ViewHolder's View.
            textView = view.findViewById(R.id.textView)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.search_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val context = viewHolder.itemView.context
        viewHolder.textView.text = dataSet[position].first
        viewHolder.itemView.setOnClickListener {
            //Toast.makeText(viewHolder.itemView.context, viewHolder.textView.text.toString(), Toast.LENGTH_LONG).show()
            val intent = Intent(context, QuantitySelectorActivity::class.java)
            intent.putExtra("itemName", viewHolder.textView.text.toString())
            intent.putExtra("itemId", dataSet[position].second.toString())
            context.startActivity(intent)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}