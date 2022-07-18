package com.example.phrasalfr.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.phrasalfr.PhrasalFRApplication
import com.example.phrasalfr.database.Phrase
import com.example.phrasalfr.databinding.FragmentPhrasesBinding
import com.example.phrasalfr.util.PhrasalUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.nl.translate.Translator
import kotlin.properties.Delegates


class PhrasesFragment : Fragment() {

    private var _binding: FragmentPhrasesBinding? = null

    private lateinit var mEnglishEditText: EditText
    private lateinit var mFrenchEditText: EditText

    private lateinit var mPhrasesFAB: FloatingActionButton
    private lateinit var mButton: Button
    private var mTranslateSuccess by Delegates.notNull<Boolean>()

    private lateinit var mPhrasalUtil : PhrasalUtil
    private lateinit var mTextToSpeech: TextToSpeech

    private lateinit var mENFRTranslator: Translator
    private lateinit var mFRENTranslator: Translator

    private var mEnglishFrenchTranslatorChoice: Boolean = true

    private lateinit var mMainViewModel: MainViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpViewModel()

        mPhrasalUtil = PhrasalUtil(context)
        mTextToSpeech = mPhrasalUtil.getTextToSpeech()

        mENFRTranslator = mPhrasalUtil.getENFRTranslator()
        mFRENTranslator = mPhrasalUtil.getFRENTranslator()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPhrasesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        linkViews()
        setOnClicks()

        return root
    }

    private fun setUpViewModel() {
        mMainViewModel = ViewModelProvider(this,
            MainViewModel.MainViewModelFactory((activity?.application as PhrasalFRApplication).phraseRepository,
            "default"))
            .get(MainViewModel::class.java)
    }

    private fun linkViews() {

        mEnglishEditText = binding.phrasesEnglishEditText
        mFrenchEditText = binding.phrasesFrenchEditText
//
//        mFrenchTextView = binding.phrasesFrenchTextView
//        mEnglishTextView = binding.phrasesEnglishTextView
//
        mPhrasesFAB = binding.phrasesAddFab
        mButton = binding.phrasesTranslateButton

    }

    private fun setOnClicks() {

        mFrenchEditText.setOnFocusChangeListener { _, hasFocus ->
            mEnglishFrenchTranslatorChoice = if (hasFocus) {

                Log.i("pTAG", "French EditText has focus: Translate French to English")
                mEnglishEditText.setText("")
                mFrenchEditText.setText("")
                false
            } else {

                Log.i("pTAG", "French EditText lost focus: Translate English to French")
                true
            }
        }

        mEnglishEditText.setOnFocusChangeListener { _, hasFocus ->
            mEnglishFrenchTranslatorChoice = if (hasFocus) {

                Log.i("pTAG", "English EditText has focus: Translate English to French")
                mEnglishEditText.setText("")
                mFrenchEditText.setText("")
                true
            }
            else {

                Log.i("pTAG", "English EditText lost focus: Translate French to English")
                false
            }
        }

        mButton.setOnClickListener {
            if(mEnglishFrenchTranslatorChoice) {
                translateEnglishToFrench()
            }
            else {
                translateFrenchToEnglish()
            }
        }

        mPhrasesFAB.setOnClickListener {
            addPhraseToDB()
        }

//        // Clears the EditText on first click
//        mEnglishEditText.onFocusChangeListener = View.OnFocusChangeListener { _, _ ->
//            mEnglishEditText.setText("")
//        }
    }

    @Suppress("DEPRECATION")
    private fun translateEnglishToFrench() {

        val englishText = mEnglishEditText.text.toString()

        mENFRTranslator.translate(englishText)
            .addOnSuccessListener {
                Log.i("mTAG", it.toString())

                mFrenchEditText.setText(it.toString())

                mTextToSpeech.speak(mFrenchEditText.text.toString(),
                TextToSpeech.QUEUE_ADD,
                null)

                mTranslateSuccess = true
            }
            .addOnFailureListener {
                it.printStackTrace()
                Log.i("mTAG", it.printStackTrace().toString())

                mTranslateSuccess = false
            }

    }

    @Suppress("DEPRECATION")
    private fun translateFrenchToEnglish() {

        val frenchText = mFrenchEditText.text.toString()

        mFRENTranslator.translate(frenchText)
            .addOnSuccessListener {
                Log.i("mTAG", it.toString())

                mEnglishEditText.setText(it.toString())

                mTextToSpeech.speak(mFrenchEditText.text.toString(),
                    TextToSpeech.QUEUE_ADD,
                    null)

                mTranslateSuccess = true
            }
            .addOnFailureListener {
                it.printStackTrace()
                Log.i("mTAG", it.printStackTrace().toString())

                mTranslateSuccess = false
            }

    }

    private fun addPhraseToDB() {

        if (mTranslateSuccess) {
            val englishText = mEnglishEditText.text.toString()
            val frenchText = mFrenchEditText.text.toString()

            val phrase = Phrase("User Phrase",englishText, frenchText)

            mMainViewModel.insert(phrase)

            Toast.makeText(context,"Phrase Added!", Toast.LENGTH_SHORT).show()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}