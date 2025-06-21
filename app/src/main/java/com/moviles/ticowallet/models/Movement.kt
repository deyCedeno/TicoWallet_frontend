package com.moviles.ticowallet.models

data class Movement(
    val id: Int,
    val amount: Double,
    val description: String,
    val date: String,
    val time: String,
    val accountName: String,
    val category: Category,
    val currency: String,
    val type: String,
    val methodPayment: String,
    val warranty: Int,
    val state: String,
    val location: String,
    val account: Account,
    val destinationAccount: Account
)
