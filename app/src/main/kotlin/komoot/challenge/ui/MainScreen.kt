package komoot.challenge.ui

import android.app.Activity
import android.content.Context
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import komoot.challenge.logic.TrackWalkWithImages
import komoot.challenge.ui.components.PermissionDeniedDialog
import komoot.challenge.ui.components.TopBar
import komoot.challenge.ui.components.shouldShowPermissionsDeniedDialog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.koin.android.annotation.KoinViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreenStateFull(
    modifier: Modifier = Modifier,
    vm: MainViewModel = koinViewModel()
) {

    val isRunning by vm.isRunning.collectAsState()
    val photos by vm.photos.collectAsState()

    val localContext = LocalContext.current
    var permissionDeniedDialog by remember { mutableStateOf(false) }

    val permissions = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { hasPermissions ->
            if (hasPermissions) {
                vm.toggle()
            } else if (localContext.shouldShowPermissionsDeniedDialog()) {
                permissionDeniedDialog = true
            }
        }
    )

    PermissionDeniedDialog(
        isShow = permissionDeniedDialog,
        onClose = { permissionDeniedDialog = false }
    )

    MainScreen(
        modifier = modifier,
        photos = photos,
        isStarted = isRunning,
        toggleStartStop = {
            permissions.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        },
    )
}

@KoinViewModel
class MainViewModel(
    private val trackWalkWithImages: TrackWalkWithImages
) : ViewModel() {

    val isRunning = trackWalkWithImages.isStarted.asState(false)
    val photos = trackWalkWithImages.images.asState(emptyList())

    fun toggle() {
        if (isRunning.value) trackWalkWithImages.stop() else trackWalkWithImages.start()
    }

    private fun <T> Flow<T>.asState(initial: T) =
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initial)

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
