// ScheduledPayment.kt - Modelo principal (GET responses)
package com.moviles.ticowallet.models

import com.google.gson.annotations.SerializedName

data class ScheduledPayment(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("paymentName")
    val paymentName: String = "",

    @SerializedName("userId")
    val userId: Int = 0,

    @SerializedName("accountId")
    val accountId: AccountInfo = AccountInfo(),

    @SerializedName("categoryId")
    val categoryId: CategoryInfo = CategoryInfo(),

    @SerializedName("amount")
    val amount: Double = 0.0,

    @SerializedName("paymentMethod")
    val paymentMethod: String = "",

    @SerializedName("frequency")
    val frequency: String = "",

    @SerializedName("startDate")
    val startDate: String = ""
)

data class AccountInfo(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("name")
    val name: String = ""
)

data class CategoryInfo(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("name")
    val name: String = ""
)
/*
// Account.kt - Para dropdowns
data class Account(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String
)
*/
// CreateScheduledPaymentDto.kt - for POST/PUT
data class CreateScheduledPaymentDto(
    @SerializedName("paymentName")
    val paymentName: String,

    @SerializedName("accountId")
    val accountId: Int,

    @SerializedName("categoryId")
    val categoryId: Int,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("paymentMethod")
    val paymentMethod: String,

    @SerializedName("frequency")
    val frequency: String,

    @SerializedName("startDate")
    val startDate: String
)