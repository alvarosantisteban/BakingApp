package com.alvarosantisteban.bakingapp;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Saves and restores the position of the last seen video.
 */
public class VideoPositionSharedPreferences {

    static final long INVALID_LAST_VIDEO_POS = -1;

    private static final String SHARED_PREFS_KEY_LAST_VIDEO_POS = "sharedPrefLastVideoPos";
    private static final String SHARED_PREFS_KEY_LAST_VIDEO_ID = "sharedPrefLastVideoId";

    static void putVideoPosition(@NonNull SharedPreferences sharedPreferences, int recipeStepId, long videoPosition) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SHARED_PREFS_KEY_LAST_VIDEO_ID, recipeStepId);
        editor.putLong(SHARED_PREFS_KEY_LAST_VIDEO_POS, videoPosition);
        editor.apply();
    }

    static long getVideoPosition(@NonNull SharedPreferences sharedPreferences, int recipeStepId) {
        if (sharedPreferences.getInt(SHARED_PREFS_KEY_LAST_VIDEO_ID, -1) == recipeStepId) {
            return sharedPreferences.getLong(SHARED_PREFS_KEY_LAST_VIDEO_POS, INVALID_LAST_VIDEO_POS);
        } else {
            return INVALID_LAST_VIDEO_POS;
        }
    }

    static void resetVideoPosition(@NonNull SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SHARED_PREFS_KEY_LAST_VIDEO_ID, -1);
        editor.putLong(SHARED_PREFS_KEY_LAST_VIDEO_POS, INVALID_LAST_VIDEO_POS);
        editor.apply();
    }
}
