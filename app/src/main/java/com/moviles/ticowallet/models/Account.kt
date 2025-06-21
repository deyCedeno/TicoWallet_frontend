package com.moviles.ticowallet.models

import com.google.gson.annotations.SerializedName

data class Account(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    val name: String,
    val accountType: String,
    val balance: Double,
    val currency: String,
    val movements: List<Movement>? = null
)
