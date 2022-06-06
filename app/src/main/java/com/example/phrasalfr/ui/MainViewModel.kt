package com.example.phrasalfr.ui

import androidx.lifecycle.*
import com.example.phrasalfr.MainActivity
import com.example.phrasalfr.database.Phrase
import com.example.phrasalfr.database.PhraseDatabase
import com.example.phrasalfr.database.Repository

class MainViewModel(private val repository: Repository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

    val text: LiveData<String> = _text


    suspend fun getAllPhrases() : List<Phrase> {
        val allPhrases = repository.getAllPhrases()
        return allPhrases
    }


    @Suppress("UNCHECKED_CAST")
    class MainViewModelFactory constructor(private val repository: Repository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>)
                : T = MainViewModel(repository) as T
    }


}