package com.moviles.ticowallet.ui.main

import android.content.Intent
import android.util.Log
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.moviles.ticowallet.LoginActivity
import com.moviles.ticowallet.ui.account.AccountsScreen
import com.moviles.ticowallet.ui.account.CreateAccountScreen
import com.moviles.ticowallet.ui.account.UpdateAccountScreen
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
import com.moviles.ticowallet.ui.exchangerate.ExchangeRateScreen
import com.moviles.ticowallet.ui.movements.MovementsScreen
import com.moviles.ticowallet.viewmodel.goals.GoalsViewModel
import com.moviles.ticowallet.ui.warranties.AddEditWarrantyScreen
import com.moviles.ticowallet.ui.warranties.WarrantiesScreen
import com.moviles.ticowallet.ui.warranties.WarrantyDetailScreen
import com.moviles.ticowallet.viewmodel.warranties.WarrantiesViewModel
import com.moviles.ticowallet.ui.scheduledPayment.ScheduledPaymentListScreen
import com.moviles.ticowallet.ui.scheduledPayment.CreateScheduledPaymentScreen
import com.moviles.ticowallet.ui.scheduledPayment.ScheduledPaymentDetailScreen
import com.moviles.ticowallet.viewmodel.movements.MovementViewModel

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
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

                TopAppBar(
                    title = {
                        val title = when {
                            currentRoute == "create_scheduled_payment" -> "Nuevo pago programado"
                            currentRoute?.startsWith("scheduled_payment_detail") == true -> "Editar pago programado"
                            currentRoute == "agregar_garantia" -> "Nueva Garantía"
                            currentRoute?.startsWith("editar_garantia") == true -> "Editar Garantía"
                            currentRoute?.startsWith("detalle_garantia") == true -> "Detalle Garantía"
                            else -> uiState.menuItems.find { it.route == uiState.selectedItemRoute }?.title ?: "Inicio"
                        }
                        Text(
                            text = title,
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
                when (currentRoute) {
                    "objetivos" -> {
                        FloatingActionButton(
                            onClick = {
                                navController.navigate("create_goal")
                            },
                            containerColor = colorTeal,
                            contentColor = colorWhite,
                            shape = CircleShape
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar objetivo")
                        }
                    }
                    "pagos_programados" -> {
                        FloatingActionButton(
                            onClick = {
                                navController.navigate("create_scheduled_payment")
                            },
                            containerColor = colorTeal,
                            contentColor = colorWhite,
                            shape = CircleShape
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar pago programado")
                        }
                    }
                    "garantias" -> {
                        FloatingActionButton(
                            onClick = {
                                navController.navigate("agregar_garantia")
                            },
                            containerColor = colorTeal,
                            contentColor = colorWhite,
                            shape = CircleShape
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar garantía")
                        }
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
        composable("registros") {
            val movementViewModel: MovementViewModel = viewModel()
            MovementsScreen(navController = navController, viewModel = movementViewModel)
        }
        composable("estadisticas") { PlaceholderScreen("Estadísticas", PaddingValues()) }

        composable("pagos_programados") {
            ScheduledPaymentListScreen(
                navController = navController,
                onMenuClick = { /* Handled by main scaffold */ }
            )
        }

        composable("create_scheduled_payment") {
            CreateScheduledPaymentScreen(
                navController = navController
            )
        }

        composable(
            route = "scheduled_payment_detail/{paymentId}",
            arguments = listOf(navArgument("paymentId") { type = NavType.IntType })
        ) { backStackEntry ->
            val paymentId = backStackEntry.arguments?.getInt("paymentId") ?: 0
            ScheduledPaymentDetailScreen(
                navController = navController,
                paymentId = paymentId
            )
        }

        composable("deudas") { PlaceholderScreen("Deudas", PaddingValues()) }

        composable("objetivos") {
            val goalsViewModel: GoalsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            GoalsScreen(
                navController = navController,
                paddingValues = PaddingValues(),
                onNavigateToCreateGoal = {
                    navController.navigate("create_goal")
                },
                goalsViewModel = goalsViewModel
            )
        }

        composable("create_goal") {
            val goalsViewModel: GoalsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            CreateGoalScreen(
                navController = navController,
                paddingValues = PaddingValues(),
                goalsViewModel = goalsViewModel
            )
        }

        composable(
            route = "goal_detail/{goalId}",
            arguments = listOf(navArgument("goalId") {
                type = NavType.StringType
                nullable = false
            })
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getString("goalId")
            if (goalId != null) {
                GoalDetailScreen(
                    navController = navController,
                    goalId = goalId
                )
            }
        }

        composable("garantias") {
            val warrantiesViewModel: WarrantiesViewModel = viewModel()
            val warranties by warrantiesViewModel.warranties.collectAsStateWithLifecycle()
            val isLoading by warrantiesViewModel.isLoading.collectAsStateWithLifecycle()
            val error by warrantiesViewModel.error.collectAsStateWithLifecycle()

            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            LaunchedEffect(currentBackStackEntry) {
                val currentRoute = currentBackStackEntry?.destination?.route
                if (currentRoute == "garantias") {
                    Log.d("Navigation", "Refreshing warranties list")
                    warrantiesViewModel.refreshWarranties()
                }
            }

            error?.let { errorMessage ->
                LaunchedEffect(errorMessage) {
                    android.util.Log.e("WarrantiesScreen", "Error: $errorMessage")
                    warrantiesViewModel.clearError()
                }
            }

            WarrantiesScreen(
                warranties = warranties,
                isLoading = isLoading,
                onWarrantyClick = { warranty ->
                    navController.navigate("detalle_garantia/${warranty.idWarranty}")
                },
                onAddWarrantyClick = {
                    navController.navigate("agregar_garantia")
                }
            )
        }

        composable(
            route = "detalle_garantia/{warrantyId}",
            arguments = listOf(navArgument("warrantyId") { type = NavType.IntType })
        ) { backStackEntry ->
            val warrantyId = backStackEntry.arguments?.getInt("warrantyId") ?: 0
            val warrantiesViewModel: WarrantiesViewModel = viewModel()
            val selectedWarranty by warrantiesViewModel.selectedWarranty.collectAsStateWithLifecycle()
            val isLoading by warrantiesViewModel.isLoading.collectAsStateWithLifecycle()

            LaunchedEffect(warrantyId) {
                warrantiesViewModel.loadWarrantyById(warrantyId)
            }

            WarrantyDetailScreen(
                warranty = selectedWarranty,
                isLoading = isLoading,
                onBackClick = {
                    navController.popBackStack()
                },
                onEditClick = {
                    navController.navigate("editar_garantia/$warrantyId")
                },
                onDeleteConfirm = {
                    warrantiesViewModel.deleteWarranty(warrantyId)
                    navController.popBackStack()
                }
            )
        }

        composable("agregar_garantia") {
            val warrantiesViewModel: WarrantiesViewModel = viewModel()
            val isLoading by warrantiesViewModel.isLoading.collectAsStateWithLifecycle()
            var hasNavigatedBack by remember { mutableStateOf(false) }

            AddEditWarrantyScreen(
                warranty = null,
                isLoading = isLoading,
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveClick = { name, price, purchaseDate, expirationDate, icon ->
                    warrantiesViewModel.createWarranty(
                        name = name,
                        price = price,
                        purchaseDate = purchaseDate,
                        expirationDate = expirationDate,
                        icon = icon
                    ) {
                        navController.popBackStack()
                    }
                }

            )
        }

        composable(
            route = "editar_garantia/{warrantyId}",
            arguments = listOf(navArgument("warrantyId") { type = NavType.IntType })
        ) { backStackEntry ->
            val warrantyId = backStackEntry.arguments?.getInt("warrantyId") ?: 0
            val warrantiesViewModel: WarrantiesViewModel = viewModel()
            val selectedWarranty by warrantiesViewModel.selectedWarranty.collectAsStateWithLifecycle()
            val isLoading by warrantiesViewModel.isLoading.collectAsStateWithLifecycle()
            var hasNavigatedBack by remember { mutableStateOf(false) }

            LaunchedEffect(warrantyId) {
                if (selectedWarranty?.idWarranty != warrantyId) {
                    warrantiesViewModel.loadWarrantyById(warrantyId)
                }
            }

            AddEditWarrantyScreen(
                warranty = selectedWarranty,
                isLoading = isLoading,
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveClick = { name, price, purchaseDate, expirationDate, icon ->
                    warrantiesViewModel.updateWarranty(
                        id = warrantyId,
                        name = name,
                        price = price,
                        purchaseDate = purchaseDate,
                        expirationDate = expirationDate,
                        icon = icon
                    ) {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable("tipo_cambio") {
            ExchangeRateScreen(paddingValues = PaddingValues())
        }

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

        composable("crear_cuenta") {
            val accountViewModel: AccountViewModel = viewModel()
            CreateAccountScreen(navController = navController, accountViewModel)
        }

        composable(
            route = "modificar_cuenta/{accountId}",
            arguments = listOf(navArgument("accountId") { type = NavType.IntType })
        ) { backStackEntry ->
            val accountId = backStackEntry.arguments?.getInt("accountId") ?: 0
            val accountViewModel: AccountViewModel = viewModel()
            UpdateAccountScreen(
                navController = navController,
                accountId = accountId,
                viewModel = accountViewModel
            )
        }

//        Movimientos
        composable("crear_movimiento") {
            val movementViewModel: MovementViewModel = viewModel()
//            (navController = navController, movementViewModel)
        }

        composable(
            route = "modificar_movimiento/{movementId}",
            arguments = listOf(navArgument("movementId") { type = NavType.IntType })
        ) { backStackEntry ->
            val movementId = backStackEntry.arguments?.getInt("movementId") ?: 0
            val movementViewModel: MovementViewModel = viewModel()
//            UpdateMovementScreen(
//                navController = navController,
//                movementId = movementId,
//                viewModel = movementViewModel
//            )
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
