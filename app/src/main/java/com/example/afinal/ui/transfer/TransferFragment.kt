package com.example.afinal.ui.transfer

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.afinal.R
import com.example.afinal.database.TransactionsData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_transfer.*
import java.text.DateFormat
import java.util.*

class TransferFragment : Fragment(R.layout.fragment_transfer) {

    private lateinit var navController: NavController

    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        navController = Navigation.findNavController(view)

        var uid = FirebaseAuth.getInstance().currentUser!!.uid;
        uid?.let {
            FirebaseDatabase.getInstance().reference.child("Deposits").child(it)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.value != null) {
                            var map = p0.value as Map<String, Any>
                            // Display available balances (per currency) on card
                            if (map["gelCurrency"] == true) {
                                gelAvailableDisplay.text = map["gelBalance"].toString() + ".00₾"
                            }
                            if (map["usdCurrency"] == true) {
                                firstCurrAvailableDisplay.text = map["usdBalance"].toString() + ".00$"
                            } else {
                                firstCurrAvailableDisplay.text = ""
                            }
                            if (map["eurCurrency"] == true) {
                                secondCurrAvailableDisplay.text = map["eurBalance"].toString() + ".00€"
                            } else {
                                secondCurrAvailableDisplay.text = ""
                            }
                            if (map["gbpCurrency"] == true) {
                                thirdCurrAvailableDisplay.text = map["gbpBalance"].toString() + ".00£"
                            } else {
                                thirdCurrAvailableDisplay.text = ""
                            }
                            if (map["cadCurrency"] == true) {
                                fourthCurrAvailableDisplay.text = map["cadBalance"].toString() + ".00 CA\$"
                            } else {
                                fourthCurrAvailableDisplay.text = ""
                            }
                            // Display card number (Dashed XXXX XXXX XXXX)
                            var creditCardNumber = map["creditCardNumber"].toString()
                            val firstDash: String = creditCardNumber.substring(0, 4)
                            val secondDash: String = creditCardNumber.substring(4, 8)
                            val thirdDash: String = creditCardNumber.substring(8, 12)
                            var creditCardDashedString = "$firstDash $secondDash $thirdDash"
                            creditCardNumbDisplay.text = creditCardDashedString

                            // Send Money button
                            sendBtn.setOnClickListener() {
                                // Currency we entered (available options for now GEL / USD / EUR / GBP / CAD)
                                var enteredCurrency = transferCurrency.text.toString().toLowerCase()
                                // Amount we entered (aka how much we are sending)
                                var enteredAmount = targetTransferAmount.text.toString()
                                // Validating currency we entered
                                var supportedCurrency = validateCurrency(enteredCurrency, map)
                                if (supportedCurrency) {
                                    // Current cash we have in the said currency
                                    var availableCash = map[enteredCurrency+"Balance"].toString()
                                    // Current funds we would have if we deducted sent amount
                                    var enoughFunds = availableCash.toInt() - enteredAmount.toInt()
                                    // If funds are enough we proceed
                                    if (enoughFunds > 0) {
                                        var map = mutableMapOf<String, Any>()
                                        map[enteredCurrency + "Balance"] = enoughFunds

                                        // Call end user changes
                                        endTransaction(enteredCurrency, enteredAmount.toInt())

                                        FirebaseDatabase.getInstance().reference
                                            .child("Deposits")
                                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                            .updateChildren(map)
                                        // Save transaction for display
                                        saveTransaction()

                                    } else {
                                        // If transaction failed (Balance less than amount to be sent) | display error message
                                        hfNotificationText.text = "Invalid amount, make sure you entered valid amount!"
                                    }
                                } else {
                                    // If transaction failed (Invalid or non-supported Currency) | display error message
                                    hfNotificationText.text = "Invalid or non-supported Currency!"
                                }

                            }
                        }
                    }

                })
        }

    }
    fun validateCurrency(currency : String, map : Map<String, Any>) : Boolean {
        // Currency validation, check if entered currency matches with one of the currencies they have entered
        var currency = currency.toUpperCase()
        if (map["gelCurrency"] != true && currency == "GEL") {
            return false
        }
        if (map["usdCurrency"] != true && currency == "USD") {
            return false
        }
        if (map["eurCurrency"] != true && currency == "EUR") {
            return false
        }
        if (map["gbpCurrency"] != true && currency == "GBP") {
            return false
        }
        if (map["cadCurrency"] != true && currency == "CAD") {
            return false
        }
        return true
    }

    fun endTransaction(enteredCurrency : String, enteredCash : Int) {
        var targetCurrency = targetCreditCard.text.toString().takeLast(3).toLowerCase()

        FirebaseDatabase.getInstance().reference
            .child("Deposits")
            .orderByChild(targetCurrency + "CreditCardNumber").equalTo(targetCreditCard.text.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.value != null) {
                        // Target user used to update data
                        var targetUID = p0.children.first().key.toString()
                        var map = p0.children.first().value as Map<String, Any>
                        // Check if end (target) user has currency account for sent currency
                        var validTargetAccount = map[enteredCurrency + "Currency"]
                        if (validTargetAccount == true) {
                            // End user's current currency balance
                            var targetCurrentCurrencyBalance = map[enteredCurrency + "Balance"].toString()
                            // End user's updated currency balance (Adding sent money - same currency)
                            var targetUpdatedCurrencyBalance = targetCurrentCurrencyBalance.toInt() + enteredCash

                            var map1 = mutableMapOf<String, Any>()
                            map1[enteredCurrency + "Balance"] = targetUpdatedCurrencyBalance

                            // Update data
                            FirebaseDatabase.getInstance().reference
                                .child("Deposits")
                                .child(targetUID)
                                .updateChildren(map1)
                        }
                    } else {
                        hfNotificationText.text = "Transaction failed, make sure you entered valid values!"
                    }
                }
            })

    }
    fun saveTransaction() {

        val calendar = Calendar.getInstance()
        val currentDate = DateFormat.getDateInstance().format((calendar.time))

        val targetCCNumber= targetCreditCard.text.toString()
        var enteredAmt = targetTransferAmount.text.toString()
        var originCCNumber=creditCardNumbDisplay.text.toString()
        var currency= transferCurrency.text.toString()
        val ref= FirebaseDatabase.getInstance().getReference("Transactions")

        val transID= ref.push().key

        val transactionsData = TransactionsData(originCCNumber, targetCCNumber, enteredAmt, currency, currentDate.toString())
        ref.child(auth.currentUser?.uid!!).child(transID.toString()).setValue(transactionsData)
    }
}


