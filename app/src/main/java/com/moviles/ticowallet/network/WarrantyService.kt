package com.moviles.ticowallet.network

import com.moviles.ticowallet.models.Warranty
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface WarrantyService {
    @GET("api/warranty")
    suspend fun getWarranties(): List<Warranty>

    @GET("api/warranty/{id}")
    suspend fun getWarrantyById(@Path("id") id: Int): Warranty

    @GET("api/warranty/expired")
    suspend fun getExpiredWarranties(): List<Warranty>

    @GET("api/warranty/expiring-soon")
    suspend fun getExpiringWarranties(@Query("days") days: Int = 30): List<Warranty>

    @GET("api/warranty/statistics")
    suspend fun getWarrantyStatistics(): WarrantyStatistics

    @POST("api/warranty")
    suspend fun createWarranty(@Body warranty: CreateWarrantyRequest): Warranty

    @PUT("api/warranty/{id}")
    suspend fun updateWarranty(@Path("id") id: Int, @Body warranty: UpdateWarrantyRequest): Warranty

    @DELETE("api/warranty/{id}")
    suspend fun deleteWarranty(@Path("id") id: Int): Response<Void>

    @DELETE("api/warranty/bulk")
    suspend fun deleteWarranties(@Body ids: IntArray): Response<Void>
}


data class CreateWarrantyRequest(
    val name: String,
    val price: Double,
    val purchaseDate: String,
    val expirationDate: String,
    val icon: String?
)

data class UpdateWarrantyRequest(
    val name: String,
    val price: Double,
    val purchaseDate: String,
    val expirationDate: String,
    val icon: String?
)

data class WarrantyStatistics(
    val total: Int,
    val active: Int,
    val expired: Int,
    val expiringSoon: Int,
    val totalValue: Double,
    val activeValue: Double,
    val expiredValue: Double
)