// network/ApiServiceExchangeRate.kt
package com.moviles.ticowallet.network

import com.moviles.ticowallet.models.ExchangeRate
import com.moviles.ticowallet.models.ExchangeRateResponse
import com.moviles.ticowallet.models.CreateExchangeRateDto
import retrofit2.Response
import retrofit2.http.*

interface ApiServiceExchangeRate {

    @GET("api/ExchangeRate")
    suspend fun getExchangeRates(): Response<List<ExchangeRate>>

    @GET("api/ExchangeRate/current")
    suspend fun getCurrentExchangeRates(): Response<ExchangeRateResponse>

    @GET("api/ExchangeRate/{fromCurrency}/{toCurrency}")
    suspend fun getExchangeRate(
        @Path("fromCurrency") fromCurrency: String,
        @Path("toCurrency") toCurrency: String
    ): Response<ExchangeRate>

    @POST("api/ExchangeRate")
    suspend fun createExchangeRate(@Body exchangeRate: CreateExchangeRateDto): Response<ExchangeRate>

    @PUT("api/ExchangeRate/{id}")
    suspend fun updateExchangeRate(
        @Path("id") id: Int,
        @Body exchangeRate: CreateExchangeRateDto
    ): Response<Unit>

    @DELETE("api/ExchangeRate/{id}")
    suspend fun deleteExchangeRate(@Path("id") id: Int): Response<Unit>
}