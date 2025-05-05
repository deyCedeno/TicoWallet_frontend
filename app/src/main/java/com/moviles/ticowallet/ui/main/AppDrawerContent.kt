package com.moviles.ticowallet.ui.main // <-- Cambiado

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moviles.ticowallet.R
// Importar MenuItem desde su nueva ubicación
import com.moviles.ticowallet.ui.navigation.MenuItem
import com.moviles.ticowallet.ui.navigation.defaultMenuItems
import com.moviles.ticowallet.ui.theme.TicoWalletTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerContent(
    userName: String,
    menuItems: List<MenuItem>,
    selectedItemRoute: String,
    onMenuItemClick: (MenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(modifier = modifier) {
        // Encabezado del Drawer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // Asegúrate que R se importe correctamente
                contentDescription = "Logo de la aplicación",
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tico Wallet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = userName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

        // Lista de ítems del menú
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(items = menuItems, key = { it.route }) { item ->
                NavigationDrawerItem(
                    icon = { Icon(item.icon, contentDescription = item.title) },
                    label = { Text(item.title) },
                    selected = item.route == selectedItemRoute,
                    onClick = { onMenuItemClick(item) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}


@Preview(showBackground = true, name = "Drawer Content Preview")
@Composable
fun AppDrawerContentPreview() {
    TicoWalletTheme {
        AppDrawerContent(
            userName = "Usuario Preview",
            menuItems = defaultMenuItems,
            selectedItemRoute = "inicio",
            onMenuItemClick = {}
        )
    }
}