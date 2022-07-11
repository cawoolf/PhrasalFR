package com.example.phrasalfr.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "phrases_table")
data class Phrase(
    val category: String,
    @PrimaryKey() val phraseEnglish: String,
    val phraseFrench: String,
)