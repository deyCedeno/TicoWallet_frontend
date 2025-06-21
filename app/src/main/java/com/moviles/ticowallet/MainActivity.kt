package com.moviles.ticowallet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.moviles.ticowallet.models.User
import android.util.Log
import com.moviles.ticowallet.common.Constants
import com.moviles.ticowallet.ui.main.MainAppScaffold
import com.moviles.ticowallet.ui.theme.TicoWalletTheme
import dagger.hilt.android.AndroidEntryPoint

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val user = intent.getSerializableExtra("user") as? User
        if (user != null) {
            Constants.USERNAME = user.name
            Log.i("MainActivity", "Usuario logueado: ${user.name} - ${user.email}")
        } else {
            Log.e("MainActivity", "No se recibió usuario, redirigiendo al Login")

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

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