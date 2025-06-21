package com.moviles.ticowallet.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class Warranty(
    @SerializedName("idWarranty")
    val idWarranty: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("price")
    val price: Double,

    @SerializedName("purchaseDate")
    @JsonAdapter(DateAdapter::class)
    val purchaseDate: Date,

    @SerializedName("expirationDate")
    @JsonAdapter(DateAdapter::class)
    val expirationDate: Date,

    @SerializedName("icon")
    val icon: String?,

    @SerializedName("userId")
    val userId: Int? = null,

    @SerializedName("isExpired")
    val isExpired: Boolean,

    @SerializedName("daysRemaining")
    val daysRemaining: Int,

    @SerializedName("createdAt")
    @JsonAdapter(DateAdapter::class)
    val createdAt: Date? = null
) {
    val iconVector: ImageVector
        get() = when {
            icon?.contains("computer", ignoreCase = true) == true ||
                    name.contains("MSI", ignoreCase = true) ||
                    name.contains("laptop", ignoreCase = true) -> Icons.Default.Computer

            icon?.contains("keyboard", ignoreCase = true) == true ||
                    name.contains("keyboard", ignoreCase = true) ||
                    name.contains("redragon", ignoreCase = true) -> Icons.Default.Keyboard

            icon?.contains("tv", ignoreCase = true) == true ||
                    name.contains("TV", ignoreCase = true) ||
                    name.contains("TCL", ignoreCase = true) -> Icons.Default.Tv

            icon?.contains("fan", ignoreCase = true) == true ||
                    name.contains("abanico", ignoreCase = true) -> Icons.Default.Air

            icon?.contains("phone", ignoreCase = true) == true ||
                    name.contains("smartphone", ignoreCase = true) ||
                    name.contains("xiaomi", ignoreCase = true) -> Icons.Default.PhoneAndroid

            icon?.contains("monitor", ignoreCase = true) == true ||
                    name.contains("monitor", ignoreCase = true) ||
                    name.contains("viewsonic", ignoreCase = true) -> Icons.Default.Monitor

            icon?.contains("speaker", ignoreCase = true) == true ||
                    name.contains("alexa", ignoreCase = true) -> Icons.Default.Speaker

            icon?.contains("camera", ignoreCase = true) == true -> Icons.Default.CameraAlt

            icon?.contains("headphones", ignoreCase = true) == true -> Icons.Default.Headphones

            icon?.contains("tablet", ignoreCase = true) == true -> Icons.Default.Tablet

            else -> Icons.Default.Devices
        }
}

class DateAdapter : com.google.gson.JsonDeserializer<Date>, com.google.gson.JsonSerializer<Date> {
    private val dateFormatWithTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    private val dateFormatSimple = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dateFormatWithMillis = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())

    override fun deserialize(json: com.google.gson.JsonElement?, typeOfT: java.lang.reflect.Type?, context: com.google.gson.JsonDeserializationContext?): Date {
        return try {
            val dateString = json?.asString ?: return Date()

            when {
                dateString.contains("T") && dateString.contains(".") -> {
                    try {
                        dateFormatWithMillis.parse(dateString) ?: Date()
                    } catch (e: Exception) {
                        dateFormatWithTime.parse(dateString) ?: Date()
                    }
                }
                dateString.contains("T") -> {
                    dateFormatWithTime.parse(dateString) ?: Date()
                }
                else -> {
                    dateFormatSimple.parse(dateString) ?: Date()
                }
            }
        } catch (e: Exception) {
            Date()
        }
    }

    override fun serialize(src: Date?, typeOfSrc: java.lang.reflect.Type?, context: com.google.gson.JsonSerializationContext?): com.google.gson.JsonElement {
        return com.google.gson.JsonPrimitive(dateFormatSimple.format(src ?: Date()))
    }
}