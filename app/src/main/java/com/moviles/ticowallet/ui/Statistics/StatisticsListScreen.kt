package com.moviles.ticowallet.ui.Statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.moviles.ticowallet.models.DashboardResponse
import com.moviles.ticowallet.viewmodel.Statistics.StatisticsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(navController: NavController, viewModel: StatisticsViewModel) {
    // State management for dashboard data
    val dashboardState = remember { mutableStateOf<DashboardResponse?>(null) }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val lastRefresh = remember { mutableStateOf("") }

    // Load dashboard data from API
    fun loadData() {
        viewModel.getDashboardStats(
            onSuccess = { dashboard ->
                dashboardState.value = dashboard
                errorMessage.value = null
                // Update refresh timestamp
                lastRefresh.value = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            },
            onError = { error ->
                errorMessage.value = error
                println("Statistics Error: $error")
            },
            onLoading = { loading ->
                isLoading.value = loading
            }
        )
    }

    // Trigger initial data load
    LaunchedEffect(Unit) {
        loadData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A2639))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header with refresh button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {

                if (lastRefresh.value.isNotEmpty()) {
                    Text(
                        text = "Última actualización: ${lastRefresh.value}",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                }
            }

            IconButton(
                onClick = { loadData() },
                enabled = !isLoading.value
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Actualizar",
                    tint = if (isLoading.value) Color.Gray else Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading.value -> {
                // Loading state with spinner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF388E8E)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Cargando datos reales...",
                            color = Color.LightGray,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            errorMessage.value != null -> {
                // Error state with retry option
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE53935))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error al cargar datos",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = errorMessage.value ?: "Error desconocido",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { loadData() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFFE53935)
                            )
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            dashboardState.value != null -> {
                // Main content when data is loaded successfully
                val dashboard = dashboardState.value!!

                // Check if user has any meaningful data
                val hasData = dashboard.accountsOverview.totalAccounts > 0 ||
                        dashboard.monthlyFlow.income > 0 ||
                        dashboard.monthlyFlow.expenses > 0

                if (hasData) {
                    // Real cash flow data
                    CashFlowCard(dashboard.monthlyFlow)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Real account overview
                    AccountOverviewCard(dashboard.accountsOverview)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Real expenses data or empty state
                    if (dashboard.topExpensesByCategory.isNotEmpty()) {
                        TopExpensesCard(dashboard.topExpensesByCategory)
                    } else {
                        NoExpensesCard()
                    }
                } else {
                    // Empty state for new users
                    EmptyStateCard()
                }
            }
        }
    }
}

@Composable
fun NoExpensesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2B3A4F))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No se encontraron gastos para este período",
                color = Color.LightGray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun BalanceCard(balances: List<com.moviles.ticowallet.models.CurrencyBalance>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2B3A4F))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Saldo por monedas",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Calculate percentages for the progress bar
            val totalBalance = balances.sumOf { Math.abs(it.totalBalance) }

            if (totalBalance > 0) {
                // Progress bar showing currency distribution
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .background(Color(0xFF1A2639), RoundedCornerShape(10.dp)),
                    horizontalArrangement = Arrangement.Start
                ) {
                    balances.forEach { balance ->
                        val percentage = Math.abs(balance.totalBalance) / totalBalance
                        val color = when (balance.currency) {
                            "CRC", "Mensual" -> Color(0xFF8E44AD)
                            "USD" -> Color(0xFFE74C3C)
                            "EUR" -> Color(0xFF3498DB)
                            else -> Color(0xFF95A5A6)
                        }

                        if (percentage > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(percentage.toFloat())
                                    .background(color, RoundedCornerShape(10.dp))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Real balance list
            balances.forEach { balance ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = balance.currency,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${balance.currency} ${String.format("%,.0f", balance.totalBalance)}",
                        color = if (balance.totalBalance >= 0) Color(0xFF4CAF50) else Color(0xFFE53935),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CashFlowCard(monthlyFlow: com.moviles.ticowallet.models.MonthlyFlow) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2B3A4F))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Card title and month indicator
            Text(
                text = "Flujo de fondos",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = monthlyFlow.month,
                color = Color.LightGray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display income, expenses and net flow
            FlowItem(
                label = "Ingresos",
                amount = monthlyFlow.income,
                color = Color(0xFF4CAF50),
                currency = "CRC"
            )

            FlowItem(
                label = "Gastos",
                amount = monthlyFlow.expenses,
                color = Color(0xFFE53935),
                currency = "CRC"
            )

            Divider(
                color = Color(0xFF5B6E80),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Net flow calculation result
            FlowItem(
                label = "Flujo neto",
                amount = monthlyFlow.netFlow,
                color = if (monthlyFlow.netFlow >= 0) Color(0xFF4CAF50) else Color(0xFFE53935),
                currency = "CRC",
                isBold = true
            )
        }
    }
}

@Composable
fun FlowItem(label: String, amount: Double, color: Color, currency: String, isBold: Boolean = false) {
    // Individual row for each financial metric
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = "$currency ${String.format("%,.0f", amount)}",
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AccountOverviewCard(overview: com.moviles.ticowallet.models.AccountsOverview) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2B3A4F))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Resumen de cuentas",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Show account statistics in grid format
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OverviewItem("Total cuentas", overview.totalAccounts.toString())
                OverviewItem("Activas este mes", overview.activeAccountsThisMonth.toString())
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OverviewItem("Transacciones", overview.totalTransactionsThisMonth.toString())
                OverviewItem("Promedio", String.format("%.0f", overview.averageTransactionAmount))
            }
        }
    }
}

@Composable
fun OverviewItem(label: String, value: String) {
    // Statistical display component for numeric values
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(140.dp) // Increased width for better alignment
    ) {
        Text(
            text = value,
            color = Color(0xFF388E8E),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color.LightGray,
            fontSize = 12.sp
        )
    }
}

@Composable
fun TopExpensesCard(expenses: List<com.moviles.ticowallet.models.CategoryExpense>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2B3A4F))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Top ${expenses.size} categorías de gastos",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // List each expense category with ranking
            expenses.forEachIndexed { index, expense ->
                ExpenseItem(
                    rank = index + 1,
                    category = expense.categoryName,
                    amount = expense.totalAmount,
                    currency = expense.currency,
                    transactionCount = expense.transactionCount,
                    lastDate = expense.lastTransactionDate
                )
                if (index < expenses.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun ExpenseItem(
    rank: Int,
    category: String,
    amount: Double,
    currency: String,
    transactionCount: Int,
    lastDate: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circle with rank number
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color(0xFF388E8E), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rank.toString(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = category,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$transactionCount transacciones • Última: $lastDate",
                    color = Color.LightGray,
                    fontSize = 11.sp
                )
            }
        }

        Text(
            text = "$currency ${String.format("%,.0f", amount)}",
            color = Color(0xFFE53935),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EmptyStateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2B3A4F))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Welcome icon or illustration placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFF388E8E), RoundedCornerShape(40.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📊",
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "¡Bienvenido a Estadísticas!",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Para ver tus estadísticas financieras necesitas:",
                color = Color.LightGray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Requirements list
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                RequirementItem("✅ Crear al menos una cuenta")
                RequirementItem("✅ Registrar algunos movimientos")
                RequirementItem("✅ Categorizar tus gastos")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Una vez que agregues datos, aquí verás:",
                color = Color.LightGray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                FeatureItem("📈 Flujo de ingresos y gastos")
                FeatureItem("💳 Resumen de tus cuentas")
                FeatureItem("🏆 Top categorías de gastos")
            }
        }
    }
}

@Composable
fun RequirementItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = Color(0xFF4CAF50),
            fontSize = 14.sp
        )
    }
}

@Composable
fun FeatureItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = Color(0xFF388E8E),
            fontSize = 12.sp
        )
    }
}