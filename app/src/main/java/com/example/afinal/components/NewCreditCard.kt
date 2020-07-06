package com.example.afinal.components

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.afinal.R
import com.example.afinal.database.DepositData
import com.example.afinal.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_new_card.*
import java.util.*

class NewCreditCard : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    // Bunch of variables
    val random = Random()
    var creditCardNumb = ""
    var creditCardDashedString = ""
    var defaultBalance = 100

    var firstName = ""
    var lastName = ""

    var cardValidYears = 4

    var cardType = "MasterCard"

    var gelCurrencyChecked = false;
    var usdCurrencyChecked = false;
    var eurCurrencyChecked = false;
    var gbpCurrencyChecked = false;
    var cadCurrencyChecked = false;

    var gelCreditCardNumb = ""
    var usdCreditCardNumb = ""
    var eurCreditCardNumb = ""
    var gbpCreditCardNumb = ""
    var cadCreditCardNumb = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_new_card)

        auth = FirebaseAuth.getInstance()

        // Generate 12 digit random credit card number
        for (x in 0..11) {
            var randNumb = rand(0, 9)
            creditCardNumb += randNumb
        }
        // Split randomly generated credit card number for display purposes
        val firstDash: String = creditCardNumb.substring(0, 4)
        val secondDash: String = creditCardNumb.substring(4, 8)
        val thirdDash: String = creditCardNumb.substring(8, 12)

        creditCardDashedString = "$firstDash $secondDash $thirdDash"

        creditCardGenerated.text = creditCardDashedString;
        mainCardNumberDisplay.text = creditCardDashedString;
        // Get firstName and LastName to add into card
        var uid= FirebaseAuth.getInstance().currentUser!!.uid;

        uid?.let {
            FirebaseDatabase.getInstance().reference
                .child("Users")
                .child(it)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}

                    override fun onDataChange(p0: DataSnapshot) {
                        var map = p0.value as Map<String,Any>
                        firstName = map["firstName"].toString()
                        lastName = map["lastName"].toString()
                    }
                })
        }
        // Switch card colors on click (Blue)
        blueBtn.setOnClickListener() {
            linearLayout2.background = ContextCompat.getDrawable(this, R.drawable.card_style_blue);
        }
        // Switch card colors on click (Yellow)
        yellowBtn.setOnClickListener() {
            linearLayout2.background = ContextCompat.getDrawable(this, R.drawable.card_style_yellow);
        }
        // Switch card colors on click (Red)
        redBtn.setOnClickListener() {
            linearLayout2.background = ContextCompat.getDrawable(this, R.drawable.card_style_red);
        }
        // Switch card colors on click (Green)
        greenBtn.setOnClickListener() {
            linearLayout2.background = ContextCompat.getDrawable(this, R.drawable.card_style_green);
        }

        // Create card on button click
        createCardBtn.setOnClickListener() {
            saveDataDeposit()
        }
    }
    // Generate random number
    private fun rand(from: Int, to: Int) : Int {
        return random.nextInt(to - from) + from
    }
    // Save card information
    private fun saveDataDeposit() {
        val ref = FirebaseDatabase.getInstance().getReference("Deposits")

        if(gelCheckBox.isChecked){
            gelCurrencyChecked = true
            gelCreditCardNumb = creditCardNumb + "GEL"
        }
        if(usdCheckBox.isChecked){
            usdCurrencyChecked = true
            usdCreditCardNumb = creditCardNumb + "USD"
        }
        if(eurCheckBox.isChecked){
            eurCurrencyChecked = true
            eurCreditCardNumb = creditCardNumb + "EUR"
        }
        if(gbpCheckBox.isChecked){
            gbpCurrencyChecked = true
            gbpCreditCardNumb = creditCardNumb + "GBP"
        }
        if(cadCheckBox.isChecked){
            cadCurrencyChecked = true
            cadCreditCardNumb = creditCardNumb + "CAD"
        }
        val cardNumberGenerated = creditCardNumb.toLong()
        val data = DepositData(cardNumberGenerated, gelCreditCardNumb, usdCreditCardNumb, eurCreditCardNumb, gbpCreditCardNumb, cadCreditCardNumb,
            defaultBalance, defaultBalance, defaultBalance, defaultBalance, defaultBalance, cardType,
            gelCurrencyChecked, usdCurrencyChecked, eurCurrencyChecked, gbpCurrencyChecked, cadCurrencyChecked, cardValidYears, firstName, lastName)

        ref.child(auth.currentUser?.uid!!).setValue(data)

    }
}
