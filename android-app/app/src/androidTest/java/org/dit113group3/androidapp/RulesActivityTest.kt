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
class RulesActivityTest {

    val sleep = Thread.sleep(2000)

    @Test   //Checks if Rules activity is launching
    fun test_isRulesActivityInView() {
        val activityScenario = ActivityScenario.launch(RulesActivity::class.java)
        onView(withId(R.id.rulesLayout)).check(matches(isDisplayed()))
        sleep
    }

    @Test   //Checks if initially visible Rules activity elements are present
    fun test_visibility_titles_on_rulesScreen() {
        val activityScenario = ActivityScenario.launch(RulesActivity::class.java)
        onView(withId(R.id.rulesTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.rulesIntroText)).check(matches(isDisplayed()))
        sleep
    }

    @Test   //Checks if title text is displayed correctly on Rules screen
    fun test_isExpectedTextDisplayed_title() {
        val activityScenario = ActivityScenario.launch(RulesActivity::class.java)
        onView(withId(R.id.rulesTitle)).check(matches(withText(R.string.rulesTitle)))
        sleep
    }

    @Test   //Checks if introduction text is displayed correctly on Rules screen
    fun test_isExpectedTextDisplayed_intro() {
        val activityScenario = ActivityScenario.launch(RulesActivity::class.java)
        onView(withId(R.id.rulesIntroText)).check(matches(withText(R.string.rulesIntroText)))
        sleep
    }

    @Test   //Checks if title and text for controlling the tank are displayed correctly on Rules screen
    fun test_isExpectedTextDisplayed_control() {
        val activityScenario = ActivityScenario.launch(RulesActivity::class.java)
        onView(withId(R.id.tankControlsTitle)).check(matches(withText(R.string.tankControlsTitle)))
        onView(withId(R.id.tankControlText)).check(matches(withText(R.string.tankControlText)))
        sleep
    }

    @Test   //Checks if title and text for shooting are displayed correctly on Rules screen
    fun test_isExpectedTextDisplayed_shooting() {
        val activityScenario = ActivityScenario.launch(RulesActivity::class.java)
        onView(withId(R.id.shootingTitle)).check(matches(withText(R.string.shootingTitle)))
        onView(withId(R.id.shootingText)).check(matches(withText(R.string.shootingText)))
        sleep
    }

    @Test   //Checks if title and text for exit are displayed correctly on Rules screen
    fun test_isExpectedTextDisplayed_exit() {
        val activityScenario = ActivityScenario.launch(RulesActivity::class.java)
        onView(withId(R.id.exitTitle)).check(matches(withText(R.string.exitTitle)))
        onView(withId(R.id.exitText)).check(matches(withText(R.string.exitText)))
        sleep
    }

    @Test   //Checks if conclusion text for exit is displayed correctly on Rules screen
    fun test_isExpectedTextDisplayed_conclusion() {
        val activityScenario = ActivityScenario.launch(RulesActivity::class.java)
        onView(withId(R.id.conclusion)).check(matches(withText(R.string.conclusion)))
        sleep
    }

    @Test   //Checks if button EXIT has correct navigation
    fun test_exit_button_navigation() {
        val activityScenario = ActivityScenario.launch(RulesActivity::class.java)
        onView(withId(R.id.exit)).perform(ViewActions.click())
        onView(withId(R.id.mainMenu)).check(matches(isDisplayed()))
        sleep
    }

    @Test   //Checks if rotation from portrait mode changes to landscape
    fun test_landscapeMode() {
        val activityScenario = ActivityScenario.launch(RulesActivity::class.java)
        onView(isRoot()).perform(ScreenOrientationChange.orientationLandscape())
        sleep
        onView(withId(R.id.rulesLayout)).check(matches(isDisplayed()))
        sleep
    }
}