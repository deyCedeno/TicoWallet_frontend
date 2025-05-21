package com.moviles.ticowallet.navigation
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavHostController // Para el tipo del parámetro
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.moviles.ticowallet.ui.goals.GoalDetailScreen
import com.moviles.ticowallet.ui.goals.GoalsScreen

// Define tus rutas
object AppDestinations {
    const val GOALS_ROUTE = "goals"
    const val GOAL_DETAIL_ROUTE = "goal_detail"
    const val GOAL_ID_ARG = "goalId"
    const val CREATE_GOAL_ROUTE = "create_goal"
}


@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = AppDestinations.GOALS_ROUTE) {
        composable(AppDestinations.GOALS_ROUTE) {

            GoalsScreen(
                navController = navController,
                paddingValues = PaddingValues(),
                onNavigateToCreateGoal = {
                    navController.navigate(AppDestinations.CREATE_GOAL_ROUTE)
                }

            )
        }

        composable(
            route = "${AppDestinations.GOAL_DETAIL_ROUTE}/{${AppDestinations.GOAL_ID_ARG}}",
            arguments = listOf(navArgument(AppDestinations.GOAL_ID_ARG) {
                type = NavType.StringType
                nullable = true // O false, dependiendo de si siempre esperas un ID
            })
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getString(AppDestinations.GOAL_ID_ARG)
            GoalDetailScreen(navController = navController, goalId = goalId)
        }

    }
}