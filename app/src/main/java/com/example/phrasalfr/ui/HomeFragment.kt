package com.example.phrasalfr.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.phrasalfr.PhrasalFRApplication
import com.example.phrasalfr.databinding.FragmentQuizSettingsBinding


class HomeFragment : Fragment() {


    private var _binding: FragmentQuizSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mMainViewModel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentQuizSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        linkViews()

        return root
    }

    private fun linkViews() {

    }

    private fun setUpViewModel() {

        mMainViewModel = ViewModelProvider(this,
            MainViewModel.MainViewModelFactory((activity?.application as PhrasalFRApplication).repository))
            .get(MainViewModel::class.java)
    }




//    private fun testViewModel() {
//
//        val textView: TextView = binding.textHome
//
//        lifecycleScope.launch {
//
//            try {
//                textView.text = mMainViewModel.getAllPhrases()[0].phraseFrench
//            }
//            catch (e: Exception) {
//                Log.i("vmTAG", e.toString())
//            }
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}