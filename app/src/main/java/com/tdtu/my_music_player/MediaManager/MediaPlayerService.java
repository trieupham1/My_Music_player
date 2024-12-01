package com.tdtu.my_music_player.MediaManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.tdtu.my_music_player.Notification.NotificationPanel;
import com.tdtu.my_music_player.R;

public class MediaPlayerService extends Service {

    public static final String ACTION_PLAY = "com.tdtu.my_music_player.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.tdtu.my_music_player.ACTION_PAUSE";
    public static final String ACTION_NEXT = "com.tdtu.my_music_player.ACTION_NEXT";
    public static final String ACTION_PREVIOUS = "com.tdtu.my_music_player.ACTION_PREVIOUS";

    private NotificationPanel notificationPanel;
    private MediaPlayerManager mediaPlayerManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayerManager = MediaPlayerManager.getInstance();

        // Initialize the NotificationPanel
        notificationPanel = new NotificationPanel(
                this,
                mediaPlayerManager.getCurrentSongTitle(),
                mediaPlayerManager.getCurrentArtistName(),
                mediaPlayerManager.getCurrentAlbumCoverResource(),
                mediaPlayerManager.isPlaying()
        );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            handleAction(intent.getAction());
        }

        // Update the notification with the current playback state
        notificationPanel.updateNotification(
                mediaPlayerManager.getCurrentSongTitle(),
                mediaPlayerManager.getCurrentArtistName(),
                mediaPlayerManager.getCurrentAlbumCoverResource(),
                mediaPlayerManager.isPlaying()
        );

        return START_NOT_STICKY;
    }

    private void handleAction(String action) {
        switch (action) {
            case ACTION_PLAY:
                mediaPlayerManager.resumeSong();
                break;

            case ACTION_PAUSE:
                mediaPlayerManager.pauseSong();
                break;

            case ACTION_NEXT:
                mediaPlayerManager.playNextSong(this);
                break;

            case ACTION_PREVIOUS:
                mediaPlayerManager.playPreviousSong(this);
                break;

            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Cancel the notification when the service is destroyed
        if (notificationPanel != null) {
            notificationPanel.cancelNotification();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
