package com.example.phrasalfr

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PhrasesTranslatorInstrumentedTest {

    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun navigateToPhrasesTab()
    {
        Espresso.onView(ViewMatchers.withId(R.id.navigation_phrases)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.fragment_phrases_parent_layout)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }

    @Test
    fun translateEnglishToFrench() {


        val frenchEditText = Espresso.onView(ViewMatchers.withId(R.id.phrases_french_editText))
        val englishEditText = Espresso.onView(ViewMatchers.withId(R.id.phrases_english_editText))
//        val translateButton = Espresso.onView(ViewMatchers.withId(R.id.phrases_translate_button))

        // Must click the English EditText so that it gets focus
        englishEditText.perform(ViewActions.click())

        // Sets the text to be translated
        englishEditText.perform(ViewActions.replaceText("Hello"))
        englishEditText.perform(ViewActions.closeSoftKeyboard())

        // Translates the text
//        translateButton.perform(ViewActions.click())
        Thread.sleep(3000)

        // Assert that the translation worked and is correct
       frenchEditText.check(matches(withText("Bonjour")))

    }

    @Test
    fun translateFrenchToEnglish() {

        val frenchEditText = Espresso.onView(ViewMatchers.withId(R.id.phrases_french_editText))
        val englishEditText = Espresso.onView(ViewMatchers.withId(R.id.phrases_english_editText))
//        val translateButton = Espresso.onView(ViewMatchers.withId(R.id.phrases_translate_button))

        // Must click the English EditText so that it gets focus
        frenchEditText.perform(ViewActions.click())

        // Sets the text to be translated
        frenchEditText.perform(ViewActions.replaceText("Bonjour"))
        frenchEditText.perform(ViewActions.closeSoftKeyboard())

        // Translates the text
//        translateButton.perform(ViewActions.click())
        Thread.sleep(3000)

        // Assert that the translation worked and is correct
        englishEditText.check(matches(withText("Hello")))

    }
}