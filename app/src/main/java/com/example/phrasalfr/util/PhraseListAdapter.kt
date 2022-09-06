package com.example.phrasalfr.util
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.phrasalfr.R
import com.example.phrasalfr.database.Phrase


class PhraseListAdapter(private val allPhrases: List<Phrase>) : RecyclerView.Adapter<PhraseListAdapter.PhraseViewHolder>() {

    private val phrases = allPhrases

    class PhraseViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var category = view.findViewById<TextView>(R.id.list_item_category)
        val english = view.findViewById<TextView>(R.id.list_item_english)
        val french = view.findViewById<TextView>(R.id.list_item_french)
        val delete = view.findViewById<Button>(R.id.list_item_delete)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhraseViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.phrase_list_item, parent, false)

        return PhraseViewHolder(layout)
    }

    override fun onBindViewHolder(holder: PhraseViewHolder, position: Int) {
        val item = phrases[position]

        holder.category.text = item.category
        holder.english.text = item.phraseEnglish
        holder.french.text = item.phraseFrench
        holder.delete.setOnClickListener {
            //deleteEntry()
        }

    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}