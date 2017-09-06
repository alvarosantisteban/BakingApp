package com.alvarosantisteban.bakingapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.alvarosantisteban.bakingapp.model.Ingredient;
import com.alvarosantisteban.bakingapp.model.Recipe;

/**
 * Helper class with static methods.
 */
public final class Utils {

    public static boolean isTablet(@NonNull Context context) {
        return context.getResources().getBoolean(R.bool.isTablet);
    }

    public static boolean isLandscape(@NonNull Context context) {
        return context.getResources().getBoolean(R.bool.isLandscape);
    }

    public static boolean isNetworkAvailable(@NonNull Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Taken from https://stackoverflow.com/a/14126736
    public static String formatFloatToString(float d) {
        if(d == (long) d) {
            return String.format("%d", (long) d);
        } else {
            return String.format("%s", d);
        }
    }

    static String formatAllIngredients(@NonNull final Recipe recipe) {
        String ingredientsString = "";
        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredientsString = ingredientsString +Utils.formatFloatToString(ingredient.getQuantity()) + " (" +ingredient.getMeasure() +") " +ingredient.getIngredient() +"\n";
        }
        return ingredientsString;
    }
}
