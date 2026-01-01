package io.tkolbusz.ocr

import android.graphics.Bitmap
import androidx.camera.core.ImageInfo
import androidx.camera.core.ImageProxy
import com.google.firebase.vertexai.GenerativeModel
import com.google.firebase.vertexai.type.GenerateContentResponse
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import java.nio.ByteBuffer
import com.google.common.truth.Truth.assertThat
import com.google.firebase.vertexai.type.Content
import org.mockito.kotlin.anyVararg
import java.io.ByteArrayOutputStream


@RunWith(RobolectricTestRunner::class)
class GeminiOcrServiceTest {

    @Mock
    private lateinit var mockGenerativeModel: GenerativeModel

    @Mock
    private lateinit var mockImageProxy: ImageProxy

    @Mock
    private lateinit var mockImageInfo: ImageInfo

    private lateinit var geminiOcrService: GeminiOcrService

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        geminiOcrService = GeminiOcrService(mockGenerativeModel)
    }

    @Test
    fun performs_ocr_successfully_with_valid_image() = runTest {
        // given
        val expectedText = "Sample extracted text from image"
        val mockResponse = mock<GenerateContentResponse>()

        setupMockImageProxy(rotationDegrees = 0)

        whenever(mockResponse.text).thenReturn(expectedText)
        whenever(mockGenerativeModel.generateContent(anyVararg<Content>())).thenReturn(mockResponse)

        // when
        val result = geminiOcrService.performOcr(mockImageProxy)

        // then
        assertThat(result).isEqualTo(expectedText)
    }

    @Test
    fun returns_empty_string_when_response_text_is_null() = runTest {
        // given
        val mockResponse = mock<GenerateContentResponse>()

        setupMockImageProxy(rotationDegrees = 0)

        whenever(mockResponse.text).thenReturn(null)
        whenever(mockGenerativeModel.generateContent(anyVararg<Content>())).thenReturn(mockResponse)

        // when
        val result = geminiOcrService.performOcr(mockImageProxy)

        // then
        assertThat(result).isEqualTo("")
    }

    @Test
    fun handles_image_rotation_90_degrees() = runTest {
        // given
        val expectedText = "Rotated text"
        val mockResponse = mock<GenerateContentResponse>()

        setupMockImageProxy(rotationDegrees = 90)

        whenever(mockResponse.text).thenReturn(expectedText)
        whenever(mockGenerativeModel.generateContent(anyVararg<Content>())).thenReturn(mockResponse)
        // when
        val result = geminiOcrService.performOcr(mockImageProxy)

        // then
        assertThat(result).isEqualTo(expectedText)
    }


    private fun setupMockImageProxy(rotationDegrees: Int) {
        val bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val data = outputStream.toByteArray()

        val buffer = ByteBuffer.allocate(data.size)
        buffer.put(data)
        buffer.rewind()

        val plane = mock<ImageProxy.PlaneProxy>()
        whenever(plane.buffer).thenReturn(buffer)

        whenever(mockImageProxy.planes).thenReturn(arrayOf(plane))
        whenever(mockImageProxy.imageInfo).thenReturn(mockImageInfo)
        whenever(mockImageInfo.rotationDegrees).thenReturn(rotationDegrees)
    }
}
