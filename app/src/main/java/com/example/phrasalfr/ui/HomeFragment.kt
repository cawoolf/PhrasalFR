package com.example.phrasalfr.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.phrasalfr.PhrasalFRApplication
import com.example.phrasalfr.R
import com.example.phrasalfr.databinding.FragmentHomeBinding
import com.example.phrasalfr.databinding.FragmentHomeSimpleBinding
import com.google.android.material.chip.Chip


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeSimpleBinding? = null
    private val binding get() = _binding!!     // This property is only valid between onCreateView and onDestroyView

    // Chips for selecting Category of phrases
    // How to do that exactly? If it's dynamically generated from the db

    // Radio Buttons for formatting quiz Question
    private lateinit var mQuestionEnglishText: RadioButton
    private lateinit var mQuestionFrenchText: RadioButton
    private lateinit var mQuestionFrenchAudio: RadioButton

    // Radio Buttons for formatting quiz Answer
    private lateinit var mAnswerEnglishText: RadioButton
    private lateinit var mAnswerFrenchText: RadioButton
    private lateinit var mAnswerFrenchAudio: RadioButton

    // Chips for phrase categories
    private lateinit var mGreetingsChip: Chip
    private lateinit var mGrammarChip: Chip
    private lateinit var mAllPhrasesChip: Chip
    private lateinit var mUserPhrasesChip: Chip

    private lateinit var mPhrasesChip: Chip
    private lateinit var mVocabularyChip: Chip

    private lateinit var mSaveSettingsButton: Button

    private lateinit var mMainViewModel : MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeSimpleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        linkViews()
        checkSettings()
        setOnClicks()

        return root
    }

    override fun onResume() {
        super.onResume()
        checkSettings()

    }

    private fun setUpViewModel() {

        mMainViewModel = ViewModelProvider(this,
            MainViewModel.MainViewModelFactory((activity?.application as PhrasalFRApplication).phraseRepository,
            "default"))
            .get(MainViewModel::class.java)
    }

    private fun linkViews() {

        mQuestionEnglishText = binding.settingsQuestionEnglishTextRadioButton
        mQuestionFrenchText = binding.settingsQuestionFrenchTextRadioButton
        mQuestionFrenchAudio = binding.settingsQuestionFrenchAudioRadioButton

        mAnswerEnglishText = binding.settingsAnswerEnglishTextRadioButton
        mAnswerFrenchText = binding.settingsAnswerFrenchTextRadioButton
        mAnswerFrenchAudio = binding.settingsAnswerFrenchAudioRadioButton


        mPhrasesChip = binding.settingsPhrasesChip
        mVocabularyChip = binding.settingsVocabularyChip

//        mGreetingsChip = binding.settingsGreetingsChip
//        mGrammarChip = binding.settingsGrammarChip
//        mAllPhrasesChip = binding.settingsAllPhrasesChip
//        mUserPhrasesChip = binding.settingsUserPhrasesChip

//        mSaveSettingsButton = binding.settingSaveQuizButton

    }

    private fun checkSettings() {
        val sharedPref = activity?.getSharedPreferences(
            getString(R.string.quiz_settings_sharedPrefs), Context.MODE_PRIVATE)

        val questionSetting = sharedPref?.getString(getString(R.string.question_format_key), "default")
        val answerSetting = sharedPref?.getString(getString(R.string.answer_format_key), "default")
        val categorySetting = sharedPref?.getString(getString(R.string.phrase_category_key),"default")

        when (questionSetting){
            getString(R.string.question_format_value_english_text) -> mQuestionEnglishText.isChecked = true
            getString(R.string.question_format_value_french_text) -> mQuestionFrenchText.isChecked = true
            getString(R.string.question_format_value_french_audio) -> mQuestionFrenchAudio.isChecked = true
        }

        when (answerSetting) {
            getString(R.string.answer_format_value_english_text) -> mAnswerEnglishText.isChecked = true
            getString(R.string.answer_format_value_french_text) -> mAnswerFrenchText.isChecked = true
            getString(R.string.answer_format_value_french_audio) -> mAnswerFrenchAudio.isChecked = true
        }

        when(categorySetting) {
            getString(R.string.user_phrases_category) -> mUserPhrasesChip.isChecked = true
            getString(R.string.grammar_category) -> mGrammarChip.isChecked = true
            getString(R.string.greetings_category) -> mGreetingsChip.isChecked = true
            getString(R.string.all_phrases_category) -> mAllPhrasesChip.isChecked = true
        }

    }

    private fun setOnClicks() {

        // Sets the Quiz Settings to sharedPrefs by handling click events on the settings UI
        val sharedPref = activity?.getSharedPreferences(
            getString(R.string.quiz_settings_sharedPrefs), Context.MODE_PRIVATE)

        val editor: SharedPreferences.Editor? = sharedPref?.edit()

        mQuestionEnglishText.setOnClickListener{
            editor?.putString(getString(R.string.question_format_key), getString(R.string.question_format_value_english_text))
            editor?.apply()
        }

        mQuestionFrenchText.setOnClickListener {
            editor?.putString(getString(R.string.question_format_key), getString(R.string.question_format_value_french_text))
            editor?.apply()
        }

        mQuestionFrenchAudio.setOnClickListener {
            editor?.putString(getString(R.string.question_format_key), getString(R.string.question_format_value_french_audio))
            editor?.apply()
        }

        mAnswerEnglishText.setOnClickListener {
            editor?.putString(getString(R.string.answer_format_key), getString(R.string.answer_format_value_english_text))
            editor?.apply()
        }

        mAnswerFrenchText.setOnClickListener {
            editor?.putString(getString(R.string.answer_format_key), getString(R.string.answer_format_value_french_text))
            editor?.apply()
        }

        mAnswerFrenchAudio.setOnClickListener {
            editor?.putString(getString(R.string.answer_format_key), getString(R.string.answer_format_value_french_audio))
            editor?.apply()
        }

        mPhrasesChip.setOnClickListener {
            editor?.putString(getString(R.string.phrase_category_key), getString(R.string.phrases_category))
        }

        mVocabularyChip.setOnClickListener {
            editor?.putString(getString(R.string.phrase_category_key), getString(R.string.vocabulary_category))
        }

//        mUserPhrasesChip.setOnClickListener {
//            editor?.putString(getString(R.string.phrase_category_key), getString(R.string.user_phrases_category))
//            editor?.apply()
//        }

//        mGrammarChip.setOnClickListener {
//            editor?.putString(getString(R.string.phrase_category_key), getString(R.string.grammar_category))
//            editor?.apply()
//            Log.i("homeTAG", "Grammar Chip clicked")
//            Log.i("homeTAG", sharedPref?.getString(getString(R.string.phrase_category_key),"default").toString())
//        }
//
//        mGreetingsChip.setOnClickListener {
//            editor?.putString(getString(R.string.phrase_category_key), getString(R.string.greetings_category))
//            editor?.apply()
//        }
//
//        mAllPhrasesChip.setOnClickListener {
//            editor?.putString(getString(R.string.phrase_category_key), getString(R.string.all_phrases_category))
//            editor?.apply()
//        }

//        mSaveSettingsButton.setOnClickListener {
//            editor?.apply()
//        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}