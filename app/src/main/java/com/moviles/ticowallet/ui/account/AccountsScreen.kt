package com.moviles.ticowallet.ui.account

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.moviles.ticowallet.models.Account
import com.moviles.ticowallet.ui.theme.TicoWalletTheme
import com.moviles.ticowallet.viewmodel.account.AccountViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun AccountsScreen(navController: NavController, viewModel: AccountViewModel) {
    val accountState = remember { mutableStateOf<List<Account>?>(null) }
    val showNoAccountsMessage = remember { mutableStateOf(false) }
    val context = LocalContext.current

    val refreshAccounts: () -> Unit = {
        showNoAccountsMessage.value = false
        accountState.value = null

        viewModel.getAllAccounts(
            onSuccess = { ac ->
                accountState.value = ac
            },
            onError = { error ->
                println("Error al obtener las cuentas: $error")
                accountState.value = emptyList()
            }
        )
    }

    LaunchedEffect(Unit) {
        refreshAccounts()

        launch {
            delay(5000)
            if (accountState.value.isNullOrEmpty()) {
                showNoAccountsMessage.value = true
            }
        }
    }

    TicoWalletTheme {
        val scrollState = rememberScrollState()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF27496d))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when {
                        accountState.value == null -> {
                            if (!showNoAccountsMessage.value) {
                                Text(text = "Cargando cuentas...", color = Color.White)
                            } else {
                                Text(text = "No hay cuentas.", color = Color.White)
                            }
                        }
                        accountState.value!!.isEmpty() -> {
                            Text(text = "No hay cuentas.", color = Color.White)
                        }
                        else -> {
                            accountState.value!!.forEach { account ->
                                AccountItem(
                                    account = account,
                                    onDeleteClick = { accountToDelete ->
                                        accountToDelete.id?.let {
                                            viewModel.deleteAccount(
                                                it,
                                                onSuccess = {
                                                    println("Cuenta eliminada exitosamente: ${accountToDelete.name}")
                                                    refreshAccounts()
                                                    Toast.makeText(context, "Cuenta ${accountToDelete.name} eliminada exitosamente.", Toast.LENGTH_SHORT).show()
                                                },
                                                onError = { error ->
                                                    println("Error al eliminar la cuenta: $error")
                                                    Toast.makeText(context, "No se pudo eliminar la cuenta.", Toast.LENGTH_SHORT).show()
                                                }
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

    }

}

@Composable
fun AccountItem(account: Account, onDeleteClick: (Account) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2639)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = account.name,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = Color.White,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 4.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp).clickable { onDeleteClick(account) }
                )
            }
        }
    }
}