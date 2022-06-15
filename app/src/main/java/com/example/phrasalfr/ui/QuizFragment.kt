package com.example.phrasalfr.ui

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.phrasalfr.PhrasalFRApplication
import com.example.phrasalfr.R
import com.example.phrasalfr.database.Phrase
import com.example.phrasalfr.databinding.FragmentQuizBinding
import com.example.phrasalfr.util.PhrasalUtil
import com.google.mlkit.nl.translate.Translator
import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception

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

    private lateinit var mAnswerLinearLayoutA: LinearLayout
    private lateinit var mAnswerLinearLayoutB: LinearLayout
    private lateinit var mAnswerLinearLayoutC: LinearLayout
    private lateinit var mAnswerLinearLayoutD: LinearLayout

    private lateinit var mAnswerImageButtonA: ImageButton
    private lateinit var mAnswerImageButtonB: ImageButton
    private lateinit var mAnswerImageButtonC: ImageButton
    private lateinit var mAnswerImageButtonD: ImageButton

    private lateinit var mSubmitButton: Button

    private lateinit var mAllPhrases: List<Phrase>
    private lateinit var mQuestionPhrase: Phrase
    private lateinit var mAnswerPhrasesSet: MutableSet<Phrase>
    private lateinit var mAnswerPhrasesIndexArray: IntArray

    private lateinit var mPhraseCategory: String

    private lateinit var mAnswerPhraseA: Phrase
    private lateinit var mAnswerPhraseB: Phrase
    private lateinit var mAnswerPhraseC: Phrase
    private lateinit var mAnswerPhraseD: Phrase

    private lateinit var mMainViewModel: MainViewModel
    private lateinit var mPhrasalUtil: PhrasalUtil
    private lateinit var mTranslator: Translator
    private lateinit var mTextToSpeech: TextToSpeech

    private lateinit var mQuestionSetting: String
    private lateinit var mAnswerSetting: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getSharedPrefs()
        setUpViewModel()
        setUpUtils()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        val root: View = binding.root

        linkViews()
        setupQuiz() // This logic should be in the View Model
        setOnClicks()


        return root
    }


    private fun linkViews() {

        mQuestionTextView = binding.quizQuestionTextView
        mQuestionImageButton = binding.quizQuestionImageButton

        mSubmitButton = binding.quizSubmitButton

        mAnswerTextViewA = binding.quizAnswerALinearLayoutTextView
        mAnswerTextViewB = binding.quizAnswerBLinearLayoutTextView
        mAnswerTextViewC = binding.quizAnswerCLinearLayoutTextView
        mAnswerTextViewD = binding.quizAnswerDLinearLayoutTextView

        mAnswerLinearLayoutA = binding.quizAnswerALinearLayout
        mAnswerLinearLayoutB = binding.quizAnswerBLinearLayout
        mAnswerLinearLayoutC = binding.quizAnswerCLinearLayout
        mAnswerLinearLayoutD = binding.quizAnswerDLinearLayout

    }

    private fun setupQuiz() {

        try{
            mMainViewModel.buildQuestion()
            mMainViewModel.generateAnswerPhrases()
            mQuestionPhrase = mMainViewModel.getQuestionPhrase()
            mAnswerPhrasesSet = mMainViewModel.getAnswerPhraseSet()
            mAnswerPhrasesIndexArray = mMainViewModel.getAnswerPhrasesIndexArray()
            formatQuizUI()
        } catch (e: Exception) {
            Log.i("mTAG", e.toString())
        }


    }

    private fun setOnClicks(){

        mQuestionImageButton.setOnClickListener {
            val frenchText = mQuestionPhrase.phraseFrench.toString()
            mPhrasalUtil.useTextToSpeech(frenchText)

        }

        // If setting is French Text, Also play the audio!

        mAnswerLinearLayoutA.setOnClickListener {
            mAnswerLinearLayoutA.background = resources.getDrawable(R.drawable.rounded_corner_selected, context?.theme)
            mAnswerLinearLayoutB.background = resources.getDrawable(R.drawable.rounded_corner, context?.theme)
            mAnswerLinearLayoutC.background = resources.getDrawable(R.drawable.rounded_corner, context?.theme)
            mAnswerLinearLayoutD.background = resources.getDrawable(R.drawable.rounded_corner, context?.theme)

            if(mAnswerSetting.toString() == getString(R.string.answer_format_value_french_audio)) {
                val frenchText = mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[0]).phraseFrench
                mPhrasalUtil.useTextToSpeech(frenchText)
            }
        }

        mAnswerLinearLayoutB.setOnClickListener {
            mAnswerLinearLayoutA.background = resources.getDrawable(R.drawable.rounded_corner, context?.theme)
            mAnswerLinearLayoutB.background = resources.getDrawable(R.drawable.rounded_corner_selected, context?.theme)
            mAnswerLinearLayoutC.background = resources.getDrawable(R.drawable.rounded_corner, context?.theme)
            mAnswerLinearLayoutD.background = resources.getDrawable(R.drawable.rounded_corner, context?.theme)

            if(mAnswerSetting.toString() == getString(R.string.answer_format_value_french_audio)) {
                val frenchText = mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[1]).phraseFrench
                mPhrasalUtil.useTextToSpeech(frenchText)
            }
        }

        mAnswerLinearLayoutC.setOnClickListener {
            mAnswerLinearLayoutA.background = resources.getDrawable(R.drawable.rounded_corner, context?.theme)
            mAnswerLinearLayoutB.background = resources.getDrawable(R.drawable.rounded_corner, context?.theme)
            mAnswerLinearLayoutC.background = resources.getDrawable(R.drawable.rounded_corner_selected, context?.theme)
            mAnswerLinearLayoutD.background = resources.getDrawable(R.drawable.rounded_corner, context?.theme)

            if(mAnswerSetting.toString() == getString(R.string.answer_format_value_french_audio)) {
                val frenchText = mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[2]).phraseFrench
                mPhrasalUtil.useTextToSpeech(frenchText)
            }
        }

        mAnswerLinearLayoutD.setOnClickListener {
            mAnswerLinearLayoutA.background = resources.getDrawable(R.drawable.rounded_corner, context?.theme)
            mAnswerLinearLayoutB.background = resources.getDrawable(R.drawable.rounded_corner, context?.theme)
            mAnswerLinearLayoutC.background = resources.getDrawable(R.drawable.rounded_corner, context?.theme)
            mAnswerLinearLayoutD.background = resources.getDrawable(R.drawable.rounded_corner_selected, context?.theme)

            if(mAnswerSetting.toString() == getString(R.string.answer_format_value_french_audio)) {
                val frenchText = mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[3]).phraseFrench
                mPhrasalUtil.useTextToSpeech(frenchText)
            }
        }

        mSubmitButton.setOnClickListener{
            setupQuiz()
        }
    }



    private fun formatQuizUI() {

        Log.i("qTAG", mQuestionSetting.toString())

        // Controls the visibility of the Question as Text or the Audio image button depending on the User settings
        // When statement wasn't working for some reason.
        if (mQuestionSetting.toString() == getString(R.string.question_format_value_english_text)) {
            mQuestionTextView.visibility = View.VISIBLE
            mQuestionImageButton.visibility = View.GONE
            mQuestionTextView.text = mQuestionPhrase.phraseEnglish
        }

        if (mQuestionSetting.toString() == getString(R.string.question_format_value_french_text)) {
            mQuestionTextView.visibility = View.VISIBLE
            mQuestionImageButton.visibility = View.GONE
            mQuestionTextView.text = mQuestionPhrase.phraseFrench
        }
        if (mQuestionSetting.toString() == getString(R.string.answer_format_value_french_audio)) {
            mQuestionTextView.visibility = View.INVISIBLE
            mQuestionImageButton.visibility = View.VISIBLE
        }

        if(mAnswerSetting.toString() == getString(R.string.answer_format_value_english_text)) {
            mAnswerTextViewA.text = mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[0]).phraseEnglish
            mAnswerTextViewB.text = mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[1]).phraseEnglish
            mAnswerTextViewC.text = mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[2]).phraseEnglish
            mAnswerTextViewD.text = mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[3]).phraseEnglish
        }

        if(mAnswerSetting.toString() == getString(R.string.answer_format_value_french_text)) {
            mAnswerTextViewA.text = mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[0]).phraseFrench
            mAnswerTextViewB.text = mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[1]).phraseFrench
            mAnswerTextViewC.text = mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[2]).phraseFrench
            mAnswerTextViewD.text = mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[3]).phraseFrench
        }

        if(mAnswerSetting.toString() == getString(R.string.answer_format_value_french_audio)) {
            mAnswerTextViewA.text = ""
            mAnswerTextViewB.text = ""
            mAnswerTextViewC.text = ""
            mAnswerTextViewD.text = ""

            mAnswerTextViewA.background = resources.getDrawable(R.drawable.ic_baseline_volume_up_24, context?.theme)
            mAnswerTextViewB.background = resources.getDrawable(R.drawable.ic_baseline_volume_up_24, context?.theme)
            mAnswerTextViewC.background = resources.getDrawable(R.drawable.ic_baseline_volume_up_24, context?.theme)
            mAnswerTextViewD.background = resources.getDrawable(R.drawable.ic_baseline_volume_up_24, context?.theme)
        }

    }



    private fun getSharedPrefs() {
        val sharedPref = activity?.getSharedPreferences(
            getString(R.string.quiz_settings_sharedPrefs), Context.MODE_PRIVATE
        )

        mQuestionSetting = sharedPref?.getString(getString(R.string.question_format_key), "default").toString()
        mAnswerSetting = sharedPref?.getString(getString(R.string.answer_format_key), "default").toString()

    }


    private fun setUpViewModel() {

        mMainViewModel = ViewModelProvider(
            this,
            MainViewModel.MainViewModelFactory((activity?.application as PhrasalFRApplication).repository,
                mQuestionSetting,
                mAnswerSetting ))
            .get(MainViewModel::class.java)
    }

    private fun setUpUtils() {
        mPhrasalUtil = PhrasalUtil(context)
        mTranslator = mPhrasalUtil.getTranslator()
        mTextToSpeech = mPhrasalUtil.getTextToSpeech()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}