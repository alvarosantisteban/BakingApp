package com.alvarosantisteban.bakingapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alvarosantisteban.bakingapp.model.Ingredient;
import com.alvarosantisteban.bakingapp.model.Step;

import java.util.List;

/**
 * A fragment representing a single RecipeStep detail screen.
 * This fragment is either contained in a {@link RecipeStepListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeStepDetailActivity}
 * on handsets.
 */
public class RecipeStepDetailFragment extends Fragment {

    public static final String ARG_RECIPE_STEP = "recipeStep";
    public static final String ARG_RECIPE_INGREDIENTS = "recipeIngredients";

    @Nullable
    private Step selectedStep;
    private List<Ingredient> ingredients;

    public RecipeStepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_RECIPE_STEP) && getArguments().containsKey(ARG_RECIPE_INGREDIENTS)) {

            selectedStep = getArguments().getParcelable(ARG_RECIPE_STEP);
            ingredients = getArguments().getParcelableArrayList((ARG_RECIPE_INGREDIENTS));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                String title = selectedStep != null ? selectedStep.getDescription() : ingredients.get(0).getIngredient();
                appBarLayout.setTitle(title);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipestep_detail, container, false);
        String title = selectedStep != null ? selectedStep.getDescription() : ingredients.get(0).getIngredient();
        ((TextView) rootView.findViewById(R.id.recipestep_detail)).setText(title);

        return rootView;
    }
}
