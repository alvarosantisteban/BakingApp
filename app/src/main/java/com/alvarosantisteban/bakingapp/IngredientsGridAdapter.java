package com.alvarosantisteban.bakingapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alvarosantisteban.bakingapp.model.Ingredient;

import java.util.List;

/**
 * Small adapter for the ingredient's grid.
 */
class IngredientsGridAdapter extends RecyclerView.Adapter<IngredientsGridAdapter.IngredientsViewHolder> {

    private List<Ingredient> ingredients;

    IngredientsGridAdapter(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public IngredientsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipestep_ingredients_rv_item, parent, false);
        return new IngredientsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientsGridAdapter.IngredientsViewHolder holder, int position) {
        holder.setIngredientInfo(ingredients.get(position));
    }

    @Override
    public int getItemCount() {
        return ingredients != null ? ingredients.size() : 0;
    }

    class IngredientsViewHolder extends RecyclerView.ViewHolder {

        private final TextView ingredient;
        private final TextView quantity;
        private final TextView measure;

        IngredientsViewHolder(View itemView) {
            super(itemView);
            ingredient = (TextView) itemView.findViewById(R.id.ingredient_item_description);
            quantity = (TextView) itemView.findViewById(R.id.ingredient_item_quantity);
            measure = (TextView) itemView.findViewById(R.id.ingredient_item_measure);
        }


        void setIngredientInfo(Ingredient ingredientInfo) {
            ingredient.setText(ingredientInfo.getIngredient());
            quantity.setText(Utils.formatFloatToString(ingredientInfo.getQuantity()));
            measure.setText(ingredientInfo.getMeasure());
        }
    }
}
