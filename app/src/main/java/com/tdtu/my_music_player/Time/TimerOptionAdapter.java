package com.tdtu.my_music_player.Time;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tdtu.my_music_player.R;

import java.util.List;

public class TimerOptionAdapter extends RecyclerView.Adapter<TimerOptionAdapter.TimerViewHolder> {

    private final List<String> options;
    private final OnOptionClickListener listener;

    public TimerOptionAdapter(List<String> options, OnOptionClickListener listener) {
        this.options = options;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new TimerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerViewHolder holder, int position) {
        holder.textView.setText(options.get(position));
        holder.itemView.setOnClickListener(v -> listener.onOptionClick(position));
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    public interface OnOptionClickListener {
        void onOptionClick(int position);
    }

    public static class TimerViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public TimerViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
