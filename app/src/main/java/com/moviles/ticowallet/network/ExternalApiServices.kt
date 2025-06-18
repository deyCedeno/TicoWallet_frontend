// network/ExternalApiServices.kt
package com.moviles.ticowallet.network

import com.moviles.ticowallet.models.BCCRExchangeRate
import com.moviles.ticowallet.models.ExchangeRateApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface BCCRService {
    @GET("api")
    suspend fun getCurrentExchangeRate(): Response<BCCRExchangeRate>
}

interface ExchangeRateApiService {
    @GET("v4/latest/USD")
    suspend fun getExchangeRatesFromUSD(): Response<ExchangeRateApiResponse>
}

// network/ExternalRetrofitClient.kt
object ExternalRetrofitClient {

    // Cliente para BCCR (Costa Rica)
    private val bccrRetrofit = Retrofit.Builder()
        .baseUrl("https://tipodecambio.paginasweb.cr/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Cliente para ExchangeRate-API (Euros)
    private val exchangeRateRetrofit = Retrofit.Builder()
        .baseUrl("https://api.exchangerate-api.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val bccrService: BCCRService by lazy {
        bccrRetrofit.create(BCCRService::class.java)
    }

    val exchangeRateService: ExchangeRateApiService by lazy {
        exchangeRateRetrofit.create(ExchangeRateApiService::class.java)
    }
}