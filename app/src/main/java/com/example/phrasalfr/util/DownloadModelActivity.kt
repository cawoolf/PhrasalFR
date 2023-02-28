package com.example.phrasalfr.util


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.phrasalfr.MainActivity
import com.example.phrasalfr.R
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel


class DownloadModelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.phrasalfr.R.layout.activity_download_model)

        val sharedPrefs = getSharedPreferences(
            getString(R.string.quiz_settings_sharedPrefs), Context.MODE_PRIVATE)

        val isModelDownloaded: Boolean = sharedPrefs.getBoolean("is_model_downloaded", false)

        if (isModelDownloaded) {
            // Language model is already downloaded, transition to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        else {
            // Language model is not downloaded, show the loading screen
            setContentView(R.layout.activity_download_model)
            downloadFrenchMLKitModel()
        }
    }

    private fun downloadFrenchMLKitModel() {

        Log.i("mTAG", "starting download")
        val modelManager = RemoteModelManager.getInstance()

        // Get translation models stored on the device.
        modelManager.getDownloadedModels(TranslateRemoteModel::class.java)
            .addOnSuccessListener { models ->
                updatedSharedPrefs()
            }
            .addOnFailureListener {
                // Error.
            }
    // Download the French model.
        val frenchModel = TranslateRemoteModel.Builder(TranslateLanguage.FRENCH).build()
        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        modelManager.download(frenchModel, conditions)
            .addOnSuccessListener {
                updatedSharedPrefs()
                Log.i("mTAG", "Download Finished")

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

            }
            .addOnFailureListener {
                // Error.
            }

    }

    private fun updatedSharedPrefs() {
        val sharedPrefs = getSharedPreferences(
            getString(R.string.quiz_settings_sharedPrefs), Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor? = sharedPrefs?.edit()
        editor?.putBoolean("is_model_downloaded", true)
        editor?.apply()
    }
}