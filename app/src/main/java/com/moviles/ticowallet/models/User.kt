package com.moviles.ticowallet.models

data class User (
    val Id: Int?,
    val Email: String,
    val Password: String,
    val Name: String,
    val UrlImage: String?
)