package com.moviles.ticowallet.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.moviles.ticowallet.models.HomePageResponse
import com.moviles.ticowallet.ui.theme.TicoWalletTheme
import com.moviles.ticowallet.viewmodel.main.HomeViewModel


@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel) {
    val homeState = remember { mutableStateOf<HomePageResponse?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getAllHome(
            onSuccess = { home ->
                homeState.value = home
            },
            onError = { error ->
                println("Error al obtener los datos del inicio: $error")
            }
        )
    }

    TicoWalletTheme {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(Color(0xFF1A2639))
                .padding(16.dp)
        ) {
            // "Crear cuenta" button
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { navController.navigate("crear_cuenta") },
                    colors = ButtonDefaults.buttonColors(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Crear cuenta", color = Color.White)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = "Bank Account",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

            }

            Spacer(modifier = Modifier.height(24.dp))

//            First card -> Account list
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2B3A4F))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Lista de cuentas",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        homeState.value?.accounts?.forEach { account ->
                            AccountCard(
                                type = account.name,
                                amount = "${account.currency} ${String.format("%,.0f", account.balance)}",
                                backgroundColor = Color(0xFF339B9B)
                            )
                        }
                        if(homeState.value?.accounts.isNullOrEmpty()){
                            Text(text = "No hay cuentas registradas.", color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            navController.navigate("cuentas")
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF388E8E),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Mostrar más", color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
//            Secound card -> Last records
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2B3A4F))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Últimos registros",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        homeState.value?.movements?.forEach { movements ->
                            RecordsCard(
                                description = movements.description,
                                amount = movements.amount,
                                currency = movements.currency,
                                account = movements.accountName,
                                date = movements.date
                            )
                        }
                        if(homeState.value?.movements.isNullOrEmpty()){
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(text = "No hay movimientos.", color = Color.White)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { /* TODO: Lógica para mostrar más */ },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF388E8E),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Mostrar más", color = Color.White)
                    }
                }
            }
            // ... continue with other sections
        }
    }

}

@Composable
fun AccountCard(type: String, amount: String, backgroundColor: Color) {
    Card (
        modifier = Modifier
            .width(100.dp)
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = type, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
            Text(text = amount, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RecordsCard(description: String, amount: Double, currency: String, account: String, date: String) {
    val amountColor = if (amount < 0) Color(0xFFE53935) else Color(0xFF4CAF50)
    val formattedAmount = if (amount < 0) {
        "-" + currency + " ${String.format("%,.0f", -amount)}"
    } else {
        currency + " ${String.format("%,.0f", amount)}"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = description,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Column(
                horizontalAlignment = androidx.compose.ui.Alignment.End
            ) {
                Text(
                    text = formattedAmount,
                    color = amountColor,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = date,
                    color = Color.LightGray,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = account,
                color = Color.LightGray,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Divider(
            color = Color(0xFF5B6E80),
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 0.dp)
        )

    }
}