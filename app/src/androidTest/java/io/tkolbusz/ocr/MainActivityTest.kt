package io.tkolbusz.ocr

import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.google.common.truth.Truth.assertThat
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        CAMERA
    )

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun teardown() {
        scenario.close()
    }

    @Test
    fun displays_main_screen() {
        // main screen visible
        onView(withId(R.id.previewView))
            .check(matches(isDisplayed()))
        // capture image button is visible
        onView(withId(R.id.captureButton))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
        // progress bar is initially hidden
        onView(withId(R.id.progressBar))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun capture_button_click_shows_progress_bar_and_disables_capture_button() {
        // when
        onView(withId(R.id.captureButton))
            .perform(click())

        // then
        onView(withId(R.id.progressBar))
            .check(matches(isDisplayed()))

        onView(withId(R.id.captureButton))
            .check(matches(not(isEnabled())))
    }

    @Test
    fun activity_has_camera_permission_after_grant() {
        // when + then
        scenario.onActivity { activity ->
            val permissionStatus = activity.checkSelfPermission(CAMERA)
            assertThat(permissionStatus).isEqualTo(PERMISSION_GRANTED)
        }
    }
}
