package com.example.phrasalfr.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phrasalfr.PhrasalFRApplication
import com.example.phrasalfr.R
import com.example.phrasalfr.database.Phrase
import com.example.phrasalfr.databinding.ActivityEditDatabaseBinding
import com.example.phrasalfr.util.PhraseListAdapter
import kotlinx.coroutines.runBlocking

class EditDatabaseActivity : AppCompatActivity(), PhraseListAdapter.IAdapterDeletePhrase {

   private lateinit var recyclerView : RecyclerView

    private lateinit var mMainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_database)

        val binding = ActivityEditDatabaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.editDbRecyclerView


        setUpViewModel()
        loadDataIntoRecyclerView()
    }

    private fun loadDataIntoRecyclerView() {

        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = PhraseListAdapter(getAllPhrases(), this)

    }

    private fun getAllPhrases() : List<Phrase> {

        // get all Phrases here!

        val phraseList : List<Phrase> = runBlocking {  mMainViewModel.getAllPhrases() }

        return phraseList
    }

    private fun setUpViewModel() {

        mMainViewModel = ViewModelProvider(this,
            MainViewModel.MainViewModelFactory((application as PhrasalFRApplication).phraseRepository,
                "default"))
            .get(MainViewModel::class.java)
    }



    override fun deletePhrase(englishPhrase: String) {
       mMainViewModel.delete(englishPhrase)

       loadDataIntoRecyclerView()
    }
}