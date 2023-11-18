package komoot.challenge.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import komoot.challenge.logic.TrackWalkWithImages
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.koin.android.annotation.KoinViewModel

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
