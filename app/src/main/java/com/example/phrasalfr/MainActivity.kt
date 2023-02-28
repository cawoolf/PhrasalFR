package com.example.phrasalfr

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.phrasalfr.databinding.ActivityMainBinding
import com.example.phrasalfr.ui.MainViewModel
import com.example.phrasalfr.util.PhrasalUtil


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mMainViewModel : MainViewModel
    private lateinit var mPhrasalUtil: PhrasalUtil
//    private val mMainViewModel: MainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_quiz, R.id.navigation_phrases
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        setUpViewModel()
        setUpQuizButtonObserver()
        Log.i("mMA", "started")


    }

    private fun setUpQuizButtonObserver() {
        mMainViewModel.navigateToQuiz.observe(this, Observer {

            binding.navView.selectedItemId = R.id.navigation_quiz
            Log.i("mMA", it.toString())

        })
    }


    private fun setUpViewModel() {

        mMainViewModel = ViewModelProvider(this,
            MainViewModel.MainViewModelFactory((this.application as PhrasalFRApplication).phraseRepository,
                "default"))
            .get(MainViewModel::class.java)
    }

    private fun testDB() {

    }

}