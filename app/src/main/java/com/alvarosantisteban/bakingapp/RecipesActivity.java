package com.alvarosantisteban.bakingapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.alvarosantisteban.bakingapp.model.Recipe;
import com.fasterxml.jackson.databind.ObjectMapper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RecipesActivity extends AppCompatActivity {

    private static final String TAG = RecipesActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        downloadInitialJson();
    }

    private void downloadInitialJson() {
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
                        Log.d(TAG, "Recipes list downloaded: " + recipeList[0].getName());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Recipe[]> call, @NonNull Throwable t) {
                Log.e(TAG, "Error downloading or parsing recipes.", t);
            }
        });
    }
}