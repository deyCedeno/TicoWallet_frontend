package com.moviles.ticowallet.DAO

import com.moviles.ticowallet.models.User

data class UpdateUserProfileResponse(
    val message: String,
    val user: User
)