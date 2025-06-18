// ui/exchangerate/ExchangeRateScreen.kt
package com.moviles.ticowallet.ui.exchangerate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.ticowallet.ui.theme.*
import com.moviles.ticowallet.viewmodel.exchangerate.ExchangeRateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeRateScreen(
    paddingValues: PaddingValues,
    viewModel: ExchangeRateViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadExchangeRates()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorDarkBlue1)
            .padding(paddingValues)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            // Header con título y botón refresh
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tipo de cambio",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = colorWhite
                )

                IconButton(
                    onClick = { viewModel.refreshExchangeRates() },
                    enabled = !uiState.isRefreshing
                ) {
                    if (uiState.isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = colorWhite,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar",
                            tint = colorWhite
                        )
                    }
                }
            }

            // Loading state
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = colorWhite,
                        strokeWidth = 3.dp
                    )
                }
            } else {
                // Sección Precio Venta
                ExchangeRateSection(
                    title = "Precio Venta",
                    usdRate = uiState.usdVenta,
                    eurRate = uiState.eurVenta
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Sección Precio Compra
                ExchangeRateSection(
                    title = "Precio Compra",
                    usdRate = uiState.usdCompra,
                    eurRate = uiState.eurCompra
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Última actualización
                if (uiState.lastUpdated.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = colorWhite.copy(alpha = 0.1f)
                        )
                    ) {
                        Text(
                            text = "Última actualización: ${uiState.lastUpdated}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorWhite.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        )
                    }
                }
            }

            // Error message
            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Cerrar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExchangeRateSection(
    title: String,
    usdRate: Double,
    eurRate: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = colorWhite.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Título de la sección
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = colorDarkBlue1,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Colones → Dólares
            CurrencyConverter(
                title = "Colones → Dólares",
                fromCurrency = "CRC",
                toCurrency = "USD",
                rate = if (usdRate > 0) 1.0 / usdRate else 0.0,
                symbol1 = "₡",
                symbol2 = "$"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Colones → Euros
            CurrencyConverter(
                title = "Colones → Euros",
                fromCurrency = "CRC",
                toCurrency = "EUR",
                rate = if (eurRate > 0) 1.0 / eurRate else 0.0,
                symbol1 = "₡",
                symbol2 = "€"
            )
        }
    }
}

@Composable
fun CurrencyConverter(
    title: String,
    fromCurrency: String,
    toCurrency: String,
    rate: Double,
    symbol1: String,
    symbol2: String
) {
    var inputAmount by remember { mutableStateOf("") }
    val convertedAmount = remember(inputAmount, rate) {
        val amount = inputAmount.toDoubleOrNull() ?: 0.0
        if (amount > 0 && rate > 0) {
            String.format("%.6f", amount * rate)
        } else ""
    }

    Column {
        // Título del convertidor
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            ),
            color = colorDarkBlue1,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Input
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fromCurrency,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = colorDarkBlue2,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                OutlinedTextField(
                    value = inputAmount,
                    onValueChange = { newValue ->
                        // Solo permitir números y un punto decimal
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                            inputAmount = newValue
                        }
                    },
                    placeholder = {
                        Text(
                            text = "1000",
                            color = colorDarkBlue2.copy(alpha = 0.5f)
                        )
                    },
                    leadingIcon = {
                        Text(
                            text = symbol1,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = colorDarkBlue1
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorTeal,
                        unfocusedBorderColor = colorDarkBlue2.copy(alpha = 0.3f),
                        focusedTextColor = colorDarkBlue1,
                        unfocusedTextColor = colorDarkBlue1
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Icono de intercambio
            Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = "Convertir",
                tint = colorTeal,
                modifier = Modifier.size(24.dp)
            )

            // Output
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = toCurrency,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = colorDarkBlue2,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Gray.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = symbol2,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = colorDarkBlue1,
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        Text(
                            text = convertedAmount.ifEmpty {
                                if (toCurrency == "USD") "1.00000" else "0.001724"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (convertedAmount.isNotEmpty()) colorDarkBlue1 else colorDarkBlue2.copy(alpha = 0.5f),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Tasa de cambio
        Text(
            text = "Tasa: 1 $fromCurrency = ${String.format("%.6f", rate)} $toCurrency",
            style = MaterialTheme.typography.bodySmall,
            color = colorDarkBlue2.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}