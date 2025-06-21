package com.moviles.ticowallet.DAO

import java.io.Serializable

data class ResetPasswordRequestDto(
    val email: String,
    val code: String,
    val newPassword: String,
    val confirmPassword: String
) : Serializable