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
        get() = when (icon?.lowercase()) {
            "computer" -> Icons.Default.Computer
            "keyboard" -> Icons.Default.Keyboard
            "tv" -> Icons.Default.Tv
            "fan" -> Icons.Default.Air
            "phone" -> Icons.Default.PhoneAndroid
            "monitor" -> Icons.Default.Monitor
            "speaker" -> Icons.Default.Speaker
            "camera" -> Icons.Default.CameraAlt
            "headphones" -> Icons.Default.Headphones
            "tablet" -> Icons.Default.Tablet
            else -> Icons.Default.Devices
        }
}

class DateAdapter : com.google.gson.JsonDeserializer<Date>, com.google.gson.JsonSerializer<Date> {
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    private val isoFormatSimple = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    private val dateFormatSimple = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun deserialize(json: com.google.gson.JsonElement?, typeOfT: java.lang.reflect.Type?, context: com.google.gson.JsonDeserializationContext?): Date {
        return try {
            val dateString = json?.asString ?: return Date()

            when {
                dateString.contains("T") && dateString.contains("Z") -> {
                    isoFormat.parse(dateString) ?: Date()
                }
                dateString.contains("T") -> {
                    isoFormatSimple.parse(dateString) ?: Date()
                }
                else -> {
                    dateFormatSimple.parse(dateString) ?: Date()
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("DateAdapter", "Error parsing date: ${json?.asString}", e)
            Date()
        }
    }

    override fun serialize(src: Date?, typeOfSrc: java.lang.reflect.Type?, context: com.google.gson.JsonSerializationContext?): com.google.gson.JsonElement {
        return com.google.gson.JsonPrimitive(dateFormatSimple.format(src ?: Date()))
    }
}