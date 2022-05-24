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

    @Test   //Checks if Credits Activity is launching
    fun test_isCreditsActivityInView() {
        val activityScenario = ActivityScenario.launch(CreditsActivity::class.java)
        onView(withId(R.id.creditsLayout)).check(matches(isDisplayed()))
    }

    @Test   //Checks if Credits Activity elements are present
    fun test_visibility_titles_on_creditsScreen() {
        val activityScenario = ActivityScenario.launch(CreditsActivity::class.java)
        onView(withId(R.id.devTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.devBody)).check(matches(isDisplayed()))
        onView(withId(R.id.teacherTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.teacherBody)).check(matches(isDisplayed()))
        onView(withId(R.id.taTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.taBody)).check(matches(isDisplayed()))
        onView(withId(R.id.importantlyText)).check(matches(isDisplayed()))
        onView(withId(R.id.youText)).check(matches(isDisplayed()))
        onView(withId(R.id.playingText)).check(matches(isDisplayed()))
    }

    @Test   //Checks if Credits Activity elements have correct text displayed
    fun test_isExpectedTextDisplayed() {
        val activityScenario = ActivityScenario.launch(CreditsActivity::class.java)
        onView(withId(R.id.devTitle)).check(matches(withText(R.string.devTitle)))
        onView(withId(R.id.teacherTitle)).check(matches(withText(R.string.teachersTitle)))
        onView(withId(R.id.taTitle)).check(matches(withText(R.string.taTitle)))
        onView(withId(R.id.importantlyText)).check(matches(withText(R.string.importantly)))
        onView(withId(R.id.youText)).check(matches(withText(R.string.you)))
        onView(withId(R.id.playingText)).check(matches(withText(R.string.playing)))
    }

    @Test   //Checks if button EXIT has correct navigation
    fun test_exit_button_navigation() {
        val activityScenario = ActivityScenario.launch(CreditsActivity::class.java)
        onView(withId(R.id.creditsExit)).perform(ViewActions.click())
        onView(withId(R.id.mainMenu)).check(matches(isDisplayed()))
    }

    @Test   //Checks if game logo is present on Credits screen
    fun test_isLogoVisible() {
        val activityScenario = ActivityScenario.launch(CreditsActivity::class.java)
        onView(withId(R.id.tankBattleIcon)).check(matches(isDisplayed()))
    }
}