package io.tkolbusz.ocr

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OcrResultBottomSheetTest {

    private lateinit var scenario: FragmentScenario<OcrResultBottomSheet>
    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @After
    fun teardown() {
        if (::scenario.isInitialized) {
            scenario.close()
        }
    }

    @Test
    fun displays_result_text_and_buttons() {
        val testText = "Package ID 123577"
        scenario = launchBottomSheetWithText(testText)

        onView(withId(R.id.resultTextView))
            .check(matches(isDisplayed()))
            .check(matches(withText(testText)))

        onView(withId(R.id.copyButton))
            .check(matches(isDisplayed()))

        onView(withId(R.id.closeButton))
            .check(matches(isDisplayed()))
    }

    @Test
    fun copy_button_copies_text_to_clipboard() {
        // given
        val testText = "test"
        scenario = launchBottomSheetWithText(testText)

        // when
        onView(withId(R.id.copyButton))
            .perform(click())

        // then
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboard.primaryClip
        assertThat(clipData).isNotNull()
        assertThat(clipData!!.getItemAt(0).text.toString()).isEqualTo(testText)
    }

    @Test
    fun close_button_dismisses_bottom_sheet() {
        // given
        scenario = launchBottomSheetWithText("Test")

        // when
        onView(withId(R.id.closeButton))
            .perform(click())

        // then
        onView(withId(R.id.closeButton)).check(doesNotExist())
    }


    private fun launchBottomSheetWithText(text: String): FragmentScenario<OcrResultBottomSheet> {
        val bundle = Bundle().apply {
            putString("result_text", text)
        }
        return launchFragmentInContainer(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_OCR
        )
    }
}
