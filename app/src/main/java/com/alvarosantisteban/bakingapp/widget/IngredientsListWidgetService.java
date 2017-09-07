package com.alvarosantisteban.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.alvarosantisteban.bakingapp.R;
import com.alvarosantisteban.bakingapp.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

/**
 * The service to be connected to for a remote adapter to request RemoteViews. The inner class
 * ListRemoteViewsFactory is used to populate the underlaying list view.
 */
public class IngredientsListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }

    private class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        @Nullable
        private List<Ingredient> ingredients = new ArrayList<>();
        private Context context;

        ListRemoteViewsFactory(Context applicationContext) {
            this.context = applicationContext;
        }

        @Override
        public void onCreate() {
            this.ingredients = IngredientsSharedPreferences.getIngredients(PreferenceManager.getDefaultSharedPreferences(context));
        }

        @Override
        public void onDataSetChanged() {
            this.ingredients = IngredientsSharedPreferences.getIngredients(PreferenceManager.getDefaultSharedPreferences(context));
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return ingredients != null ? ingredients.size() : 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            // We construct a remote views item based on our widget item xml file, and set the
            // text based on the position.
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget_item);
            rv.setTextViewText(R.id.appwidget_ingredient, ingredients != null ? ingredients.get(position).getIngredient() : "");

            // Next, we set a fill-intent which will be used to fill-in the pending intent template
            // which is set on the collection view in IngredientsWidgetProvider.
            Intent fillInIntent = new Intent();
            rv.setOnClickFillInIntent(R.id.appwidget_ingredient, fillInIntent);

            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1; // All elements should look the same
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
