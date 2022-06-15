package com.example.phrasalfr

import android.app.Application
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.phrasalfr.database.Phrase
import com.example.phrasalfr.database.PhraseDatabase
import com.example.phrasalfr.database.Repository
import com.example.phrasalfr.util.PhrasalUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PhrasalFRApplication : Application() {

    // No need to cancel this scope as it'll be torn down with the process
    private val applicationScope = CoroutineScope(SupervisorJob())
    lateinit var database: PhraseDatabase
    lateinit var repository: Repository
    lateinit var phrasalUtil: PhrasalUtil

    override fun onCreate() {
        super.onCreate()
        database = PhraseDatabase.getDatabase(this, applicationScope)
        repository = Repository(database.phraseDao)
        phrasalUtil = PhrasalUtil(this)
        phrasalUtil.getTranslator()
        Log.i("mTAG", "Database started in PhrasalFRApplication: $database")
        testDB()

    }

    /* This test function causes the DB to initialize correctly and immediately on first application launch
     Without this you have to perform some kind of db function to get things moving. Otherwise when you make
     a request to build a quiz, you get a index out of bounds error. And the quiz just loads with defaults text values.
     Just the first time only, and after the db will work correctly.
     */
    private fun testDB() {

        applicationScope.launch {
            val testPhrase =  Phrase("Greeting", "Hello", "Bonjour")
           repository.insert(testPhrase)
        }
    }

}