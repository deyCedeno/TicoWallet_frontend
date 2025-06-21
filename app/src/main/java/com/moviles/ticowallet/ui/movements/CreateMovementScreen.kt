package com.moviles.ticowallet.ui.movements

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.moviles.ticowallet.models.Account
import com.moviles.ticowallet.models.Category
import com.moviles.ticowallet.models.CreateMovementDto
import com.moviles.ticowallet.models.MovementGet
import com.moviles.ticowallet.ui.scheduledPayment.CreateFormDropdownField
import com.moviles.ticowallet.ui.theme.colorDarkBlue1
import com.moviles.ticowallet.ui.theme.colorDarkBlue2
import com.moviles.ticowallet.ui.theme.colorTeal
import com.moviles.ticowallet.ui.theme.colorWhite
import com.moviles.ticowallet.viewmodel.account.AccountViewModel
import com.moviles.ticowallet.viewmodel.movements.MovementViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun CreateMovementScreen(navController: NavController, viewModel: MovementViewModel) {
    val context = LocalContext.current
    // State variables for the form fields
    val accountName = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val accountId = remember { mutableStateOf("") }
    // Updated: Define account type options
    val accountTypeOptions = listOf("Ingreso", "Gasto", "Transferencia")
    val selectedAccountType = remember { mutableStateOf(accountTypeOptions[0]) } // Set initial default

    val methodPayment = listOf("Efectivo", "Tarjeta", "Simpe")
    val selectedMethod = remember { mutableStateOf(methodPayment[0]) }

    val warrantyOptions = listOf("0 meses", "1 mes", "2 meses", "3 meses")
    val selectedWarranty = remember { mutableStateOf(warrantyOptions[0]) }

    val stateOptions = listOf("Procesado", "Pendiente")
    val selectedState = remember { mutableStateOf(stateOptions[0]) }

    val currentBalance = remember { mutableStateOf("") }
    val location = remember { mutableStateOf("") }
    val categories by viewModel.categories.collectAsState()
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedAccount by remember { mutableStateOf<Account?>(null) }
    var selectedAccountRe by remember { mutableStateOf<Account?>(null) }
    val userAccounts by viewModel.userAccounts.collectAsState()

    // Updated: Define currency options
    val currencyOptions = listOf("CRC", "USD")
    val selectedCurrency = remember { mutableStateOf(currencyOptions[0]) } // Set initial default
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorDarkBlue1)
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth().verticalScroll(scrollState),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF27496d))

        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Text(
                    text = "Descripción",
                    color = colorWhite,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description.value,
                    onValueChange = { description.value = it },
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
                    label = "Tipo de movimiento",
                    selectedItem = selectedAccountType.value,
                    onItemSelected = { selectedAccountType.value = it },
                    items = accountTypeOptions,
                    placeholder = "Selecciona un tipo de movimiento"
                )

                Spacer(modifier = Modifier.height(16.dp))
                CreateSimpleDropdownField(
                    label = "Forma de pago",
                    selectedItem = selectedMethod.value,
                    onItemSelected = { selectedMethod.value = it },
                    items = methodPayment,
                    placeholder = "Selecciona un tipo de movimiento"
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Monto",
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

                CreateSimpleDropdownField(
                    label = "Garantia",
                    selectedItem = selectedWarranty.value,
                    onItemSelected = { selectedWarranty.value = it },
                    items = warrantyOptions,
                    placeholder = "Selecciona una garantía"
                )

                Spacer(modifier = Modifier.height(16.dp))

                CreateSimpleDropdownField(
                    label = "Estado",
                    selectedItem = selectedState.value,
                    onItemSelected = { selectedState.value = it },
                    items = stateOptions,
                    placeholder = "Selecciona un estado"
                )

                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Lugar",
                    color = colorWhite,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = location.value,
                    onValueChange = { location.value = it },
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
                Spacer(modifier = Modifier.height(8.dp))
                CreateFormDropdownField(
                    label = "Categoría",
                    selectedItem = selectedCategory?.name ?: "",
                    onItemSelected = { category -> selectedCategory = category },
                    items = categories,
                    itemToString = { it.name },
                    placeholder = "Selecciona una categoría",
                    isLoading = categories.isEmpty()
                )
                Spacer(modifier = Modifier.height(8.dp))
                CreateFormDropdownField(
                    label = "Cuenta emisora",
                    selectedItem = selectedAccount?.name ?: "",
                    onItemSelected = { account -> selectedAccount = account },
                    items = userAccounts,
                    itemToString = { it.name },
                    placeholder = "Selecciona una cuenta",
                    isLoading = userAccounts.isEmpty()
                )
                Spacer(modifier = Modifier.height(0.dp))
                CreateFormDropdownField(
                    label = "Cuenta receptora",
                    selectedItem = selectedAccountRe?.name ?: "",
                    onItemSelected = { account -> selectedAccountRe = account },
                    items = userAccounts,
                    itemToString = { it.name },
                    placeholder = "Selecciona una cuenta",
                    isLoading = userAccounts.isEmpty()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        try {
                            val amountDouble = currentBalance.value.toDoubleOrNull()
                            if (amountDouble == null) {
                                Toast.makeText(context, "El monto ingresado no es válido.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val warrantyInt = warrantyOptions.indexOf(selectedWarranty.value)
                            val movementToSend = CreateMovementDto(
                                type = selectedAccountType.value,
                                description = description.value,
                                methodPayment = selectedMethod.value,
                                amount = amountDouble,
                                warranty = warrantyInt,
                                state = selectedState.value,
                                location = location.value,
                                categoryId = selectedCategory?.id ?: 0,
                                destinationAccountId = selectedAccount?.id ?: 0, // o el correcto
                                date = LocalDate.now().toString(), // yyyy-MM-dd
                                time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                                accountId = selectedAccount?.id ?: 0
                            )
                                var newAccount = viewModel.createMovement(movementToSend)
                            Toast.makeText(context, "Mae cuenta creada con éxito.", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } catch (e: NumberFormatException) {
                            println("El error: " + e.message)
                            Toast.makeText(context, "El saldo no es valido.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorTeal),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Crear movimiento", color = Color.White, fontSize = 18.sp)
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

