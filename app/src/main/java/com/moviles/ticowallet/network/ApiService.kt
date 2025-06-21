package com.moviles.ticowallet.network

import com.moviles.ticowallet.DAO.ResetPasswordRequestDto
import com.moviles.ticowallet.DAO.UpdateImageResponse
import com.moviles.ticowallet.DAO.UpdateUserProfileDto
import com.moviles.ticowallet.DAO.UpdateUserProfileResponse
import com.moviles.ticowallet.models.Account
import com.moviles.ticowallet.models.Category
import com.moviles.ticowallet.models.CreateScheduledPaymentDto
import com.moviles.ticowallet.models.DashboardResponse
import com.moviles.ticowallet.models.Goal
import com.moviles.ticowallet.models.HomePageResponse
import com.moviles.ticowallet.models.ScheduledPayment
import com.moviles.ticowallet.models.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @Multipart
    @POST("api/user/register")
    suspend fun addUser(@Part("Name") name: RequestBody,
                         @Part("Email") email: RequestBody,
                         @Part("Password") password: RequestBody,
                         @Part("ConfirmPassword") confirmPassword: RequestBody): User

    @PUT("api/user/update_profile")
    suspend fun updateUserProfile(@Body updateDto: UpdateUserProfileDto): Response<UpdateUserProfileResponse>

    @Multipart
    @PUT("api/images/user/update_image")
    suspend fun updateUserImage(@Part image: MultipartBody.Part): Response<UpdateImageResponse>

    @POST("/api/user/login")
    suspend fun signIn(@Body userDto: User) : Response<User>

    @POST("/api/user/send_code")
    suspend fun sendCode(@Body userDto: User) : User

    @POST("/api/user/reset_password")
    suspend fun resetPassword(@Body resetPasswordDto: ResetPasswordRequestDto): User

    @GET("/api/user")
    suspend fun getUser() : User

    //    GOAL
    @GET("api/goal/get_all")
    suspend fun getGoals(): List<Goal>

    @GET("api/goal/{id}")
    suspend fun getGoalById(@Path("id") id: String): Goal

    @PUT("api/goal/{id}")
    suspend fun updateGoal(@Path("id") id: String, @Body goal: Goal): Goal

    @DELETE("api/goal/{id}")
    suspend fun deleteGoal(@Path("id") id: String): Response<Void>

    //  Account
    @GET("api/statistics/home")
    suspend fun getAllHome(): HomePageResponse

    @GET("api/account")
    suspend fun getAllAccounts(): List<Account>

    @DELETE("api/account/{id}")
    suspend fun deleteAccount(@Path("id") Id: Int): Response<Void>

    @POST("/api/account/register")
    suspend fun addAccount(@Body account: Account) : Account


    @POST("api/goal")
    suspend fun createGoal(@Body goal: Goal): Goal

    // SCHEDULED PAYMENT ENDPOINTS

    @GET("api/scheduled-payment")
    suspend fun getScheduledPayments(): List<ScheduledPayment>

    @GET("api/scheduled-payment/{id}")
    suspend fun getScheduledPaymentById(@Path("id") id: Int): ScheduledPayment

    @GET("api/scheduled-payment/accounts")
    suspend fun getUserAccounts(): List<Account>

    @GET("api/scheduled-payment/categories")
    suspend fun getCategories(): List<Category>

    @POST("api/scheduled-payment")
    suspend fun createScheduledPayment(@Body scheduledPayment: CreateScheduledPaymentDto): Response<ApiResponse>

    @PUT("api/scheduled-payment/{id}")
    suspend fun updateScheduledPayment(@Path("id") id: Int, @Body scheduledPayment: CreateScheduledPaymentDto): Response<ApiResponse>

    @DELETE("api/scheduled-payment/{id}")
    suspend fun deleteScheduledPayment(@Path("id") id: Int): Response<ApiResponse>

    // Dashboard

    @GET("api/statistics/dashboard")
    suspend fun getDashboardStats(): DashboardResponse
}

// Generic API response for success/error messages
data class ApiResponse(
    val message: String? = null,
    val error: String? = null
)