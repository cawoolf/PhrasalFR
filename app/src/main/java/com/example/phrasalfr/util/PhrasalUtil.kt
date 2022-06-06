package com.example.phrasalfr.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import org.json.JSONObject
import java.util.*

class PhrasalUtil(val context: Context?) {

    private lateinit var mTextToSpeech: TextToSpeech
    private lateinit var mTranslator: Translator


    fun sayHello() {
        Log.i("uTAG", "Hello")
    }

    private fun MLKitTranslate() {
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

        mTranslator = englishFrenchTranslator
    }


    // Sets up the TextToSpeech function for use in the Fragment
    private fun setUpTTS() {
        mTextToSpeech = TextToSpeech(context, TextToSpeech.OnInitListener {
            if(it== TextToSpeech.SUCCESS){
                mTextToSpeech.language = Locale.CANADA_FRENCH
                mTextToSpeech.setSpeechRate(1.0f)
                Log.i("mTTS", "TTS Success")
            }
            if(it== TextToSpeech.ERROR) {
                Log.i("mTTS", "TTS Failed" + TextToSpeech.ERROR)
            }
        })

    }

    fun getTextToSpeech(): TextToSpeech {
        setUpTTS()
        return mTextToSpeech
    }

    fun getTranslator() : Translator {
        MLKitTranslate()
        return mTranslator
    }



//    fun testTransltor() {
//        mTranslator.translate("Hello")
//            .addOnSuccessListener {
//                Log.i("mTAG", it.toString())
//            }
//            .addOnFailureListener {
//                it.printStackTrace()
//                Log.i("mTAG", it.printStackTrace().toString())
//            }
//    }
}