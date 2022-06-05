package com.example.phrasalfr.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "phrase_table")
data class Phrase(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val listName: String,
    val phraseEnglish: String,
    val phraseFrench: String,
)