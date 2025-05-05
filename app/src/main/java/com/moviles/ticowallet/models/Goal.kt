package com.moviles.ticowallet.models

import java.util.Date // O considera kotlinx-datetime o java.time

// Data class actualizada según el modelo C#
data class Goal(
    // Usamos String para el ID por flexibilidad con APIs y navegación,
    // aunque el backend use int. La conversión se haría al interactuar con la API.
    val id: String,
    // val idUser: Int, // No la incluimos directamente aquí, se asume filtrado por usuario logueado
    val name: String,
    val quantity: Double, // Cantidad objetivo (era targetAmount) - Mapea 'decimal' a Double
    val goalDate: Date,   // Fecha objetivo (era deadline)
    val currentQuantity: Double = 0.0, // Cantidad actual (era currentAmount) - Mapea 'decimal' a Double
    val icon: String,     // Nombre o URL del ícono
    val state: String = "Activo", // Estado del objetivo
    val note: String? = null // Nota opcional (era description)
    // val user: User? = null // No necesitamos el objeto User completo aquí
)