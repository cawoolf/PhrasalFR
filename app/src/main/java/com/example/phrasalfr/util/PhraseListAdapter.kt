package com.example.phrasalfr.util
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.phrasalfr.R
import com.example.phrasalfr.database.Phrase
import com.example.phrasalfr.ui.MainViewModel


class PhraseListAdapter(private val allPhrases: List<Phrase>, listener: IAdapterDeletePhrase) : RecyclerView.Adapter<PhraseListAdapter.PhraseViewHolder>() {

    private val phrases = allPhrases
    private val mListener = listener;


    // The ViewHolder handles all the events on the Cards
    class PhraseViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
//        var category = view.findViewById<TextView>(R.id.list_item_category)
        val english = view.findViewById<TextView>(R.id.list_item_english)
        val french = view.findViewById<TextView>(R.id.list_item_french)
        val delete = view.findViewById<ImageView>(R.id.list_item_delete)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhraseViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.phrase_list_item, parent, false)

        return PhraseViewHolder(layout)
    }

    override fun onBindViewHolder(holder: PhraseViewHolder, position: Int) {
        val item = phrases[position]

//        holder.category.text = item.category
        holder.english.text = item.phraseEnglish
        holder.french.text = item.phraseFrench
        holder.delete.setOnClickListener {

            // mListener is an instance of the IAdapterDeletePhrase
            mListener.deletePhrase(item.phraseEnglish)

        }

        holder.view.setOnClickListener { mListener.speakFrenchPhrase(item.phraseFrench) }

    }

    override fun getItemCount(): Int {
        return phrases.size
    }

    /* Basically, through the mListener (which is connected to the EditDatabaseActivity)
     the interface sends the englishPhrase of whichever card is clicked over to the
     EditDatabaseActivity, which in turn passes that phrase to the ViewModel to be deleted
     */
    interface IAdapterDeletePhrase {
        fun deletePhrase(englishPhrase : String)
        fun speakFrenchPhrase(frenchPhrase : String)
    }
}