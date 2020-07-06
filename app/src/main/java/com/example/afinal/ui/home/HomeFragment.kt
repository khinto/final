package com.example.afinal.ui.home

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.afinal.R
import com.example.afinal.components.NewCreditCard
import com.example.afinal.components.ProfilePage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.firstCurrAvailableDisplay
import kotlinx.android.synthetic.main.fragment_home.fourthCurrAvailableDisplay
import kotlinx.android.synthetic.main.fragment_home.homeFragment_title
import kotlinx.android.synthetic.main.fragment_home.secondCurrAvailableDisplay
import kotlinx.android.synthetic.main.fragment_home.thirdCurrAvailableDisplay

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var navController: NavController
    var firstName = ""
    var lastName = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        // Open up create card page on click
        createCardBtn.setOnClickListener {
            val createCardActivity = Intent(context, NewCreditCard::class.java)
            startActivity(createCardActivity)
        }
        accountBtn.setOnClickListener {
            val profilePageActivity = Intent(context, ProfilePage::class.java)
            startActivity(profilePageActivity)
        }
        readSingleData()
    }
    private fun readSingleData() {
        var uid= FirebaseAuth.getInstance().currentUser!!.uid;

        uid.let {
            FirebaseDatabase.getInstance().reference
                .child("Users")
                .child(it)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}

                    override fun onDataChange(userData: DataSnapshot) {
                        if (userData.exists()) {
                            var map = userData.value as Map<String, Any>
                            firstName = map["firstName"].toString()
                            lastName = map["lastName"].toString()
                            homeFragment_title.text = "Welcome, $firstName $lastName"
                        }
                    }
                })
        }

        uid.let {
            FirebaseDatabase.getInstance().reference
                .child("Deposits")
                .child(it)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            var map = dataSnapshot.value as Map<String, Any>
                            if (map["gelCurrency"] == true) {
                                gelAvailableDisplayFH.text = map["gelBalance"].toString() + ".00₾"
                            }
                            if (map["usdCurrency"] == true) {
                                firstCurrAvailableDisplay.text =
                                    map["usdBalance"].toString() + ".00$"
                            } else {
                                firstCurrAvailableDisplay.text = ""
                            }
                            if (map["eurCurrency"] == true) {
                                secondCurrAvailableDisplay.text =
                                    map["eurBalance"].toString() + ".00€"
                            } else {
                                secondCurrAvailableDisplay.text = ""
                            }
                            if (map["gbpCurrency"] == true) {
                                thirdCurrAvailableDisplay.text =
                                    map["gbpBalance"].toString() + ".00£"
                            } else {
                                thirdCurrAvailableDisplay.text = ""
                            }
                            if (map["cadCurrency"] == true) {
                                fourthCurrAvailableDisplay.text =
                                    map["cadBalance"].toString() + ".00 CA\$"
                            } else {
                                fourthCurrAvailableDisplay.text = ""
                            }
                            // Display card number (Dashed XXXX XXXX XXXX)
                            var creditCardNumber = map["creditCardNumber"].toString()
                            val firstDash: String = creditCardNumber.substring(0, 4)
                            val secondDash: String = creditCardNumber.substring(4, 8)
                            val thirdDash: String = creditCardNumber.substring(8, 12)
                            var creditCardDashedString = "$firstDash $secondDash $thirdDash"
                            creditCardNumberDisplay.text = creditCardDashedString
                            layoutCreateCard.visibility = View.INVISIBLE
                        } else {
                            creditCardNumberDisplay.text = "No Card Found"
                        }
                    }
                })
        }

    }
}


