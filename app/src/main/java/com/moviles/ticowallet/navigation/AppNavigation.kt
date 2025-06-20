package com.moviles.ticowallet.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavHostController
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.moviles.ticowallet.ui.goals.GoalDetailScreen
import com.moviles.ticowallet.ui.goals.GoalsScreen
import com.moviles.ticowallet.ui.goals.CreateGoalScreen

object AppDestinations {
    const val GOALS_ROUTE = "objetivos"
    const val GOAL_DETAIL_ROUTE = "goal_detail"
    const val GOAL_ID_ARG = "goalId"
    const val CREATE_GOAL_ROUTE = "create_goal"
}

@Composable
fun GoalsNavigation(
    navController: NavHostController,
    paddingValues: PaddingValues = PaddingValues(),
    currentRoute: String?
) {
    when {
        currentRoute == AppDestinations.GOALS_ROUTE -> {
            GoalsScreen(
                navController = navController,
                paddingValues = paddingValues,
                onNavigateToCreateGoal = {
                    navController.navigate(AppDestinations.CREATE_GOAL_ROUTE)
                }
            )
        }

        currentRoute == AppDestinations.CREATE_GOAL_ROUTE -> {
            CreateGoalScreen(
                navController = navController,
                paddingValues = paddingValues
            )
        }

        currentRoute?.startsWith("${AppDestinations.GOAL_DETAIL_ROUTE}/") == true -> {
            val goalId = currentRoute.substringAfter("${AppDestinations.GOAL_DETAIL_ROUTE}/")
            GoalDetailScreen(
                navController = navController,
                goalId = goalId
            )
        }
    }
}

fun addGoalsRoutes(
    navGraphBuilder: androidx.navigation.NavGraphBuilder,
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    navGraphBuilder.apply {

        composable(AppDestinations.GOALS_ROUTE) {
            GoalsScreen(
                navController = navController,
                paddingValues = paddingValues,
                onNavigateToCreateGoal = {
                    navController.navigate(AppDestinations.CREATE_GOAL_ROUTE)
                }
            )
        }


        composable(
            route = "${AppDestinations.GOAL_DETAIL_ROUTE}/{${AppDestinations.GOAL_ID_ARG}}",
            arguments = listOf(navArgument(AppDestinations.GOAL_ID_ARG) {
                type = NavType.IntType
                nullable = false
            })
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getInt(AppDestinations.GOAL_ID_ARG)
            if (goalId != null) {
                GoalDetailScreen(
                    navController = navController,
                    goalId = goalId.toString()
                )
            }
        }

        composable(AppDestinations.CREATE_GOAL_ROUTE) {
            CreateGoalScreen(
                navController = navController,
                paddingValues = paddingValues
            )
        }
    }
}