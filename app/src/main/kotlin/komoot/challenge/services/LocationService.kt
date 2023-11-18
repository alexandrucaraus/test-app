package komoot.challenge.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.annotation.Single

interface LocationService {
    fun stream(params: LocationRequestParams = LocationRequestParams()): Flow<Location>
    data class LocationRequestParams(
        val timeIntervalMillis: Long = 1_000,
        val minUpdateDistanceMeters: Float = 100f,
        val accurateLocation: Boolean = true,
    )
}

@Single(binds = [LocationService::class])
class AndroidLocationService(
    private val context: Context
) : LocationService {

    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private fun hasRequiredPermissions() =
        context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED

    @RequiresPermission(
        allOf = [
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_FINE_LOCATION"
        ])
    override fun stream(
        params: LocationService.LocationRequestParams
    ): Flow<Location> = callbackFlow {

        if (hasRequiredPermissions().not()) close()

        val locationRequest = LocationRequest
            .Builder(params.timeIntervalMillis)
            .setMinUpdateDistanceMeters(params.minUpdateDistanceMeters)
            .setWaitForAccurateLocation(params.accurateLocation)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.forEach { trySend(it) }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )

        awaitClose {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

}
