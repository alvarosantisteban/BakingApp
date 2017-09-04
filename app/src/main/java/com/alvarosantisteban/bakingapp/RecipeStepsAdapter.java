package com.alvarosantisteban.bakingapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alvarosantisteban.bakingapp.model.Step;

import java.util.List;

/**
 * The adapter for the list of steps that conform a recipe.
 */
class RecipeStepsAdapter extends RecyclerView.Adapter<RecipeStepsAdapter.RecipeStepViewHolder> {

    static final int NO_STEP_SELECTED_POS = -1;

    public interface OnItemClickListener {
        void onItemClick(int stepPosition);
    }

    private static final int INGREDIENT_TYPE = 0;
    private static final int STEP_TYPE = 1;

    private final List<Step> recipeSteps;
    private RecipeStepsAdapter.OnItemClickListener onItemClickListener;

    RecipeStepsAdapter(@NonNull List<Step> recipeSteps, @NonNull RecipeStepsAdapter.OnItemClickListener onItemClickListener) {
        this.recipeSteps = recipeSteps;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecipeStepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recipe_ingredient, parent, false);
        return new RecipeStepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeStepViewHolder holder, int position) {
        switch(holder.getItemViewType()) {
            case INGREDIENT_TYPE:
                holder.setListener(NO_STEP_SELECTED_POS);
            break;
            case STEP_TYPE:
                holder.setStepName(recipeSteps.get(position-1).getShortDescription());
                holder.setListener(position-1);
            break;
            default:
                throw new NoSuchFieldError("There is no such type.");
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? INGREDIENT_TYPE : STEP_TYPE;
    }

    @Override
    public int getItemCount() {
        return recipeSteps.size() + 1;
    }

    class RecipeStepViewHolder extends RecyclerView.ViewHolder {

        private final TextView text;

        RecipeStepViewHolder(View view) {
            super(view);
            text = (TextView) view.findViewById(R.id.recipe_step_text);
        }

        void setStepName(String stepName) {
            text.setText(stepName);
        }

        void setListener(final int stepPosition) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    onItemClickListener.onItemClick(stepPosition);
                }
            });
        }
    }
}
