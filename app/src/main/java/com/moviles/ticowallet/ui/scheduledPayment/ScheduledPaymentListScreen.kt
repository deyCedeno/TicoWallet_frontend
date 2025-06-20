package com.moviles.ticowallet.ui.scheduledPayment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.moviles.ticowallet.models.ScheduledPayment
import com.moviles.ticowallet.ui.theme.*
import com.moviles.ticowallet.viewmodel.scheduledPayment.ScheduledPaymentViewModel

/**
 * Screen to display list of scheduled payments for authenticated user
 * Removed duplicate navigation elements to work with MainAppScaffold
 */
@Composable
fun ScheduledPaymentListScreen(
    navController: NavController,
    onMenuClick: () -> Unit, // This parameter is kept for compatibility but not used
    viewModel: ScheduledPaymentViewModel = viewModel()
) {
    // Collect state from ViewModel
    val scheduledPayments by viewModel.allScheduledPayments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Load scheduled payments when screen opens
    LaunchedEffect(Unit) {
        viewModel.loadScheduledPayments()
    }

    // Main content without TopAppBar and FloatingActionButton (handled by MainAppScaffold)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorDarkBlue1)
            .padding(16.dp)
    ) {
        // Show error message if exists
        error?.let { errorMessage ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Red.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Error: $errorMessage",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp
                )
            }
        }

        // Content based on loading state
        when {
            isLoading -> {
                // Loading indicator centered
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = colorTeal,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            scheduledPayments.isEmpty() -> {
                // Empty state with reload option
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "No tienes pagos programados",
                            color = colorWhite.copy(alpha = 0.7f),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Toca el botón + para agregar tu primer pago programado",
                            color = colorWhite.copy(alpha = 0.5f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )

                        Button(
                            onClick = { viewModel.loadScheduledPayments() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorTeal
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Recargar",
                                color = colorWhite
                            )
                        }
                    }
                }
            }

            else -> {
                // List of scheduled payments
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(scheduledPayments) { payment ->
                        ScheduledPaymentCard(
                            scheduledPayment = payment,
                            onClick = {
                                navController.navigate("scheduled_payment_detail/${payment.id}")
                            },
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual card component for displaying scheduled payment information
 */
@Composable
fun ScheduledPaymentCard(
    scheduledPayment: ScheduledPayment,
    onClick: () -> Unit,
    viewModel: ScheduledPaymentViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorDarkBlue2
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header row with payment name and amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Payment name
                    Text(
                        text = scheduledPayment.paymentName,
                        color = colorWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Account name
                    Text(
                        text = scheduledPayment.accountId.name,
                        color = colorTeal,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    // Category name
                    Text(
                        text = scheduledPayment.categoryId.name,
                        color = colorWhite.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    // Amount
                    Text(
                        text = viewModel.formatCurrency(scheduledPayment.amount),
                        color = colorTeal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Start date
                    Text(
                        text = viewModel.formatDateString(scheduledPayment.startDate),
                        color = colorWhite.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer row with frequency and payment method
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Frequency
                Text(
                    text = viewModel.getFrequencyText(scheduledPayment.frequency),
                    color = colorWhite.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                // Payment method
                Text(
                    text = scheduledPayment.paymentMethod,
                    color = colorWhite.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            }
        }
    }
}