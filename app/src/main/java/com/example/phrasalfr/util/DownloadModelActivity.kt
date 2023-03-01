package com.example.phrasalfr.util


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.phrasalfr.MainActivity
import com.example.phrasalfr.R
import com.example.phrasalfr.databinding.ActivityDownloadModelBinding
import com.example.phrasalfr.databinding.ActivityEditDatabaseBinding
import com.example.phrasalfr.databinding.ActivityMainBinding
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow

class DownloadModelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDownloadModelBinding
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownloadModelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPrefs = getSharedPreferences(
            getString(R.string.quiz_settings_sharedPrefs), Context.MODE_PRIVATE
        )

        val isModelDownloaded: Boolean = sharedPrefs.getBoolean("is_model_downloaded", false)

        if (isModelDownloaded) {
            // Language model is already downloaded, transition to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Language model is not downloaded, show the loading screen
            downloadFrenchMLKitModel()
            coroutineScope.startLoadingAnimation()

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
                coroutineScope.cancel()
                finish()

            }
            .addOnFailureListener {
                // Error.
            }

    }

    private fun updatedSharedPrefs() {
        val sharedPrefs = getSharedPreferences(
            getString(R.string.quiz_settings_sharedPrefs), Context.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor? = sharedPrefs?.edit()
        editor?.putBoolean("is_model_downloaded", true)
        editor?.apply()

    }


    private fun CoroutineScope.startLoadingAnimation() {

        val frenchFlag1 = binding.progressFlag1
        val frenchFlag2 = binding.progressFlag2
        val frenchFlag3 = binding.progressFlag3

        val flagArray = arrayOf(frenchFlag1, frenchFlag2, frenchFlag3)

        flagArray.forEach { it.visibility = View.INVISIBLE }
        var counter = 0

        launch {
            while (true) {

                if (counter > 2) {
                    counter = 0
                    flagArray.forEach { it.visibility = View.INVISIBLE }
                    delay(650) // Wait for 1 second before starting again
                }

                flagArray[counter].visibility = View.VISIBLE

                counter += 1

                delay(650) // Wait for 1 second before displaying the next flag
            }
        }
    }
}