package com.example.phrasalfr.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PhraseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhrase(phrase: Phrase)

    @Query("SELECT * FROM phrase_table ORDER BY listName ASC")
    suspend fun getAllPhrases(): List<Phrase>
}