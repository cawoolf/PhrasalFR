package com.example.phrasalfr.ui

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import androidx.test.core.app.ApplicationProvider
import com.example.phrasalfr.database.Phrase
import com.example.phrasalfr.database.PhraseRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainViewModel(private val phraseRepository: PhraseRepository,
                    private val categorySetting: String): ViewModel() {


    private lateinit var mPhraseList: List<Phrase>
    private lateinit var mQuestionPhrase: Phrase
    private lateinit var mAnswerPhrasesSet: MutableSet<Phrase>
    private lateinit var mAnswerPhrasesIndexArray: IntArray


    // Quiz Logic
    fun buildQuestion(phraseCategory: String) {


        runBlocking {

            mPhraseList = getPhrasesByCategory(phraseCategory)
        }

        val randIndex = (mPhraseList.indices).random()
        val randomPhrase = mPhraseList[randIndex]
        mQuestionPhrase = randomPhrase

//        mPhraseCategory = randomPhrase.category

    }

    fun generateAnswerPhrases() {

        mAnswerPhrasesSet = mutableSetOf()
        mAnswerPhrasesSet.add(mQuestionPhrase)

        // Trying to generate a unique set of Phrase to be assigned to the Answer
        // Using a Set. This works well enough unless the DB is smaller that 4.. Then we're trapped in a infinite loop
        while (mAnswerPhrasesSet.size < 4){
            val randIndex = (mPhraseList.indices).random()
            val randomPhrase = mPhraseList[randIndex]
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
    private suspend fun getAllPhrases(): List<Phrase> {
        return phraseRepository.getAllPhrases()
    }

    private suspend fun getPhrasesByCategory(phraseCategory: String): List<Phrase> {
        return phraseRepository.getPhrasesByCategory(phraseCategory)
    }

    fun insert(phrase: Phrase) = viewModelScope.launch {
        phraseRepository.insert(phrase)

    }

    // View Model factory used for creating the shared view model across fragments
    @Suppress("UNCHECKED_CAST")
    class MainViewModelFactory constructor(private val phraseRepository: PhraseRepository,
                                           private val categorySetting: String): ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>)
                : T = MainViewModel(phraseRepository, categorySetting) as T
    }




}