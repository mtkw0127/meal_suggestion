import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    capturedImage: String? = null,
    onClickTakePhoto: () -> Unit = {},
    onClickSendPhoto: () -> Unit = {},
    onBackToTakePhoto: () -> Unit = {},
) {
    MaterialTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp).fillMaxSize()
        ) {
            if (capturedImage == null) {
                NotYetCaptured(onClickTakePhoto = onClickTakePhoto)
            } else {
                CapturedAndSentToGemini(
                    capturedImage = capturedImage,
                    onClickSendPhoto = onClickSendPhoto,
                    onBackToTakePhoto = onBackToTakePhoto,
                )
            }
        }
    }
}

@Composable
private fun NotYetCaptured(
    onClickTakePhoto: () -> Unit,
) {
    Text("写真から作れる料理を提案します")
    Spacer(Modifier.size(8.dp))
    Text("写真を送信してみましょう！")
    Spacer(Modifier.size(8.dp))
    Button(onClick = { onClickTakePhoto() }) {
        Text("撮影する")
    }
}

@Composable
private fun CapturedAndSentToGemini(
    capturedImage: String,
    onClickSendPhoto: () -> Unit,
    onBackToTakePhoto: () -> Unit,
) {
    AsyncImage(model = capturedImage, contentDescription = null)
    Spacer(Modifier.size(16.dp))
    Text("食材を撮影できましたか？")
    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = { onBackToTakePhoto() }) {
            Text("戻る")
        }
        Spacer(Modifier.size(16.dp))
        Button(onClick = { onClickSendPhoto() }) {
            Text("送信")
        }
    }
}