package com.moviles.ticowallet.models

data class CreateMovementDto(
    val type: String,
    val description: String,
    val methodPayment: String,
    val amount: Double,
    val warranty: Int,
    val state: String,
    val location: String,
    val categoryId: Int,
    val destinationAccountId: Int,
    val date: String,
    val time: String,
    val accountId: Int
)
