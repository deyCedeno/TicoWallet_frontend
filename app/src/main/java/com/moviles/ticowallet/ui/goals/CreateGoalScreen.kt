package com.moviles.ticowallet.ui.goals

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.moviles.ticowallet.ui.theme.TicoWalletTheme
import java.util.*

private val screenBgColor = Color(0xFF27496d)
private val appBarColor = Color(0xFF0A3B4C)
private val onAppBarColor = Color.White

private val textFieldContainerColor = Color(0xFF122850)
private val textFieldTextColor = Color.White
private val textFieldLabelColor = Color.White.copy(alpha = 0.7f)
private val textFieldCursorColor = Color.White

private val iconSelectorTextColor = Color.White
private val selectedIconColor = Color.White

private val buttonBackgroundColor = Color(0xFF0c7b93)
private val onButtonBackgroundColor = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalScreen(
    navController: NavController,
    paddingValues: PaddingValues
) {
    var nameGoal by remember { mutableStateOf("") }
    var quantityGoal by remember { mutableStateOf("") }
    var currentQuantity by remember { mutableStateOf("") }
    var goalDate by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf<ImageVector>(Icons.Filled.Flag) }

    var showIconPickerDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            goalDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
        }, year, month, day
    )

    if (showIconPickerDialog) {
        IconPickerDialog(
            onDismissRequest = { showIconPickerDialog = false },
            onIconSelected = { icon ->
                selectedIcon = icon
                showIconPickerDialog = false
            }
        )
    }

    TicoWalletTheme(darkTheme = true) {
        Scaffold(
            containerColor = screenBgColor,
            /*
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Nuevo objetivo",
                            color = onAppBarColor,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = "Volver atrás",
                                tint = onAppBarColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = appBarColor,
                    ),
                    modifier = Modifier.height(64.dp)
                )
            }
            */
        ) { innerPaddingScaffoldCrear ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPaddingScaffoldCrear)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                val textFieldShape = RoundedCornerShape(12.dp)
                val customTextFieldColors = TextFieldDefaults.colors(
                    focusedTextColor = textFieldTextColor,
                    unfocusedTextColor = textFieldTextColor,
                    disabledTextColor = textFieldTextColor.copy(alpha = 0.7f),
                    focusedContainerColor = textFieldContainerColor,
                    unfocusedContainerColor = textFieldContainerColor,
                    disabledContainerColor = textFieldContainerColor.copy(alpha = 0.7f),
                    cursorColor = textFieldCursorColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedLabelColor = textFieldLabelColor,
                    unfocusedLabelColor = textFieldLabelColor,
                    disabledLabelColor = textFieldLabelColor.copy(alpha = 0.7f),
                    focusedLeadingIconColor = textFieldLabelColor,
                    unfocusedLeadingIconColor = textFieldLabelColor,
                    disabledLeadingIconColor = textFieldLabelColor.copy(alpha = 0.7f),
                    focusedTrailingIconColor = textFieldLabelColor,
                    unfocusedTrailingIconColor = textFieldLabelColor,
                    disabledTrailingIconColor = textFieldLabelColor.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Column {
                    Text(
                        text = "Nombre del objetivo",
                        color = textFieldLabelColor,
                        modifier = Modifier.padding(bottom = 6.dp),
                        fontSize = 12.sp
                    )
                    TextField(
                        value = nameGoal,
                        onValueChange = { nameGoal = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        colors = customTextFieldColors,
                        shape = textFieldShape
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Column {
                    Text(
                        text = "Monto del objetivo",
                        color = textFieldLabelColor,
                        modifier = Modifier.padding(bottom = 6.dp),
                        fontSize = 12.sp
                    )
                    TextField(
                        value = quantityGoal,
                        onValueChange = { quantityGoal = it },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        colors = customTextFieldColors,
                        shape = textFieldShape,
                        leadingIcon = { Text("₡", color = textFieldLabelColor, fontSize = 18.sp, fontWeight = FontWeight.Normal) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column {
                    Text(
                        text = "Saldo actual",
                        color = textFieldLabelColor,
                        modifier = Modifier.padding(bottom = 6.dp),
                        fontSize = 12.sp
                    )
                    TextField(
                        value = currentQuantity,
                        onValueChange = { currentQuantity = it },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        colors = customTextFieldColors,
                        shape = textFieldShape,
                        leadingIcon = { Text("₡", color = textFieldLabelColor, fontSize = 18.sp, fontWeight = FontWeight.Normal) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column {
                    Text(
                        text = "Notas",
                        color = textFieldLabelColor,
                        modifier = Modifier.padding(bottom = 6.dp),
                        fontSize = 12.sp
                    )
                    TextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Opcional") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 100.dp),
                        colors = customTextFieldColors,
                        shape = textFieldShape,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Fecha                                             Ícono",
                        color = textFieldLabelColor,
                        modifier = Modifier.padding(bottom = 6.dp),
                        fontSize = 12.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(textFieldShape)
                                .background(textFieldContainerColor)
                                .clickable { datePickerDialog.show() }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (goalDate.isEmpty()) "dd/mm/aaaa" else goalDate,
                                    color = if (goalDate.isEmpty()) textFieldLabelColor.copy(alpha = 0.7f)
                                    else textFieldTextColor,
                                    fontSize = 16.sp
                                )
                                Icon(
                                    Icons.Filled.DateRange,
                                    contentDescription = "Select date",
                                    tint = textFieldLabelColor
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(textFieldShape)
                                .background(textFieldContainerColor)
                                .clickable { showIconPickerDialog = true }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Elige",
                                    color = iconSelectorTextColor,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal
                                )
                                Icon(
                                    imageVector = selectedIcon,
                                    contentDescription = "Selected icon",
                                    tint = selectedIconColor,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonBackgroundColor,
                        contentColor = onButtonBackgroundColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Crear Objetivo", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconPickerDialog(
    onDismissRequest: () -> Unit,
    onIconSelected: (ImageVector) -> Unit
) {
    val icons = listOf(
        Icons.Filled.Home, Icons.Filled.ShoppingCart, Icons.Filled.Star, Icons.Filled.Favorite,
        Icons.Filled.AccountBalanceWallet, Icons.Filled.CardGiftcard, Icons.Filled.Build, Icons.Filled.Flight,
        Icons.Filled.School, Icons.Filled.Savings, Icons.Filled.Lightbulb, Icons.Filled.Pets,
        Icons.Filled.DirectionsCar, Icons.Filled.PhoneAndroid, Icons.Filled.Computer, Icons.Filled.Book,
        Icons.Filled.Fastfood, Icons.Filled.FitnessCenter, Icons.Filled.MusicNote, Icons.Filled.LocalHospital
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Seleccionar Icono", color = Color.Black) },
        containerColor = Color.White,
        textContentColor = Color.DarkGray,
        shape = RoundedCornerShape(16.dp),
        text = {
            LazyVerticalGrid(
                columns = androidx.compose.foundation.lazy.grid.GridCells.Adaptive(minSize = 60.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp, start = 8.dp, end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(icons.size) { index ->
                    val icon = icons[index]
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray.copy(alpha = 0.2f))
                            .clickable { onIconSelected(icon) }
                            .padding(12.dp)
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = icon.name,
                            tint = Color.DarkGray,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar", color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}


@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
fun CreateGoalScreenPreview_Refined() {
    CreateGoalScreen(navController = rememberNavController(), paddingValues = PaddingValues(0.dp))
}

@Preview(showBackground = true)
@Composable
fun IconPickerDialogPreview() {
    TicoWalletTheme {
        IconPickerDialog(onDismissRequest = {}, onIconSelected = {})
    }
}