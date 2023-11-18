package komoot.challenge.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun Permissions(
    onRequested: () -> Unit,
    onGranted: () -> Unit
) {
    val localContext = LocalContext.current
    var permissionDeniedDialog by remember { mutableStateOf(false) }



    val permissions = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { hasPermissions ->
            if (hasPermissions) {
                onGranted()
            } else if (localContext.shouldShowPermissionsDeniedDialog()) {
                permissionDeniedDialog = true
            }
        }
    )

    PermissionDeniedDialog(
        isShow = permissionDeniedDialog,
        onClose = { permissionDeniedDialog = false }
    )

}