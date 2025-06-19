package com.moviles.ticowallet.models

data class Movement(
    val id: Int,
    val amount: Double,
    val description: String,
    val date: String,
    val time: String,
    val accountName: String,
    val category: Category,
    val currency: String
)
