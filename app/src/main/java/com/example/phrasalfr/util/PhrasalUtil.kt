package com.example.phrasalfr.util

import android.content.Context
import android.content.SharedPreferences
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.phrasalfr.R
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.*
import java.util.*


class PhrasalUtil(val context: Context?) {

    private lateinit var mTextToSpeech: TextToSpeech
    private lateinit var mENFRTranslator: Translator
    private lateinit var mFRENTranslator: Translator

    private fun buildTranslators() {
        // Create an English-French Translator
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.FRENCH)
            .build()

        val englishFrenchTranslator = Translation.getClient(options)

        mENFRTranslator = englishFrenchTranslator

        val options2 = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.FRENCH)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()

        val frenchEnglishTranslator = Translation.getClient(options2)
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
        if (!mTextToSpeech.isSpeaking) {

        mTextToSpeech.setSpeechRate(1.0F)
        mTextToSpeech.speak(frenchText,
            TextToSpeech.QUEUE_ADD,
            null)
        }
    }

    fun getTextToSpeech(): TextToSpeech {
        setUpTTS()
        return mTextToSpeech
    }

    fun getENFRTranslator() : Translator {
        buildTranslators()
        return mENFRTranslator
    }

    fun getFRENTranslator() : Translator {
        buildTranslators()
        return mFRENTranslator
    }


}