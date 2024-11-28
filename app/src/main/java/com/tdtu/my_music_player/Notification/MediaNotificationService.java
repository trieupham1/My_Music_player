package com.tdtu.my_music_player.Notification;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.media.app.NotificationCompat.MediaStyle;

import com.tdtu.my_music_player.R;

public class MediaNotificationService {

    private static final String CHANNEL_ID = "MusicPlayerChannel";
    private static final int NOTIFICATION_ID = 1;
    private final Context context;
    private final MediaSessionCompat mediaSession;

    public MediaNotificationService(@NonNull Context context, MediaSessionCompat mediaSession) {
        this.context = context;
        this.mediaSession = mediaSession;
    }

    public void createNotification(String songTitle, String artistName, boolean isPlaying) {
        // Create Notification Channel (Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Music Player", NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Media Playback Controls");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // Play/Pause Action
        PendingIntent playPauseAction = PendingIntent.getBroadcast(
                context,
                0,
                new Intent(context, MediaNotificationReceiver.class)
                        .setAction(isPlaying ? "ACTION_PAUSE" : "ACTION_PLAY"),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Next Action
        PendingIntent nextAction = PendingIntent.getBroadcast(
                context,
                1,
                new Intent(context, MediaNotificationReceiver.class).setAction("ACTION_NEXT"),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Previous Action
        PendingIntent previousAction = PendingIntent.getBroadcast(
                context,
                2,
                new Intent(context, MediaNotificationReceiver.class).setAction("ACTION_PREVIOUS"),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build Media Notification
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.musiclogo) // Replace with your app's icon
                .setContentTitle(songTitle)
                .setContentText(artistName)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(new MediaStyle().setMediaSession(mediaSession.getSessionToken()))
                .addAction(R.drawable.ic_previous, "Previous", previousAction)
                .addAction(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play, isPlaying ? "Pause" : "Play", playPauseAction)
                .addAction(R.drawable.ic_next, "Next", nextAction)
                .build();

        // Show Notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, notification);
        }
    }

    public void removeNotification() {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(NOTIFICATION_ID);
        }
    }
}
