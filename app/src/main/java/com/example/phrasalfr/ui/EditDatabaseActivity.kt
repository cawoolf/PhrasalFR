package com.example.phrasalfr.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phrasalfr.PhrasalFRApplication
import com.example.phrasalfr.database.Phrase
import com.example.phrasalfr.databinding.ActivityEditDatabaseBinding
import com.example.phrasalfr.util.PhrasalUtil
import com.example.phrasalfr.util.PhraseListAdapter
import kotlinx.coroutines.runBlocking

class EditDatabaseActivity : AppCompatActivity(), PhraseListAdapter.IAdapterDeletePhrase {

    private lateinit var recyclerView: RecyclerView

    private lateinit var mMainViewModel: MainViewModel

    private lateinit var mPhrasalUtil: PhrasalUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_edit_database)

        val binding = ActivityEditDatabaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.editDbRecyclerView

        setUpUtils()
        setUpActionBar()
        setUpViewModel()
        loadDataIntoRecyclerView()
    }

    private fun setUpUtils() {
        mPhrasalUtil = PhrasalUtil(this)
        mPhrasalUtil.setUpTTS()

    }


    private fun loadDataIntoRecyclerView() {

        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = PhraseListAdapter(getAllPhrases(), this)

    }

    private fun getAllPhrases(): List<Phrase> {

        // get all Phrases here!
        val phraseList: List<Phrase> = runBlocking { mMainViewModel.getAllPhrases() }

        return phraseList
    }

    // Because this a new activity the view model must be reinitialized?
    private fun setUpViewModel() {

        mMainViewModel = ViewModelProvider(
            this,
            MainViewModel.MainViewModelFactory(
                (application as PhrasalFRApplication).phraseRepository,
                "default"
            )
        )
            .get(MainViewModel::class.java)
    }

    override fun deletePhrase(englishPhrase: String) {
        mMainViewModel.delete(englishPhrase)

        Toast.makeText(this, "Phrase deleted!", Toast.LENGTH_SHORT).show()
        loadDataIntoRecyclerView()
    }

    override fun speakFrenchPhrase(frenchPhrase: String) {
        mPhrasalUtil.useTextToSpeech(frenchPhrase)
    }

    // Navigation logic for the back Arrow at the top of the Activity
    private fun setUpActionBar() {
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Edit Phrases"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val fm: FragmentManager = supportFragmentManager
        val backStackCount: Int = fm.backStackEntryCount
        if (backStackCount > 0) {
            // If there are Fragments on the back stack, pop the topmost Fragment
            fm.popBackStackImmediate()
        } else {
            // If there are no Fragments on the back stack, call super to exit the app
            super.onBackPressed()
        }
    }
}