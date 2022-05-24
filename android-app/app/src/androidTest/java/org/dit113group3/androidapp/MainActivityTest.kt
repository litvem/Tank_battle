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

    @Test   //Checks of Main Activity is launching
    fun test_isMainActivityInView() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.mainLayout)).check(matches(isDisplayed()))
    }

    @Test   //Checks if Main Activity elements are present
    fun test_visibility_titles_on_mainScreen() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.titleTankBattle)).check(matches(isDisplayed()))
        onView(withId(R.id.shoot)).check(matches(isDisplayed()))
    }

    @Test   //Checks if Main Activity elements have correct text displayed
    fun test_isExpectedTextDisplayed() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.titleTankBattle)).check(matches(withText(R.string.tankBattle)))
        onView(withId(R.id.shoot)).check(matches(withText(R.string.shoot)))
    }
}