package com.example.afinal.components

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.afinal.R
import com.example.afinal.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_profile.*

class ProfilePage : AppCompatActivity() {

    var firstName = ""
    var lastName = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        // Display data
        retrieveData()

        // Logout button
        signOutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    private fun retrieveData() {
        var uid = FirebaseAuth.getInstance().currentUser!!.uid;

        uid.let {
            FirebaseDatabase.getInstance().reference
                .child("Users")
                .child(it)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var map = dataSnapshot.value as Map<String, Any>
                        firstName = map["firstName"].toString()
                        lastName = map["lastName"].toString()
                        val emailAddress= map["emailAddress"].toString()
                        profileName.text = "$firstName $lastName"
                        profileEmail.text = emailAddress
                    }
                })
        }

    }


}