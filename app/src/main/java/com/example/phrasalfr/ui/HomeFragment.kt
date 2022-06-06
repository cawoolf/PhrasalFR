package com.example.phrasalfr.ui

import android.app.Service
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.phrasalfr.PhrasalApplication
import com.example.phrasalfr.database.PhraseDatabase
import com.example.phrasalfr.database.Repository
import com.example.phrasalfr.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {


    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mMainViewModel : MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        setUpViewModel()

        testViewModel()

        return root
    }

    private fun setUpViewModel() {

        mMainViewModel = ViewModelProvider(this,
            MainViewModel.MainViewModelFactory((activity?.application as PhrasalApplication).repository))
            .get(MainViewModel::class.java)
    }

    private fun testViewModel() {

        val textView: TextView = binding.textHome

        lifecycleScope.launch {

            textView.text =  mMainViewModel.getAllPhrases()[0].phraseFrench
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}