package komoot.challenge.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import komoot.challenge.logic.TrackWalkWithImages
import komoot.challenge.ui.components.TopBar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.koin.android.annotation.KoinViewModel
import org.koin.androidx.compose.koinViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import komoot.challenge.ui.components.MissingLocationPermissionsRationaleDialog
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreenStateFull(
    modifier: Modifier = Modifier,
    vm: MainViewModel = koinViewModel()
) {

    val permissions = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    val isRunning by vm.isRunning.collectAsState()
    val photos by vm.photos.collectAsState()

    var settings by remember { mutableStateOf(false) }
    var perms by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = permissions.status, key2 = perms) {
        if (permissions.status.isGranted and perms) {
            vm.toggle()
            perms = false
        } else if (permissions.status.isGranted.not() and permissions.status.shouldShowRationale) {
            permissions.launchPermissionRequest()
        } else if (permissions.status.isGranted.not() and permissions.status.shouldShowRationale.not()) {
            settings = true
        }
    }

    MissingLocationPermissionsRationaleDialog(
        isShow = settings,
        onClose = { settings = false }
    )

    MainScreen(
        modifier = modifier,
        photos = photos,
        isStarted = isRunning,
        toggleStartStop = vm::toggle
    )
}

@KoinViewModel
class MainViewModel(
    private val trackWalkWithImages: TrackWalkWithImages
) : ViewModel() {

    val isRunning = trackWalkWithImages.isStarted.asState(false)
    val photos = trackWalkWithImages.images.asState(emptyList())

    val permissionsState = MutableStateFlow<VmPermissions>(VmPermissions.Unknown)

    fun toggle() {
        val isRunning = isRunning.value
        if (isRunning) trackWalkWithImages.stop() else trackWalkWithImages.start()
    }

    private fun <T> Flow<T>.asState(initial: T) =
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initial)

}

sealed class VmPermissions {
    object Unknown: VmPermissions()
    object Granted: VmPermissions()
    object Requested: VmPermissions()
    object Missing: VmPermissions()
    object Denied: VmPermissions()
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    photos: List<String> = emptyList(),
    isStarted: Boolean = false,
    toggleStartStop: () -> Unit,
) {
    Column(modifier) {
        TopBar(isStarted = isStarted, onStartStop = toggleStartStop)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(photos) { pictureUrl ->
                Picture(url = pictureUrl)
            }
        }
    }
}

@Composable
fun Picture(
    modifier: Modifier = Modifier,
    url: String
) {
    AsyncImage(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),
        model = url,
        contentScale = ContentScale.Crop,
        contentDescription = "Location picture"
    )
}
