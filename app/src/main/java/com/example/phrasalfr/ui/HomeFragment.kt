package com.example.phrasalfr.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.phrasalfr.PhrasalFRApplication
import com.example.phrasalfr.R
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

    private lateinit var mStartQuizButton: Button

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

        mStartQuizButton = binding.settingStartQuizButton

    }

    private fun checkSettings() {
        val sharedPref = activity?.getSharedPreferences(
            getString(R.string.quiz_settings_sharedPrefs), Context.MODE_PRIVATE)

        val questionSetting = sharedPref?.getString(getString(R.string.question_format_key), "default")
        val answerSetting = sharedPref?.getString(getString(R.string.answer_format_key), "default")
        val categorySetting = sharedPref?.getString(getString(R.string.phrase_category_key),"Vocabulary")

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

        mStartQuizButton.setOnClickListener {
//            view?.findNavController()?.navigate(R.id.navigation_quiz)
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}