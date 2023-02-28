package com.example.phrasalfr

import android.app.Application
import android.media.AudioManager
import android.util.Log
import com.example.phrasalfr.database.Phrase
import com.example.phrasalfr.database.PhraseDatabase
import com.example.phrasalfr.database.PhraseRepository
import com.example.phrasalfr.util.PhrasalUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class PhrasalFRApplication : Application() {

    // No need to cancel this scope as it'll be torn down with the process
    private val applicationScope = CoroutineScope(SupervisorJob())
    lateinit var database: PhraseDatabase
    lateinit var phraseRepository: PhraseRepository
    lateinit var phrasalUtil: PhrasalUtil

    override fun onCreate() {
        super.onCreate()
        database = PhraseDatabase.getDatabase(this)
        phraseRepository = PhraseRepository(database.phraseDao)
        phrasalUtil = PhrasalUtil(this)
//        phrasalUtil.getFRENTranslator()
//        phrasalUtil.getENFRTranslator()
//        phrasalUtil.startTranslatorDownload()
        Log.i("mTAG", "Database started in PhrasalFRApplication: $database")
//        testDB()
        setDeviceVolume()

    }

    /* This test function causes the DB to initialize correctly and immediately on first application launch
     Without this you have to perform some kind of db function to get things moving. Otherwise when you make
     a request to build a quiz, you get a index out of bounds error. And the quiz just loads with defaults text values.
     Just the first time only, and after the db will work correctly.

     Looks like this isn't needed anymore?
     */
    private fun testDB() {

        applicationScope.launch {
            val testPhrase =  Phrase("Greeting", "Hello", "Bonjour")
           phraseRepository.insert(testPhrase)
        }
    }

    private fun setDeviceVolume() {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val desiredVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, desiredVolume, 0);
    }

}