package com.moviles.ticowallet.ui.movements


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.moviles.ticowallet.models.Movement
import com.moviles.ticowallet.models.MovementGet
import com.moviles.ticowallet.ui.theme.TicoWalletTheme
import com.moviles.ticowallet.ui.theme.colorDarkBlue1
import com.moviles.ticowallet.ui.theme.colorTeal
import com.moviles.ticowallet.ui.theme.colorWhite
import com.moviles.ticowallet.viewmodel.movements.MovementViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun MovementsScreen(navController: NavController, viewModel: MovementViewModel) {
    val MovementState = remember { mutableStateOf<List<MovementGet>?>(null) }
    val showNoMovementsMessage = remember { mutableStateOf(false) }
    val context = LocalContext.current

    val refreshMovements: () -> Unit = {
        showNoMovementsMessage.value = false
        MovementState.value = null

        viewModel.getAllMovements(
            onSuccess = { m ->
                MovementState.value = m
            },
            onError = { error ->
                println("Error al obtener los movimientos: $error")
                MovementState.value = emptyList()
            }
        )
    }

    LaunchedEffect(Unit) {
        refreshMovements()

        launch {
            delay(5000)
            if (MovementState.value.isNullOrEmpty()) {
                showNoMovementsMessage.value = true
            }
        }
    }

    TicoWalletTheme {
        Box(modifier = Modifier.fillMaxSize()) { // Use Box to allow placing elements at specific alignments
            val scrollState = rememberScrollState()
            if ((MovementState.value == null && showNoMovementsMessage.value) || MovementState.value?.isEmpty() == true) {
                MovementNotFound()
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 8.dp, vertical = 8.dp),
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
                            MovementState.value?.forEach { movement ->
                                MovementItem(
                                    movement = movement,
                                    onEditClick = {
                                        navController.navigate("modificar_movimiento/${movement.id}")
                                    },
                                    onDeleteClick = { movementToDelete ->
                                        movementToDelete.id?.let {
                                            viewModel.deleteMovement(
                                                it,
                                                onSuccess = {
                                                    println("Movimiento eliminado exitosamente: ${movementToDelete.description}")
                                                    refreshMovements()
                                                    Toast.makeText(
                                                        context,
                                                        "Movimientos ${movementToDelete.description} eliminado exitosamente.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                },
                                                onError = { error ->
                                                    println("Error al eliminar el movimiento: $error")
                                                    Toast.makeText(
                                                        context,
                                                        "No se pudo eliminar el movimiento.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
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

            FloatingActionButton(
                onClick = { navController.navigate("crear_movimiento") },
                containerColor = colorTeal,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar cuenta"
                )
            }
        }
    }
}

@Composable
fun MovementItem(movement: MovementGet, onDeleteClick: (MovementGet) -> Unit, onEditClick: (MovementGet) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2639)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = movement.description,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = movement.type + " ",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = movement.location,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${movement.currency} ${movement.amount}",
                    color = Color.Red,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movement.date,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movement.state,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }


            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = Color.White,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 4.dp)
                        .clickable { onEditClick(movement) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.White,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onDeleteClick(movement) }
                )
            }
        }
    }
}

@Composable
fun MovementNotFound(modifier: Modifier = Modifier){
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorDarkBlue1),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.ListAlt,
                contentDescription = null,
                tint = colorWhite.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No hay movimientos.",
                color = colorWhite.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "¡Crea tu primer registro!",
                color = colorTeal,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}