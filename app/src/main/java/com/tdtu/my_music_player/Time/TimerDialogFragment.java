package com.tdtu.my_music_player.Time;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tdtu.my_music_player.R;

import java.util.Arrays;
import java.util.List;

public class TimerDialogFragment extends DialogFragment {

    public interface TimerOptionSelectedListener {
        void onTimerOptionSelected(int minutes);
    }

    private TimerOptionSelectedListener listener;

    public TimerDialogFragment(TimerOptionSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_timer_selection, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rv_timer_options);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Timer options (in minutes)
        List<String> timerOptions = Arrays.asList("Tắt", "10 phút", "15 phút", "20 phút", "30 phút", "45 phút", "1 giờ", "Khi hết video");
        List<Integer> timerMinutes = Arrays.asList(0, 10, 15, 20, 30, 45, 60, -1); // Corresponding values

        // Set up RecyclerView adapter
        TimerOptionAdapter adapter = new TimerOptionAdapter(timerOptions, position -> {
            int selectedMinutes = timerMinutes.get(position);
            if (listener != null) {
                listener.onTimerOptionSelected(selectedMinutes);
            }
            dismiss(); // Close the dialog
        });

        recyclerView.setAdapter(adapter);
        return view;
    }
}
