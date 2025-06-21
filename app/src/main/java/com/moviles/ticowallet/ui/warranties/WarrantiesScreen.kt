package com.moviles.ticowallet.ui.warranties

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.ticowallet.models.Warranty
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarrantiesScreen(
    warranties: List<Warranty> = getSampleWarranties(),
    onBackClick: () -> Unit = {},
    onWarrantyClick: (Warranty) -> Unit = {},
    onAddWarrantyClick: () -> Unit = {},
    isLoading: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF27496D))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp)
                ) {
                    items(warranties) { warranty ->
                        WarrantyCard(
                            warranty = warranty,
                            onClick = { onWarrantyClick(warranty) }
                        )
                    }

                    if (warranties.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Assignment,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color.White.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No tienes garantías registradas",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Presiona + para agregar una",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAddWarrantyClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFF1976D2),
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Agregar garantía"
            )
        }
    }
}

@Composable
fun WarrantyCard(
    warranty: Warranty,
    onClick: () -> Unit = {}
) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("es", "CR")).apply {
            currency = Currency.getInstance("CRC")
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = warranty.iconVector,
                    contentDescription = warranty.name,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = warranty.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )

                    Text(
                        text = currencyFormatter.format(warranty.price),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Compra: ${dateFormatter.format(warranty.purchaseDate)}",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Text(
                        text = "Vence: ${dateFormatter.format(warranty.expirationDate)}",
                        fontSize = 13.sp,
                        color = if (warranty.isExpired) Color(0xFFFF6B6B) else Color.White.copy(alpha = 0.8f)
                    )
                }

                if (warranty.isExpired) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "VENCIDA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B6B)
                    )
                } else if (warranty.daysRemaining <= 30) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Vence en ${warranty.daysRemaining} días",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB74D)
                    )
                }
            }
        }
    }
}

val Warranty.iconVector: ImageVector
    get() = when (icon?.lowercase()) {
        "computer" -> Icons.Default.Computer
        "keyboard" -> Icons.Default.Keyboard
        "tv" -> Icons.Default.Tv
        "fan" -> Icons.Default.Air
        "phone" -> Icons.Default.Phone
        "monitor" -> Icons.Default.Monitor
        "speaker" -> Icons.Default.Speaker
        else -> Icons.Default.DeviceUnknown
    }

fun getSampleWarranties(): List<Warranty> {
    return listOf(
        Warranty(
            idWarranty = 1,
            name = "MSI Cyborg 15 A12U",
            price = 777000.0,
            purchaseDate = Date(125, 0, 1),
            expirationDate = Date(127, 0, 1),
            icon = "computer",
            userId = 1,
            isExpired = false,
            daysRemaining = 730,
            createdAt = Date()
        )
    )
}

@Preview(showBackground = true)
@Composable
fun WarrantiesScreenPreview() {
    MaterialTheme {
        WarrantiesScreen()
    }
}