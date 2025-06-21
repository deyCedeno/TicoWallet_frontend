package com.moviles.ticowallet.models

data class MovementGet (
    val id: Int,
    val amount: Double,
    val description: String,
    val date: String,
    val accountName: String,
    val accountId: Int,
    val location: String,
    val currency: String,
    val state: String,
    val type: String
)