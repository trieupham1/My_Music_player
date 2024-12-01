package com.tdtu.my_music_player.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.tdtu.my_music_player.MediaManager.MediaPlayerService;
import com.tdtu.my_music_player.R;

public class NotificationPanel {

    private static final String CHANNEL_ID = "Music_Player_Channel";
    private static final int NOTIFICATION_ID = 101;

    private Context context;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private RemoteViews remoteView;

    public NotificationPanel(Context context, String songTitle, String artistName, int albumArtResourceId, boolean isPlaying) {
        this.context = context;

        // Create Notification Channel (Android 8.0+)
        createNotificationChannel();

        // Build the Notification
        notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.musiclogo)
                .setOngoing(true) // Make it persistent
                .setPriority(NotificationCompat.PRIORITY_LOW) // Lower priority to avoid too much prominence
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        // Create custom RemoteViews layout
        remoteView = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
        populateNotificationLayout(songTitle, artistName, albumArtResourceId, isPlaying);

        // Attach RemoteViews to Notification
        notificationBuilder.setCustomContentView(remoteView);
        notificationBuilder.setCustomBigContentView(remoteView);

        // Get NotificationManager
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Display the Notification
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void populateNotificationLayout(String songTitle, String artistName, int albumArtResourceId, boolean isPlaying) {
        // Set text and album art
        remoteView.setTextViewText(R.id.notification_song_title, songTitle);
        remoteView.setTextViewText(R.id.notification_artist_name, artistName);
        remoteView.setImageViewResource(R.id.notification_album_cover, albumArtResourceId);

        // Set play/pause button icon and click listener
        Intent playPauseIntent = new Intent(context, MediaPlayerService.class)
                .setAction(isPlaying ? MediaPlayerService.ACTION_PAUSE : MediaPlayerService.ACTION_PLAY);
        PendingIntent playPausePendingIntent = PendingIntent.getService(context, 0, playPauseIntent, PendingIntent.FLAG_IMMUTABLE);
        remoteView.setOnClickPendingIntent(R.id.notification_play_pause_button, playPausePendingIntent);
        remoteView.setImageViewResource(R.id.notification_play_pause_button, isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);

        // Set next button click listener
        Intent nextIntent = new Intent(context, MediaPlayerService.class)
                .setAction(MediaPlayerService.ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getService(context, 1, nextIntent, PendingIntent.FLAG_IMMUTABLE);
        remoteView.setOnClickPendingIntent(R.id.notification_next_button, nextPendingIntent);

        // Set previous button click listener
        Intent previousIntent = new Intent(context, MediaPlayerService.class)
                .setAction(MediaPlayerService.ACTION_PREVIOUS);
        PendingIntent previousPendingIntent = PendingIntent.getService(context, 2, previousIntent, PendingIntent.FLAG_IMMUTABLE);
        remoteView.setOnClickPendingIntent(R.id.notification_prev_button, previousPendingIntent);
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Player",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Music playback controls");
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public void updateNotification(String songTitle, String artistName, int albumArtResourceId, boolean isPlaying) {
        // Update RemoteViews with new details
        populateNotificationLayout(songTitle, artistName, albumArtResourceId, isPlaying);

        // Update the notification
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public void cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }
}
