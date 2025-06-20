package com.moviles.ticowallet.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.moviles.ticowallet.ui.theme.colorDarkBlue1
import com.moviles.ticowallet.ui.theme.colorTeal
import com.moviles.ticowallet.ui.theme.colorWhite

@Composable
fun CreateAccountScreen(navController: NavController) {
    // State variables for the form fields
    val accountName = remember { mutableStateOf("") }
    val accountType = remember { mutableStateOf("Tarjeta de crédito") }
    val currentBalance = remember { mutableStateOf("0") }
    val currency = remember { mutableStateOf("CRC") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorDarkBlue1)
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF27496d)) // Adjust card color as needed
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Nombre de la cuenta",
                    color = colorWhite,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = accountName.value,
                    onValueChange = { accountName.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1A2639),
                        unfocusedContainerColor = Color(0xFF1A2639),
                        focusedBorderColor = colorTeal,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = colorWhite,
                        unfocusedTextColor = colorWhite
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tipo",
                    color = colorWhite,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = accountType.value,
                    onValueChange = { accountType.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    readOnly = true, // Make it read-only if it's a dropdown or pre-selected
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1A2639),
                        unfocusedContainerColor = Color(0xFF1A2639),
                        focusedBorderColor = colorTeal,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = colorWhite,
                        unfocusedTextColor = colorWhite
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Saldo actual",
                    color = colorWhite,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = currentBalance.value,
                    onValueChange = { currentBalance.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1A2639),
                        unfocusedContainerColor = Color(0xFF1A2639),
                        focusedBorderColor = colorTeal,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = colorWhite,
                        unfocusedTextColor = colorWhite
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Moneda",
                    color = colorWhite,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = currency.value,
                    onValueChange = { currency.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    readOnly = true, // Make it read-only if it's a dropdown or pre-selected
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1A2639),
                        unfocusedContainerColor = Color(0xFF1A2639),
                        focusedBorderColor = colorTeal,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = colorWhite,
                        unfocusedTextColor = colorWhite
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        // Handle account creation logic here
                        // You can access accountName.value, accountType.value, etc.
                        // After creation, you might want to navigate back:
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorTeal),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Crear cuenta", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}