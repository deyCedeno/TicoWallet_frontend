package com.moviles.ticowallet.network

import com.moviles.ticowallet.common.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.API_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val apiServiceExchangeRate: ApiServiceExchangeRate by lazy {
        retrofit.create(ApiServiceExchangeRate::class.java)
    }

    val apiServiceGoals: ApiServiceGoals by lazy {
        retrofit.create(ApiServiceGoals::class.java)
    }

    val warrantyApi: WarrantyService by lazy {
        retrofit.create(WarrantyService::class.java)
    }
}
