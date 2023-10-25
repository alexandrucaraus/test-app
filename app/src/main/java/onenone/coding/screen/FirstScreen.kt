package onenone.coding.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.annotation.KoinViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun FirstScreen(
    vm: FirstViewModel = koinViewModel()
) {
    val count = vm.count.collectAsState().value
    Text(
        modifier = Modifier.fillMaxWidth().padding(top= 40.dp),
        textAlign = TextAlign.Center,
        text = "First Screen count = $count"
    )
}

@KoinViewModel
class FirstViewModel : ViewModel() {
    val count = MutableStateFlow(0)
    init {
        count.value = count.value + 1
    }
}