package com.moviles.ticowallet.network

import com.moviles.ticowallet.DAO.CreateGoalContributionDto
import com.moviles.ticowallet.DAO.CreateGoalRequestDto
import com.moviles.ticowallet.DAO.GoalContributionDto
import com.moviles.ticowallet.DAO.UpdateGoalRequestDto
import com.moviles.ticowallet.models.Goal
import retrofit2.Response
import retrofit2.http.*

interface ApiServiceGoals {

    @GET("api/goal/get_all")
    suspend fun getGoals(): Response<List<Goal>>

    @GET("api/goal/{id}")
    suspend fun getGoalById(@Path("id") id: String): Response<GoalDetailResponseDto>

    @POST("api/goal")
    suspend fun createGoal(@Body goal: CreateGoalRequestDto): Response<CreateGoalResponseDto>

    @PUT("api/goal/{id}")
    suspend fun updateGoal(@Path("id") id: String, @Body goal: UpdateGoalRequestDto): Response<UpdateGoalResponseDto>

    @DELETE("api/goal/{id}")
    suspend fun deleteGoal(@Path("id") id: String): Response<DeleteGoalResponseDto>

    @GET("api/goal/{id}/contributions")
    suspend fun getGoalContributions(@Path("id") id: String): Response<List<GoalContributionDto>>

    @POST("api/goal/{id}/contributions")
    suspend fun addContribution(@Path("id") id: String, @Body contribution: CreateGoalContributionDto): Response<AddContributionResponseDto>
}

data class GoalDetailResponseDto(
    val goal: Goal,
    val contributions: List<GoalContributionDto>
)

data class CreateGoalResponseDto(
    val message: String,
    val data: Goal
)

data class UpdateGoalResponseDto(
    val message: String,
    val data: Goal
)

data class DeleteGoalResponseDto(
    val message: String
)

data class AddContributionResponseDto(
    val message: String,
    val data: GoalContributionDto,
    val updatedGoal: Goal
)