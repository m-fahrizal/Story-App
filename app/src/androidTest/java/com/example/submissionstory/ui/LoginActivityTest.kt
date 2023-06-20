package com.example.submissionstory.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.submissionstory.R
import com.example.submissionstory.data.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {

    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun loginSuccess() {

        onView(withId(R.id.ed_login_email)).perform(typeText("rizal@mail.my"))
        onView(withId(R.id.ed_login_password)).perform(typeText("password"), closeSoftKeyboard())
        onView(withId(R.id.button_login)).perform(click())

    }


}