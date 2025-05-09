package com.moviles.ticowallet.models

import java.util.Date
data class Goal(

    val id: String,
    // val idUser: Int, // No la incluimos directamente aquí, se asume filtrado por usuario logueado
    val name: String,
    val quantity: Double,
    val goalDate: Date,
    val currentQuantity: Double = 0.0,
    val icon: String,
    val state: String = "Activo",
    val note: String? = null
    // val user: User? = null
)