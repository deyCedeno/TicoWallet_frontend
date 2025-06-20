package com.moviles.ticowallet.models

import java.util.Date
data class Goal(

    val id: String,
    val name: String,
    val quantity: Double,
    val goalDate: Date,
    val currentQuantity: Double = 0.0,
    val icon: String,
    val state: String = "Activo",
    val note: String? = null
)