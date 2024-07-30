package com.mtkw.meal_suggestion

import App
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize.FULL_BANNER
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val capturedImage = MutableStateFlow<String?>(null)
    private val sampleBitmap = MutableStateFlow<Bitmap?>(null)
    private val sampleMode = false

    private val viewModel = MainViewModel()

    private var rewardedAd: RewardedAd? = null

    private val firebaseAnalytics: FirebaseAnalytics
        get() = (application as MealSuggestionApplication).firebaseAnalytics

    private fun onTakePhotoLog() {
        firebaseAnalytics.logEvent(
            "on_take_photo",
            Bundle()
        )
    }

    private fun onClickAgain() {
        firebaseAnalytics.logEvent(
            "on_click_request_answer_again",
            Bundle()
        )
    }

    private fun onClickCopy() {
        firebaseAnalytics.logEvent(
            "on_click_copy",
            Bundle()
        )
    }

    private fun onRequestAnswer() {
        firebaseAnalytics.logEvent(
            "on_request_answer",
            Bundle()
        )
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                CameraActivity.obtainPhotoUrl(checkNotNull(result.data))?.let { uri ->
                    capturedImage.value = uri.path
                }
                assets.open("sample.jpg").use { inputStream ->
                    sampleBitmap.value = BitmapFactory.decodeStream(inputStream)
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
                isSample = sampleMode,
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
                            AdRequest.Builder().build()
                                .let { adView.loadAd(it) }
                        }
                        adView
                    })
                },
                onClickTakePhoto = {
                    onTakePhotoLog()
                    this@MainActivity.capturedImage.update { null }
                    viewModel.clearAll()
                    cameraLauncher.launch(CameraActivity.createIntent(this))
                },
                onClickSendPhoto = {
                    onRequestAnswer()
                    requestAdAndGetAnswer(
                        anotherMeal = false
                    )
                },
                onBackToTakePhoto = {
                    this@MainActivity.capturedImage.update { null }
                },
                onClickAgain = {
                    onClickAgain()
                    requestAdAndGetAnswer(
                        anotherMeal = true
                    )
                },
                updateAnswer = {
                    viewModel.updateAnswer(it)
                },
                onClickCopy = {
                    onClickCopy()
                }
            )
        }
    }

    private fun requestAdAndGetAnswer(
        anotherMeal: Boolean,
    ) {
        if (viewModel.isLoading) return
        val path = capturedImage.value ?: return
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            this,
            if (BuildConfig.DEBUG) {
                "ca-app-pub-3940256099942544/5224354917"
            } else {
                "ca-app-pub-2002859886618281/7070172044"
            },
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    ad.show(this@MainActivity) {}
                    val bitmap = if (sampleMode.not()) {
                        BitmapFactory.decodeFile(path)
                    } else {
                        sampleBitmap.value
                    }
                    viewModel.requestAnswerToAi(checkNotNull(bitmap), anotherMeal)
                }
            }
        )
    }
}