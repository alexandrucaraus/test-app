package komoot.challenge.services

import android.location.Location
import org.koin.core.annotation.Single
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
class FlickrApiServiceImpl : FlickrService {

    private val dummyData = listOf(
        "https://fastly.picsum.photos/id/730/200/300.jpg?hmac=Xa_29B3ZIok8lz4JGLZUBU_ARxJew0twrMWMHEy_T1I",
        "https://fastly.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI",
        "https://fastly.picsum.photos/id/478/200/300.jpg?blur=2&hmac=os6ROUycy49LorQt1pWWt8fiaAfcsZsDj_7Yfwy8X7k",
    )

    override suspend fun get(request: FlickrService.Request): FlickrService.Response {
        return FlickrService.Response.Success(dummyData[Random.nextInt(dummyData.size)])
    }

}
