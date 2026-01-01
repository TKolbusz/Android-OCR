package io.tkolbusz.ocr

import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager.*
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import io.tkolbusz.ocr.OcrResultBottomSheet.Companion.OCR_BOTTOM_SHEET_TAG
import io.tkolbusz.ocr.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors.newSingleThreadExecutor

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var geminiOcrService: GeminiOcrService

    private val requestPermissionLauncher = registerForActivityResult(
        RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(this, R.string.error_camera_permission, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        geminiOcrService = GeminiOcrService()
        cameraExecutor = newSingleThreadExecutor()

        if (hasCameraPermission()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(CAMERA)
        }

        binding.captureButton.setOnClickListener { capturePhoto() }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, CAMERA) == PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.surfaceProvider = binding.previewView.surfaceProvider
                }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageCapture
            )
        }, ContextCompat.getMainExecutor(this))
    }

    private fun capturePhoto() {
        val imageCapture = imageCapture ?: return
        binding.progressBar.visibility = VISIBLE
        binding.captureButton.isEnabled = false

        imageCapture.takePicture(
            cameraExecutor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    lifecycleScope.launch {
                        try {
                            val result = geminiOcrService.performOcr(image)
                            runOnUiThread {
                                binding.progressBar.visibility = View.GONE
                                binding.captureButton.isEnabled = true
                                showResult(result)
                            }
                        } catch (e: Exception) {
                            runOnUiThread {
                                binding.progressBar.visibility = View.GONE
                                binding.captureButton.isEnabled = true
                                Toast.makeText(this@MainActivity, getString(R.string.error_ocr_failed) + ": ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        } finally {
                            image.close()
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.captureButton.isEnabled = true
                        Toast.makeText(this@MainActivity, "Capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    private fun showResult(text: String) {
        val bottomSheet = OcrResultBottomSheet.newInstance(text)
        bottomSheet.show(supportFragmentManager, OCR_BOTTOM_SHEET_TAG)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
