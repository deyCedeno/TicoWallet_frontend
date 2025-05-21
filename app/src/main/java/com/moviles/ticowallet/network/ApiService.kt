package com.moviles.ticowallet.network

import com.moviles.ticowallet.models.Goal
import com.moviles.ticowallet.models.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
    suspend fun addUser(
        @Part("Name") name: RequestBody,
        @Part("Password") password: RequestBody,
        @Part("Email") email: RequestBody,
        @Part("Date") date: RequestBody,
        @Part file: MultipartBody.Part?
    ): User
    /*
    @POST("api/user/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): User
    */

    @GET("api/user")
    suspend fun getUsers(): List<User>

    @GET("api/user/logout")
    suspend fun logout(): Response<Void>

    @GET("api/user/send_code")
    suspend fun sendCode(
        @Query("email") email: String
    ): Response<Void>

    @GET("api/user/reset_password")
    suspend fun resetPassword(
        @Query("email") email: String,
        @Query("code") code: String,
        @Query("password") password: String
    ): Response<Void>


    @GET("api/goal/get_all")
    suspend fun getGoals(): List<Goal>

    @GET("api/goal/{id}")
    suspend fun getGoalById(@Path("id") id: String): Goal

    @PUT("api/goal/{id}")
    suspend fun updateGoal(@Path("id") id: String, @Body goal: Goal): Goal

    @DELETE("api/goal/{id}")
    suspend fun deleteGoal(@Path("id") id: String): Response<Void>

    @POST("api/goal")
    suspend fun createGoal(@Body goal: Goal): Goal





}