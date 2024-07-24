package com.mtkw.meal_suggestion

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OutputFileOptions
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.os.BundleCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import java.text.SimpleDateFormat
import java.util.Locale

class CameraActivity : ComponentActivity() {
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var camera: Camera
    private lateinit var imageCapture: ImageCapture
    private lateinit var previewView: PreviewView

    // 権限をリクエストしカメラを起動する
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGrant ->
            if (isGrant) {
                startCamera()
            } else {
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        setContent {
            Box {
                CameraPreview()
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Button(
                        shape = RoundedCornerShape(0.dp),
                        onClick = { takePhoto() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF0000FF),
                            contentColor = Color.White,
                        ),
                    ) {
                        Text(text = "撮影")
                    }
                }
            }
        }
        cameraLauncher.launch(android.Manifest.permission.CAMERA)
    }

    private fun takePhoto() {
        imageCapture.takePicture(
            createFileOutputOptions(this),
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    outputFileResults.savedUri?.let { uri: Uri ->
                        setResult(RESULT_OK, Intent().apply {
                            putExtra(RESULT_PHOTO_URL, uri)
                        })
                        finish()
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    setResult(RESULT_CANCELED)
                    finish()
                }
            }
        )
    }

    private fun startCamera() {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview: Preview = Preview.Builder().build()
            val cameraSelector: CameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
            preview.setSurfaceProvider(previewView.getSurfaceProvider())
            imageCapture = ImageCapture.Builder().build()

            camera = cameraProvider.bindToLifecycle(
                this as LifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        }, ContextCompat.getMainExecutor(this))
    }

    private fun createFileOutputOptions(context: Context): OutputFileOptions {
        val fileName = SimpleDateFormat(
            FILE_FORMAT,
            Locale.JAPAN
        ).format(System.currentTimeMillis()) + PHOTO_EXTENSION
        val path = context.filesDir.toPath().resolve(fileName)
        return OutputFileOptions
            .Builder(path.toFile())
            .build()
    }

    @Composable
    private fun CameraPreview() {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                PreviewView(context).apply {
                    previewView = this
                }
            }
        )
    }

    companion object {
        private const val PHOTO_EXTENSION = ".jpg"
        private const val FILE_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSSS"
        private const val RESULT_PHOTO_URL = "RESULT_PHOTO_URL"

        fun obtainPhotoUrl(intent: Intent): Uri? {
            return BundleCompat.getParcelable(
                intent.extras ?: return null,
                RESULT_PHOTO_URL,
                Uri::class.java
            )
        }

        fun createIntent(context: Context): Intent {
            return Intent(context, CameraActivity::class.java)
        }
    }
}