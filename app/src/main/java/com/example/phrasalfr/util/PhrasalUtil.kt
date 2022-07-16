package com.example.phrasalfr.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.*

class PhrasalUtil(val context: Context?) {

    private lateinit var mTextToSpeech: TextToSpeech
    private lateinit var mENFRTranslator: Translator
    private lateinit var mFRENTranslator: Translator


    private fun mlKitTranslate() {
        // Create an English-French Translator
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.FRENCH)
            .build()

        val englishFrenchTranslator = Translation.getClient(options)
//
        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        Log.i("mTAG", "starting download")

        englishFrenchTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                // Model downloaded successfully. Okay to start translating.
                // (Set a flag, unhide the translation UI, etc.)
                Log.i(
                    "mTAG",
                    "Download finished"
                ) // Only sets the on click if the model has already been downloaded.
//                testTransltor()

            }
            .addOnFailureListener { exception ->
                // Model couldn’t be downloaded or other internal error.
                Log.i("mTAG", exception.toString())
            }

        mENFRTranslator = englishFrenchTranslator

        val options2 = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.FRENCH)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()

        val frenchEnglishTranslator = Translation.getClient(options2)
        frenchEnglishTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                // Model downloaded successfully. Okay to start translating.
                // (Set a flag, unhide the translation UI, etc.)
                Log.i(
                    "mTAG",
                    "Download finished"
                ) // Only sets the on click if the model has already been downloaded.
//                testTransltor()

            }
            .addOnFailureListener { exception ->
                // Model couldn’t be downloaded or other internal error.
                Log.i("mTAG", exception.toString())
            }

        mFRENTranslator = frenchEnglishTranslator

    }


    // Sets up the TextToSpeech function for use in the Fragment
    private fun setUpTTS() {
        mTextToSpeech = TextToSpeech(context, TextToSpeech.OnInitListener {
            if(it== TextToSpeech.SUCCESS){
                mTextToSpeech.language = Locale.FRENCH
                mTextToSpeech.setSpeechRate(1.0f)
                Log.i("mTTS", "TTS Success")
            }
            if(it== TextToSpeech.ERROR) {
                Log.i("mTTS", "TTS Failed" + TextToSpeech.ERROR)
            }
        })

    }

    fun useTextToSpeech(frenchText: String) {
        mTextToSpeech.setSpeechRate(1.0F)
        mTextToSpeech.speak(frenchText,
            TextToSpeech.QUEUE_ADD,
            null)
    }

    fun getTextToSpeech(): TextToSpeech {
        setUpTTS()
        return mTextToSpeech
    }

    fun getENFRTranslator() : Translator {
        mlKitTranslate()
        return mENFRTranslator
    }

    fun getFRENTranslator() : Translator {
        mlKitTranslate()
        return mFRENTranslator
    }

    fun startTranslatorDownload() {
        mlKitTranslate()
    }

}