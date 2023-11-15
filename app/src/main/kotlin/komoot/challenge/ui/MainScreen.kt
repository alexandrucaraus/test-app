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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.annotation.KoinViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreenStateFull(
    modifier: Modifier = Modifier,
    vm: MainViewModel = koinViewModel()
) {
    val photos by vm.photos.collectAsState()
    val isStarted by vm.isStarted.collectAsState()
    MainScreen(
        modifier = modifier,
        photos = photos,
        isStarted = isStarted,
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
            modifier = Modifier.fillMaxSize().padding(8.dp),
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

@KoinViewModel
class MainViewModel(
    private val trackWalkWithImages: TrackWalkWithImages
) : ViewModel() {
    val photos = MutableStateFlow<List<String>>(emptyList())
    init {
        trackWalkWithImages
            .images
            .onEach { newImage -> photos.value = photos.value + newImage }
            .launchIn(viewModelScope)
    }

    val isStarted = MutableStateFlow(false)
    fun toggleStartStop() {}
}