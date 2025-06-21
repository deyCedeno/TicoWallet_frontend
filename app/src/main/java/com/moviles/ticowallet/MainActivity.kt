package com.moviles.ticowallet

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.moviles.ticowallet.ui.main.MainAppScaffold
import com.moviles.ticowallet.ui.theme.TicoWalletTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TicoWalletTheme {
                val context = LocalContext.current
                MainAppScaffold(
                    onNotificationsClick = {
                        Toast.makeText(context, "Notificaciones presionadas", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TicoWalletTheme {
        MainAppScaffold(onNotificationsClick = {})
    }
}