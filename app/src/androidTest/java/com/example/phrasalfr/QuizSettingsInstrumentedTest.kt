package com.example.phrasalfr

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.testing.launchFragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.phrasalfr.ui.QuizFragment
import org.hamcrest.Matcher
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
        
        val question = onView(withId(R.id.quiz_questionTextView))
        Log.i("testTag", getText(question))

//        val scenario = launchFragmentInContainer<QuizFragment>()

    }

//    @Test
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


    // Gets the text out of the TextView identified by the Matcher
    private fun getText(matcher: ViewInteraction): String {
        var text = String()
        matcher.perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(TextView::class.java)
            }

            override fun getDescription(): String {
                return "Text of the view"
            }

            override fun perform(uiController: UiController, view: View) {
                val tv = view as TextView
                text = tv.text.toString()
            }
        })

        return text
    }
}