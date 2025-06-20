package com.moviles.ticowallet.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.moviles.ticowallet.DAO.GoalContributionDto
import com.moviles.ticowallet.models.Goal
import com.moviles.ticowallet.ui.theme.*
import com.moviles.ticowallet.viewmodel.goals.GoalDetailViewModel
import com.moviles.ticowallet.viewmodel.goals.GoalsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    navController: NavController,
    goalId: String?,
    detailViewModel: GoalDetailViewModel = viewModel(),
    goalsViewModel: GoalsViewModel = viewModel()
) {
    val uiState by detailViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        detailViewModel.setCallbacks(
            onGoalUpdated = { updatedGoal ->
                goalsViewModel.updateGoalLocally(updatedGoal)
            },
            onGoalDeleted = { goalId ->
                goalsViewModel.refreshGoals()
                navController.popBackStack()
            }
        )
    }

    LaunchedEffect(goalId) {
        if (goalId != null) {
            detailViewModel.loadGoalDetail(goalId)
        }
    }

    Scaffold(
        containerColor = colorDarkBlue1,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.goal?.name ?: "Detalle del objetivo",
                        color = colorWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = colorWhite
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        detailViewModel.showEditDialog()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = colorWhite
                        )
                    }
                    IconButton(onClick = {
                        detailViewModel.showDeleteDialog()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = colorWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorDarkBlue1
                )
            )
        }
    ) { paddingValues ->

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colorTeal)
            }
        } else if (uiState.goal != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(colorDarkBlue1),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    GoalHeaderCard(
                        goal = uiState.goal!!,
                        goalsViewModel = goalsViewModel
                    )
                }

                item {
                    GoalProgressCard(goal = uiState.goal!!)
                }

                item {
                    Button(
                        onClick = { detailViewModel.showAddContributionDialog() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = colorTeal),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Agregar cantidad ahorrada",
                            color = colorWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                item {
                    ContributionsHeader()
                }

                items(uiState.contributions) { contribution ->
                    ContributionItem(contribution = contribution)
                }

                if (uiState.contributions.isEmpty()) {
                    item {
                        EmptyContributionsMessage()
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Error al cargar el objetivo",
                        color = colorWhite,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { goalId?.let { detailViewModel.loadGoalDetail(it) } },
                        colors = ButtonDefaults.buttonColors(containerColor = colorTeal)
                    ) {
                        Text("Reintentar", color = colorWhite)
                    }
                }
            }
        }
    }

    if (uiState.showAddContributionDialog) {
        AddContributionDialog(
            onDismiss = { detailViewModel.hideAddContributionDialog() },
            onConfirm = { amount, description ->
                goalId?.let {
                    detailViewModel.addContribution(it, amount, description)
                }
            },
            isLoading = uiState.isAddingContribution
        )
    }

    if (uiState.showEditDialog && uiState.goal != null) {
        EditGoalDialog(
            goal = uiState.goal!!,
            onDismiss = { detailViewModel.hideEditDialog() },
            onConfirm = { name, quantity, goalDate, note, icon, state ->
                goalsViewModel.updateGoal(
                    goalId = uiState.goal!!.id,
                    name = name,
                    quantity = quantity,
                    goalDate = goalDate,
                    note = note,
                    icon = icon,
                    state = state
                )
                detailViewModel.hideEditDialog()
                goalId?.let { detailViewModel.loadGoalDetail(it) }
            },
            goalsViewModel = goalsViewModel
        )
    }

    if (uiState.showDeleteDialog && uiState.goal != null) {
        DeleteConfirmationDialog(
            goalName = uiState.goal!!.name,
            onDismiss = { detailViewModel.hideDeleteDialog() },
            onConfirm = {
                detailViewModel.deleteGoal(uiState.goal!!.id)
            },
            isDeleting = uiState.isDeleting
        )
    }

    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            // TODO: Mostrar snackbar con el error
        }
    }
}

@Composable
fun GoalHeaderCard(
    goal: Goal,
    goalsViewModel: GoalsViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorDarkBlue2),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = goal.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = colorWhite,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colorTeal),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = goalsViewModel.getIconVector(goal.icon),
                        contentDescription = null,
                        tint = colorWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            goal.goalDate?.let { date ->
                Spacer(modifier = Modifier.height(8.dp))
                val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                Text(
                    text = "Fecha objetivo: ${dateFormatter.format(date)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorWhite.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = when (goal.state) {
                        "Activo" -> colorTeal.copy(alpha = 0.2f)
                        "Pausado" -> colorOrange.copy(alpha = 0.2f)
                        "Conseguido" -> colorGreen1.copy(alpha = 0.2f)
                        else -> colorWhite.copy(alpha = 0.1f)
                    }
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Estado: ${goal.state}",
                    style = MaterialTheme.typography.bodySmall,
                    color = when (goal.state) {
                        "Activo" -> colorTeal
                        "Pausado" -> colorOrange.copy(alpha = 0.2f)
                        "Conseguido" -> colorGreen1
                        else -> colorWhite
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            goal.note?.takeIf { it.isNotBlank() }?.let { note ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorWhite.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun GoalProgressCard(goal: Goal) {
    val progress = if (goal.quantity > 0) {
        (goal.currentQuantity / goal.quantity).coerceIn(0.0, 1.0).toFloat()
    } else 0f

    val progressPercent = (progress * 100).toInt()
    val remainingAmount = goal.quantity - goal.currentQuantity

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorTeal),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "${String.format("%.0f", goal.currentQuantity)} / ${String.format("%.0f", goal.quantity)} CRC",
                style = MaterialTheme.typography.headlineSmall,
                color = colorWhite,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = colorWhite,
                trackColor = colorWhite.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Progreso",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorWhite.copy(alpha = 0.9f)
                )
                Text(
                    text = "$progressPercent%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorWhite,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Falta por ahorrar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorWhite.copy(alpha = 0.9f)
                )
                Text(
                    text = "CRC ${String.format("%.0f", remainingAmount.coerceAtLeast(0.0))}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorWhite,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ContributionsHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorDarkBlue2),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Fecha y hora",
                style = MaterialTheme.typography.titleMedium,
                color = colorWhite,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Monto",
                style = MaterialTheme.typography.titleMedium,
                color = colorWhite,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ContributionItem(contribution: GoalContributionDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorWhite.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = contribution.contributionDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorWhite,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "CRC ${String.format("%.0f", contribution.amount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorTeal,
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            contribution.description?.takeIf { it.isNotBlank() }?.let { desc ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorWhite.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun EmptyContributionsMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Aún no has agregado ningún aporte.\n¡Comienza ahora!",
            style = MaterialTheme.typography.bodyLarge,
            color = colorWhite.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContributionDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double, String) -> Unit,
    isLoading: Boolean = false
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        containerColor = colorWhite,
        title = {
            Text(
                text = "Agregar aporte",
                color = colorDarkBlue1,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Monto") },
                    placeholder = { Text("100000") },
                    leadingIcon = { Text("₡") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción (opcional)") },
                    placeholder = { Text("Aporte mensual") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull()
                    if (amountValue != null && amountValue > 0) {
                        onConfirm(amountValue, description)
                    }
                },
                enabled = !isLoading && amount.toDoubleOrNull()?.let { it > 0 } == true,
                colors = ButtonDefaults.buttonColors(containerColor = colorTeal)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = colorWhite,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Agregar", color = colorWhite)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGoalDialog(
    goal: Goal,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, Date?, String?, String, String) -> Unit,
    goalsViewModel: GoalsViewModel
) {
    var name by remember { mutableStateOf(goal.name) }
    var quantity by remember { mutableStateOf(goal.quantity.toString()) }
    var note by remember { mutableStateOf(goal.note ?: "") }
    var selectedIcon by remember { mutableStateOf(goalsViewModel.getIconVector(goal.icon)) }
    var selectedState by remember { mutableStateOf(goal.state) }
    var goalDate by remember { mutableStateOf("") }
    var showIconPickerDialog by remember { mutableStateOf(false) }

    LaunchedEffect(goal.goalDate) {
        goal.goalDate?.let { date ->
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            goalDate = dateFormat.format(date)
        }
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            goalDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    if (showIconPickerDialog) {
        IconPickerDialog(
            onDismissRequest = { showIconPickerDialog = false },
            onIconSelected = { icon ->
                selectedIcon = icon
                showIconPickerDialog = false
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colorWhite,
        title = {
            Text(
                text = "Editar Objetivo",
                color = colorDarkBlue1,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(400.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre del objetivo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Monto objetivo") },
                        leadingIcon = { Text("₡") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Notas (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }

                item {
                    var expanded by remember { mutableStateOf(false) }
                    val states = listOf("Activo", "Pausado", "Conseguido")

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedState,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Estado") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            states.forEach { state ->
                                DropdownMenuItem(
                                    text = { Text(state) },
                                    onClick = {
                                        selectedState = state
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = goalDate,
                            onValueChange = {},
                            label = { Text("Fecha") },
                            readOnly = true,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { datePickerDialog.show() },
                            trailingIcon = {
                                Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                            }
                        )

                        OutlinedTextField(
                            value = "Icono",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showIconPickerDialog = true },
                            trailingIcon = {
                                Icon(
                                    imageVector = selectedIcon,
                                    contentDescription = "Icono seleccionado",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val quantityValue = quantity.toDoubleOrNull()
                    if (name.isNotBlank() && quantityValue != null && quantityValue > 0) {
                        val parsedDate = try {
                            if (goalDate.isNotBlank()) {
                                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                dateFormat.parse(goalDate)
                            } else null
                        } catch (e: Exception) {
                            null
                        }

                        onConfirm(
                            name,
                            quantityValue,
                            parsedDate,
                            note.takeIf { it.isNotBlank() },
                            goalsViewModel.getIconName(selectedIcon),
                            selectedState
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorTeal)
            ) {
                Text("Guardar", color = colorWhite)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun DeleteConfirmationDialog(
    goalName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isDeleting: Boolean = false
) {
    AlertDialog(
        onDismissRequest = { if (!isDeleting) onDismiss() },
        containerColor = colorWhite,
        title = {
            Text(
                text = "Eliminar Objetivo",
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "¿Estás seguro de que deseas eliminar el objetivo \"$goalName\"?\n\nEsta acción no se puede deshacer y se perderán todos los aportes realizados.",
                color = colorDarkBlue1
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isDeleting,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = colorWhite,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Eliminar", color = colorWhite)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isDeleting
            ) {
                Text("Cancelar")
            }
        }
    )
}