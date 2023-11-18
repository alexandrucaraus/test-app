package komoot.challenge.ui.components

import android.app.Activity
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
fun PermissionDeniedDialog(
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
                    context.openAppSettings()
                    onClose()
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

private fun Context.openAppSettings() =
    Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", packageName, null)
    }.let(::startActivity)

fun Context.shouldShowPermissionsDeniedDialog(
    permission: String = android.Manifest.permission.ACCESS_FINE_LOCATION,
): Boolean {
    return !((this as? Activity)?.shouldShowRequestPermissionRationale(permission) ?: false)
}