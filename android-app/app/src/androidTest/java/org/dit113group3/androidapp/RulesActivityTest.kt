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

    @Test   //Checks if Rules Activity is launching
    fun test_isRulesActivityInView() {
        val activityScenario = ActivityScenario.launch(RulesActivity::class.java)
        onView(withId(R.id.rulesLayout)).check(matches(isDisplayed()))
    }

    @Test   //Checks if initially visible Rules Activity elements are present
    fun test_visibility_titles_on_rulesScreen() {
        val activityScenario = ActivityScenario.launch(RulesActivity::class.java)
        onView(withId(R.id.rulesTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.rulesIntroText)).check(matches(isDisplayed()))
    }

    @Test   //Checks if Rules Activity elements have correct text displayed
    fun test_isExpectedTextDisplayed() {
        val activityScenario = ActivityScenario.launch(RulesActivity::class.java)
        onView(withId(R.id.rulesTitle)).check(matches(withText(R.string.rulesTitle)))
        onView(withId(R.id.rulesIntroText)).check(matches(withText(R.string.rulesIntroText)))
        onView(withId(R.id.tankControlsTitle)).check(matches(withText(R.string.tankControlsTitle)))
        onView(withId(R.id.tankControlText)).check(matches(withText(R.string.tankControlText)))
        onView(withId(R.id.shootingTitle)).check(matches(withText(R.string.shootingTitle)))
        onView(withId(R.id.shootingText)).check(matches(withText(R.string.shootingText)))
        onView(withId(R.id.exitTitle)).check(matches(withText(R.string.exitTitle)))
        onView(withId(R.id.exitText)).check(matches(withText(R.string.exitText)))
        onView(withId(R.id.conclusion)).check(matches(withText(R.string.conclusion)))
    }

    @Test   //Checks if button EXIT has correct navigation
    fun test_exit_button_navigation() {
        val activityScenario = ActivityScenario.launch(RulesActivity::class.java)
        onView(withId(R.id.exit)).perform(ViewActions.click())
        onView(withId(R.id.mainMenu)).check(matches(isDisplayed()))
    }
}