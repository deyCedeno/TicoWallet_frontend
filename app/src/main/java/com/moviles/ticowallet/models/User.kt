package com.moviles.ticowallet.models

import java.io.Serializable

data class User (
    val id: Int? = null,
    val email: String,
    val password: String?,
    val confirmPassword: String?,
    val name: String,
    val urlImage: String?
) : Serializable