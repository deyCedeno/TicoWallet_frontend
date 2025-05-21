package com.moviles.ticowallet.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moviles.ticowallet.R
import com.moviles.ticowallet.ui.navigation.MenuItem
import com.moviles.ticowallet.ui.navigation.defaultMenuItems
import com.moviles.ticowallet.ui.theme.TicoWalletTheme
import com.moviles.ticowallet.ui.theme.colorDarkBlue2
import com.moviles.ticowallet.ui.theme.colorWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerContent(
    userName: String,
    menuItems: List<MenuItem>,
    selectedItemRoute: String,
    onMenuItemClick: (MenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier,
        drawerContainerColor = colorDarkBlue2,
        drawerContentColor = colorWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Logo de la aplicación",
                    modifier = Modifier.size(40.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tico Wallet",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorWhite
                )
            }

            Text(
                text = userName,
                style = MaterialTheme.typography.bodyLarge,
                color = colorWhite,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Divider(thickness = 0.5.dp, color = colorWhite.copy(alpha = 0.2f))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(items = menuItems, key = { it.route }) { item ->
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = colorWhite
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            color = colorWhite
                        )
                    },
                    selected = item.route == selectedItemRoute,
                    onClick = { onMenuItemClick(item) },
                    modifier = Modifier.padding(vertical = 4.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = colorWhite.copy(alpha = 0.15f),
                        unselectedContainerColor = Color.Transparent,
                        selectedIconColor = colorWhite,
                        unselectedIconColor = colorWhite,
                        selectedTextColor = colorWhite,
                        unselectedTextColor = colorWhite
                    )
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
            userName = "Nombre",
            menuItems = defaultMenuItems,
            selectedItemRoute = "inicio",
            onMenuItemClick = {}
        )
    }
}