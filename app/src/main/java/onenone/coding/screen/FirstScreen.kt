package onenone.coding.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import onenone.coding.db.Content
import onenone.coding.db.ContentDao
import org.koin.android.annotation.KoinViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import java.util.UUID

@Composable
fun FirstScreen(
    vm: FirstViewModel = koinViewModel()
) {
    val count = vm.count.collectAsState().value
    val data = vm.data.collectAsState().value
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        textAlign = TextAlign.Center,
        text = "First Screen count = $count"
    )
    Spacer(modifier = Modifier.padding(vertical = 24.dp))
    Text(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        text = data
    )
}

@KoinViewModel
class FirstViewModel(
    val contentRepo: Repository
) : ViewModel() {
    val count = MutableStateFlow(1)
    val data = MutableStateFlow("")
    init {
        viewModelScope.launch(Dispatchers.IO) {
            data.value = contentRepo().content ?: ""
        }
    }
}

@Factory
class Repository(
    private val client: HttpClient,
    private val contentDao: ContentDao
) {
    suspend operator fun invoke(): Content {
        val contentRaw = client.get("https://ktor.io/").body<String>()
        val content = Content(UUID.randomUUID().toString(), contentRaw)
        contentDao.insertAll(contents = arrayOf(content))
        return contentDao.loadAllByIds(arrayOf(content.uid)).first()
    }
}

