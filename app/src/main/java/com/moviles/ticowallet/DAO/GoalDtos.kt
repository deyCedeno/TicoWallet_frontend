package com.moviles.ticowallet.DAO

data class CreateGoalRequestDto(
    val name: String,
    val quantity: Double,
    val goalDate: String,
    val icon: String?,
    val note: String?
)

data class UpdateGoalRequestDto(
    val name: String?,
    val quantity: Double?,
    val goalDate: String?,
    val currentQuantity: Double?,
    val icon: String?,
    val state: String?,
    val note: String?
)

data class GoalContributionDto(
    val id: Int,
    val goalId: Int,
    val amount: Double,
    val contributionDate: String,
    val description: String?
)

data class CreateGoalContributionDto(
    val amount: Double,
    val contributionDate: String,
    val description: String?
)