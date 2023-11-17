package komoot.challenge.services

import android.location.Location
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import io.ktor.client.plugins.logging.*
import kotlinx.serialization.Serializable
import kotlin.random.Random

interface FlickrService {
    suspend fun get(request: Request): Response
    data class Request(val location: Location)
    sealed class Response {
        data class Success(val url:String): Response()
        data class Error(val reason: String): Response()
    }
}

@Single(binds=[FlickrService::class])
class FlickrApiServiceImpl(
    private val httpClient: HttpClient
) : FlickrService {

    private val flickrBaseUrl = "https://api.flickr.com/services/rest/"
    private val apiKey = "d5aedb1c5fb7959bb9654c1d6efec484"

    override suspend fun get(request: FlickrService.Request): FlickrService.Response {
        val (lat, lon) = with(request.location) { latitude to longitude}
        return try {
            val response = httpClient.get(flickrBaseUrl) {
                url {
                    parameters.append("method", "flickr.photos.search")
                    parameters.append("api_key", apiKey)
                    parameters.append("lat", lat.toString())
                    parameters.append("lon", lon.toString())
                    parameters.append("format", "json")
                    parameters.append("nojsoncallback", "1")
                    parameters.append("per_page", "100")
                }
            }.body<FlickrResponse>()
            val photoUrl = response.photos.photo[Random.nextInt(response.photos.photo.size)].toUrl()
            FlickrService.Response.Success(url = photoUrl)
        } catch (error: Throwable) {
            FlickrService.Response.Error(reason = error.toString())
        }
    }

    private fun Photo.toUrl() = "https://farm${farm}.staticflickr.com/${server}/${id}_${secret}.jpg"
}

@Single
fun httpClient(): HttpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    install(Logging) {
        logger = Logger.ANDROID
        level = LogLevel.ALL
    }
}

@Serializable
data class FlickrResponse(
    val photos: Photos
)

@Serializable
data class Photos(
    val photo: List<Photo>
)

@Serializable
data class Photo(
    val id: String,
    val secret: String,
    val server: String,
    val farm: Int,
)
