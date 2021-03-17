package com.example.automaksi

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class SearchItemsAdapter(private var dataSet: Array<String>) :
    RecyclerView.Adapter<SearchItemsAdapter.ViewHolder>() {

    fun addItem(text : String) {
        dataSet = dataSet.plus(text)
        super.notifyItemInserted(dataSet.size - 1)
    }

    fun clearItems() {
        dataSet = emptyArray<String>()
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
        viewHolder.textView.text = dataSet[position]
        viewHolder.itemView.setOnClickListener {
            //Toast.makeText(viewHolder.itemView.context, viewHolder.textView.text.toString(), Toast.LENGTH_LONG).show()
            val intent = Intent(context, QuantitySelectorActivity::class.java)
            intent.putExtra("itemName", viewHolder.textView.text.toString())
            context.startActivity(intent)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}