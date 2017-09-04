package com.alvarosantisteban.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.alvarosantisteban.bakingapp.model.Recipe;

/**
 * An activity representing a list of RecipeSteps. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeStepDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeStepListActivity extends AppCompatActivity implements RecipeStepsAdapter.OnItemClickListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean isTwoPane;
    private Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipestep_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            recipe = extras.getParcelable(RecipesActivity.RECIPE_EXTRA);
            if(recipe != null) {
                setTitle(recipe.getName());
            }
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recipestep_list);
        recyclerView.setAdapter(new RecipeStepsAdapter(recipe.getSteps(), this));

        if (findViewById(R.id.recipestep_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts (res/values-w900dp).
            // If this view is present, then the activity should be in two-pane mode.
            isTwoPane = true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int stepPosition) {
        if (isTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(RecipeStepDetailFragment.ARG_RECIPE, recipe);
            arguments.putInt(RecipeStepDetailFragment.ARG_RECIPE_STEP_POS, stepPosition);
            arguments.putBoolean(RecipeStepDetailFragment.ARG_IS_TWO_PANE, isTwoPane);
            RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipestep_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, RecipeStepDetailActivity.class);
            intent.putExtra(RecipeStepDetailFragment.ARG_RECIPE, recipe);
            intent.putExtra(RecipeStepDetailFragment.ARG_RECIPE_STEP_POS, stepPosition);
            intent.putExtra(RecipeStepDetailFragment.ARG_IS_TWO_PANE, isTwoPane);

            startActivity(intent);
        }
    }
}
