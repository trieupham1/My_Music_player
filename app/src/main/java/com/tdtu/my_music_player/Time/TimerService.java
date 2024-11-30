package com.tdtu.my_music_player.Time;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.widget.Toast;

public class TimerService extends Service {

    private final IBinder binder = new TimerBinder();
    private CountDownTimer countDownTimer;
    private long remainingMillis = 0;

    private TimerListener listener;

    public interface TimerListener {
        void onTick(long millisUntilFinished);
        void onFinish();
    }

    public class TimerBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setTimerListener(TimerListener listener) {
        this.listener = listener;
    }

    public void startTimer(long durationMillis) {
        remainingMillis = durationMillis;

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(remainingMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingMillis = millisUntilFinished;
                if (listener != null) {
                    listener.onTick(millisUntilFinished);
                }
            }

            @Override
            public void onFinish() {
                if (listener != null) {
                    listener.onFinish();
                }
                stopSelf(); // Stop service when timer finishes
            }
        }.start();

        Toast.makeText(this, "Timer started", Toast.LENGTH_SHORT).show();
    }

    public void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        remainingMillis = 0;
        stopSelf(); // Stop service when timer is canceled
        Toast.makeText(this, "Timer canceled", Toast.LENGTH_SHORT).show();
    }

    public long getRemainingMillis() {
        return remainingMillis;
    }
}
