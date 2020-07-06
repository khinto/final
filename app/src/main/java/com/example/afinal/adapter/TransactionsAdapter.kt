package com.example.afinal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.R

class TransactionsAdapter(private val transactionsList: Map<String, Any>) :
    RecyclerView.Adapter<TransactionsAdapter.TransactionsViewHolder>() {

    class TransactionsViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionsViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)

        return TransactionsViewHolder(view)

    }

    override fun getItemCount(): Int = transactionsList.size


    override fun onBindViewHolder(holder: TransactionsViewHolder, position: Int) {
        val pos = transactionsList
        println("Position $pos")

    }


}