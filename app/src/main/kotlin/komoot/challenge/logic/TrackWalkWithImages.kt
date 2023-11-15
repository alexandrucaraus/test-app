package komoot.challenge.logic

import android.location.Location
import komoot.challenge.services.FlickrService
import komoot.challenge.services.LocationService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import org.koin.core.annotation.Factory

interface TrackWalkWithImages {
    val images: Flow<List<String>>
    val isStarted: Flow<Boolean>
    fun toggle()
}

@OptIn(ExperimentalCoroutinesApi::class)
@Factory(binds = [TrackWalkWithImages::class])
class TrackWalkWithImagesImpl(
    private val apiService: FlickrService,
    private val locationService: LocationService,
) : TrackWalkWithImages {

    private val walkImagesCache = HashSet<String>()

    private val _isStarted = MutableStateFlow(false)
    override val isStarted : StateFlow<Boolean> = _isStarted

    private val _images = MutableStateFlow(emptyList<String>())
    override val images: Flow<List<String>> = isStarted
        .flatMapLatest(::streamLocation)
        .mapLatest(::getImageUrl)
        .filterNotNull()
        .flatMapLatest(::updatedImages)

    override fun toggle() {
        _isStarted.value = isStarted.value.not()
    }

    private fun streamLocation(isStarted: Boolean): Flow<Location> =
        if (isStarted) locationService.stream() else emptyFlow()

    private suspend fun getImageUrl(location: Location): String? {
        return when (val result = apiService.get(FlickrService.Request(location))) {
            is FlickrService.Response.Success -> result.url
            else -> null
        }
    }

    private fun updatedImages(url:String): Flow<List<String>> =
        if (walkImagesCache.add(url)) flowOf(walkImagesCache.toList()) else emptyFlow()

    private fun clearImageCache(isStarted: Boolean) {
        if (isStarted.not()) walkImagesCache.clear()
    }

}
