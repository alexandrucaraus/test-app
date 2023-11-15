package komoot.challenge.logic

import android.location.Location
import komoot.challenge.services.FlickrService
import komoot.challenge.services.LocationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import org.koin.core.annotation.Factory

interface TrackWalkWithImages {
    val images: Flow<String>
    fun start()
    fun stop()
}

@Factory(binds = [TrackWalkWithImages::class])
class TrackWalkWithImagesImpl(
    private val apiService: FlickrService,
    private val locationService: LocationService
) : TrackWalkWithImages {

    override val images: Flow<String> = locationService
        .stream()
        .mapLatest(::getImageUrl)
        .filterNotNull()

    override fun start() {}

    override fun stop() {}

    private suspend fun getImageUrl(location: Location): String? =
        when(val result = apiService.get(FlickrService.Request(location))) {
            is FlickrService.Response.Success -> result.url
            else -> null
    }
}
