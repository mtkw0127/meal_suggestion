package com.mtkw.meal_suggestion

import App
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdSize.FULL_BANNER
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val capturedImage = MutableStateFlow<String?>(null)

    private val viewModel = MainViewModel()

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
            val answer by viewModel.answer.collectAsState()
            val scope = rememberCoroutineScope()
            App(
                answer = answer,
                capturedImage = capturedImage,
                createBanner = {
                    AndroidView(factory = { context ->
                        val adView = AdView(context).apply {
                            adUnitId = if (BuildConfig.DEBUG) {
                                "ca-app-pub-3940256099942544/9214589741"
                            } else {
                                "ca-app-pub-2002859886618281/4131748591"
                            }
                            setAdSize(FULL_BANNER)
                        }
                        scope.launch {
                            com.google.android.gms.ads.AdRequest.Builder().build()
                                .let { adView.loadAd(it) }
                        }
                        adView
                    })
                },
                onClickTakePhoto = {
                    cameraLauncher.launch(CameraActivity.createIntent(this))
                },
                onClickSendPhoto = {
                    val bitmap = BitmapFactory.decodeFile(capturedImage)
                    viewModel.requestAnswerToAi(bitmap)
                },
                onBackToTakePhoto = {
                    this@MainActivity.capturedImage.update { null }
                },
                onClickAgain = {
                    val bitmap = BitmapFactory.decodeFile(capturedImage)
                    viewModel.requestAnswerToAi(bitmap)
                }
            )
        }
    }
}