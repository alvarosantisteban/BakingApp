package com.alvarosantisteban.bakingapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alvarosantisteban.bakingapp.model.Recipe;

import static com.alvarosantisteban.bakingapp.R.id.recipe_step_next_button;
import static com.alvarosantisteban.bakingapp.R.id.recipe_step_previous_button;

/**
 * A fragment representing a single RecipeStep detail screen.
 * This fragment is either contained in a {@link RecipeStepListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeStepDetailActivity}
 * on handsets.
 */
public class RecipeStepDetailFragment extends Fragment implements View.OnClickListener {

    public static final String ARG_RECIPE = "recipe";
    public static final String ARG_RECIPE_STEP_POS = "recipeStepPOS";
    public static final String ARG_IS_TWO_PANE = "isTwoPane";

    private int selectedStepPos;
    private Recipe selectedRecipe;
    private boolean isTwoPane;

    public RecipeStepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_RECIPE) &&
                getArguments().containsKey(ARG_RECIPE_STEP_POS) &&
                getArguments().containsKey(ARG_IS_TWO_PANE)) {

            selectedRecipe = getArguments().getParcelable(ARG_RECIPE);
            selectedStepPos = getArguments().getInt(ARG_RECIPE_STEP_POS);
            isTwoPane = getArguments().getBoolean(ARG_IS_TWO_PANE);

            String title = selectedStepPos > RecipeStepsAdapter.NO_STEP_SELECTED_POS ?
                    selectedRecipe.getSteps().get(selectedStepPos).getShortDescription() :
                    selectedRecipe.getIngredients().get(0).getIngredient();
            getActivity().setTitle(title);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipestep_detail, container, false);
        String description = selectedStepPos > RecipeStepsAdapter.NO_STEP_SELECTED_POS ?
                selectedRecipe.getSteps().get(selectedStepPos).getDescription() :
                selectedRecipe.getIngredients().get(0).getIngredient();
        ((TextView) rootView.findViewById(R.id.recipe_step_description)).setText(description);

        ImageView previous = (ImageView) rootView.findViewById(recipe_step_previous_button);
        ImageView next = (ImageView) rootView.findViewById(R.id.recipe_step_next_button);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);

        if(isTwoPane) {
            // Hide the navigation arrows, they are not needed
            previous.setVisibility(View.GONE);
            next.setVisibility(View.GONE);
        }

        FrameLayout frameLayout = (FrameLayout) rootView.findViewById(R.id.recipe_step_upper_area);
        if(selectedStepPos > RecipeStepsAdapter.NO_STEP_SELECTED_POS) {
            if (!TextUtils.isEmpty(selectedRecipe.getSteps().get(selectedStepPos).getVideoUrl())) {
                frameLayout.addView(inflater.inflate(R.layout.recipestep_video, container, false));
            } else if(!TextUtils.isEmpty(selectedRecipe.getSteps().get(selectedStepPos).getImageUrl())) {
                frameLayout.addView(inflater.inflate(R.layout.recipestep_image, container, false));
            }
        } else {
            // TODO Display ingredients

        }

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case recipe_step_previous_button:
                // TODO Go to previous step
                // TODO Pass the whole Recipe to make things easier here
                Toast.makeText(getActivity(), "previous", Toast.LENGTH_SHORT).show();
                break;
            case recipe_step_next_button:
                // TODO Go to next step
                Toast.makeText(getActivity(), "next", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
