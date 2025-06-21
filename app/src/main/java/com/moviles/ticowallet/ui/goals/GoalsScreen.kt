package com.moviles.ticowallet.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.ticowallet.models.Goal
import com.moviles.ticowallet.viewmodel.goals.GoalsViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.moviles.ticowallet.ui.theme.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    onNavigateToCreateGoal: () -> Unit,
    goalsViewModel: GoalsViewModel = viewModel()
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Activo", "Pausado", "Conseguido")

    val uiState by goalsViewModel.uiState.collectAsStateWithLifecycle()
    val activeGoals by goalsViewModel.activeGoals.collectAsStateWithLifecycle()
    val pausedGoals by goalsViewModel.pausedGoals.collectAsStateWithLifecycle()
    val achievedGoals by goalsViewModel.achievedGoals.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        goalsViewModel.refreshGoals()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        containerColor = colorDarkBlue1,

    ) { innerScaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerScaffoldPadding)
                .background(colorDarkBlue1)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = colorDarkBlue1,
                contentColor = colorWhite,
                divider = {
                    Divider(
                        thickness = 1.dp,
                        color = colorDarkBlue2.copy(alpha = 0.6f)
                    )
                },
                indicator = { tabPositions ->
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedTabIndex])
                            .fillMaxWidth(1f / tabs.size)
                            .height(3.dp)
                            .background(colorTeal)
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTabIndex == index) colorWhite else colorWhite.copy(alpha = 0.7f),
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorDarkBlue1),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorTeal)
                }
            } else {
                when (selectedTabIndex) {
                    0 -> FilteredGoalsContent(
                        modifier = Modifier.weight(1f),
                        goals = activeGoals,
                        statusTitle = "Activo",
                        goalsViewModel = goalsViewModel,
                        navController = navController
                    )
                    1 -> FilteredGoalsContent(
                        modifier = Modifier.weight(1f),
                        goals = pausedGoals,
                        statusTitle = "Pausado",
                        goalsViewModel = goalsViewModel,
                        navController = navController
                    )
                    2 -> FilteredGoalsContent(
                        modifier = Modifier.weight(1f),
                        goals = achievedGoals,
                        statusTitle = "Conseguido",
                        goalsViewModel = goalsViewModel,
                        navController = navController
                    )
                }
            }
        }
    }

    uiState.successMessage?.let { message ->
        LaunchedEffect(message) {
            goalsViewModel.clearMessages()
        }
    }

    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            goalsViewModel.clearMessages()
        }
    }
}

@Composable
fun FilteredGoalsContent(
    modifier: Modifier = Modifier,
    goals: List<Goal>,
    statusTitle: String,
    goalsViewModel: GoalsViewModel,
    navController: NavController
) {
    if (goals.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(colorDarkBlue1),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = null,
                    tint = colorWhite.copy(alpha = 0.5f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No hay objetivos en estado '$statusTitle'",
                    color = colorWhite.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (statusTitle == "Activo") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "¡Crea tu primer objetivo!",
                        color = colorTeal,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(colorDarkBlue1),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(goals, key = { goal -> goal.id }) { goal ->
                GoalCard(
                    goal = goal,
                    goalsViewModel = goalsViewModel,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun GoalCard(
    goal: Goal,
    goalsViewModel: GoalsViewModel,
    navController: NavController
) {
    val progress = if (goal.quantity > 0) {
        (goal.currentQuantity / goal.quantity).coerceIn(0.0, 1.0).toFloat()
    } else {
        0f
    }

    val progressPercent = (progress * 100).toInt()

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedDate = goal.goalDate?.let { dateFormat.format(it) }

    val icon = goalsViewModel.getIconVector(goal.icon)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {

                navController.navigate("goal_detail/${goal.id}")
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colorDarkBlue2)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = goal.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = colorWhite,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = when (goal.state) {
                                "Activo" -> colorTeal.copy(alpha = 0.2f)
                                "Pausado" -> colorOrange.copy(alpha = 0.2f)
                                "Conseguido" -> colorGreen1.copy(alpha = 0.2f)
                                else -> colorWhite.copy(alpha = 0.1f)
                            }
                        ),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = goal.state,
                            style = MaterialTheme.typography.bodySmall,
                            color = when (goal.state) {
                                "Activo" -> colorTeal
                                "Pausado" -> colorOrange
                                "Conseguido" -> colorGreen1
                                else -> colorWhite
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(colorTeal),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = goal.name,
                        tint = colorWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formattedDate?.let { "Fecha límite: $it" } ?: "Sin fecha límite",
                style = MaterialTheme.typography.bodySmall,
                color = colorWhite.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    trackColor = colorWhite.copy(alpha = 0.3f),
                    color = when (goal.state) {
                        "Conseguido" -> colorGreen1
                        "Pausado" -> colorOrange
                        else -> colorTeal
                    }
                )

                Text(
                    text = "$progressPercent%",
                    color = colorWhite,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Ahorrado",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorWhite.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "₡${formatAmount(goal.currentQuantity)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorGreen1,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Objetivo",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorWhite.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "₡${formatAmount(goal.quantity)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (goal.state != "Conseguido" && goal.currentQuantity < goal.quantity) {
                Spacer(modifier = Modifier.height(8.dp))
                val remaining = goal.quantity - goal.currentQuantity
                Text(
                    text = "Faltan: ₡${formatAmount(remaining)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorOrange,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

fun formatAmount(amount: Double): String {
    val longAmount = amount.toLong()
    return longAmount.toString()
        .reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()
}