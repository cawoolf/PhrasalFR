package com.example.phrasalfr.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phrasalfr.R
import com.example.phrasalfr.databinding.ActivityEditDatabaseBinding
import com.example.phrasalfr.databinding.ActivityMainBinding
import com.example.phrasalfr.util.PhraseListAdapter

class EditDatabase : AppCompatActivity() {

   private lateinit var recyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_database)

        val binding = ActivityEditDatabaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.editDbRecyclerView

        loadDataIntoRecyclerView()
    }

    private fun loadDataIntoRecyclerView() {

        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = PhraseListAdapter()
    }


}