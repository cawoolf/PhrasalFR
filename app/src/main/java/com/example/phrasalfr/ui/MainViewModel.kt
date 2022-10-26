package com.example.phrasalfr.ui

import android.util.Log
import androidx.lifecycle.*
import com.example.phrasalfr.database.Phrase
import com.example.phrasalfr.database.PhraseRepository
import com.example.phrasalfr.util.CustomEvent
import com.google.android.datatransport.Event
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainViewModel(private val phraseRepository: PhraseRepository,
                    private val categorySetting: String): ViewModel() {


    private lateinit var mPhraseList: List<Phrase>
    private var mTotalPhraseCount: Int = 0
    private lateinit var mQuestionPhrase: Phrase
    private lateinit var mAnswerPhrasesSet: MutableSet<Phrase>
    private val mAskedQuestionSet: MutableSet<Phrase> = mutableSetOf()
    private lateinit var mAnswerPhrasesIndexArray: IntArray

    val navigateToQuiz = MutableLiveData<Boolean>()


    // Quiz Logic
    fun buildQuestion(phraseCategory: String) {


        Log.i("vmTAG", phraseCategory.toString())

        runBlocking {

            mPhraseList = getPhrasesByCategory(phraseCategory)
            mTotalPhraseCount = mPhraseList.size


        }

        val randIndex = (mPhraseList.indices).random()
        val randomPhrase = mPhraseList[randIndex]
        mQuestionPhrase = randomPhrase

//        mPhraseCategory = randomPhrase.category

    }

    fun uniqueQuestion(phrase: Phrase): Boolean {

        // Works but breaks into an infinite loop if it goes through all the db

        Log.i("quizTag", "inside uniqueQuestion")
        var unique = false

        val questionCount = mAskedQuestionSet.size
        Log.i("quizTag", questionCount.toString())

        // Attempts to add the question to a Set, which can only have unique entries
        // If this fails than the count is not incremented, which means that the question was not unique
        mAskedQuestionSet.add(phrase)

        if(mAskedQuestionSet.size > questionCount){
            unique = true
        }

        Log.i("quizTag", questionCount.toString())


        return unique

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

    // Navigation for Button on Home page
    fun userClicksStartQuiz(buttonClicked: Boolean) {
        navigateToQuiz.value = buttonClicked
        Log.i("mVM", navigateToQuiz.value.toString())

    }

    // Getters
    fun getQuestionPhrase(): Phrase {
        return mQuestionPhrase
    }

    fun getNavigateQuizValue() : Boolean? {
        return navigateToQuiz.value
    }

    fun getAnswerPhraseSet(): MutableSet<Phrase> {
        return mAnswerPhrasesSet
    }

    fun getAnswerPhrasesIndexArray() : IntArray {
        return mAnswerPhrasesIndexArray
    }

    fun getAskedQuestionCount() : Int {
        return mAskedQuestionSet.size
    }

    fun resetAskedQuestionSet() {
        mAskedQuestionSet.removeAll(mPhraseList)
        Log.i("quizTag", mAskedQuestionSet.size.toString())

    }

    fun getTotalPhraseCount() : Int {
        return mTotalPhraseCount
    }

    fun resetTotalPhraseCount() {
        mTotalPhraseCount = 0
    }

    // Database functions
    suspend fun getAllPhrases(): List<Phrase> {
        return phraseRepository.getAllPhrases()
    }

    private suspend fun getPhrasesByCategory(phraseCategory: String): List<Phrase> {
        return phraseRepository.getPhrasesByCategory(phraseCategory)
    }

    fun insert(phrase: Phrase) = viewModelScope.launch {
        phraseRepository.insert(phrase)

    }

    fun delete(deletePhrase: String) = viewModelScope.launch {
        phraseRepository.deletePhrase(deletePhrase)
    }

    // View Model factory used for creating the shared view model across fragments
    @Suppress("UNCHECKED_CAST")
    class MainViewModelFactory constructor(private val phraseRepository: PhraseRepository,
                                           private val categorySetting: String): ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>)
                : T = MainViewModel(phraseRepository, categorySetting) as T
    }




}