package com.alvarosantisteban.bakingapp;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alvarosantisteban.bakingapp.model.Recipe;
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
public class RecipesActivity extends AppCompatActivity {

    private static final String TAG = RecipesActivity.class.getSimpleName();
    private static final String LIST_POS = "bundleListPos";

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private int currentPosition;
    private boolean hasBeenScrolledAutomatically;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recipes_list);

        recyclerView.setLayoutManager(Utils.isTablet(this) ? new GridLayoutManager(this, 2) : new LinearLayoutManager(this));

        if(savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(LIST_POS);
        }

        if(Utils.isNetworkAvailable(this)) {
            downloadInitialJson();
        } else {
            Toast.makeText(this, getString(R.string.error_no_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save list's position
        currentPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        outState.putInt(LIST_POS, currentPosition);
    }

    /**
     * Downloads asynchronously the JSON with the list of recipes.
     */
    private void downloadInitialJson() {
        progressBar.setVisibility(View.VISIBLE);

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

                        RecipesAdapter adapter = new RecipesAdapter(Arrays.asList(recipeList), RecipesActivity.this);
                        recyclerView.setAdapter(adapter);

                        scrollToLastVisitedPosition();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Recipe[]> call, @NonNull Throwable t) {
                Log.e(TAG, "Error downloading or parsing recipes.", t);

                Toast.makeText(RecipesActivity.this, R.string.error_download_json, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void scrollToLastVisitedPosition() {
        if(!hasBeenScrolledAutomatically) {
            Handler handler = new Handler();
            Runnable r = new Runnable() {
                public void run() {
                    recyclerView.scrollToPosition(currentPosition);
                    hasBeenScrolledAutomatically = true;
                }
            };
            handler.postDelayed(r, 200);
        }
    }
}