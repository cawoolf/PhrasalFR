package com.example.phrasalfr

import android.app.Application
import android.util.Log
import com.example.phrasalfr.database.PhraseDatabase
import com.example.phrasalfr.database.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class PhrasalFRApplication : Application() {

    // No need to cancel this scope as it'll be torn down with the process
    private val applicationScope = CoroutineScope(SupervisorJob())
    lateinit var database: PhraseDatabase
    lateinit var repository: Repository

    override fun onCreate() {
        super.onCreate()
        database = PhraseDatabase.getDatabase(this, applicationScope)
        repository = Repository(database.phraseDao)
        Log.i("dbTAG", "Database started in PhrasalFRApplication: $database")
    }

}