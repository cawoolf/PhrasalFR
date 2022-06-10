package com.example.phrasalfr.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.phrasalfr.PhrasalFRApplication
import com.example.phrasalfr.R
import com.example.phrasalfr.database.Phrase
import com.example.phrasalfr.databinding.FragmentQuizBinding
import kotlinx.coroutines.launch

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mQuestionTextView: TextView
    private lateinit var mQuestionImageButton: ImageButton

    private lateinit var mAnswerTextViewA: TextView
    private lateinit var mAnswerTextViewB: TextView
    private lateinit var mAnswerTextViewC: TextView
    private lateinit var mAnswerTextViewD: TextView

    private lateinit var mAnswerImageButtonA: ImageButton
    private lateinit var mAnswerImageButtonB: ImageButton
    private lateinit var mAnswerImageButtonC: ImageButton
    private lateinit var mAnswerImageButtonD: ImageButton


    private lateinit var mAllPhrases : List<Phrase>

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
//        val quizViewModel =
//            ViewModelProvider(this).get(MainViewModel::class.java)

        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        val root: View = binding.root


        linkViews()
        checkSettings()

        lifecycleScope.launch {
            mAllPhrases = mMainViewModel.getAllPhrases()
            setPhrases()
            Log.i("qTAG", mAllPhrases[1].phraseFrench.toString())
        }


        return root
    }

    override fun onResume() {
        super.onResume()
        checkSettings()
    }

    private fun linkViews() {

        mQuestionTextView = binding.quizQuestionTextView
        mQuestionImageButton = binding.quizQuestionImageButton

    }

    private fun checkSettings() {
        val sharedPref = activity?.getSharedPreferences(
            getString(R.string.quiz_settings_sharedPrefs), Context.MODE_PRIVATE)

        val questionSetting = sharedPref?.getString(getString(R.string.question_format_key), "default")
        val answerSetting = sharedPref?.getString(getString(R.string.answer_format_key), "default")

        // Controls the visibility of the Question as Text or the Audio image button depending on the User settings
        when(questionSetting) {
            getString(R.string.question_format_value_english_text) -> mQuestionTextView.visibility= View.VISIBLE
            getString(R.string.question_format_value_english_text) -> mQuestionImageButton.visibility = View.GONE

            getString(R.string.question_format_value_french_text) -> mQuestionTextView.visibility= View.VISIBLE
            getString(R.string.question_format_value_french_text) -> mQuestionImageButton.visibility = View.GONE

            getString(R.string.question_format_value_french_audio) -> mQuestionTextView.visibility = View.GONE
            getString(R.string.question_format_value_french_audio) -> mQuestionImageButton.visibility = View.VISIBLE
        }
    }

    private fun setPhrases() {
        mQuestionTextView.text = mAllPhrases[1].phraseFrench
    }

    private fun setUpViewModel() {

        mMainViewModel = ViewModelProvider(this,
            MainViewModel.MainViewModelFactory((activity?.application as PhrasalFRApplication).repository))
            .get(MainViewModel::class.java)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}