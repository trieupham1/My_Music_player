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

    private final List<String> timerOptions;
    private final OnOptionClickListener listener;

    public TimerOptionAdapter(List<String> timerOptions, OnOptionClickListener listener) {
        this.timerOptions = timerOptions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_timer_option, parent, false);
        return new TimerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerViewHolder holder, int position) {
        String option = timerOptions.get(position);
        holder.textView.setText(option);

        holder.itemView.setOnClickListener(v -> listener.onOptionClick(position));
    }

    @Override
    public int getItemCount() {
        return timerOptions.size();
    }

    public interface OnOptionClickListener {
        void onOptionClick(int position);
    }

    static class TimerViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public TimerViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.timer_option_text);
        }
    }
}
