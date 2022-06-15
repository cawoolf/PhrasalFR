package com.example.phrasalfr.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.phrasalfr.MainActivity
import com.example.phrasalfr.R
import com.example.phrasalfr.database.Phrase
import com.example.phrasalfr.database.PhraseDatabase
import com.example.phrasalfr.database.Repository
import com.google.mlkit.nl.translate.Translator
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainViewModel(private val repository: Repository,
                    private val questionSetting: String,
                    private val answerSetting: String) : ViewModel() {


    private lateinit var mAllPhrases: List<Phrase>
    private lateinit var mQuestionPhrase: Phrase
    private lateinit var mAnswerPhrasesSet: MutableSet<Phrase>
    private lateinit var mAnswerPhrasesIndexArray: IntArray

    private lateinit var mPhraseCategory: String


    // Quiz Logic
    fun buildQuestion() {

        // In a separate thread, Makes a DB query for all phrases
            runBlocking {
                val allPhrases = async { mAllPhrases = getAllPhrases() }

                // .join() Should make the main thread wait for the query to finish before continuing
                allPhrases.join()
                Log.i("mTAG", "Phrases = " + mAllPhrases[0])
            }


        val randIndex = (mAllPhrases.indices).random()
        val randomPhrase = mAllPhrases[randIndex]
        mQuestionPhrase = randomPhrase

        mPhraseCategory = randomPhrase.category

    }

    fun generateAnswerPhrases() {

        mAnswerPhrasesSet = mutableSetOf()
        mAnswerPhrasesSet.add(mQuestionPhrase)

        // Trying to generate a unique set of Phrase to be assigned to the Answer
        // Using a Set. This works well enough unless the DB is smaller that 4.. Then we're trapped in a infinite loop
        while (mAnswerPhrasesSet.size < 4){
            val randIndex = (mAllPhrases.indices).random()
            val randomPhrase = mAllPhrases[randIndex]
            mAnswerPhrasesSet.add(randomPhrase)

        }

        // Creates an array of Indexes that can be shuffled to randomize the answers
        mAnswerPhrasesIndexArray = intArrayOf(0,1,2,3)

//        Log.i("qTAG", mAnswerPhrasesIndexArray.contentToString())

        mAnswerPhrasesIndexArray.shuffle()

        Log.i("qTAG", "Answer is always index[0]")
        Log.i("qTAG", mAnswerPhrasesIndexArray.contentToString())
        Log.i("qTAG", "Question: " + mQuestionPhrase.phraseEnglish)
        Log.i("qTAG", "A: " + mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[0]).phraseEnglish) // Index 0 will always be the question itself
        Log.i("qTAG", "B: " + mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[1]).phraseEnglish)
        Log.i("qTAG", "C: " + mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[2]).phraseEnglish)
        Log.i("qTAG", "D: " + mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[3]).phraseEnglish)



    }


    // Getters
    fun getQuestionPhrase(): Phrase {
        return mQuestionPhrase
    }

    fun getAnswerPhraseSet(): MutableSet<Phrase> {
        return mAnswerPhrasesSet
    }

    fun getAnswerPhrasesIndexArray() : IntArray {
        return mAnswerPhrasesIndexArray
    }

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