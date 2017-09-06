package com.alvarosantisteban.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.alvarosantisteban.bakingapp.model.Recipe;

import static com.alvarosantisteban.bakingapp.RecipesActivity.RECIPE_EXTRA;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientsWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, @Nullable Recipe recipe) {

        CharSequence widgetText = context.getString(R.string.recipe_ingredient_title);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget_provider);
        views.setTextViewText(R.id.appwidget_title, widgetText);

        // Create an intent to go to the main site or to the recipe site
        Intent intent;
        if(recipe != null) {
            views.setTextViewText(R.id.appwidget_ingredients, Utils.formatAllIngredients(recipe));
            intent = new Intent(context, RecipeStepListActivity.class);
            intent.putExtra(RECIPE_EXTRA, recipe);
        } else {
            intent = new Intent(context, RecipesActivity.class);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_layout, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, null);
        }
    }

    public static void updateRecipe(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, @NonNull Recipe recipe) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipe);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

