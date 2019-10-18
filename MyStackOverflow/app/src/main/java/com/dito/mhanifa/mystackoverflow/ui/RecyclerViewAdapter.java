package com.dito.mhanifa.mystackoverflow.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dito.mhanifa.mystackoverflow.data.remote.Answer;

import java.util.List;

public class dRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<Answer> data;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);

        }
    }

    public RecyclerViewAdapter(Context context, List<Answer> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(android.R.layout.simple_selectable_list_item, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Toast.makeText(context.getApplicationContext(), "onBindViewHolder #" + position,
                Toast.LENGTH_SHORT).show();
        Answer answer = data.get(position);
        viewHolder.textView.setText(answer.toString());
        viewHolder.itemView.setTag(answer.answerId);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
