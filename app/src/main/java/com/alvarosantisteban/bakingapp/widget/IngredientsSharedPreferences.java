package com.alvarosantisteban.bakingapp.widget;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alvarosantisteban.bakingapp.model.Ingredient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class used to stored a list of ingredients into shared preferences as a set of strings.
 */
public class IngredientsSharedPreferences {

    private static final String KEY_INGREDIENTS = "keyIngredients";

    private static final String INGREDIENT_SEPARATOR = "|";

    public static void putIngredients(@NonNull SharedPreferences sharedPreferences, @NonNull List<Ingredient> ingredients) {
        Set<String> ingredientsSet = new HashSet<>();
        for(Ingredient ingredient : ingredients){
            ingredientsSet.add(ingredient.getQuantity() + INGREDIENT_SEPARATOR + ingredient.getMeasure() + INGREDIENT_SEPARATOR + ingredient.getIngredient() );
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.contains(KEY_INGREDIENTS)) {
            editor.remove(KEY_INGREDIENTS);
            editor.apply();
        }
        editor.putStringSet(KEY_INGREDIENTS, ingredientsSet);
        editor.apply();
    }


    @Nullable
    public static List<Ingredient> getIngredients(@NonNull SharedPreferences sharedPreferences){
        if (sharedPreferences.contains(KEY_INGREDIENTS)) {
            Set<String> ingredientsSet = sharedPreferences.getStringSet(KEY_INGREDIENTS, null);
            if(ingredientsSet != null) {
                List<Ingredient> ingredientsList = new ArrayList<>();
                for (String ingredient : ingredientsSet) {
                    float quantity = Float.valueOf(ingredient.substring(0, ingredient.indexOf(INGREDIENT_SEPARATOR)));
                    String measure = ingredient.substring(ingredient.indexOf(INGREDIENT_SEPARATOR), ingredient.indexOf(INGREDIENT_SEPARATOR));
                    String ingredientDescription = ingredient.substring(ingredient.lastIndexOf(INGREDIENT_SEPARATOR) +1, ingredient.length());
                    ingredientsList.add(new Ingredient(quantity, measure, ingredientDescription));
                }
                return ingredientsList;
            }
        }
        return null;
    }
}
