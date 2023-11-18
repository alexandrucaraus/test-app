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

    private val walkImagesCache = HashSet<Pair<Location, String>>()
    private var lastLocation: Location? = null

    private val _isStarted = MutableStateFlow(false)
    override val isStarted: Flow<Boolean> = _isStarted

    override fun start() {
        _isStarted.value = true
    }

    override fun stop() {
        _isStarted.value = false
    }

    override val images: Flow<List<String>> = isStarted.flatMapLatest { isStarted ->
        if (isStarted) collectImages() else clearImages()
    }

    private fun clearImages(): Flow<List<String>> =
        flowOf(Unit)
            .mapLatest {
                walkImagesCache.clear()
                emptyList()
            }

    private fun collectImages(): Flow<List<String>> =
        flowOf(Unit)
            .flatMapLatest { locationService.stream() }
            .flatMapLatest (::checkLocationMoreThan100M)
            .mapLatest(::getImageUrl)
            .filterNotNull()
            .flatMapLatest(::updateCache)

    private fun checkLocationMoreThan100M(location: Location): Flow<Location> {
        val lastLocation = lastLocation
        return if (lastLocation == null) {
            this.lastLocation = location
            flowOf(location)
        } else if (location.distanceTo(lastLocation) >= 100L) {
            flowOf(location)
        } else emptyFlow()
    }

    private suspend fun getImageUrl(location: Location): Pair<Location, String>? {
        return when (val result = apiService.get(FlickrService.Request(location))) {
            is FlickrService.Response.Success -> location to result.url
            else -> null // TODO: handle errors, now silenced
        }
    }

    private fun updateCache(images: Pair<Location, String>): Flow<List<String>> =
        if (walkImagesCache.add(images)) {
            flowOf(walkImagesCache.sortedByDescending { it.first.time }.map { it.second })
        } else emptyFlow()

}
