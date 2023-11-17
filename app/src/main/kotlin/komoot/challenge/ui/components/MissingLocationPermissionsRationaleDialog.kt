package komoot.challenge.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun MissingLocationPermissionsRationaleDialog(
    isShow: Boolean,
    onClose: () -> Unit = {},
    title: String = "Permissions Error",
    message: String = "You permanently denied permissions, set them back in settings ⬇️"
) {
    if (isShow) {
        val context = LocalContext.current
        AlertDialog(
            onDismissRequest = onClose,
            confirmButton = {
                Button(onClick = {
                    context.setPermissionManually()
                }) {
                    Text("Settings")
                }
            },
            dismissButton = {
                Button(onClick = onClose) {
                    Text("Dismiss")
                }
            },
            title = { Text(title) },
            text = { Text(message) }
        )
    }
}

private fun Context.setPermissionManually() {
    this.startActivity(
        Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.parse("package:$packageName")
        }
    )
}
