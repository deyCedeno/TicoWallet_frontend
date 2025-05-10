package com.moviles.ticowallet.ui.main

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.moviles.ticowallet.ui.theme.TicoWalletTheme
import com.moviles.ticowallet.viewmodel.main.MainViewModel
import kotlinx.coroutines.launch
import com.moviles.ticowallet.ui.goals.GoalsScreen
import com.moviles.ticowallet.ui.goals.CreateGoalScreen
import com.moviles.ticowallet.ui.theme.*

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
                    paddingValues = innerPadding,
                    startDestination = uiState.menuItems.firstOrNull()?.route ?: "inicio"
                )
            }
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    paddingValues: PaddingValues,
    startDestination: String
) {
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize()
    ) {
        composable("inicio") { PlaceholderScreen("Inicio", paddingValues) }
        composable("registros") { PlaceholderScreen("Registros", paddingValues) }
        composable("estadisticas") { PlaceholderScreen("Estadísticas", paddingValues) }
        composable("pagos_programados") { PlaceholderScreen("Pagos Programados", paddingValues) }
        composable("deudas") { PlaceholderScreen("Deudas", paddingValues) }

        composable("objetivos") {
            GoalsScreen(
                paddingValues = paddingValues,
                onNavigateToCreateGoal = {
                    navController.navigate("crear_objetivo_screen")
                }
            )
        }
        composable("crear_objetivo_screen") {
            CreateGoalScreen(
                navController = navController,
                paddingValues = paddingValues
            )
        }
        composable("garantias") { PlaceholderScreen("Garantías", paddingValues) }
        composable("tipo_cambio") { PlaceholderScreen("Tipo de Cambio", paddingValues) }
        composable("ajustes") { PlaceholderScreen("Ajustes", paddingValues) }

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

@Preview(showBackground = true, name = "Main App Scaffold Preview")
@Composable
fun MainAppScaffoldPreview() {
    TicoWalletTheme {
        MainAppScaffold(
            onNotificationsClick = {}
        )
    }
}