package com.example.phrasalfr.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.phrasalfr.R
import com.example.phrasalfr.database.Phrase
import com.example.phrasalfr.databinding.FragmentQuizBinding
import com.example.phrasalfr.util.PhrasalUtil
import com.google.mlkit.nl.translate.Translator

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mQuestionTextView: TextView
    private lateinit var mQuestionImageButton: ImageButton
    private lateinit var mQuestionCountTextView: TextView

    private lateinit var mAnswerTextViewA: TextView
    private lateinit var mAnswerTextViewB: TextView
    private lateinit var mAnswerTextViewC: TextView
    private lateinit var mAnswerTextViewD: TextView

    private lateinit var mAnswerLinearLayoutA: LinearLayout
    private lateinit var mAnswerLinearLayoutB: LinearLayout
    private lateinit var mAnswerLinearLayoutC: LinearLayout
    private lateinit var mAnswerLinearLayoutD: LinearLayout

    private lateinit var mSubmitButton: RelativeLayout
    private lateinit var mProgressBar: ProgressBar

    private var mEnoughWordsInDB: Boolean = true;
    private lateinit var mQuestionPhrase: Phrase
    private lateinit var mAskedQuestions: MutableSet<Phrase>
    private lateinit var mAnswerPhrasesSet: MutableSet<Phrase>
    private lateinit var mAnswerPhrasesIndexArray: IntArray


    private val mMainViewModel: MainViewModel by activityViewModels<MainViewModel>()
    private lateinit var mPhrasalUtil: PhrasalUtil
    private lateinit var mTranslator: Translator

    private lateinit var mQuestionSetting: String
    private lateinit var mAnswerSetting: String
    private lateinit var mCategorySetting: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getSharedPrefs()

        setUpUtils()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        val root: View = binding.root

        linkViews()
        createQuizLayout()


        return root
    }

    override fun onResume() {
        super.onResume()
        updateProgressBar()
    }

    private fun createQuizLayout() {
        mMainViewModel.resetAskedQuestionSet()
        mMainViewModel.resetTotalPhraseCount()

        mEnoughWordsInDB = dataCheck();

        if (mEnoughWordsInDB) {
            setupQuiz()
        } else {
            mQuestionTextView.text = "Not enough Phrases in the database! \n" +
                    "Add more from the Phrases Tab to create the Quiz!"
            mQuestionCountTextView.text = "0 / 0"
        }

    }

    private fun dataCheck(): Boolean {

        mMainViewModel.buildQuestion(getString(R.string.phrases_category));
        if (mMainViewModel.getTotalPhraseCount() < 4) {
            // Then don't run the quiz. It will crash without atleast 4 entries in the database
            return false;
        }
        return true;
    }


    private fun linkViews() {

        mQuestionTextView = binding.quizQuestionTextView
        mQuestionImageButton = binding.quizQuestionImageButton
        mQuestionCountTextView = binding.quizQuestionCountTextView

        mSubmitButton = binding.submitQuizButton
        mProgressBar = binding.quizProgressBar

        mAnswerTextViewA = binding.quizAnswerALinearLayoutTextView
        mAnswerTextViewB = binding.quizAnswerBLinearLayoutTextView
        mAnswerTextViewC = binding.quizAnswerCLinearLayoutTextView
        mAnswerTextViewD = binding.quizAnswerDLinearLayoutTextView

        mAnswerLinearLayoutA = binding.quizAnswerALinearLayout
        mAnswerLinearLayoutB = binding.quizAnswerBLinearLayout
        mAnswerLinearLayoutC = binding.quizAnswerCLinearLayout
        mAnswerLinearLayoutD = binding.quizAnswerDLinearLayout

    }

    private fun setupQuiz() {

        // Will crash if there are not enough words in the database. Needs to be words >= 4
        // Or break into an infinite loop..

        // Setup the Progress Bar

        // Phrases is the default Phrase category!
        mMainViewModel.buildQuestion(getString(R.string.phrases_category))

        mMainViewModel.generateAnswerPhrases()
        mQuestionPhrase = mMainViewModel.getQuestionPhrase()
        Log.i("quizTag", "setUpQuiz")

        // Checks to see if the question is unique
        if (mMainViewModel.uniqueQuestion(mQuestionPhrase)) {
            Log.i("quizTag", "inside If")
            mAnswerPhrasesSet = mMainViewModel.getAnswerPhraseSet()
            mAnswerPhrasesIndexArray = mMainViewModel.getAnswerPhrasesIndexArray()
            formatQuizUI()
            setOnClicks()

            Log.i(
                "quizTag",
                "Question Count: " + mMainViewModel.getAskedQuestionCount().toString() + "\n" +
                        "Total Phrase Count: " + mMainViewModel.getTotalPhraseCount().toString()
            )

            updateProgressBar()

        }

        // Re runs setUpQuiz if the Question was not unique for this session
        else {
            Log.i("quizTag", "inside else")

            // If the total asked questions are equal or exceed the phrase list size the quiz resets
            if (mMainViewModel.getAskedQuestionCount() >= mMainViewModel.getTotalPhraseCount()) {
                Toast.makeText(context, "Quiz Finished!... Resetting", Toast.LENGTH_LONG).show()
                mMainViewModel.resetAskedQuestionSet()
                mMainViewModel.resetTotalPhraseCount()
                setupQuiz()

            }

            // Else the recursively tries to generate another question until it's unique.
            // Not the most efficient for sure..
            else {
                setupQuiz()
            }
        }

    }

    private fun updateProgressBar() {
        mQuestionCountTextView.text =
            "${
                mMainViewModel.getAskedQuestionCount().toString()
            } / ${mMainViewModel.getTotalPhraseCount().toString()}"

        mMainViewModel.updateProgressBar(mProgressBar)
    }

    private fun resetProgressBar() {
        mMainViewModel.resetProgressBar(mProgressBar)

    }


    private fun setOnClicks() {

        // Empty initialization just to make Kotlin happy. Used to check for correct answer
        var answerPhrase = Phrase("default", "default", "default")

        // Plays French audio of the question if user has made that selection
        mQuestionImageButton.setOnClickListener {
            val frenchText = mQuestionPhrase.phraseFrench.toString()
            mPhrasalUtil.useTextToSpeech(frenchText)

        }

        // Resets the UI so all answers look unselected on a loading a new question
        mAnswerLinearLayoutA.background =
            resources.getDrawable(R.drawable.quiz_button_not_selected, context?.theme)
        mAnswerLinearLayoutB.background =
            resources.getDrawable(R.drawable.quiz_button_not_selected, context?.theme)
        mAnswerLinearLayoutC.background =
            resources.getDrawable(R.drawable.quiz_button_not_selected, context?.theme)
        mAnswerLinearLayoutD.background =
            resources.getDrawable(R.drawable.quiz_button_not_selected, context?.theme)


        // Handles click events on the answers, changing the background color when selected
        // And playing French audio when needed.
        mAnswerLinearLayoutA.setOnClickListener {
            mAnswerLinearLayoutA.background =
                resources.getDrawable(R.drawable.quiz_button_selected, context?.theme)
            mAnswerLinearLayoutB.background =
                resources.getDrawable(R.drawable.quiz_button_not_selected, context?.theme)
            mAnswerLinearLayoutC.background =
                resources.getDrawable(R.drawable.quiz_button_not_selected, context?.theme)
            mAnswerLinearLayoutD.background =
                resources.getDrawable(R.drawable.quiz_button_not_selected, context?.theme)

            answerPhrase = mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[0])

            if (mAnswerSetting.toString() == getString(R.string.answer_format_value_french_audio) ||
                mAnswerSetting.toString() == getString(R.string.answer_format_value_french_text)
            ) {
                val frenchText = answerPhrase.phraseFrench
                mPhrasalUtil.useTextToSpeech(frenchText)
            }


        }

        mAnswerLinearLayoutB.setOnClickListener {
            mAnswerLinearLayoutA.background =
                resources.getDrawable(R.drawable.quiz_button_not_selected, context?.theme)
            mAnswerLinearLayoutB.background =
                resources.getDrawable(R.drawable.quiz_button_selected, context?.theme)
            mAnswerLinearLayoutC.background =
                resources.getDrawable(R.drawable.quiz_button_not_selected, context?.theme)
            mAnswerLinearLayoutD.background =
                resources.getDrawable(R.drawable.quiz_button_not_selected, context?.theme)

            answerPhrase = mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[1])

            if (mAnswerSetting.toString() == getString(R.string.answer_format_value_french_audio) ||
                mAnswerSetting.toString() == getString(R.string.answer_format_value_french_text)
            ) {
                val frenchText = answerPhrase.phraseFrench
                mPhrasalUtil.useTextToSpeech(frenchText)
            }

        }

        mAnswerLinearLayoutC.setOnClickListener {
            mAnswerLinearLayoutA.background =
                resources.getDrawable(R.drawable.quiz_button_not_selected, context?.theme)
            mAnswerLinearLayoutB.background =
                resources.getDrawable(R.drawable.quiz_button_not_selected, context?.theme)
            mAnswerLinearLayoutC.background =
                resources.getDrawable(R.drawable.quiz_button_selected, context?.theme)
            mAnswerLinearLayoutD.background =
                resources.getDrawable(R.drawable.quiz_button_not_selected, context?.theme)

            answerPhrase = mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[2])

            if (mAnswerSetting.toString() == getString(R.string.answer_format_value_french_audio) ||
                mAnswerSetting.toString() == getString(R.string.answer_format_value_french_text)
            ) {
                val frenchText = answerPhrase.phraseFrench
                mPhrasalUtil.useTextToSpeech(frenchText)
            }

        }

        mAnswerLinearLayoutD.setOnClickListener {
            mAnswerLinearLayoutA.background =
                resources.getDrawable(R.drawable.quiz_button_not_selected, context?.theme)
            mAnswerLinearLayoutB.background =
                resources.getDrawable(R.drawable.quiz_button_not_selected, context?.theme)
            mAnswerLinearLayoutC.background =
                resources.getDrawable(R.drawable.quiz_button_not_selected, context?.theme)
            mAnswerLinearLayoutD.background =
                resources.getDrawable(R.drawable.quiz_button_selected, context?.theme)

            answerPhrase = mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[3])

            if (mAnswerSetting.toString() == getString(R.string.answer_format_value_french_audio) ||
                mAnswerSetting.toString() == getString(R.string.answer_format_value_french_text)
            ) {
                val frenchText = answerPhrase.phraseFrench
                mPhrasalUtil.useTextToSpeech(frenchText)
            }

        }

        // Checks for the correct answer, and loads a new question if user is correct.
        mSubmitButton.setOnClickListener {

            if (mQuestionPhrase.phraseEnglish == answerPhrase.phraseEnglish) {
                Toast.makeText(context, "Correct!", Toast.LENGTH_SHORT).show()
                setupQuiz()
            } else {
                Toast.makeText(context, "Incorrect.. Try Again", Toast.LENGTH_SHORT).show()
            }

            Log.i("mVM", mMainViewModel.getNavigateQuizValue().toString())


        }
    }


    private fun formatQuizUI() {

        Log.i("quizTAG", "FormatQuiz:" + mQuestionSetting.toString())

        // Controls the visibility of the Question as Text or the Audio image button depending on the User settings
        if (mQuestionSetting.toString() == getString(R.string.question_format_value_english_text)) {
            mQuestionTextView.visibility = View.VISIBLE
            mQuestionImageButton.visibility = View.GONE
            mQuestionTextView.text = mQuestionPhrase.phraseEnglish
        }

        if (mQuestionSetting.toString() == getString(R.string.question_format_value_french_text)) {
            mQuestionTextView.visibility = View.VISIBLE
            mQuestionImageButton.visibility = View.VISIBLE
            mQuestionTextView.text = mQuestionPhrase.phraseFrench

            mPhrasalUtil.useTextToSpeech(mQuestionPhrase.phraseFrench)
        }
        if (mQuestionSetting.toString() == getString(R.string.answer_format_value_french_audio)) {
            mQuestionTextView.visibility = View.INVISIBLE
            mQuestionImageButton.visibility = View.VISIBLE

            mPhrasalUtil.useTextToSpeech(mQuestionPhrase.phraseFrench)
        }

        // Controls the visibility of the Answers as Text or the Audio image button depending on the User settings
        if (mAnswerSetting.toString() == getString(R.string.answer_format_value_english_text)) {
            mAnswerTextViewA.text =
                mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[0]).phraseEnglish
            mAnswerTextViewB.text =
                mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[1]).phraseEnglish
            mAnswerTextViewC.text =
                mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[2]).phraseEnglish
            mAnswerTextViewD.text =
                mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[3]).phraseEnglish
        }

        if (mAnswerSetting.toString() == getString(R.string.answer_format_value_french_text)) {
            mAnswerTextViewA.text =
                mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[0]).phraseFrench
            mAnswerTextViewB.text =
                mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[1]).phraseFrench
            mAnswerTextViewC.text =
                mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[2]).phraseFrench
            mAnswerTextViewD.text =
                mAnswerPhrasesSet.elementAt(mAnswerPhrasesIndexArray[3]).phraseFrench
        }

        if (mAnswerSetting.toString() == getString(R.string.answer_format_value_french_audio)) {
            mAnswerTextViewA.text = ""
            mAnswerTextViewB.text = ""
            mAnswerTextViewC.text = ""
            mAnswerTextViewD.text = ""

            mAnswerTextViewA.background =
                resources.getDrawable(R.drawable.ic_baseline_volume_up_24, context?.theme)
            mAnswerTextViewB.background =
                resources.getDrawable(R.drawable.ic_baseline_volume_up_24, context?.theme)
            mAnswerTextViewC.background =
                resources.getDrawable(R.drawable.ic_baseline_volume_up_24, context?.theme)
            mAnswerTextViewD.background =
                resources.getDrawable(R.drawable.ic_baseline_volume_up_24, context?.theme)
        }

    }


    // Basic sharedPrefs used to format the Question and Answers based on the user choice from the
    // home Fragment
    private fun getSharedPrefs() {
        val sharedPref = activity?.getSharedPreferences(
            getString(R.string.quiz_settings_sharedPrefs), Context.MODE_PRIVATE
        )

        mQuestionSetting =
            sharedPref?.getString(
                getString(R.string.question_format_key),
                getString(R.string.question_format_value_french_text)
            ).toString()
        mAnswerSetting =
            sharedPref?.getString(
                getString(R.string.answer_format_key),
                getString(R.string.answer_format_value_english_text)
            ).toString()
        mCategorySetting = sharedPref?.getString(
            getString(R.string.phrase_category_key),
            getString(R.string.all_phrases_category)
        ).toString()

        Log.i("prefsTAG", mQuestionSetting.toString())
        Log.i("prefsTAG", mAnswerSetting.toString())
    }


    // Builds the Utils for translating and TextToSpeech
    private fun setUpUtils() {
        mPhrasalUtil = PhrasalUtil(context)
        mTranslator = mPhrasalUtil.getFRENTranslator()
        mPhrasalUtil.setUpTTS()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}