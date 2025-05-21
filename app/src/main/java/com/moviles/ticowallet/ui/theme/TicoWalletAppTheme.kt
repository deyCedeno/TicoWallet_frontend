package com.moviles.ticowallet.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = colorDarkBlue1,
    secondary = colorTeal,
    tertiary = colorLightBlue,
    background = colorDarkBlue1,
    surface = colorDarkBlue2,
    onPrimary = colorWhite,
    onSecondary = colorWhite,
    onTertiary = colorDarkBlue1,
    onBackground = colorWhite,
    onSurface = colorWhite,
    primaryContainer = colorDarkBlue1,
    onPrimaryContainer = colorWhite
)

private val LightColorScheme = lightColorScheme(
    primary = colorDarkBlue1,
    secondary = colorTeal,
    tertiary = colorLightBlue,
    background = colorWhite,
    surface = colorWhite,
    onPrimary = colorWhite,
    onSecondary = colorWhite,
    onTertiary = colorDarkBlue1,
    onBackground = colorDarkBlue1,
    onSurface = colorDarkBlue1,
    primaryContainer = colorDarkBlue1,
    onPrimaryContainer = colorWhite
)

@Composable
fun TicoWalletAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorDarkBlue1.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}