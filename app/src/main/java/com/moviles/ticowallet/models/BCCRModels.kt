package com.moviles.ticowallet.models
import com.google.gson.annotations.SerializedName

data class BCCRExchangeRate(
    val compra: Double,
    val venta: Double,
    val fecha: String
)

data class ExchangeRateApiResponse(
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)