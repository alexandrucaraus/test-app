package onenone.coding.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.SystemUiController

fun Modifier.customSystemBarsInsets() = this.padding(top = 24.dp, bottom = 48.dp).fillMaxSize()

fun Modifier.customStatusBarInsets() = this.padding(top = 24.dp).fillMaxSize()

val localSystemUiControllerProvider = compositionLocalOf<SystemUiController?>{ null }

@Composable
fun statusBar(darkText: Boolean = false) {
    localSystemUiControllerProvider.current?.statusBarDarkContentEnabled = darkText
}