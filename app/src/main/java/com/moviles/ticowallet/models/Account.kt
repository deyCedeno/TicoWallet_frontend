package com.moviles.ticowallet.models

data class Account(
    val id: Int? = null,
    val name: String,
    val accountType: String,
    val balance: Double,
    val currency: String
)
