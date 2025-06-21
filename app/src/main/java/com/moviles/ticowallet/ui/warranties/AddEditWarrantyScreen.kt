package com.moviles.ticowallet.ui.warranties

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.ticowallet.models.Warranty
import java.text.SimpleDateFormat
import java.util.*

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String>
)

data class FieldValidation(
    val isValid: Boolean,
    val errorMessage: String?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditWarrantyScreen(
    warranty: Warranty? = null,
    onBackClick: () -> Unit = {},
    onSaveClick: (String, Double, Date, Date, String) -> Unit = { _, _, _, _, _ -> },
    isLoading: Boolean = false
) {
    val isEditMode = warranty != null
    val context = LocalContext.current

    var isInitialized by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf(warranty?.name ?: "") }
    var price by remember { mutableStateOf(warranty?.price?.toString() ?: "") }
    var purchaseDate by remember { mutableStateOf(warranty?.purchaseDate ?: Date()) }
    var expirationDate by remember { mutableStateOf(warranty?.expirationDate ?: Date()) }
    var selectedIcon by remember { mutableStateOf(warranty?.icon ?: "computer") }

    var nameValidation by remember { mutableStateOf(FieldValidation(true, null)) }
    var priceValidation by remember { mutableStateOf(FieldValidation(true, null)) }
    var dateValidation by remember { mutableStateOf(FieldValidation(true, null)) }

    LaunchedEffect(warranty) {
        if (warranty != null && !isInitialized) {
            name = warranty.name
            price = warranty.price.toString()
            purchaseDate = warranty.purchaseDate
            expirationDate = warranty.expirationDate
            selectedIcon = warranty.icon ?: "computer"
            isInitialized = true
        }
    }

    LaunchedEffect(name) {
        nameValidation = validateName(name)
    }

    LaunchedEffect(price) {
        priceValidation = validatePrice(price)
    }

    LaunchedEffect(purchaseDate, expirationDate) {
        dateValidation = validateDates(purchaseDate, expirationDate)
    }

    var showPurchaseDatePicker by remember { mutableStateOf(false) }
    var showExpirationDatePicker by remember { mutableStateOf(false) }
    var showIconSelector by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    val isFormValid = nameValidation.isValid &&
            priceValidation.isValid &&
            dateValidation.isValid &&
            name.isNotBlank() &&
            price.isNotBlank()

    if (isEditMode && warranty == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF27496D)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
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
                    text = if (isEditMode) "Editar Garantía" else "Nueva Garantía",
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
                TextButton(
                    onClick = {
                        if (isFormValid) {
                            onSaveClick(
                                name,
                                price.toDouble(),
                                purchaseDate,
                                expirationDate,
                                selectedIcon
                            )
                        }
                    },
                    enabled = isFormValid && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (isEditMode) "Actualizar" else "Guardar",
                            color = if (isFormValid) Color.White else Color.White.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

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
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Icono",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { showIconSelector = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getIconForName(selectedIcon),
                            contentDescription = "Icono seleccionado",
                            modifier = Modifier.size(48.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Toca para cambiar",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del producto") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = if (nameValidation.isValid) Color.White else Color.Red,
                    unfocusedBorderColor = if (nameValidation.isValid) Color.White.copy(alpha = 0.5f) else Color.Red.copy(alpha = 0.5f),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                ),
                singleLine = true,
                isError = !nameValidation.isValid,
                supportingText = if (!nameValidation.isValid) {
                    { Text(nameValidation.errorMessage ?: "", color = Color.Red) }
                } else null
            )

            OutlinedTextField(
                value = price,
                onValueChange = {
                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                        price = it
                    }
                },
                label = { Text("Precio (₡)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = if (priceValidation.isValid) Color.White else Color.Red,
                    unfocusedBorderColor = if (priceValidation.isValid) Color.White.copy(alpha = 0.5f) else Color.Red.copy(alpha = 0.5f),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                ),
                singleLine = true,
                prefix = { Text("₡ ", color = Color.White.copy(alpha = 0.7f)) },
                isError = !priceValidation.isValid,
                supportingText = if (!priceValidation.isValid) {
                    { Text(priceValidation.errorMessage ?: "", color = Color.Red) }
                } else null
            )

            OutlinedTextField(
                value = dateFormatter.format(purchaseDate),
                onValueChange = { },
                label = { Text("Fecha de compra") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPurchaseDatePicker = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.White,
                    disabledBorderColor = Color.White.copy(alpha = 0.5f),
                    disabledLabelColor = Color.White.copy(alpha = 0.7f)
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Seleccionar fecha",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            )

            OutlinedTextField(
                value = dateFormatter.format(expirationDate),
                onValueChange = { },
                label = { Text("Fecha de vencimiento") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showExpirationDatePicker = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.White,
                    disabledBorderColor = if (dateValidation.isValid) Color.White.copy(alpha = 0.5f) else Color.Red.copy(alpha = 0.5f),
                    disabledLabelColor = Color.White.copy(alpha = 0.7f)
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Seleccionar fecha",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                },
                isError = !dateValidation.isValid,
                supportingText = if (!dateValidation.isValid) {
                    { Text(dateValidation.errorMessage ?: "", color = Color.Red) }
                } else null
            )

            if (isFormValid) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50).copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Formulario válido - Listo para guardar",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }

    if (showPurchaseDatePicker) {
        CustomDatePickerDialog(
            title = "Fecha de Compra",
            initialDate = purchaseDate,
            maxDate = Date(),
            onDateSelected = { selectedDate ->
                purchaseDate = selectedDate
                showPurchaseDatePicker = false
            },
            onDismiss = { showPurchaseDatePicker = false }
        )
    }

    if (showExpirationDatePicker) {
        CustomDatePickerDialog(
            title = "Fecha de Vencimiento",
            initialDate = expirationDate,
            minDate = purchaseDate,
            onDateSelected = { selectedDate ->
                expirationDate = selectedDate
                showExpirationDatePicker = false
            },
            onDismiss = { showExpirationDatePicker = false }
        )
    }

    if (showIconSelector) {
        IconSelectorDialog(
            selectedIcon = selectedIcon,
            onIconSelected = {
                selectedIcon = it
                showIconSelector = false
            },
            onDismiss = { showIconSelector = false }
        )
    }
}

fun validateName(name: String): FieldValidation {
    return when {
        name.isBlank() -> FieldValidation(false, "El nombre es requerido")
        name.length < 2 -> FieldValidation(false, "El nombre debe tener al menos 2 caracteres")
        name.length > 100 -> FieldValidation(false, "El nombre no puede exceder 100 caracteres")
        !name.matches(Regex("^[a-zA-Z0-9\\s\\-_áéíóúÁÉÍÓÚñÑ]+$")) ->
            FieldValidation(false, "El nombre contiene caracteres no válidos")
        else -> FieldValidation(true, null)
    }
}

fun validatePrice(price: String): FieldValidation {
    if (price.isBlank()) return FieldValidation(false, "El precio es requerido")

    val priceValue = price.toDoubleOrNull()
    return when {
        priceValue == null -> FieldValidation(false, "El precio debe ser un número válido")
        priceValue <= 0 -> FieldValidation(false, "El precio debe ser mayor a 0")
        priceValue > 999999999 -> FieldValidation(false, "El precio es demasiado alto")
        else -> FieldValidation(true, null)
    }
}

fun validateDates(purchaseDate: Date, expirationDate: Date): FieldValidation {
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.time

    return when {
        purchaseDate.after(today) ->
            FieldValidation(false, "La fecha de compra no puede ser futura")
        expirationDate.before(purchaseDate) || expirationDate == purchaseDate ->
            FieldValidation(false, "La fecha de vencimiento debe ser posterior a la de compra")
        (expirationDate.time - purchaseDate.time) > (15 * 365 * 24 * 60 * 60 * 1000L) ->
            FieldValidation(false, "La garantía no puede exceder 15 años")
        (expirationDate.time - purchaseDate.time) < (24 * 60 * 60 * 1000L) ->
            FieldValidation(false, "La garantía debe ser de al menos 1 día")
        else -> FieldValidation(true, null)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    title: String,
    initialDate: Date,
    minDate: Date? = null,
    maxDate: Date? = null,
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    val initialDateMillis = initialDate.time
    val minDateMillis = minDate?.time
    val maxDateMillis = maxDate?.time

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val minValid = minDateMillis?.let { utcTimeMillis >= it } ?: true
                val maxValid = maxDateMillis?.let { utcTimeMillis <= it } ?: true
                return minValid && maxValid
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(Date(millis))
                    }
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
            DatePicker(
                state = datePickerState,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun IconSelectorDialog(
    selectedIcon: String,
    onIconSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val icons = listOf(
        "computer" to Icons.Default.Computer,
        "keyboard" to Icons.Default.Keyboard,
        "tv" to Icons.Default.Tv,
        "phone" to Icons.Default.Phone,
        "monitor" to Icons.Default.Monitor,
        "speaker" to Icons.Default.Speaker,
        "fan" to Icons.Default.Air,
        "camera" to Icons.Default.CameraAlt,
        "headphones" to Icons.Default.Headphones,
        "tablet" to Icons.Default.Tablet
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar icono") },
        text = {
            Column {
                icons.chunked(3).forEach { rowIcons ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowIcons.forEach { (iconName, icon) ->
                            IconButton(
                                onClick = { onIconSelected(iconName) },
                                modifier = Modifier
                                    .background(
                                        color = if (selectedIcon == iconName)
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                        else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = iconName,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

fun getIconForName(iconName: String): ImageVector {
    return when (iconName) {
        "computer" -> Icons.Default.Computer
        "keyboard" -> Icons.Default.Keyboard
        "tv" -> Icons.Default.Tv
        "phone" -> Icons.Default.Phone
        "monitor" -> Icons.Default.Monitor
        "speaker" -> Icons.Default.Speaker
        "fan" -> Icons.Default.Air
        "camera" -> Icons.Default.CameraAlt
        "headphones" -> Icons.Default.Headphones
        "tablet" -> Icons.Default.Tablet
        else -> Icons.Default.DeviceUnknown
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditWarrantyScreenPreview() {
    MaterialTheme {
        AddEditWarrantyScreen()
    }
}