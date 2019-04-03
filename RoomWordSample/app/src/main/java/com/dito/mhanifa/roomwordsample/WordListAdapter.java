package com.dito.mhanifa.roomwordsample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordListViewHolder> {

    public class WordListViewHolder extends RecyclerView.ViewHolder {
        private final TextView wordItemView;

        public WordListViewHolder(@NonNull View itemView) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.wordView);
        }
    }

    private final LayoutInflater mLayoutInflator;
    private List<Word> mWords; // cached copy of words

    public WordListAdapter(final Context context) {
        mLayoutInflator = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public WordListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = mLayoutInflator.inflate(R.layout.recyclerview_item, viewGroup, false);
        return new WordListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WordListViewHolder wordListViewHolder, int position) {
        String word = (mWords == null) ? "No words yet" :  mWords.get(position).getWord();
        wordListViewHolder.wordItemView.setText(word);
    }

    @Override
    public int getItemCount() {
        return (mWords != null) ? mWords.size() : 0;
    }

    public void setWords(List<Word> words) {
        mWords = words;
        notifyDataSetChanged();
    }
}
