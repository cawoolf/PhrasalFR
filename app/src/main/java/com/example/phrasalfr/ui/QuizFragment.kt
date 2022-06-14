package com.example.phrasalfr.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.phrasalfr.PhrasalFRApplication
import com.example.phrasalfr.R
import com.example.phrasalfr.database.Phrase
import com.example.phrasalfr.databinding.FragmentQuizBinding
import kotlinx.coroutines.launch

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mQuestionTextView: TextView
    private lateinit var mQuestionImageButton: ImageButton

    private lateinit var mAnswerTextViewA: TextView
    private lateinit var mAnswerTextViewB: TextView
    private lateinit var mAnswerTextViewC: TextView
    private lateinit var mAnswerTextViewD: TextView

    private lateinit var mAnswerImageButtonA: ImageButton
    private lateinit var mAnswerImageButtonB: ImageButton
    private lateinit var mAnswerImageButtonC: ImageButton
    private lateinit var mAnswerImageButtonD: ImageButton


    private lateinit var mAllPhrases: List<Phrase>
    private lateinit var mQuestionPhrase: Phrase
    private lateinit var mAnswerPhrasesSet: MutableSet<Phrase>
    private lateinit var mAnswerPhrasesIndexArray: IntArray

    private lateinit var mEnglishText: String
    private lateinit var mFrenchText: String
    private lateinit var mPhraseCategory: String


    private lateinit var mAnswerPhraseA: Phrase
    private lateinit var mAnswerPhraseB: Phrase
    private lateinit var mAnswerPhraseC: Phrase
    private lateinit var mAnswerPhraseD: Phrase

    private lateinit var mMainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        val root: View = binding.root

        linkViews()

        lifecycleScope.launch {

            mAllPhrases = mMainViewModel.getAllPhrases()

            val sharedPref = activity?.getSharedPreferences(
                getString(R.string.quiz_settings_sharedPrefs), Context.MODE_PRIVATE
            )

            val questionSetting =
                sharedPref?.getString(getString(R.string.question_format_key), "default")
            val answerSetting =
                sharedPref?.getString(getString(R.string.answer_format_key), "default")

            try {
                buildQuestion(questionSetting.toString())
//            generateAnswerPhrases()
                formatQuizUI(questionSetting.toString(), answerSetting.toString())
            }
            catch (e: Exception) {
                Log.i("mTAG", e.toString())
            }


        }


        return root
    }


    private fun linkViews() {

        mQuestionTextView = binding.quizQuestionTextView
        mQuestionImageButton = binding.quizQuestionImageButton

        mAnswerTextViewA = binding.quizAnswerALinearLayoutTextView
        mAnswerTextViewB = binding.quizAnswerBLinearLayoutTextView
        mAnswerTextViewC = binding.quizAnswerCLinearLayoutTextView
        mAnswerTextViewD = binding.quizAnswerDLinearLayoutTextView

    }

    private fun buildQuestion(questionSetting: String) {

        val randIndex = (mAllPhrases.indices).random()
        val randomPhrase = mAllPhrases[randIndex]
        mQuestionPhrase = randomPhrase

        mEnglishText = randomPhrase.phraseEnglish
        mFrenchText = randomPhrase.phraseFrench
        mPhraseCategory = randomPhrase.category


        if (questionSetting.toString() == getString(R.string.question_format_value_english_text)) {
            mQuestionTextView.text = mEnglishText
        }

        if (questionSetting.toString() == getString(R.string.question_format_value_french_text)) {
            mQuestionTextView.text = mFrenchText
        }

    }

    private fun generateAnswerPhrases() {

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
        Log.i("qTAG", "Question: " + mQuestionTextView.text)
        Log.i("qTAG", "A: " + mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[0]).phraseEnglish) // Index 0 will always be the question itself
        Log.i("qTAG", "B: " + mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[1]).phraseEnglish)
        Log.i("qTAG", "C: " + mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[2]).phraseEnglish)
        Log.i("qTAG", "D: " + mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[3]).phraseEnglish)



    }

    private fun formatQuizUI(questionSetting: String, answerSetting: String) {


        Log.i("qTAG", questionSetting.toString())

        // Controls the visibility of the Question as Text or the Audio image button depending on the User settings
        // When statement wasn't working for some reason.
        if (questionSetting.toString() == getString(R.string.question_format_value_english_text)) {
            mQuestionTextView.visibility = View.VISIBLE
            mQuestionImageButton.visibility = View.GONE
        }

        if (questionSetting.toString() == getString(R.string.question_format_value_french_text)) {
            mQuestionTextView.visibility = View.VISIBLE
            mQuestionImageButton.visibility = View.GONE
        }
        if (questionSetting.toString() == getString(R.string.answer_format_value_french_audio)) {
            mQuestionTextView.visibility = View.INVISIBLE
            mQuestionImageButton.visibility = View.VISIBLE
        }

//        if(answerSetting.toString() == getString(R.string.answer_format_value_english_text)) {
//            mAnswerTextViewA.text = mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[0]).phraseEnglish
//            mAnswerTextViewB.text = mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[1]).phraseEnglish
//            mAnswerTextViewC.text = mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[2]).phraseEnglish
//            mAnswerTextViewD.text = mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[3]).phraseEnglish
//        }
//
//        if(answerSetting.toString() == getString(R.string.answer_format_value_french_text)) {
//            mAnswerTextViewA.text = mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[0]).phraseFrench
//            mAnswerTextViewB.text = mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[1]).phraseFrench
//            mAnswerTextViewC.text = mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[2]).phraseFrench
//            mAnswerTextViewD.text = mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[3]).phraseFrench
//        }

    }


    private fun setUpViewModel() {

        mMainViewModel = ViewModelProvider(
            this,
            MainViewModel.MainViewModelFactory((activity?.application as PhrasalFRApplication).repository)
        )
            .get(MainViewModel::class.java)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}