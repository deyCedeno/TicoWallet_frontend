package com.moviles.ticowallet.ui.scheduledPayment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import android.widget.Toast
import com.moviles.ticowallet.models.CreateScheduledPaymentDto
import com.moviles.ticowallet.models.Account
import com.moviles.ticowallet.models.Category
import com.moviles.ticowallet.ui.theme.*
import com.moviles.ticowallet.viewmodel.scheduledPayment.ScheduledPaymentViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar

/**
 * Screen for creating new scheduled payments with comprehensive form validation
 * Includes real-time input validation and user-friendly Toast messages
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScheduledPaymentScreen(
    navController: NavController,
    viewModel: ScheduledPaymentViewModel = viewModel()
) {
    // Get context for Toast messages
    val context = LocalContext.current

    // Form state variables
    var paymentName by remember { mutableStateOf("") }
    var selectedAccount by remember { mutableStateOf<Account?>(null) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var amount by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Date()) }
    var showDatePicker by remember { mutableStateOf(false) }

    // ViewModel state observers
    val userAccounts by viewModel.userAccounts.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingDropdowns by viewModel.isLoadingDropdowns.collectAsState()
    val createSuccess by viewModel.createSuccess.collectAsState()
    val error by viewModel.error.collectAsState()

    // Predefined dropdown options
    val paymentMethods = listOf("Efectivo", "Tarjeta", "Transferencia")
    val frequencies = listOf("Diario", "Semanal", "Mensual", "Anual")

    // Load dropdown data when screen opens
    LaunchedEffect(Unit) {
        viewModel.loadDropdownData()
    }

    // Show success Toast and navigate back when payment is created
    LaunchedEffect(createSuccess) {
        if (createSuccess) {
            Toast.makeText(context, "¡Pago programado creado exitosamente!", Toast.LENGTH_SHORT).show()
            viewModel.resetCreateSuccess()
            navController.popBackStack()
        }
    }

    // Date Picker Dialog with date validation
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.time
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val newDate = Date(millis)
                            val today = Calendar.getInstance()
                            val selectedCalendar = Calendar.getInstance().apply { time = newDate }

                            // Validate that selected date is not in the past
                            if (selectedCalendar.before(today)) {
                                Toast.makeText(
                                    context,
                                    "La fecha no puede ser anterior a hoy",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                selectedDate = newDate
                            }
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Confirmar", color = colorTeal)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar", color = colorTeal)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = colorDarkBlue2
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = colorDarkBlue2,
                    titleContentColor = colorWhite,
                    headlineContentColor = colorWhite,
                    weekdayContentColor = colorWhite,
                    subheadContentColor = colorWhite,
                    yearContentColor = colorWhite,
                    currentYearContentColor = colorTeal,
                    selectedYearContentColor = colorWhite,
                    selectedYearContainerColor = colorTeal,
                    dayContentColor = colorWhite,
                    selectedDayContentColor = colorWhite,
                    selectedDayContainerColor = colorTeal,
                    todayContentColor = colorTeal,
                    todayDateBorderColor = colorTeal
                )
            )
        }
    }

    Scaffold(
        containerColor = colorDarkBlue1
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(colorDarkBlue1)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error message display
            error?.let { errorMessage ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
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

            // Loading indicator while fetching dropdown data
            if (isLoadingDropdowns) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            color = colorTeal,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Cargando datos...",
                            color = colorWhite.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                // Payment name field with validation (letters, numbers, spaces only)
                CreateFormFieldWithValidation(
                    label = "Nombre del pago",
                    value = paymentName,
                    onValueChange = { newValue ->
                        // Allow only letters, numbers, spaces and common special characters
                        if (newValue.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ0-9\\s\\-\\.]*$"))) {
                            paymentName = newValue
                        } else {
                            Toast.makeText(
                                context,
                                "Solo se permiten letras, números y espacios",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    placeholder = "Ej: Alquiler de casa"
                )

                // Account dropdown selection
                CreateFormDropdownField(
                    label = "Cuenta",
                    selectedItem = selectedAccount?.name ?: "",
                    onItemSelected = { account -> selectedAccount = account },
                    items = userAccounts,
                    itemToString = { it.name },
                    placeholder = "Selecciona una cuenta",
                    isLoading = userAccounts.isEmpty()
                )

                // Category dropdown selection
                CreateFormDropdownField(
                    label = "Categoría",
                    selectedItem = selectedCategory?.name ?: "",
                    onItemSelected = { category -> selectedCategory = category },
                    items = categories,
                    itemToString = { it.name },
                    placeholder = "Selecciona una categoría",
                    isLoading = categories.isEmpty()
                )

                // Amount field with numeric validation
                CreateFormFieldWithValidation(
                    label = "Monto",
                    value = amount,
                    onValueChange = { newValue ->
                        // Allow only numbers and decimal point
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                            amount = newValue
                        } else {
                            Toast.makeText(
                                context,
                                "Solo se permiten números",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    placeholder = "100000",
                    keyboardType = KeyboardType.Number
                )

                // Payment method dropdown
                CreateSimpleDropdownField(
                    label = "Forma de pago",
                    selectedItem = paymentMethod,
                    onItemSelected = { paymentMethod = it },
                    items = paymentMethods,
                    placeholder = "Selecciona forma de pago"
                )

                // Frequency dropdown
                CreateSimpleDropdownField(
                    label = "Frecuencia",
                    selectedItem = frequency,
                    onItemSelected = { frequency = it },
                    items = frequencies,
                    placeholder = "Selecciona frecuencia"
                )

                // Date picker field
                CreateFormDateField(
                    label = "Fecha inicio",
                    selectedDate = selectedDate,
                    onDateSelected = { showDatePicker = true }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Save button with comprehensive validation
                Button(
                    onClick = {
                        // Validate all required fields
                        val validationMessage = when {
                            paymentName.isBlank() -> "El nombre del pago es obligatorio"
                            selectedAccount == null -> "Debe seleccionar una cuenta"
                            selectedCategory == null -> "Debe seleccionar una categoría"
                            amount.isBlank() -> "El monto es obligatorio"
                            amount.toDoubleOrNull() == null || amount.toDoubleOrNull()!! <= 0 -> "El monto debe ser mayor a 0"
                            paymentMethod.isBlank() -> "Debe seleccionar una forma de pago"
                            frequency.isBlank() -> "Debe seleccionar una frecuencia"
                            else -> null
                        }

                        if (validationMessage != null) {
                            Toast.makeText(context, validationMessage, Toast.LENGTH_SHORT).show()
                        } else {
                            // Create and submit the scheduled payment
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                            val scheduledPaymentDto = CreateScheduledPaymentDto(
                                paymentName = paymentName.trim(),
                                accountId = selectedAccount!!.id ?: 0,
                                categoryId = selectedCategory!!.id,
                                amount = amount.toDouble(),
                                paymentMethod = paymentMethod,
                                frequency = frequency,
                                startDate = dateFormat.format(selectedDate)
                            )
                            viewModel.createScheduledPayment(scheduledPaymentDto)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(
                            color = Color(0xFF0c7b93),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    enabled = !isLoading && !isLoadingDropdowns
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = colorWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "Guardar",
                            color = colorWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/**
 * Enhanced form field component with real-time input validation
 * Provides visual feedback and prevents invalid input
 */
@Composable
fun CreateFormFieldWithValidation(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(
            text = label,
            color = colorWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorDarkBlue2.copy(alpha = 0.8f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = placeholder,
                        color = colorWhite.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorTeal,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = colorWhite,
                    unfocusedTextColor = colorWhite,
                    cursorColor = colorTeal,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
        }
    }
}

/**
 * Dropdown field component for complex objects (Account, Category)
 * Provides loading states and user-friendly selection interface
 */
@Composable
fun <T> CreateFormDropdownField(
    label: String,
    selectedItem: String,
    onItemSelected: (T) -> Unit,
    items: List<T>,
    itemToString: (T) -> String,
    placeholder: String,
    isLoading: Boolean = false
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
                    .clickable { if (!isLoading && items.isNotEmpty()) expanded = true },
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
                        text = if (selectedItem.isNotEmpty()) selectedItem else {
                            if (isLoading) "Cargando..." else placeholder
                        },
                        color = if (selectedItem.isNotEmpty()) colorWhite else colorWhite.copy(alpha = 0.6f),
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )

                    if (isLoading) {
                        CircularProgressIndicator(
                            color = colorTeal,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown",
                            tint = colorWhite.copy(alpha = 0.7f)
                        )
                    }
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
                                text = itemToString(item),
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

/**
 * Simple dropdown field component for string values
 * Used for payment methods and frequency selection
 */
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

/**
 * Date picker field component with consistent styling
 * Triggers date picker dialog when clicked
 */
@Composable
fun CreateFormDateField(
    label: String,
    selectedDate: Date,
    onDateSelected: () -> Unit
) {
    Column {
        Text(
            text = label,
            color = colorWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onDateSelected() },
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
                    text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate),
                    color = colorWhite,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Seleccionar fecha",
                    tint = colorWhite.copy(alpha = 0.7f)
                )
            }
        }
    }
}