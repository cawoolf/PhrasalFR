package com.example.phrasalfr.database

class PhraseRepository(private val phraseDao: PhraseDao) {

    suspend fun insert(phrase: Phrase) {
        phraseDao.insertPhrase(phrase)
    }

    suspend fun deletePhrase(englishPhrase: String) {
        phraseDao.deletePhrase(englishPhrase)
    }

    suspend fun getAllPhrases(): List<Phrase> {
        return phraseDao.getAllPhrases()
    }

    suspend fun getPhrasesByCategory(userCategory: String): List<Phrase> {
        return phraseDao.getPhrasesByCategory(userCategory)
    }

    // Mainly used for testing
    suspend fun getTargetPhrase(frenchPhrase: String): List<Phrase> {
        return phraseDao.getTargetPhrase(frenchPhrase)
    }
}