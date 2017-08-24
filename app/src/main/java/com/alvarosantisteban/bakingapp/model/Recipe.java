package com.alvarosantisteban.bakingapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Models a recipe.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Recipe {

    private final int id;
    private final String name;
    private final List<Ingredient> ingredients;
    private final List<Step> steps;
    private final int servings;
    private final String image;

    @JsonIgnoreProperties(ignoreUnknown=true)
    public Recipe(int id, String name, List<Ingredient> ingredients, List<Step> steps, int servings, String image) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;
    }

    @JsonCreator
    public static Recipe from(
            @JsonProperty("id") int id,
            @JsonProperty("name") String name,
            @JsonProperty("ingredients") List<Ingredient> ingredients,
            @JsonProperty("steps") List<Step> steps,
            @JsonProperty("servings") int servings,
            @JsonProperty("image") String image) {
        return new Recipe(id, name, ingredients, steps, servings, image);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public int getServings() {
        return servings;
    }

    public String getImage() {
        return image;
    }
}
