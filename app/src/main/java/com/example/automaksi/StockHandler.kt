package com.example.automaksi

import com.google.android.gms.dynamic.IFragmentWrapper
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.lang.NullPointerException

class StockHandler (private val db: FirebaseFirestore, private val user: FirebaseUser)
{
    private var idItemDataMap: MutableMap<String, MutableMap<String,Any>> = mutableMapOf()
    private var idListenerMap: MutableMap<String, ArrayList<()->Unit>> = mutableMapOf()
    private val dataLoadedListeners = mutableListOf<(ArrayList<Map<String, Any>>)->Unit>()
    init {
        db.collection("UserItems").document(user.uid).collection("items").get().addOnSuccessListener { documents->
            for (document in documents)
            {
                val data = document.data
                val itemId = data["item_id"].toString()
                idItemDataMap[itemId] = data
            }
            for (listener in dataLoadedListeners)
            {
                listener.invoke(getAllItems())
            }
        }
    }
    public fun modifyItemCount(itemId: String, countChange: Long)
    {
        if (idItemDataMap.containsKey(itemId) && idItemDataMap[itemId]!!.containsKey("current_quantity") &&
                idItemDataMap[itemId]!!["current_quantity"] as Long + countChange >= 0)
        {
            // Update count in database
            val docRef = db.collection("UserItems").document(user.uid).collection("items").whereEqualTo("item_id", itemId)
            docRef.get().addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val currentQuantity = documents.first().data["current_quantity"] as Long
                    val queryDocument = db.collection("UserItems").document(user.uid).collection("items").document(documents.first().id)
                    queryDocument.update(mapOf("current_quantity" to currentQuantity + countChange )).addOnSuccessListener {
                        idItemDataMap[itemId]!!["current_quantity"] = idItemDataMap[itemId]!!["current_quantity"] as Long + countChange
                        if (idListenerMap.containsKey(itemId))
                        {
                            // Notify listeners
                            for (listener in idListenerMap[itemId]!!) {
                                listener.invoke()
                            }
                        }
                    }
                }
            }
        }
    }
    private fun getAllItems(): ArrayList<Map<String,Any>>
    {
        val itemList = arrayListOf<Map<String,Any>>()
        for (kvp in idItemDataMap)
        {
            itemList.add(kvp.value)
        }
        return itemList;
    }
    fun getItem(itemId: String): MutableMap<String, Any>? {
        if (idItemDataMap.containsKey(itemId))
        {
            return idItemDataMap[itemId]
        }
        else
        {
            return null
        }
    }
    fun addItem(item: Map<String,Any>)
    {
        if (item.containsKey("item_id"))
        {
            val id = item["item_id"].toString()
            idItemDataMap[id] = item.toMutableMap()
            idListenerMap[id] = arrayListOf()
        }
    }
    fun deleteItem(itemId: String)
    {
        TODO("Implement item deletion in StockHandler")
    }
    fun addOnItemCountChangeListener(itemId: String, listener: ()->Unit)
    {
        if (!idListenerMap.containsKey(itemId)) {
            idListenerMap[itemId] = arrayListOf()
        }
        idListenerMap[itemId]!!.add(listener)
    }
    fun addOnDataLoadedListener(listener: (ArrayList<Map<String, Any>>) -> Unit) {
        dataLoadedListeners.add(listener)
    }

}
