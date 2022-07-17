package com.example.phrasalfr

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuizSettingsInstrumentedTest {

    // You need to get the main activity for the context of the tests
    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun defaultQuestionAnswerSettings() {

        onView(withId(R.id.settings_question_french_text_radioButton)).perform(ViewActions.click())
        onView(withId(R.id.settings_answer_english_text_radioButton)).perform(ViewActions.click())

    }

    @After
    fun navigateToHomeFragment() {

        // Clicks the bottom Nav for Home fragment. Just to make sure you can navigate back
        onView(withId(R.id.navigation_home)).perform((ViewActions.click()))
        onView(withId(R.id.fragment_home_parentLayout)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }

    @Test
    fun phraseSettingsGreetings() {

        onView(withId(R.id.settings_greetings_Chip)).perform(ViewActions.click())
        onView(withId(R.id.navigation_quiz)).perform(ViewActions.click())
    }

    @Test
    fun phraseSettingsGrammar() {

        onView(withId(R.id.settings_grammar_Chip)).perform(ViewActions.click())
        onView(withId(R.id.navigation_quiz)).perform(ViewActions.click())

    }

    @Test
    fun phraseSettingsUserPhrase() {

        onView(withId(R.id.settings_user_phrases_Chip)).perform(ViewActions.click())
        onView(withId(R.id.navigation_quiz)).perform(ViewActions.click())
    }

    @Test
    fun phraseSettingsAllPhrases() {

        onView(withId(R.id.settings_all_phrases_Chip)).perform(ViewActions.click())
        onView(withId(R.id.navigation_quiz)).perform(ViewActions.click())

    }
}