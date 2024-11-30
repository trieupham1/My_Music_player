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

        // Options for sleep timer
        List<String> timerOptions = Arrays.asList("5 minutes", "10 minutes", "15 minutes", "20 minutes", "30 minutes", "Cancel");
        List<Integer> timerMinutes = Arrays.asList(5, 10, 15, 20, 30, 0); // Corresponding values

        TimerOptionAdapter adapter = new TimerOptionAdapter(timerOptions, position -> {
            int selectedMinutes = timerMinutes.get(position);
            listener.onTimerOptionSelected(selectedMinutes);
            dismiss();
        });

        recyclerView.setAdapter(adapter);
        return view;
    }
}
