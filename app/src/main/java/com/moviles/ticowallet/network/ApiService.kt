package com.moviles.ticowallet.network

import com.moviles.ticowallet.DAO.ResetPasswordRequestDto
import com.moviles.ticowallet.DAO.UpdateImageResponse
import com.moviles.ticowallet.DAO.UpdateUserProfileDto
import com.moviles.ticowallet.DAO.UpdateUserProfileResponse
import com.moviles.ticowallet.models.Account
import com.moviles.ticowallet.models.Goal
import com.moviles.ticowallet.models.HomePageResponse
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

}