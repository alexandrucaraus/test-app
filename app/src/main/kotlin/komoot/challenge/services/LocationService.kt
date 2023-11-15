package komoot.challenge.services

import android.location.Location
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import org.koin.core.annotation.Single
import kotlin.coroutines.coroutineContext
import kotlin.random.Random

interface LocationService {
    fun stream(): Flow<Location>
}

@Single(binds = [LocationService::class])
class AndroidLocationService : LocationService {

    override fun stream(): Flow<Location> = flow {
        while (coroutineContext.isActive) {
            delay(3000)
            emit(Location("synthetic").apply {
                latitude = Random.nextDouble(200.0)
                longitude = Random.nextDouble(200.0)
            })
        }
    }

}