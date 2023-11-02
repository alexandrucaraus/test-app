package onenone.coding

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import onenone.coding.db.Content
import onenone.coding.db.ContentDao
import onenone.coding.screen.Repository
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val mockEngine = MockEngine {request ->
           respond(content = ByteReadChannel("""{"ip":"127.0.0.1"}"""),
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "text/html")
        )}

        runBlocking {
            val mockClient = HttpClient(mockEngine)
            val repository = Repository(
                client = mockClient,
                contentDao = object : ContentDao {
                    val data = mutableListOf<Content>()
                    override fun loadAllByIds(contentId: Array<String>): List<Content> {
                        println("load")
                        return data
                    }

                    override fun insertAll(vararg contents: Content) {
                        println("insertAll")
                        data.addAll(contents)
                    }

                    override fun delete(content: Content) {
                        println("Delete $content")
                        data.remove(content)
                    }
                }
            )

            val content = repository()
            println("Content repository ${content.content}")
            assertTrue(content.content.equals("""{"ip":"127.0.0.1"}"""))
        }
    }
}