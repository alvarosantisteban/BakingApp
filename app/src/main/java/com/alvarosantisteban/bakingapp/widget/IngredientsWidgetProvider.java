package com.alvarosantisteban.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.alvarosantisteban.bakingapp.R;
import com.alvarosantisteban.bakingapp.RecipesActivity;
import com.alvarosantisteban.bakingapp.widget.IngredientsListWidgetService;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientsWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget_provider);

        // Set the IngredientsListWidgetService intent to act as the adapter for the ListView
        Intent intent = new Intent(context, IngredientsListWidgetService.class);
        views.setRemoteAdapter(R.id.appwidget_ingredients_list, intent);

        // Set the RecipesActivity intent to launch when clicked
        Intent appIntent = new Intent(context, RecipesActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.appwidget_ingredients_list, appPendingIntent);

        // Handle empty view
        views.setEmptyView(R.id.appwidget_ingredients_list, R.id.empty_view);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
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

