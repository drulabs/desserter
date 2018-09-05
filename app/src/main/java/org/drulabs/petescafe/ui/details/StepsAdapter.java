package org.drulabs.petescafe.ui.details;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.drulabs.petescafe.R;
import org.drulabs.petescafe.data.model.RecipeStep;

import java.util.ArrayList;
import java.util.List;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepVH> {

    private StepSelectionListener listener;

    private List<RecipeStep> recipeSteps;

    private int currentStepId;

    StepsAdapter(StepSelectionListener listener, int currentStepId) {
        this.listener = listener;
        this.currentStepId = currentStepId;
        recipeSteps = new ArrayList<>();
    }

    @NonNull
    @Override
    public StepVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View stepView = inflater.inflate(R.layout.item_recipe_step, parent, false);
        return new StepVH(stepView);
    }

    @Override
    public void onBindViewHolder(@NonNull StepVH holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return recipeSteps.size();
    }

    void setRecipeSteps(List<RecipeStep> steps) {
        this.recipeSteps.clear();
        this.recipeSteps.addAll(steps);
        notifyDataSetChanged();
    }

    void setCurrentStepId(int currentStepId) {
        this.currentStepId = currentStepId;
        notifyDataSetChanged();
    }

    RecipeStep getCurrentStep() {
        return recipeSteps.get(currentStepId);
    }

    class StepVH extends RecyclerView.ViewHolder {
        TextView tvStepName;
        Drawable bgCurrentStep;
        Drawable bgOtherStep;

        StepVH(View itemView) {
            super(itemView);
            tvStepName = itemView.findViewById(R.id.tv_recipe_step);
            bgCurrentStep = itemView.getResources().getDrawable(R.drawable
                    .bg_step_selected_rounded);
            bgOtherStep = itemView.getResources().getDrawable(R.drawable
                    .bg_step_rounded);
        }

        void bind(int position) {
            RecipeStep recipeStep = recipeSteps.get(position);
            tvStepName.setText(recipeStep.getShortDescription());

            if (recipeStep.getId() == currentStepId) {
                itemView.setBackground(bgCurrentStep);
            } else {
                itemView.setBackground(bgOtherStep);
            }

            itemView.setOnClickListener(v -> {
                currentStepId = recipeStep.getId();
                notifyDataSetChanged();
                listener.onStepSelected(recipeStep);
            });
        }
    }

    interface StepSelectionListener {
        void onStepSelected(RecipeStep step);
    }
}
