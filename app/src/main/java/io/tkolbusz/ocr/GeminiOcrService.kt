package io.tkolbusz.ocr

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.google.firebase.vertexai.FirebaseVertexAI
import com.google.firebase.vertexai.GenerativeModel
import com.google.firebase.vertexai.type.content
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer

class GeminiOcrService(
    private val model: GenerativeModel = FirebaseVertexAI.instance
        .generativeModel(modelName = "gemini-2.5-flash-lite")
) {

    suspend fun performOcr(image: ImageProxy): String = withContext(IO) {
        val bitmap = imageProxyToBitmap(image)

        val prompt = content {
            text("Extract all text from this image. You must return only the extracted text.")
            image(bitmap)
        }

        val response = model.generateContent(prompt)
        response.text ?: ""
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        return rotateBitmap(bitmap, image.imageInfo.rotationDegrees)
    }

    private fun rotateBitmap(bitmap: Bitmap, rotationDegrees: Int): Bitmap {
        if (rotationDegrees == 0) return bitmap

        val matrix = android.graphics.Matrix()
        matrix.postRotate(rotationDegrees.toFloat())

        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }
}
