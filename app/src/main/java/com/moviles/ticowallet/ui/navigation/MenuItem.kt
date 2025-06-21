package com.moviles.ticowallet.ui.navigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

val defaultMenuItems = listOf(
    MenuItem("inicio", "Inicio", Icons.Filled.Home),
    MenuItem("registros", "Registros", Icons.Filled.ListAlt),
    MenuItem("estadisticas", "Estadísticas", Icons.Filled.BarChart),
    MenuItem("pagos_programados", "Pagos programados", Icons.Filled.Schedule),
    MenuItem("objetivos", "Objetivos", Icons.Filled.Flag),
    MenuItem("garantias", "Garantías", Icons.Filled.VerifiedUser),
    MenuItem("tipo_cambio", "Tipo de cambio", Icons.Filled.CurrencyExchange),
    MenuItem("ajustes", "Ajustes", Icons.Filled.Settings),
    MenuItem("cuentas", "Cuentas", Icons.Filled.AccountBalance)
)