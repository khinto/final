package com.example.afinal.ui.transactions

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.afinal.R
import com.example.afinal.adapter.TransactionsAdapter
import com.example.afinal.database.History
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_transactions.*


class TransactionsFragment : Fragment(R.layout.fragment_transactions) {

    private lateinit var navController: NavController

    lateinit var cars: List<History>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        readSingleData()

    }
    fun readSingleData() {

        var uid = FirebaseAuth.getInstance().currentUser!!.uid;

        uid?.let {
            FirebaseDatabase.getInstance().reference.child("Transactions")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()) {

                            for (snapshot in p0.children) {
                                // This thing just doesn't work
                            }
                            val adapter = TransactionsAdapter(p0.value as Map<String, Any>)

                            recyclerView.layoutManager = LinearLayoutManager(context)

                            recyclerView.adapter = adapter
                        }

                    }
                })
        }
    }
}