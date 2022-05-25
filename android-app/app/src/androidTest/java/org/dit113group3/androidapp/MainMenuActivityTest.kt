package org.dit113group3.androidapp

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.dit113group3.androidapp.ScreenOrientationChange.Companion.orientationLandscape
import org.junit.Test
import org.junit.runner.RunWith

@RunWith (AndroidJUnit4ClassRunner::class)
class MainMenuActivityTest {

    val sleep = Thread.sleep(2000)

    @Test   //Checks if Main Menu activity is launching
    fun test_isMainMenuActivityInView() {
        val activityScenario = ActivityScenario.launch(MainMenuActivity::class.java)
        onView(withId(R.id.mainMenu)).check(matches(isDisplayed()))
        sleep
    }

    @Test   //Checks if Welcome title is present on Main Menu screen
    fun test_visibility_welcome_title() {
        val activityScenario = ActivityScenario.launch(MainMenuActivity::class.java)
        onView(withId(R.id.welcomeText)).check(matches(isDisplayed()))
        sleep
    }

    @Test   //Checks if PLAY button is present on Main Menu screen
    fun test_visibility_play_button() {
        val activityScenario = ActivityScenario.launch(MainMenuActivity::class.java)
        onView(withId(R.id.playButton)).check(matches(isDisplayed()))
        sleep
    }

    @Test   //Checks if RULES button is present on Main Menu screen
    fun test_visibility_rules_button() {
        val activityScenario = ActivityScenario.launch(MainMenuActivity::class.java)
        onView(withId(R.id.rulesButton)).check(matches(isDisplayed()))
        sleep
    }

    @Test   //Checks if CREDITS button is present on Credits Menu screen
    fun test_visibility_credits_button() {
        val activityScenario = ActivityScenario.launch(MainMenuActivity::class.java)
        onView(withId(R.id.creditsButton)).check(matches(isDisplayed()))
        sleep
    }

    @Test   //Checks if button PLAY has correct navigation
    fun test_play_button_navigation() {
        val activityScenario = ActivityScenario.launch(MainMenuActivity::class.java)
        onView(withId(R.id.playButton)).perform(click())
        onView(withId(R.id.mainLayout)).check(matches(isDisplayed()))
        sleep
    }

    @Test   //Checks if button RULES has correct navigation
    fun test_rules_button_navigation() {
        val activityScenario = ActivityScenario.launch(MainMenuActivity::class.java)
        onView(withId(R.id.rulesButton)).perform(click())
        onView(withId(R.id.rulesLayout)).check(matches(isDisplayed()))
        sleep
    }

    @Test   //Checks if button CREDITS has correct navigation
    fun test_credits_button_navigation() {
        val activityScenario = ActivityScenario.launch(MainMenuActivity::class.java)
        onView(withId(R.id.creditsButton)).perform(click())
        onView(withId(R.id.creditsLayout)).check(matches(isDisplayed()))
        sleep
    }

    @Test   //Checks if button EXIT on RULES screen leads to Main Menu screen
    fun test_rules_screen_exit_button_navigation() {
        val activityScenario = ActivityScenario.launch(MainMenuActivity::class.java)
        onView(withId(R.id.rulesButton)).perform(click())
        onView(withId(R.id.rulesLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.exit)).perform(click())
        onView(withId(R.id.mainMenu)).check(matches(isDisplayed()))
        sleep
    }

    @Test   //Checks if button EXIT on CREDITS screen leads to Main Menu screen
    fun test_credits_screen_exit_button_navigation() {
        val activityScenario = ActivityScenario.launch(MainMenuActivity::class.java)
        onView(withId(R.id.creditsButton)).perform(click())
        onView(withId(R.id.creditsLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.creditsExit)).perform(click())
        onView(withId(R.id.mainMenu)).check(matches(isDisplayed()))
        sleep
    }

    @Test   //Checks if game logo is present on Main Menu screen
    fun test_isLogoVisible() {
        val activityScenario = ActivityScenario.launch(MainMenuActivity::class.java)
        onView(withId(R.id.logo)).check(matches(isDisplayed()))
        sleep
    }

    @Test   //Checks if rotation from portrait mode changes to landscape
    fun test_landscapeMode() {
        val activityScenario = ActivityScenario.launch(MainMenuActivity::class.java)
        onView(isRoot()).perform(orientationLandscape())
        sleep
        onView(withId(R.id.mainMenu)).check(matches(isDisplayed()))
        sleep
    }
}
