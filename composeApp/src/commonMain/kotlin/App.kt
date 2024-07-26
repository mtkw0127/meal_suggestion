import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun App(
    answer: String,
    capturedImage: String? = null,
    createBanner: @Composable () -> Unit,
    onClickTakePhoto: () -> Unit = {},
    onClickSendPhoto: () -> Unit = {},
    onBackToTakePhoto: () -> Unit = {},
    onClickAgain: () -> Unit = {},
    updateAnswer: (String) -> Unit,
) {
    MaterialTheme {
        Scaffold(
            bottomBar = {
                Column {
                    createBanner()
                    Row(
                        modifier = Modifier
                            .background(Color.White)
                            .padding(10.dp)
                    ) {
                        if (answer.isNotBlank()) {
                            CustomButton(
                                text = "再撮影",
                                onClick = onClickTakePhoto,
                            )
                            if (answer.contains("写っていません。").not()) {
                                Spacer(Modifier.size(2.dp))
                                CustomButton(
                                    text = "別の料理を要求",
                                    onClick = onClickAgain,
                                )
                            }
                        } else if (capturedImage == null) {
                            CustomButton(
                                text = "写真を撮る",
                                onClick = onClickTakePhoto,
                            )
                        } else if (answer.isBlank()) {
                            CustomButton(
                                text = "戻る",
                                onClick = onBackToTakePhoto,
                            )
                            Spacer(Modifier.size(2.dp))
                            CustomButton(
                                text = "送信",
                                onClick = onClickSendPhoto,
                            )
                        }
                    }
                }
            }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .background(
                        Color(0xFF3FB5FF).copy(0.2F)
                    )
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
            ) {
                if (capturedImage == null) {
                    NotYetCaptured()
                } else if (answer.isNotBlank()) {
                    Answer(
                        answer = answer,
                        imagePath = capturedImage,
                        updateAnswer = updateAnswer,
                    )
                } else {
                    CapturedAndSentToGemini(
                        capturedImage = capturedImage,
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.CustomButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    Button(
        shape = RoundedCornerShape(0.dp),
        onClick = onClick,
        modifier = modifier.weight(1F),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFF0000FF),
            contentColor = Color.White,
        ),
    ) {
        Text(text)
    }
}

@Composable
private fun Answer(
    answer: String,
    imagePath: String,
    updateAnswer: (String) -> Unit = {},
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(8.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Box(
            modifier = Modifier.background(Color.White).padding(10.dp)
        ) {
            BasicTextField(
                value = answer,
                onValueChange = {
                    updateAnswer(it)
                },
            )
        }
        Spacer(Modifier.size(16.dp))
        AsyncImage(
            model = imagePath,
            contentDescription = null,
        )
    }
}

@Composable
private fun NotYetCaptured() {
    Text(
        text = "写真を送信して\nAIに料理を考えてもらいましょう！",
        fontWeight = FontWeight.W600,
        textAlign = TextAlign.Center,
    )
    Spacer(Modifier.size(8.dp))
}

@Composable
private fun CapturedAndSentToGemini(
    capturedImage: String,
) {
    AsyncImage(model = capturedImage, contentDescription = null)
    Spacer(Modifier.size(16.dp))
    Text(
        text = "食材を撮影できましたか？",
        fontWeight = FontWeight.W600,
    )
}