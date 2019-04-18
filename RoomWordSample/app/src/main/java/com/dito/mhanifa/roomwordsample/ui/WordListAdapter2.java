package com.dito.mhanifa.roomwordsample.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dito.mhanifa.roomwordsample.R;
import com.dito.mhanifa.roomwordsample.model.local.Word;

import java.util.List;

public class WordListAdapter2 extends RecyclerView.Adapter<WordListAdapter2.WordListViewHolder2>
implements IUpdateAdapter{

    final LayoutInflater inflater;
    List<Word> mWords;

    public WordListAdapter2(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void setWords(List<Word> words) {
        mWords = words;
        notifyDataSetChanged();
    }

    class WordListViewHolder2 extends RecyclerView.ViewHolder {
        TextView textView;

        public WordListViewHolder2(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.info_text);
        }
    }

    @NonNull
    @Override
    public WordListViewHolder2 onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new WordListViewHolder2(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recyclerview_layout2, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WordListViewHolder2 wordListViewHolder, int position) {
        String word = (mWords != null) ? mWords.get(position).getWord() : "No words";
        wordListViewHolder.textView.setText(word);
    }

    @Override
    public int getItemCount() {
        return (mWords != null) ? mWords.size() : 0;
    }
}

