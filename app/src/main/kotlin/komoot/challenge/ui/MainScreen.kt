package komoot.challenge.ui

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreenStateFull(
    modifier: Modifier = Modifier,
    vm: MainViewModel = koinViewModel()
) {
    val permissions = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    val isRunning by vm.isRunning.collectAsState()
    val photos by vm.photos.collectAsState()
    val actionState by vm.state.collectAsState()
    val permissionsState by vm.permissionsState.collectAsState()

    LaunchedEffect(key1 = permissionsState, key2 = permissions.status) {
        if (permissions.status.isGranted) {
            vm.permissionsState(LocationPermissionsState.Granted)
        } else if(permissions.status.isGranted.not() and permissions.status.shouldShowRationale) (
            vm.permissionsState(LocationPermissionsState.UserDenied)
        ) else if (permissionsState.isRequested()) {
            permissions.launchPermissionRequest()
        }
    }

    MissingLocationPermissionsRationaleDialog(
        isShow = permissionsState.isUserDenied(),
        onClose = vm::dismissRationale
    )

    MainScreen(
        modifier = modifier,
        photos = photos,
        isStarted = isRunning,
        toggleStartStop = vm::toggleStartStop
    )
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

sealed class ActionState {
    object Started: ActionState()
    object Running: ActionState()
    object Stopped: ActionState()
}

fun ActionState.isRunning() = this is ActionState.Running
fun ActionState.isStopped() = this is ActionState.Stopped
fun ActionState.isStarted() = this is ActionState.Started

sealed class LocationPermissionsState {
    object Unknown: LocationPermissionsState()
    object Missing: LocationPermissionsState()
    object Requested: LocationPermissionsState()
    object Granted: LocationPermissionsState()
    object UserDenied: LocationPermissionsState()
}

fun LocationPermissionsState.isGranted() = this is LocationPermissionsState.Granted
fun LocationPermissionsState.isUnknown() = this is LocationPermissionsState.Unknown
fun LocationPermissionsState.isMissing() = this is LocationPermissionsState.Missing
fun LocationPermissionsState.isRequested() = this is LocationPermissionsState.Requested
fun LocationPermissionsState.isUserDenied() = this is LocationPermissionsState.UserDenied

@KoinViewModel
class MainViewModel(
    private val trackWalkWithImages: TrackWalkWithImages
) : ViewModel() {

    val isRunning = trackWalkWithImages.isStarted.mapLatest(::actionState).asState(false)
    val photos = trackWalkWithImages.images.asState(emptyList())

    val state = MutableStateFlow<ActionState>(ActionState.Stopped)
    val _permissionsState = MutableStateFlow<LocationPermissionsState>(LocationPermissionsState.Unknown)
    val permissionsState: StateFlow<LocationPermissionsState> = _permissionsState.mapLatest {
        if (it.isGranted() and state.value.isStarted()) {
            toggleStartStop()
        }
        it
    }.asState(LocationPermissionsState.Unknown)

    fun toggleStartStop() {
       if (permissionsState.value.isGranted().not()) {
           state.value = ActionState.Started
           _permissionsState.value = LocationPermissionsState.Requested
           return
       }
        if ((state.value.isStarted() or state.value.isStopped())) {
            trackWalkWithImages.start()
        } else if (state.value.isRunning()) {
            trackWalkWithImages.stop()
        }
    }

    private fun actionState(isRunning: Boolean): Boolean {
        if (isRunning.not() and state.value.isRunning()) state.value = ActionState.Stopped
        return isRunning
    }

    fun permissionsState(state: LocationPermissionsState) {
        _permissionsState.value = state
    }

    fun dismissRationale() {
        _permissionsState.value = LocationPermissionsState.Missing
    }

    private fun <T> Flow<T>.asState(initial:T) =
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initial)

}
