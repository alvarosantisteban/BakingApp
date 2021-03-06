package com.alvarosantisteban.bakingapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.alvarosantisteban.bakingapp.model.Recipe;
import com.alvarosantisteban.bakingapp.widget.IngredientsSharedPreferences;
import com.alvarosantisteban.bakingapp.widget.IngredientsWidgetProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Download the json with the recipes and displays them to the user.
 */
public class RecipesActivity extends AppCompatActivity implements RecipesAdapter.OnItemClickListener {

    private static final String TAG = RecipesActivity.class.getSimpleName();

    static final String RECIPE_EXTRA = "recipeExtra";

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public static int index = -1;
    public static int top = -1;

    @Nullable
    SimpleIdlingResource simpleIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recipes_list);

        layoutManager = Utils.isTablet(this) ? new GridLayoutManager(this, 2) : new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Scroll to the last visited position of the recycler view
        if(index != -1) {
            ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(index, top);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(Utils.isNetworkAvailable(this)) {
            getIdlingResource();
            downloadInitialJson();
        } else {
            progressBar.setVisibility(View.GONE);
            Snackbar.make(findViewById(R.id.recipes_layout), getString(R.string.error_no_internet_connection), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Save the current recycler view's position
        index = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        View v = recyclerView.getChildAt(0);
        top = (v == null) ? 0 : (v.getTop() - recyclerView.getPaddingTop());
    }

    /**
     * Downloads asynchronously the JSON with the list of recipes.
     */
    private void downloadInitialJson() {
        progressBar.setVisibility(View.VISIBLE);

        // The IdlingResource is null in production.
        if (simpleIdlingResource != null) {
            simpleIdlingResource.setIdleState(false);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RecipesApi.RECIPES_BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper()))
                .build();

        RecipesApi recipesApi = retrofit.create(RecipesApi.class);

        Call<Recipe[]> call = recipesApi.getInitialJson();
        call.enqueue(new Callback<Recipe[]>() {

            @Override
            public void onResponse(@NonNull Call<Recipe[]> call, @NonNull Response<Recipe[]> response) {
                if (response.body() != null) {
                    Recipe[] recipeList = response.body();
                    if (recipeList != null && recipeList.length > 0) {
                        progressBar.setVisibility(View.GONE);

                        RecipesAdapter adapter = new RecipesAdapter(Arrays.asList(recipeList), RecipesActivity.this, RecipesActivity.this);
                        recyclerView.setAdapter(adapter);

                        if (simpleIdlingResource != null) {
                            simpleIdlingResource.setIdleState(true);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Recipe[]> call, @NonNull Throwable t) {
                Log.e(TAG, "Error downloading or parsing recipes.", t);
                progressBar.setVisibility(View.GONE);
                Snackbar.make(findViewById(R.id.recipes_layout), getString(R.string.error_download_json), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (simpleIdlingResource == null) {
            simpleIdlingResource = new SimpleIdlingResource();
            }
        return simpleIdlingResource;
    }

    @Override
    public void onItemClick(Recipe recipe) {
        // Go to master-detail activity
        Intent intent = new Intent(this, RecipeStepListActivity.class);
        intent.putExtra(RECIPE_EXTRA, recipe);
        startActivity(intent);

        // Save the ingredients into SharedPreferences and let the widget know that it should display
        // the ingredients of a new recipe
        IngredientsSharedPreferences.putIngredients(PreferenceManager.getDefaultSharedPreferences(this), recipe.getIngredients());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, IngredientsWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_ingredients_list);
    }
}