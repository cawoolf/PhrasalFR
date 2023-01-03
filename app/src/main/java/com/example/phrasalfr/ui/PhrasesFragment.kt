package com.example.phrasalfr.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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

    private lateinit var mTranslateButton: Button
    private lateinit var mAddButton: Button
    private lateinit var mEditButton: Button

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
        mTextToSpeech = mPhrasalUtil.getTextToSpeech()

//        mENFRTranslator = mPhrasalUtil.getENFRTranslator()
//        mFRENTranslator = mPhrasalUtil.getFRENTranslator()

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

//        mTranslateButton= binding.phrasesTranslateButton
        mAddButton = binding.phrasesAddButton
        mEditButton = binding.phrasesEditButton

    }

    private fun setOnClicks() {

        mFrenchEditText.setOnFocusChangeListener { _, hasFocus ->
            mFRENTranslator = mPhrasalUtil.getFRENTranslator()
            mEnglishFrenchTranslatorChoice = if (hasFocus) {

                Log.i("pTAG", "French EditText has focus: Translate French to English")


                mFrenchEditText.addTextChangedListener(object : TextWatcher {

                    override fun afterTextChanged(s: Editable) {
                        translateFrenchToEnglish()
                    }

                    override fun beforeTextChanged(s: CharSequence, start: Int,
                                                   count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence, start: Int,
                                               before: Int, count: Int) {

                    }
                })

                false
            } else {

                Log.i("pTAG", "French EditText lost focus: Translate English to French")
                true
            }
        }

        mEnglishEditText.setOnFocusChangeListener { _, hasFocus ->
            mENFRTranslator = mPhrasalUtil.getENFRTranslator()
            mEnglishFrenchTranslatorChoice = if (hasFocus) {

                Log.i("pTAG", "English EditText has focus: Translate English to French")
//                mEnglishEditText.setText("")
//                mFrenchEditText.setText("")

                mEnglishEditText.addTextChangedListener(object : TextWatcher {
                    var timer = Timer()
                    val DELAY: Long = 500 // M

                    override fun afterTextChanged(s: Editable) {
                        timer.cancel()
                        timer = Timer()
                        timer.schedule(
                            object : TimerTask() {
                                override fun run() {
                                    // TODO: Do what you need here (refresh list).
                                    translateEnglishToFrench()
                                }
                            },
                            DELAY
                        )

                        translateEnglishToFrench()
                    }

                    override fun beforeTextChanged(s: CharSequence, start: Int,
                                                   count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence, start: Int,
                                               before: Int, count: Int) {

                    }
                })

                true
            }
            else {

                Log.i("pTAG", "English EditText lost focus: Translate French to English")
                false
            }
        }

//        mTranslateButton.setOnClickListener {
//            if(mEnglishFrenchTranslatorChoice) {
//                translateEnglishToFrench()
//            }
//            else {
//                translateFrenchToEnglish()
//            }
//        }

        mAddButton.setOnClickListener {

            var translationDataValid = checkTranslationData()
//            translationDataValid = false
            if(translationDataValid) {
                addPhraseToDB()
            }
            else {
                Toast.makeText(context, "Translation Text is Empty! \n Or Has an Invalid Character!", Toast.LENGTH_SHORT).show()
            }

        }

        mEditButton.setOnClickListener {
            val intent = Intent (activity, EditDatabaseActivity::class.java)
            activity?.startActivity(intent)
        }

    }

    private fun checkTranslationData() : Boolean {

        Log.i("dataTAG", mEnglishEditText.text.toString())
        Log.i("dataTAG", mFrenchEditText.text.toString())
        val symbols = "0123456789}]/:%&^*()+$><+=-#@]"
        val punctuation = ".!?"

        return !(mEnglishEditText.text.toString().any { it in symbols}
                || mFrenchEditText.text.toString().any {it in symbols}
                || mEnglishEditText.text.toString().isEmpty()
                || mFrenchEditText.text.toString().isEmpty())
//                ||mEnglishEditText.text.toString().none {it !in 'A'..'Z' && it !in 'a'..'z' && it !in punctuation}
//                || mFrenchEditText.text.toString().none {it !in 'A'..'Z' && it !in 'a'..'z'&& it !in punctuation})
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

//        buildPhraseAlertDialog()

        val category = "Vocabulary"

        if (mTranslateSuccess) {
            val englishText = mEnglishEditText.text.toString()
            val frenchText = mFrenchEditText.text.toString()

            val phrase = Phrase(category, englishText, frenchText)

            mMainViewModel.insert(phrase)

            Toast.makeText(context, "Phrase Added!", Toast.LENGTH_SHORT).show()

        }
    }

    private fun buildPhraseAlertDialog() {
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
            .create()
        val view = layoutInflater.inflate(R.layout.alert_dialog_add_phrase,null)

        var category = ""

        val vocabularyCategory = view.findViewById<Chip>(R.id.alert_vocabulary_Chip)
        val phrasesCategory = view.findViewById<Chip>(R.id.alert_phrases_Chip)

        val  dismissButton = view.findViewById<Button>(R.id.dismiss_button)
        val  approveButton = view.findViewById<Button>(R.id.approve_button)

        builder.setView(view)

        vocabularyCategory.setOnClickListener {
            category = "Vocabulary"
        }

        phrasesCategory.setOnClickListener {
            category = "Phrases"
        }

        dismissButton.setOnClickListener {
            builder.dismiss()
        }

        approveButton.setOnClickListener {

            if (category.equals("") ){
                Toast.makeText(context,"Select a Category!", Toast.LENGTH_SHORT).show()
            }

            else {

                if (mTranslateSuccess) {
                    val englishText = mEnglishEditText.text.toString()
                    val frenchText = mFrenchEditText.text.toString()

                    val phrase = Phrase(category, englishText, frenchText)

                    mMainViewModel.insert(phrase)

                    Toast.makeText(context, "Phrase Added!", Toast.LENGTH_SHORT).show()

                    builder.dismiss()

                }
            }

        }


        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}