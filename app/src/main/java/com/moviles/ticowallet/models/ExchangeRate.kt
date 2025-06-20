// models/ExchangeRateModels.kt
package com.moviles.ticowallet.models

import com.google.gson.annotations.SerializedName

data class ExchangeRate(
    val id: Int,
    @SerializedName("fromCurrency")
    val fromCurrency: String,
    @SerializedName("toCurrency")
    val toCurrency: String,
    val rate: Double,
    @SerializedName("lastUpdated")
    val lastUpdated: String
)

data class ExchangeRateResponse(
    @SerializedName("crc")
    val crc: String,
    @SerializedName("usd")
    val usd: String,
    @SerializedName("eur")
    val eur: String,
    @SerializedName("lastUpdated")
    val lastUpdated: String
)

data class CreateExchangeRateDto(
    @SerializedName("fromCurrency")
    val fromCurrency: String,
    @SerializedName("toCurrency")
    val toCurrency: String,
    val rate: Double
)

