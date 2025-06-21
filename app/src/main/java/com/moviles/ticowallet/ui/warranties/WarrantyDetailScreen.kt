package com.moviles.ticowallet.ui.warranties

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.ticowallet.models.Warranty
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarrantyDetailScreen(
    warranty: Warranty?,
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    isLoading: Boolean = false,
    onDeleteConfirm: () -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (warranty == null && !isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF27496D)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Garantía no encontrada",
                color = Color.White,
                fontSize = 18.sp
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF27496D))
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Detalles de Garantía",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
            },
            actions = {
                if (warranty != null) {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = Color.White
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        } else if (warranty != null) {
            WarrantyDetailContent(warranty = warranty)
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text("Eliminar Garantía")
            },
            text = {
                Text("¿Estás seguro de que deseas eliminar esta garantía? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteConfirm()
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun WarrantyDetailContent(warranty: Warranty) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("es", "CR")).apply {
            currency = Currency.getInstance("CRC")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = warranty.iconVector,
                        contentDescription = warranty.name,
                        modifier = Modifier.size(48.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = warranty.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currencyFormatter.format(warranty.price),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (warranty.isExpired) {
                    Color(0xFFFF6B6B).copy(alpha = 0.2f)
                } else if (warranty.daysRemaining <= 30) {
                    Color(0xFFFFB74D).copy(alpha = 0.2f)
                } else {
                    Color(0xFF4CAF50).copy(alpha = 0.2f)
                }
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (warranty.isExpired) Icons.Default.Warning
                    else if (warranty.daysRemaining <= 30) Icons.Default.Schedule
                    else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (warranty.isExpired) Color(0xFFFF6B6B)
                    else if (warranty.daysRemaining <= 30) Color(0xFFFFB74D)
                    else Color(0xFF4CAF50),
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (warranty.isExpired) "Garantía Vencida"
                        else if (warranty.daysRemaining <= 30) "Próximo a Vencer"
                        else "Garantía Activa",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )

                    Text(
                        text = if (warranty.isExpired) "Venció hace ${-warranty.daysRemaining} días"
                        else "Vence en ${warranty.daysRemaining} días",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Información",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                DetailRow(
                    icon = Icons.Default.ShoppingCart,
                    title = "Fecha de Compra",
                    value = dateFormatter.format(warranty.purchaseDate)
                )

                Spacer(modifier = Modifier.height(12.dp))

                DetailRow(
                    icon = Icons.Default.Event,
                    title = "Fecha de Vencimiento",
                    value = dateFormatter.format(warranty.expirationDate)
                )

                Spacer(modifier = Modifier.height(12.dp))

                DetailRow(
                    icon = Icons.Default.AttachMoney,
                    title = "Precio",
                    value = currencyFormatter.format(warranty.price)
                )

                Spacer(modifier = Modifier.height(12.dp))

                DetailRow(
                    icon = Icons.Default.DateRange,
                    title = "Registrada",
                    value = dateFormatter.format(warranty.createdAt)
                )
            }
        }
    }
}

@Composable
fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WarrantyDetailScreenPreview() {
    MaterialTheme {
        WarrantyDetailScreen(
            warranty = Warranty(
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
}