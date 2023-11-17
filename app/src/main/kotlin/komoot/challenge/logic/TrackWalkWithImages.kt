package komoot.challenge.logic

import android.location.Location
import komoot.challenge.services.FlickrService
import komoot.challenge.services.LocationService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import org.koin.core.annotation.Factory

interface TrackWalkWithImages {
    val images: Flow<List<String>>
    val isStarted: Flow<Boolean>
    fun start()
    fun stop()
}

@OptIn(ExperimentalCoroutinesApi::class)
@Factory(binds = [TrackWalkWithImages::class])
class TrackWalkWithImagesImpl(
    private val apiService: FlickrService,
    private val locationService: LocationService,
) : TrackWalkWithImages {

    private val walkImagesCache = HashSet<String>()

    private val _isStarted = MutableStateFlow(false)
    override val isStarted: Flow<Boolean> = _isStarted

    override fun start() {
        _isStarted.value = true
    }

    override fun stop() {
       _isStarted.value = false
    }

    override val images: Flow<List<String>> = isStarted.flatMapLatest(::images)


    private fun images(isStarted: Boolean): Flow<List<String>> =
        if (isStarted.not()) clearImages() else queryImages()

    private fun clearImages(): Flow<List<String>> =
        flowOf(Unit)
            .onEach { walkImagesCache.clear() }
            .mapLatest { walkImagesCache.toList() }

    private fun queryImages(): Flow<List<String>> =
        flowOf(Unit)
            .flatMapLatest { locationService.stream() }
            .mapLatest(::urlOfImageAtLocation)
            .filterNotNull()
            .flatMapLatest(::updatedImages)

    private suspend fun urlOfImageAtLocation(location: Location): String? {
        return when (val result = apiService.get(FlickrService.Request(location))) {
            is FlickrService.Response.Success -> result.url
            else -> null
        }
    }

    private fun updatedImages(url: String): Flow<List<String>> =
        if (walkImagesCache.add(url)) flowOf(walkImagesCache.toList()) else emptyFlow()

}
