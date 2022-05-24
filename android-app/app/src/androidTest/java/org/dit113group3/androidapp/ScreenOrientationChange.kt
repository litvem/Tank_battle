package org.dit113group3.androidapp

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import org.hamcrest.Matcher
import android.content.pm.ActivityInfo
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.ViewGroup

//Source: https://gist.github.com/nbarraille/03e8910dc1d415ed9740
class ScreenOrientationChange(private val orientation: Int): ViewAction {
    companion object {
        fun orientationLandscape(): ViewAction = ScreenOrientationChange(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        fun orientationPortrait(): ViewAction = ScreenOrientationChange(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    }

    override fun getDescription(): String = "change orientation to $orientation"

    override fun getConstraints(): Matcher<View> = isRoot()

    override fun perform(uiController: UiController, view: View) {
        uiController.loopMainThreadUntilIdle()
        var activity = getActivity(view.context)
        if (activity == null && view is ViewGroup) {
            val c = view.childCount
            var i = 0
            while (i < c && activity == null) {
                activity = getActivity(view.getChildAt(i).context)
                ++i
            }
        }
        activity!!.requestedOrientation = orientation
    }

    private fun getActivity(context: Context): Activity? {
        var context = context
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = (context as ContextWrapper).baseContext
        }
        return null
    }
}