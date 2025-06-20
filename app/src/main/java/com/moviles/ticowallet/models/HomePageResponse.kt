package com.moviles.ticowallet.models

data class HomePageResponse(
    val accounts: List<Account>,
    val movements: List<Movement>
)
