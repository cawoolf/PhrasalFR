package com.example.phrasalfr

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationInstrumentedTest {

    // You need to get the main activity for the context of the tests
    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun phrasesNavigationTest() {

        // App loads to Home fragment by default
        // Clicks the bottom Nav for the Phrases fragment
        onView(withId(R.id.navigation_phrases)).perform(ViewActions.click())
        onView(withId(R.id.fragment_phrases_parent_layout)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

        // Clicks the bottom Nav for Home fragment. Just to make sure you can navigate back
        onView(withId(R.id.navigation_home)).perform((ViewActions.click()))
        onView(withId(R.id.fragment_home_parentLayout)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

    }

    @Test
    fun quizNavigationTest() {

        // App loads to Home fragment by default
        // Clicks the bottom Nav for the Quiz fragment
        onView(withId(R.id.navigation_quiz)).perform((ViewActions.click()))
        onView(withId(R.id.fragment_quiz_parent_layout)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

        // Clicks the bottom Nav for Home fragment. Just to make sure you can navigate back
        onView(withId(R.id.navigation_home)).perform((ViewActions.click()))
        onView(withId(R.id.fragment_home_parentLayout)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }

    @Test
    fun homeNavigationTest() {

        // App loads to Home fragment by default
        // Clicks the bottom Nav for the Home fragment
        onView(withId(R.id.navigation_home)).perform((ViewActions.click()))
        onView(withId(R.id.fragment_home_parentLayout)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

        // Clicks the bottom Nav for Home fragment. Just to make sure you can navigate back
        onView(withId(R.id.navigation_home)).perform((ViewActions.click()))
        onView(withId(R.id.fragment_home_parentLayout)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }


}