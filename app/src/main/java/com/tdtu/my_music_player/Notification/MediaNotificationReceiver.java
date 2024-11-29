package com.tdtu.my_music_player.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tdtu.my_music_player.MediaManager.MediaPlayerManager;

public class MediaNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        MediaPlayerManager playerManager = MediaPlayerManager.getInstance();

        if ("ACTION_PLAY".equals(action)) {
            playerManager.resumeSong();
        } else if ("ACTION_PAUSE".equals(action)) {
            playerManager.pauseSong();
        } else if ("ACTION_NEXT".equals(action)) {
            playerManager.playNextSong(context);
        } else if ("ACTION_PREVIOUS".equals(action)) {
            playerManager.playPreviousSong(context);
        }

        // Update the notification after each action
        MediaNotificationService notificationService = new MediaNotificationService(
                context, playerManager.getMediaSession(context) // Ensure context is passed here
        );
        notificationService.createNotification(
                playerManager.getCurrentSongTitle(),
                playerManager.getCurrentArtistName(),
                playerManager.isPlaying()
        );
    }
}
