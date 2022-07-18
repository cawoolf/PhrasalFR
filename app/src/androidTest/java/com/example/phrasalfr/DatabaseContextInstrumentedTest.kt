package com.example.phrasalfr

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.phrasalfr.database.PhraseDatabase
import org.junit.Rule
import org.junit.Test

class DatabaseContextInstrumentedTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun accessDatabaseWithContext() {
        val database = PhraseDatabase.getDatabase(context)
    }

}