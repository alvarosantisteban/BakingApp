package com.alvarosantisteban.bakingapp;

import com.alvarosantisteban.bakingapp.model.Recipe;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * The interface to communicate using Retrofit with the API that delivers the recipes.
 */
public interface RecipesApi {

    String RECIPES_BASE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/";
    String RECIPES_INITIAL_JSON_URL = "2017/May/59121517_baking/baking.json";

    @GET(RECIPES_INITIAL_JSON_URL)
    Call<Recipe[]> getInitialJson();
}
