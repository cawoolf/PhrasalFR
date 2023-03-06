package com.example.phrasalfr.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.phrasalfr.R
import com.example.phrasalfr.database.Phrase
import com.example.phrasalfr.databinding.FragmentPhrasesBinding
import com.example.phrasalfr.util.PhrasalUtil
import com.google.android.material.chip.Chip
import com.google.mlkit.nl.translate.Translator
import java.util.*
import kotlin.properties.Delegates


class PhrasesFragment : Fragment() {

    private var _binding: FragmentPhrasesBinding? = null

    private lateinit var mEnglishEditText: EditText
    private lateinit var mFrenchEditText: EditText

    private lateinit var mSpeakButton: ImageView
    private lateinit var mAddButton: RelativeLayout
    private lateinit var mEditButton: RelativeLayout

    private var mTranslateSuccess by Delegates.notNull<Boolean>()

    private lateinit var mPhrasalUtil : PhrasalUtil
    private lateinit var mTextToSpeech: TextToSpeech

    private lateinit var mENFRTranslator: Translator
    private lateinit var mFRENTranslator: Translator

    private var mEnglishFrenchTranslatorChoice: Boolean = true

    private val mMainViewModel: MainViewModel by activityViewModels<MainViewModel>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPhrasalUtil = PhrasalUtil(context)
        mPhrasalUtil.setUpTTS()

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
        setUpEditTextTranslators()

        return root
    }

    override fun onResume() {
        super.onResume()

        // Quick fix. The app was crashing when you resumed the Fragment with text already populated
        // and tried to add it to the DB
        mEnglishEditText.text.clear()
        mFrenchEditText.text.clear()
    }


    private fun linkViews() {

        mEnglishEditText = binding.phrasesEnglishEditText
        mFrenchEditText = binding.phrasesFrenchEditText

        mSpeakButton = binding.phrasesSpeakButton
        mAddButton = binding.addPhraseToQuizButton
        mEditButton = binding.viewAllPhrasesButton

    }

    private fun setOnClicks() {

        mAddButton.setOnClickListener {

            var translationDataValid = checkTranslationData()
//            translationDataValid = false
            if(translationDataValid) {
                addPhraseToDB()
            }

        }

        mEditButton.setOnClickListener {
            val intent = Intent (activity, EditDatabaseActivity::class.java)
            activity?.startActivity(intent)
        }
        mFrenchEditText.text.toString()
        mSpeakButton.setOnClickListener {
           mPhrasalUtil.useTextToSpeech( mFrenchEditText.text.toString())
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpEditTextTranslators() {
        val frenchTextWatcher: TextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                translateFrenchToEnglish()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // your logic here
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // your logic here
            }
        }

        val englishTextWatcher: TextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                translateEnglishToFrench()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // your logic here
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // your logic here
            }
        }

        mFrenchEditText.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        mEnglishEditText.removeTextChangedListener(englishTextWatcher)
                        mFrenchEditText.addTextChangedListener(frenchTextWatcher)
                        Log.i("mTAG", "French Edit Text touched")}

                }

                return v?.onTouchEvent(event) ?: true
            }
        })


        mEnglishEditText.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        mFrenchEditText.removeTextChangedListener(frenchTextWatcher)
                        mEnglishEditText .addTextChangedListener(englishTextWatcher)
                        Log.i("mTAG", "English Edit Text touched")}
                }

                return v?.onTouchEvent(event) ?: true
            }
        })
    }

    private fun checkTranslationData() : Boolean {

        Log.i("dataTAG", mEnglishEditText.text.length.toString())
        Log.i("dataTAG", mFrenchEditText.text.length.toString())
        val symbols = "0123456789}]/:%&^*()+$><+=#@_]"
        val punctuation = ".!?"


        if(mEnglishEditText.text.toString().any { it in symbols}
            || mFrenchEditText.text.toString().any {it in symbols})
        {
            Toast.makeText(context, "Translation text has invalid characters!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (mEnglishEditText.text.toString().isEmpty()
            || mFrenchEditText.text.toString().isEmpty()) {

            Toast.makeText(context, "Translation text is empty!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (mEnglishEditText.text.length > 75
            || mFrenchEditText.text.length > 80)
        {
            Toast.makeText(context, "Translation text exceeds the character limit!", Toast.LENGTH_SHORT).show()
            return false
        }

        else {
            return true
        }

    }


    @Suppress("DEPRECATION")
    private fun translateEnglishToFrench() {

        val englishText = mEnglishEditText.text.toString()

        mENFRTranslator.translate(englishText)
            .addOnSuccessListener {
                Log.i("mTAG", it.toString())

                mFrenchEditText.setText(it.toString())

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

                mTranslateSuccess = true
            }
            .addOnFailureListener {
                it.printStackTrace()
                Log.i("mTAG", it.printStackTrace().toString())

                mTranslateSuccess = false
            }

    }

    private fun addPhraseToDB() {


        val category = getString(R.string.phrases_category)

        if (mTranslateSuccess) {
            val englishText = mEnglishEditText.text.toString()
            val frenchText = mFrenchEditText.text.toString()

            val phrase = Phrase(category, englishText, frenchText)

            mMainViewModel.insert(phrase)

            Toast.makeText(context, "Phrase Added!", Toast.LENGTH_SHORT).show()

        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}