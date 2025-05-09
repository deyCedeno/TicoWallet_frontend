package com.moviles.ticowallet.network

import com.moviles.ticowallet.models.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
//    USERS

    @Multipart
    @POST("api/user/register")
    suspend fun addUser(@Part("Name") name: RequestBody,
                         @Part("Email") location: RequestBody,
                         @Part("Password") description: RequestBody,
                         @Part("ConfirmPassword") date: RequestBody): User

    @POST("/api/user/login")
    suspend fun signIn(@Body userDto: User) : User


//    Goals
}