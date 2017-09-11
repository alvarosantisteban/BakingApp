package com.alvarosantisteban.bakingapp;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
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

    private Context context;
    private List<Ingredient> ingredients;

    IngredientsGridAdapter(List<Ingredient> ingredients, Context context) {
        this.ingredients = ingredients;
        this.context = context;
    }

    @Override
    public IngredientsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipestep_ingredients_rv_item, parent, false);
        return new IngredientsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientsGridAdapter.IngredientsViewHolder holder, int position) {
        if (position > 0) {
            holder.setIngredientInfo(ingredients.get(position-1));
        } else {
            holder.setTagInfo(context);
        }
    }

    @Override
    public int getItemCount() {
        return ingredients != null ? ingredients.size()+1 : 0;
    }

    class IngredientsViewHolder extends RecyclerView.ViewHolder {

        private final View itemView;
        private final TextView ingredient;
        private final TextView quantity;
        private final TextView measure;

        IngredientsViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ingredient = (TextView) itemView.findViewById(R.id.ingredient_item_description);
            quantity = (TextView) itemView.findViewById(R.id.ingredient_item_quantity);
            measure = (TextView) itemView.findViewById(R.id.ingredient_item_measure);
        }

        void setIngredientInfo(Ingredient ingredientInfo) {
            ingredient.setText(ingredientInfo.getIngredient());
            quantity.setText(Utils.formatFloatToString(ingredientInfo.getQuantity()));
            measure.setText(ingredientInfo.getMeasure());
        }

        void setTagInfo(Context context) {
            itemView.setBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.greyPink, null));

            ingredient.setText(R.string.ingredient_ingredient_tag);
            ingredient.setGravity(Gravity.CENTER_HORIZONTAL);
            ingredient.setTypeface(ingredient.getTypeface(), Typeface.BOLD_ITALIC);

            quantity.setText(R.string.ingredient_quantity_tag);
            quantity.setGravity(Gravity.CENTER_HORIZONTAL);
            quantity.setTypeface(quantity.getTypeface(), Typeface.BOLD_ITALIC);

            measure.setText(R.string.ingredient_measure_tag);
            measure.setGravity(Gravity.CENTER_HORIZONTAL);
            measure.setTypeface(measure.getTypeface(), Typeface.BOLD_ITALIC);
        }
    }
}
