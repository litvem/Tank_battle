package org.dit113group3.androidapp

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith (AndroidJUnit4ClassRunner::class)
class CreditsActivityTest {

    val sleep = Thread.sleep(2000)

    @Test   //Checks if Credits activity is launching
    fun test_isCreditsActivityInView() {
        val activityScenario = ActivityScenario.launch(CreditsActivity::class.java)
        onView(withId(R.id.creditsLayout)).check(matches(isDisplayed()))
        sleep
    }

    @Test   //Checks if title and body for developers section of Credits activity are present
    fun test_visibility_developers() {
        val activityScenario = ActivityScenario.launch(CreditsActivity::class.java)
        onView(withId(R.id.devTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.devBody)).check(matches(isDisplayed()))
        sleep
    }

    @Test   //Checks if title and body for teachers section of Credits activity are present
    fun test_visibility_teachers() {
        val activityScenario = ActivityScenario.launch(CreditsActivity::class.java)
        onView(withId(R.id.teacherTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.teacherBody)).check(matches(isDisplayed()))
        sleep
    }

    @Test   //Checks if title and body for TAs section of Credits activity are present
    fun test_visibility_tas() {
        val activityScenario = ActivityScenario.launch(CreditsActivity::class.java)
        onView(withId(R.id.taTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.taBody)).check(matches(isDisplayed()))
        sleep
    }

    @Test   //Checks if conclusion for Credits activity is present
    fun test_visibility_conclusion() {
        val activityScenario = ActivityScenario.launch(CreditsActivity::class.java)
        onView(withId(R.id.importantlyText)).check(matches(isDisplayed()))
        onView(withId(R.id.youText)).check(matches(isDisplayed()))
        onView(withId(R.id.playingText)).check(matches(isDisplayed()))
        sleep
    }

    @Test   //Checks if text for developers' title is correctly displayed on Credits screen
    fun test_isExpectedTextDisplayed_developers() {
        val activityScenario = ActivityScenario.launch(CreditsActivity::class.java)
        onView(withId(R.id.devTitle)).check(matches(withText(R.string.devTitle)))
        sleep
    }

    @Test   //Checks if text for teachers' title is correctly displayed on Credits screen
    fun test_isExpectedTextDisplayed_teachers() {
        val activityScenario = ActivityScenario.launch(CreditsActivity::class.java)
        onView(withId(R.id.teacherTitle)).check(matches(withText(R.string.teachersTitle)))
        sleep
    }

    @Test   //Checks if text for TAs' title is correctly displayed on Credits screen
    fun test_isExpectedTextDisplayed_tas() {
        val activityScenario = ActivityScenario.launch(CreditsActivity::class.java)
        onView(withId(R.id.taTitle)).check(matches(withText(R.string.taTitle)))
        sleep
    }

    @Test   //Checks if text for conclusion is correctly displayed on Credits screen
    fun test_isExpectedTextDisplayed_conclusion() {
        val activityScenario = ActivityScenario.launch(CreditsActivity::class.java)
        onView(withId(R.id.importantlyText)).check(matches(withText(R.string.importantly)))
        onView(withId(R.id.youText)).check(matches(withText(R.string.you)))
        onView(withId(R.id.playingText)).check(matches(withText(R.string.playing)))
        sleep
    }


    @Test   //Checks if button EXIT has correct navigation
    fun test_exit_button_navigation() {
        val activityScenario = ActivityScenario.launch(CreditsActivity::class.java)
        onView(withId(R.id.creditsExit)).perform(ViewActions.click())
        onView(withId(R.id.mainMenu)).check(matches(isDisplayed()))
        sleep
    }

    @Test   //Checks if game logo is present on Credits screen
    fun test_isLogoVisible() {
        val activityScenario = ActivityScenario.launch(CreditsActivity::class.java)
        onView(withId(R.id.tankBattleIcon)).check(matches(isDisplayed()))
        sleep
    }

    @Test   //Checks if rotation from portrait mode changes to landscape
    fun test_landscapeMode() {
        val activityScenario = ActivityScenario.launch(CreditsActivity::class.java)
        onView(isRoot()).perform(ScreenOrientationChange.orientationLandscape())
        sleep
        onView(withId(R.id.creditsLayout)).check(matches(isDisplayed()))
        sleep
    }
}