package com.example.phrasalfr.ui

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.phrasalfr.PhrasalFRApplication
import com.example.phrasalfr.databinding.FragmentPhrasesBinding
import com.example.phrasalfr.util.PhrasalUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.nl.translate.Translator

class PhrasesFragment : Fragment() {

    private var _binding: FragmentPhrasesBinding? = null

    private lateinit var mEnglishEditText: EditText
    private lateinit var mFrenchTextView: TextView
    private lateinit var mPhrasesFAB: FloatingActionButton
    private lateinit var mButton: Button

    private lateinit var mPhrasalUtil : PhrasalUtil
    private lateinit var mTextToSpeech: TextToSpeech
    private lateinit var mTranslator: Translator
    private lateinit var mMainViewModel: MainViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpViewModel()

        mPhrasalUtil = PhrasalUtil(context)
        mTextToSpeech = mPhrasalUtil.getTextToSpeech()
        mTranslator = mPhrasalUtil.getTranslator()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPhrasesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        linkViews()

        return root
    }

    private fun setUpViewModel() {
        mMainViewModel = ViewModelProvider(this,
            MainViewModel.MainViewModelFactory((activity?.application as PhrasalFRApplication).repository))
            .get(MainViewModel::class.java)
    }

    private fun linkViews() {

        mEnglishEditText = binding.phrasesEnglishEditText
        mFrenchTextView = binding.phrasesFrenchTextView
        mPhrasesFAB = binding.phrasesAddFab
        mButton = binding.phrasesTranslateButton

        mButton.setOnClickListener {
            translateEnglishToFrench()
        }


    }

    private fun translateEnglishToFrench() {

        val englishText = mEnglishEditText.text.toString()

        mTranslator.translate(englishText)
            .addOnSuccessListener {
                Log.i("mTAG", it.toString())

                mFrenchTextView.text = it.toString()

                mTextToSpeech.speak(mFrenchTextView.text.toString(),
                TextToSpeech.QUEUE_ADD,
                null)
            }
            .addOnFailureListener {
                it.printStackTrace()
                Log.i("mTAG", it.printStackTrace().toString())
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}