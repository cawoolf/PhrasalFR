package com.example.phrasalfr.ui

import androidx.lifecycle.*
import com.example.phrasalfr.MainActivity
import com.example.phrasalfr.database.Phrase
import com.example.phrasalfr.database.PhraseDatabase
import com.example.phrasalfr.database.Repository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {

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
    class MainViewModelFactory constructor(private val repository: Repository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>)
                : T = MainViewModel(repository) as T
    }


}