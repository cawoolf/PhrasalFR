package com.example.phrasalfr.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PhraseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhrase(phrase: Phrase)

    @Query("SELECT * FROM phrases_table ORDER BY category ASC")
    suspend fun getAllPhrases(): List<Phrase>

    @Query("SELECT * FROM phrases_table WHERE category =:user_category")
    suspend fun getPhrasesByCategory(user_category: String): List<Phrase>

    @Query("SELECT * FROM phrases_table WHERE phraseFrench= :french_phrase")
    suspend fun getTargetPhrase(french_phrase: String): List<Phrase>
}