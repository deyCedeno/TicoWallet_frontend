package com.moviles.ticowallet.ui.main // <-- Cambiado

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.ticowallet.ui.theme.TicoWalletTheme
// Importar ViewModel desde su nueva ubicación
import com.moviles.ticowallet.viewmodel.main.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold( // <-- Renombrada
    mainViewModel: MainViewModel = viewModel(), // <-- Usa MainViewModel
    onNotificationsClick: () -> Unit,
) {
    // Observar el estado del MainViewModel
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // Llama a AppDrawerContent (que ahora está en este mismo paquete)
            AppDrawerContent(
                userName = uiState.userName,
                menuItems = uiState.menuItems,
                selectedItemRoute = uiState.selectedItemRoute,
                onMenuItemClick = { menuItem ->
                    mainViewModel.onMenuItemSelect(menuItem) // Llama al MainViewModel
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(uiState.menuItems.find { it.route == uiState.selectedItemRoute }?.title ?: "Tico Wallet") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Abrir menú")
                        }
                    },
                    actions = {
                        IconButton(onClick = onNotificationsClick) {
                            Icon(Icons.Filled.Notifications, contentDescription = "Ver notificaciones")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { innerPadding ->
            // --- Área de Contenido Principal ---
            // Aquí es donde deberías mostrar la pantalla/sección correspondiente
            // basada en uiState.selectedItemRoute.
            // Por ahora, solo mostramos la ruta seleccionada.
            // Más adelante, aquí usarías un NavHost de Jetpack Navigation Compose.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Pantalla actual: ${uiState.selectedItemRoute}")

                // EJEMPLO de cómo mostrarías diferentes pantallas (requiere Navigation Compose):
                /*
                NavHost(navController = navController, startDestination = "inicio") {
                    composable("inicio") { /* Tu Composable para Inicio */ }
                    composable("registros") { /* Tu Composable para Registros */ }
                    composable("objetivos") { GoalsScreenActual() } // <-- Aquí iría la pantalla real de Metas
                    // ... otras rutas
                }
                */
            }
        }
    }
}


@Preview(showBackground = true, name = "Main App Scaffold Preview")
@Composable
fun MainAppScaffoldPreview() {
    TicoWalletTheme {
        MainAppScaffold( // Llama al Composable renombrado
            onNotificationsClick = {}
        )
    }
}