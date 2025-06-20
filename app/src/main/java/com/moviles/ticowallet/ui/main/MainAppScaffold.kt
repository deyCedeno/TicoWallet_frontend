package com.moviles.ticowallet.ui.main

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.moviles.ticowallet.LoginActivity
import com.moviles.ticowallet.ui.account.AccountsScreen
import com.moviles.ticowallet.ui.theme.TicoWalletTheme
import com.moviles.ticowallet.viewmodel.main.MainViewModel
import kotlinx.coroutines.launch
import com.moviles.ticowallet.ui.goals.GoalsScreen
import com.moviles.ticowallet.ui.goals.CreateGoalScreen
import com.moviles.ticowallet.ui.goals.GoalDetailScreen
import com.moviles.ticowallet.ui.theme.*
import com.moviles.ticowallet.ui.user.UserProfileScreen
import com.moviles.ticowallet.viewmodel.account.AccountViewModel
import com.moviles.ticowallet.viewmodel.main.HomeViewModel
import com.moviles.ticowallet.viewmodel.user.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(
    mainViewModel: MainViewModel = viewModel(),
    onNotificationsClick: () -> Unit,
) {
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            val route = backStackEntry.destination.route
            if (route != null && route != uiState.selectedItemRoute) {
                val selectedMenuItem = uiState.menuItems.find { it.route == route }
                if (selectedMenuItem != null) {
                    mainViewModel.onMenuItemSelect(selectedMenuItem)
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                userName = uiState.userName,
                menuItems = uiState.menuItems,
                selectedItemRoute = uiState.selectedItemRoute,
                onMenuItemClick = { menuItem ->
                    scope.launch { drawerState.close() }
                    navController.navigate(menuItem.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                    mainViewModel.onMenuItemSelect(menuItem)
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = uiState.menuItems.find { it.route == uiState.selectedItemRoute }?.title ?: "Inicio",
                            color = colorWhite
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Filled.Menu,
                                contentDescription = "Abrir menú",
                                tint = colorWhite
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onNotificationsClick) {
                            Icon(
                                Icons.Filled.Notifications,
                                contentDescription = "Ver notificaciones",
                                tint = colorWhite
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorDarkBlue1,
                        titleContentColor = colorWhite,
                        navigationIconContentColor = colorWhite,
                        actionIconContentColor = colorWhite
                    )
                )
            },
            floatingActionButton = {
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                if (currentRoute == "objetivos") {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate("crear_objetivo_screen")
                        },
                        containerColor = colorTeal,
                        contentColor = colorWhite,
                        shape = CircleShape
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar objetivo")
                    }
                }
            },
            containerColor = colorDarkBlue1
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorDarkBlue1)
            ) {
                AppNavHost(
                    navController = navController,
                    mainViewModel = mainViewModel,
                    paddingValues = innerPadding
//                    startDestination = uiState.menuItems.firstOrNull()?.route ?: "inicio"
                )
            }
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    paddingValues: PaddingValues
//    startDestination: String
) {
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = "inicio",
        modifier = Modifier.fillMaxSize().padding(paddingValues)
    ) {
        composable("inicio") {
            val viewModel: HomeViewModel = viewModel()
            HomeScreen(navController = navController, viewModel)
        }
        composable("registros") { PlaceholderScreen("Registros", PaddingValues()) }
        composable("estadisticas") { PlaceholderScreen("Estadísticas", PaddingValues()) }
        composable("pagos_programados") { PlaceholderScreen("Pagos Programados", PaddingValues()) }
        composable("deudas") { PlaceholderScreen("Deudas", PaddingValues()) }

        composable("objetivos") {
            GoalsScreen(
                navController = navController,
                paddingValues = PaddingValues(),
                onNavigateToCreateGoal = {
                    navController.navigate("crear_objetivo_screen")
                }
            )
        }
        composable("crear_objetivo_screen") {
            CreateGoalScreen(
                navController = navController,
                paddingValues = PaddingValues()
            )
        }
        composable(
            route = "detalle_objetivo_screen/{goalId}",
            arguments = listOf(navArgument("goalId") { type = NavType.StringType })
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getString("goalId")
            GoalDetailScreen(
                navController = navController,
                goalId = goalId
            )
        }
        composable("garantias") { PlaceholderScreen("Garantías", paddingValues) }
        composable("tipo_cambio") { PlaceholderScreen("Tipo de Cambio", paddingValues) }
        composable("ajustes") {
            TicoWalletTheme {
                val viewModel: UserViewModel = viewModel()
                UserProfileScreen(viewModel, onLogout = {
                    context.startActivity(Intent(context, LoginActivity::class.java))
                    if (context is ComponentActivity) {
                        context.finish()
                    }
                })
            }
        }

        composable("cuentas") {
            val accountViewModel: AccountViewModel = viewModel()
            AccountsScreen(navController = navController, viewModel = accountViewModel)
        }


    }
}

@Composable
fun PlaceholderScreen(screenTitle: String, paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorWhite)
            .padding(paddingValues)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Pantalla: $screenTitle",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Contenido de $screenTitle aquí.",
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
