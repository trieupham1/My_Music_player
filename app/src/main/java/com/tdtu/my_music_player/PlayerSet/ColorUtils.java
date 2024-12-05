package com.tdtu.my_music_player.PlayerSet;

import android.graphics.Color;

public class ColorUtils {

    // Method to darken a color
    public static int darkenColor(int color) {
        float factor = 0.7f; // Adjust brightness by this factor (0.7 makes it 30% darker)
        int red = (int) (Color.red(color) * factor);
        int green = (int) (Color.green(color) * factor);
        int blue = (int) (Color.blue(color) * factor);
        return Color.rgb(red, green, blue);
    }

    // Method to check if a color is too bright
    public static boolean isColorBright(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        // Calculate brightness using a simple formula
        double brightness = (0.299 * red + 0.587 * green + 0.114 * blue); // Weighted formula for perceived brightness
        return brightness > 200; // Threshold for "too bright"
    }
}
