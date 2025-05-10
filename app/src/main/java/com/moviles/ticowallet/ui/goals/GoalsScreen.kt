package com.moviles.ticowallet.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
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
import com.moviles.ticowallet.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    paddingValues: PaddingValues,
    onNavigateToCreateGoal: () -> Unit,
    goalsViewModel: GoalsViewModel = viewModel()
) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabs = listOf("Activo", "Pausado", "Conseguido")

    val activeGoals by goalsViewModel.activeGoals.collectAsState()
    val pausedGoals by goalsViewModel.pausedGoals.collectAsState()
    val achievedGoals by goalsViewModel.achievedGoals.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),

        containerColor = colorDarkBlue1
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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

            when (selectedTabIndex) {
                0 -> FilteredGoalsContent(
                    modifier = Modifier.weight(1f),
                    goals = activeGoals,
                    statusTitle = "Activo",
                    goalsViewModel = goalsViewModel
                )
                1 -> FilteredGoalsContent(
                    modifier = Modifier.weight(1f),
                    goals = pausedGoals,
                    statusTitle = "Pausado",
                    goalsViewModel = goalsViewModel
                )
                2 -> FilteredGoalsContent(
                    modifier = Modifier.weight(1f),
                    goals = achievedGoals,
                    statusTitle = "Conseguido",
                    goalsViewModel = goalsViewModel
                )
            }
        }
    }
}

@Composable
fun FilteredGoalsContent(
    modifier: Modifier = Modifier,
    goals: List<Goal>,
    statusTitle: String,
    goalsViewModel: GoalsViewModel
) {
    if (goals.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(colorDarkBlue1),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No hay objetivos en estado '$statusTitle'.",
                color = colorWhite,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(colorDarkBlue1),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(goals) { goal ->
                GoalCard(goal, goalsViewModel)
            }
        }
    }
}

@Composable
fun GoalCard(goal: Goal, goalsViewModel: GoalsViewModel) {
    val progress = if (goal.quantity > 0) {
        (goal.currentQuantity / goal.quantity).coerceIn(0.0, 1.0).toFloat()
    } else {
        0f
    }

    val progressPercent = (progress * 100).toInt()

    val dateFormat = SimpleDateFormat("dd/M/yyyy", Locale.getDefault())
    val formattedDate = goal.goalDate?.let { dateFormat.format(it) }

    val icon = when (goal.icon.lowercase()) {
        "home" -> Icons.Default.Home
        "directions_car" -> Icons.Default.DirectionsCar
        else -> goalsViewModel.getIconVector(goal.icon)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorDarkBlue2
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = goal.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = colorWhite,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
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

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formattedDate?.let { "Fecha límite: $it" } ?: "Sin fecha límite",
                style = MaterialTheme.typography.bodySmall,
                color = colorWhite.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

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
                    color = colorTeal
                )

                Text(
                    text = "$progressPercent%",
                    color = colorWhite,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ahorrado: CRC ${formatAmount(goal.currentQuantity.toLong())}",
                style = MaterialTheme.typography.bodyMedium,
                color = colorGreen1
            )

            Text(
                text = "Objetivo: CRC ${formatAmount(goal.quantity.toLong())}",
                style = MaterialTheme.typography.bodyMedium,
                color = colorWhite
            )
        }
    }
}

fun formatAmount(amount: Long): String {
    return amount.toString()
        .reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()
}