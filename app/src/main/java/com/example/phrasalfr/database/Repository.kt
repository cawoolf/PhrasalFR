package com.example.phrasalfr.database

import androidx.annotation.WorkerThread

class Repository(private val phraseDao: PhraseDao) {

//    @Suppress("RedundantSuspendModifier")
//    @WorkerThread
    suspend fun insert(phrase: Phrase) {
        phraseDao.insertPhrase(phrase)
    }

    suspend fun getAllPhrases() : List<Phrase> {
        val allPhrases = phraseDao.getAllPhrases()
        return allPhrases
    }
}