package komoot.challenge.services

import android.location.Location
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

interface FlickrService {
    suspend fun get(request: Request): Response
    data class Request(val location: Location)
    sealed class Response {
        data class Success(val url:String): Response()
        data class Error(val reason: String): Response()
    }
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
}

@Single(binds=[FlickrService::class])
class FlickrApiServiceImpl(
    private val httpClient: HttpClient
) : FlickrService {

    private val flickrBaseUrl = "https://api.flickr.com/services/rest/"
    private val method = "flickr.photos.search"
    private val format = "json"
    private val noJsonCallback = "1"
    private val perPage = 10
    private val apiKey = "d5aedb1c5fb7959bb9654c1d6efec484"
    private val secret = "01c0bc292eedd409"

    private val dummyData = listOf(
        "https://fastly.picsum.photos/id/730/200/300.jpg?hmac=Xa_29B3ZIok8lz4JGLZUBU_ARxJew0twrMWMHEy_T1I",
        "https://fastly.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI",
        "https://fastly.picsum.photos/id/478/200/300.jpg?blur=2&hmac=os6ROUycy49LorQt1pWWt8fiaAfcsZsDj_7Yfwy8X7k",
    )

    override suspend fun get(request: FlickrService.Request): FlickrService.Response {
        val (lat, lon) = with(request.location) { latitude to longitude}
        val apiUrl = URLBuilder(flickrBaseUrl).apply {
            protocol = URLProtocol.HTTPS
            encodedPath = "/rest"
            parameters {
                append("method", method)
                append("api_key", apiKey)
                append("lat", lat.toString())
                append("lon", lon.toString())
                append("format", format)
                append("nojsoncallback", noJsonCallback)
                append("per_page", perPage.toString())
            }
        }.buildString()
        val httpResponse =  httpClient.get(apiUrl)
        val response: FlickrPhotosResponse = httpResponse.body()
        println("response $response")
        val photo = response.photos.firstOrNull()?.url ?: "none"
        return FlickrService.Response.Success(url = photo)
    }

}

data class FlickrPhoto(val id: String, val title: String, val url: String)
data class FlickrPhotosResponse(val photos: List<FlickrPhoto>)