package com.example.phrasalfr

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.phrasalfr.database.PhraseDatabase
import com.example.phrasalfr.database.PhraseRepository
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

class DatabaseContextInstrumentedTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun accessDatabaseWithContext() {
        val database = PhraseDatabase.getDatabase(context)
        val phraseRepository = PhraseRepository(database.phraseDao)

        runBlocking {
            val allPhrases = phraseRepository.getAllPhrases()
        Log.i("testTag", allPhrases.size.toString())}
    }

}