package com.example.phrasalfr

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.phrasalfr.database.Phrase
import com.example.phrasalfr.database.PhraseDao
import com.example.phrasalfr.database.PhraseDatabase
import com.example.phrasalfr.database.PhraseRepository
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseInstrumentedTest {

    private lateinit var phraseRepository: PhraseRepository
    private lateinit var phraseDao: PhraseDao
    private lateinit var database: PhraseDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, PhraseDatabase::class.java).build()
        phraseDao = database.phraseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
   fun addPhraseToDb() {

        runBlocking {

            val allPhrasesCount = phraseDao.getAllPhrases().size

            Log.i("mTestTag", allPhrasesCount.toString())

            var phrase1 = Phrase("Test", "TestEN", "TestFR")
            phraseDao.insertPhrase(phrase1)

            val allPhrasesCount2 = phraseDao.getAllPhrases().size

            Log.i("mTestTag", allPhrasesCount.toString())

            assertEquals(allPhrasesCount2, allPhrasesCount + 1)
//        assertThat(allPhrasesCount, e)
        }
    }
}