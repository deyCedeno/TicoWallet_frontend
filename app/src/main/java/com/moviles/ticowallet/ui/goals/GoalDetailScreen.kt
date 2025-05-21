package com.moviles.ticowallet.ui.goals

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Importante para el ícono de "Atrás"
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.moviles.ticowallet.viewmodel.goals.GoalsViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    navController: NavController,
    goalId: String?,
    goalsViewModel: GoalsViewModel = viewModel()
) {

    LaunchedEffect(goalId) {
        if (goalId != null) {
            goalsViewModel.loadGoalById(goalId)
        }
    }

    val goal by goalsViewModel.selectedGoal.collectAsState()

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = goal?.name ?: "Detalle de Meta") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            if (goalId == null) {
                Text("No se ha proporcionado ID de la meta.", style = MaterialTheme.typography.headlineSmall)
            } else {
                goal?.let { currentGoal ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Icon(
                            imageVector = goalsViewModel.getIconVector(currentGoal.icon), // Asumiendo que getIconVector existe en tu ViewModel
                            contentDescription = "Icono de Meta",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Nombre: ${currentGoal.name}", style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Monto Objetivo: ${currentGoal.quantity}", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Monto Actual: ${currentGoal.currentQuantity}", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Verificación para goalDate nulo antes de formatear
                        val formattedDate = currentGoal.goalDate?.let { date ->
                            try {
                                dateFormatter.format(date)
                            } catch (e: Exception) {
                                "Fecha inválida" // Manejo de posible error de formato si la fecha es corrupta
                            }
                        } ?: "No especificada"

                        Text("Fecha Límite: $formattedDate", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Estado: ${currentGoal.state}", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))

                        currentGoal.note?.let { note ->
                            Text("Nota: $note", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // **INICIO DE LA CORRECCIÓN INDISPENSABLE**
                        val progressValue: Float
                        val progressPercentText: String

                        if (currentGoal.quantity > 0.0) {
                            progressValue = (currentGoal.currentQuantity / currentGoal.quantity).toFloat().coerceIn(0f, 1f)
                            progressPercentText = String.format(Locale.US, "%.1f", progressValue * 100)
                        } else {
                            progressValue = 0f // O 1f si un objetivo de 0 se considera 100% completado
                            progressPercentText = "0.0" // O "100.0", o incluso "-" o "N/A"
                        }

                        LinearProgressIndicator(
                            progress = { progressValue }, // Se pasa la función lambda correctamente
                            modifier = Modifier.fillMaxWidth().height(8.dp)
                        )
                        Text(
                            text = "$progressPercentText% completado",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.align(Alignment.End)
                        )
                        // **FIN DE LA CORRECCIÓN INDISPENSABLE**
                    }
                } ?: run {
                    // Muestra un indicador de carga mientras goal es null (cargando)
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            goalsViewModel.clearSelectedGoal()
        }
    }
}