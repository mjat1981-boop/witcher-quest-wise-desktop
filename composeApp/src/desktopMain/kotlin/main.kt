import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.example.MainScreen
import com.example.ui.QuestViewModel
import com.example.ui.theme.MyApplicationTheme
import org.jetbrains.compose.resources.painterResource
import witcher_quest_wise_desktop.composeapp.generated.resources.Res
import witcher_quest_wise_desktop.composeapp.generated.resources.wolf_app_icon

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "The Wild Hunt",
        icon = painterResource(Res.drawable.wolf_app_icon),
        state = rememberWindowState(width = 1200.dp, height = 800.dp)
    ) {
        val viewModel = remember { QuestViewModel.create() }
        MyApplicationTheme {
            MainScreen(viewModel)
        }
    }
}
