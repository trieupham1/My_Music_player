package com.tdtu.my_music_player.MediaManager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.tdtu.my_music_player.PlayerSet.MainActivity;
import com.tdtu.my_music_player.R;

public class MediaPlayerService extends Service {
    private static final String CHANNEL_ID = "Music_Player_Channel";
    private static final int NOTIFICATION_ID = 101;

    public static final String ACTION_PLAY = "com.tdtu.my_music_player.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.tdtu.my_music_player.ACTION_PAUSE";
    public static final String ACTION_NEXT = "com.tdtu.my_music_player.ACTION_NEXT";
    public static final String ACTION_PREVIOUS = "com.tdtu.my_music_player.ACTION_PREVIOUS";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            handleAction(intent.getAction());
        }

        // Create and show the notification
        MediaPlayerManager mediaPlayerManager = MediaPlayerManager.getInstance();
        Notification notification = createNotification(
                mediaPlayerManager.getCurrentSongTitle(),
                mediaPlayerManager.getCurrentArtistName(),
                mediaPlayerManager.getCurrentAlbumCoverResource(),
                mediaPlayerManager.isPlaying()
        );

        startForeground(NOTIFICATION_ID, notification);
        return START_NOT_STICKY;
    }

    private void handleAction(String action) {
        MediaPlayerManager mediaPlayerManager = MediaPlayerManager.getInstance();
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
        }

        // Update the notification with the latest state
        updateNotification();
    }

    private Notification createNotification(String songTitle, String artistName, int albumCoverResourceId, boolean isPlaying) {
        // Load the custom notification layout
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_layout);

        // Populate the layout with song details
        notificationLayout.setTextViewText(R.id.notification_song_title, songTitle);
        notificationLayout.setTextViewText(R.id.notification_artist_name, artistName);
        notificationLayout.setImageViewResource(R.id.notification_album_cover, albumCoverResourceId);

        // Set up intent to navigate to the PlayFragment
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("navigateTo", "Player"); // Add the navigateTo key to specify PlayFragment
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Ensure MainActivity is always brought to the front
        PendingIntent contentIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Set up playback controls
        PendingIntent playPauseIntent = PendingIntent.getService(
                this, 0,
                new Intent(this, MediaPlayerService.class).setAction(isPlaying ? ACTION_PAUSE : ACTION_PLAY),
                PendingIntent.FLAG_IMMUTABLE
        );
        PendingIntent nextIntent = PendingIntent.getService(
                this, 1,
                new Intent(this, MediaPlayerService.class).setAction(ACTION_NEXT),
                PendingIntent.FLAG_IMMUTABLE
        );
        PendingIntent prevIntent = PendingIntent.getService(
                this, 2,
                new Intent(this, MediaPlayerService.class).setAction(ACTION_PREVIOUS),
                PendingIntent.FLAG_IMMUTABLE
        );

        notificationLayout.setOnClickPendingIntent(R.id.notification_play_pause_button, playPauseIntent);
        notificationLayout.setOnClickPendingIntent(R.id.notification_next_button, nextIntent);
        notificationLayout.setOnClickPendingIntent(R.id.notification_prev_button, prevIntent);

        // Build the notification
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.musiclogo)
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayout)
                .setContentIntent(contentIntent) // Set the content intent
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();
    }




    private void updateNotification() {
        MediaPlayerManager mediaPlayerManager = MediaPlayerManager.getInstance();
        Notification notification = createNotification(
                mediaPlayerManager.getCurrentSongTitle(),
                mediaPlayerManager.getCurrentArtistName(),
                mediaPlayerManager.getCurrentAlbumCoverResource(),
                mediaPlayerManager.isPlaying()
        );
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, notification);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Player",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Media playback controls");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
