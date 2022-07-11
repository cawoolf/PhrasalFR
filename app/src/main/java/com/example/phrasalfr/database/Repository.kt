package com.example.phrasalfr.database

class Repository(private val phraseDao: PhraseDao) {

    suspend fun insert(phrase: Phrase) {
        phraseDao.insertPhrase(phrase)
    }

    suspend fun getAllPhrases() : List<Phrase> {
        val allPhrases = phraseDao.getAllPhrases()
        return allPhrases
    }
}