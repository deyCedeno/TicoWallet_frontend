package com.moviles.ticowallet.ui.goals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.ticowallet.models.Goal
import com.moviles.ticowallet.viewmodel.goals.GoalsViewModel
import com.moviles.ticowallet.viewmodel.goals.GoalStatus
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    paddingValues: PaddingValues,
    onNavigateToCreateGoal: () -> Unit,
    goalsViewModel: GoalsViewModel = viewModel() // Inyectar o obtener ViewModel
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    // Usar las constantes para los títulos de las pestañas asegura consistencia
    val tabs = listOf(GoalStatus.ACTIVE, GoalStatus.PAUSED, GoalStatus.ACHIEVED) // [cite: 3]

    // Recolectar los estados de las listas filtradas
    val activeGoals by goalsViewModel.activeGoals.collectAsState() // [cite: 2]
    val pausedGoals by goalsViewModel.pausedGoals.collectAsState() // [cite: 2]
    val achievedGoals by goalsViewModel.achievedGoals.collectAsState() // [cite: 2]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }, // [cite: 3]
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }

        // Pasar la lista filtrada correspondiente a cada contenido de pestaña
        when (selectedTabIndex) {
            0 -> FilteredGoalsContent(modifier = Modifier.weight(1f), goals = activeGoals, statusTitle = GoalStatus.ACTIVE) // [cite: 3]
            1 -> FilteredGoalsContent(modifier = Modifier.weight(1f), goals = pausedGoals, statusTitle = GoalStatus.PAUSED) // [cite: 3]
            2 -> FilteredGoalsContent(modifier = Modifier.weight(1f), goals = achievedGoals, statusTitle = GoalStatus.ACHIEVED) // [cite: 3]
        }
    }
}

@Composable
fun ActivoContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Contenido de Objetivos Activos")
    }
}

@Composable
fun PausadoContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Contenido de Objetivos Pausados")
    }
}

@Composable
fun ConseguidoContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Contenido de Objetivos Conseguidos")
    }
}

// Composable genérico para mostrar una lista de metas o un mensaje si está vacía
@Composable
fun FilteredGoalsContent(modifier: Modifier = Modifier, goals: List<Goal>, statusTitle: String) {
    if (goals.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay objetivos en estado '$statusTitle'.")
        }
    } else {

        // ...dentro de FilteredGoalsContent
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(goals) { goal -> // 'goal' aquí es de tipo Goal
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        // Los errores están en estas líneas:
                        Text(text = goal.name, style = MaterialTheme.typography.titleMedium)
                        Text(text = "Estado: ${goal.state}", style = MaterialTheme.typography.bodySmall)
                        Text(text = "Cantidad: ${goal.currentQuantity} / ${goal.quantity}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}