package com.alvarosantisteban.bakingapp.model;

/**
 * Created by alvarosantisteban on 24/08/17.
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Models an ingredient of a Recipe.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Ingredient {

    private final int quantity;
    private final String measure;
    private final String ingredient;

    @JsonIgnoreProperties(ignoreUnknown=true)
    public Ingredient(int quantity, String measure, String ingredient) {
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    @JsonCreator
    public static Ingredient from(
            @JsonProperty("quantity") int quantity,
            @JsonProperty("measure") String measure,
            @JsonProperty("ingredient") String ingredient) {
        return new Ingredient(quantity, measure, ingredient);
    }

    public int getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public String getIngredient() {
        return ingredient;
    }
}
