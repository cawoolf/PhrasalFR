package com.example.phrasalfr.ui

import android.content.Context
import androidx.lifecycle.*
import com.example.phrasalfr.MainActivity
import com.example.phrasalfr.R
import com.example.phrasalfr.database.Phrase
import com.example.phrasalfr.database.PhraseDatabase
import com.example.phrasalfr.database.Repository
import com.google.mlkit.nl.translate.Translator
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainViewModel(private val repository: Repository,
                    private val questionSetting: String,
                    private val answerSetting: String) : ViewModel() {



    // Database functions
    suspend fun getAllPhrases() : List<Phrase> {
        val allPhrases = repository.getAllPhrases()
        return allPhrases
    }

    fun insert(phrase: Phrase) = viewModelScope.launch {
        repository.insert(phrase)

    }


    // View Model factory used for creating the shared view model across fragments
    @Suppress("UNCHECKED_CAST")
    class MainViewModelFactory constructor(private val repository: Repository,
                                           private val questionSetting: String,
                                           private val answerSetting: String): ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>)
                : T = MainViewModel(repository, questionSetting, answerSetting) as T
    }




}