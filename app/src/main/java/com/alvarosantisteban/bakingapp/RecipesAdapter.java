package com.alvarosantisteban.bakingapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alvarosantisteban.bakingapp.model.Recipe;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * The adapter for the list of recipes.
 */
class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder>{

    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);
    }

    private List<Recipe> recipes;
    private Context context;
    private OnItemClickListener onItemClickListener;

    RecipesAdapter(List<Recipe> recipes, Context context, OnItemClickListener onItemClickListener) {
        super();
        this.recipes = recipes;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.setName(recipe.getName());
        holder.loadImage(recipe.getImage());
        holder.setListener(recipe);
    }

    @Override
    public int getItemCount() {
        return recipes != null ? recipes.size() : 0;
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView name;

        RecipeViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.recipe_image);
            name = (TextView) itemView.findViewById(R.id.recipe_name);
        }

        void loadImage(String imageUrl) {
            Glide
                    .with(context)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.mortar_placeholder)
                    .into(imageView);
        }

        void setName(String recipeName) {
            name.setText(recipeName);
        }

        void setListener(final Recipe recipe) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    onItemClickListener.onItemClick(recipe);
                }
            });
        }
    }
}
