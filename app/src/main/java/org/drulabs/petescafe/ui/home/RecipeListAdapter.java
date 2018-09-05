package org.drulabs.petescafe.ui.home;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import org.drulabs.petescafe.R;
import org.drulabs.petescafe.data.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeHolder> {

    private RecipeListListener listener;
    private List<Recipe> recipes;

    private int lastKnownPosition = -1;
    private int spanCount;

    RecipeListAdapter(RecipeListListener listener, int spanCount) {
        this.listener = listener;
        this.spanCount = spanCount;
        recipes = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View convertView = inflater.inflate(R.layout.item_recipe, parent, false);
        return (new RecipeHolder(convertView));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeHolder holder, int position) {
        holder.bind(position);
    }


    @Override
    public int getItemCount() {
        return recipes.size();
    }

    void setRecipes(List<Recipe> recipes) {
        this.recipes.clear();
        this.recipes.addAll(recipes);
        lastKnownPosition = -1;
        notifyDataSetChanged();

    }

    private void animate(View itemView, int position) {
        if (position > lastKnownPosition) {
            Animation animation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim
                    .item_fall_up_animation);

            int offset = itemView.getContext().getResources().getInteger(R.integer
                    .anim_duration_offset);
            offset = offset + (position % spanCount) * offset;

            animation.setStartOffset(offset);
            itemView.startAnimation(animation);
            lastKnownPosition = position;
        }
    }

    class RecipeHolder extends RecyclerView.ViewHolder {

        TextView tvRecipeTitle;

        RecipeHolder(View itemView) {
            super(itemView);
            tvRecipeTitle = itemView.findViewById(R.id.tv_recipe_name);
        }

        void bind(int position) {
            Recipe recipe = recipes.get(position);
            tvRecipeTitle.setText(recipe.getName());
            itemView.setOnClickListener(v -> listener.onTapped(recipe));

            // Animate the view
            animate(itemView, position);
        }
    }

    interface RecipeListListener {
        void onTapped(Recipe recipe);
    }
}
