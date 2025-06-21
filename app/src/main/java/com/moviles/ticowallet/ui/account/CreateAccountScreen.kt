package com.moviles.ticowallet.ui.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.moviles.ticowallet.ui.theme.colorDarkBlue1
import com.moviles.ticowallet.ui.theme.colorDarkBlue2
import com.moviles.ticowallet.ui.theme.colorTeal
import com.moviles.ticowallet.ui.theme.colorWhite
import com.moviles.ticowallet.viewmodel.account.AccountViewModel

@Composable
fun CreateAccountScreen(navController: NavController, viewModel: AccountViewModel) {
    // State variables for the form fields
    val accountName = remember { mutableStateOf("") }

    // Updated: Define account type options
    val accountTypeOptions = listOf("Tarjeta de crédito", "Efectivo", "Cuenta de ahorros", "Cuenta corriente", "Inversión")
    val selectedAccountType = remember { mutableStateOf(accountTypeOptions[0]) } // Set initial default

    val currentBalance = remember { mutableStateOf("0") }

    // Updated: Define currency options
    val currencyOptions = listOf("CRC", "USD")
    val selectedCurrency = remember { mutableStateOf(currencyOptions[0]) } // Set initial default

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
            colors = CardDefaults.cardColors(containerColor = Color(0xFF27496d))
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
                        focusedContainerColor = colorDarkBlue2.copy(alpha = 0.8f),
                        unfocusedContainerColor = colorDarkBlue2.copy(alpha = 0.8f),
                        focusedBorderColor = colorTeal,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = colorWhite,
                        unfocusedTextColor = colorWhite
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Updated: Use CreateSimpleDropdownField for account type
                CreateSimpleDropdownField(
                    label = "Tipo de cuenta",
                    selectedItem = selectedAccountType.value,
                    onItemSelected = { selectedAccountType.value = it },
                    items = accountTypeOptions,
                    placeholder = "Selecciona un tipo de cuenta"
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
                        focusedContainerColor = colorDarkBlue2.copy(alpha = 0.8f),
                        unfocusedContainerColor = colorDarkBlue2.copy(alpha = 0.8f),
                        focusedBorderColor = colorTeal,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = colorWhite,
                        unfocusedTextColor = colorWhite
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Updated: Use CreateSimpleDropdownField for currency
                CreateSimpleDropdownField(
                    label = "Moneda",
                    selectedItem = selectedCurrency.value,
                    onItemSelected = { selectedCurrency.value = it },
                    items = currencyOptions,
                    placeholder = "Selecciona una moneda"
                )


                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        // Handle account creation logic here
                        // You can now access:
                        // accountName.value
                        // selectedAccountType.value
                        // currentBalance.value
                        // selectedCurrency.value
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

@Composable
fun CreateSimpleDropdownField(
    label: String,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    items: List<String>,
    placeholder: String
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            color = colorWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))

        Box {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorDarkBlue2.copy(alpha = 0.8f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedItem.isNotEmpty()) selectedItem else placeholder,
                        color = if (selectedItem.isNotEmpty()) colorWhite else colorWhite.copy(alpha = 0.6f),
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        tint = colorWhite.copy(alpha = 0.7f)
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(colorDarkBlue2)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item,
                                color = colorWhite
                            )
                        },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}