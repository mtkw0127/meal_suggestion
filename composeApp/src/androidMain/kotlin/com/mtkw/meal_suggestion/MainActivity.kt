package com.mtkw.meal_suggestion

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MainActivity : ComponentActivity() {

    private val capturedImage = MutableStateFlow<String?>(null)

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                CameraActivity.obtainPhotoUrl(checkNotNull(result.data))?.let { uri ->
                    capturedImage.value = uri.path
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val capturedImage by capturedImage.collectAsState()
            App(
                capturedImage = capturedImage,
                onClickTakePhoto = {
                    cameraLauncher.launch(CameraActivity.createIntent(this))
                },
                onClickSendPhoto = {

                },
                onBackToTakePhoto = {
                    this@MainActivity.capturedImage.update { null }
                }
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}