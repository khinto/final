package com.example.afinal.database

import java.util.*

class DepositData (
    val creditCardNumber : Long,
    val gelCreditCardNumber : String,
    val usdCreditCardNumber : String,
    val eurCreditCardNumber : String,
    val gbpCreditCardNumber : String,
    val cadCreditCardNumber : String,
    val gelBalance : Int,
    val usdBalance : Int,
    val eurBalance : Int,
    val gbpBalance : Int,
    val cadBalance : Int,
    val cardType : String,
    val gelCurrency : Boolean,
    val usdCurrency : Boolean,
    val eurCurrency : Boolean,
    val gbpCurrency : Boolean,
    val cadCurrency : Boolean,
    val Term : Int,
    val firstName : String,
    val lastName : String
)