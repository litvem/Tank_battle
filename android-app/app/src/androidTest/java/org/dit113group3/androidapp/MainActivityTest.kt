package org.dit113group3.androidapp

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith (AndroidJUnit4ClassRunner::class)
class MainActivityTest {

    val sleep = Thread.sleep(2000)

    @Test   //Checks of Main activity is launching
    fun test_isMainActivityInView() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.mainLayout)).check(matches(isDisplayed()))
        sleep
    }

    @Test   //Checks if SHOOT button is present on Main activity screen
    fun test_visibility_shoot_button() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.shoot)).check(matches(isDisplayed()))
        sleep
    }

    @Test   //Checks if SHOOT button has correct text displayed
    fun test_isExpectedTextDisplayed() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.shoot)).check(matches(withText(R.string.shoot)))
        sleep
    }

    @Test   //Checks if rotation from portrait mode changes to landscape
    fun test_landscapeMode() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        onView(isRoot()).perform(ScreenOrientationChange.orientationLandscape())
        sleep
        onView(withId(R.id.mainLayout)).check(matches(isDisplayed()))
        sleep
    }
}