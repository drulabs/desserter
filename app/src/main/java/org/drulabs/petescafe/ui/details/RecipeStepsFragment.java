package org.drulabs.petescafe.ui.details;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.drulabs.petescafe.R;
import org.drulabs.petescafe.data.model.Ingredient;
import org.drulabs.petescafe.data.model.RecipeStep;
import org.drulabs.petescafe.di.FragmentStepsScope;

import java.util.List;
import java.util.Locale;

@FragmentStepsScope
public class RecipeStepsFragment extends Fragment implements StepsAdapter.StepSelectionListener {
    private static final String KEY_CURRENT_STEP_ID = "current_step_id";
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final int INITAL_STEP_SELECTION = 0;

    private Listener mListener;
    private StepsAdapter adapter;
    private int currentStepId = -1;
    private String strIngredients;
    private String recipeName;
    private int servings;

    DetailVM detailVM;
    private boolean isFirstLaunch = false;

    public RecipeStepsFragment() {
        // Required empty public constructor
    }

    public static RecipeStepsFragment newInstance(int currentStepId, boolean isFirstLaunch) {
        RecipeStepsFragment fragment = new RecipeStepsFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_CURRENT_STEP_ID, currentStepId);
        args.putBoolean(KEY_FIRST_LAUNCH, isFirstLaunch);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (savedInstanceState != null) {
            currentStepId = savedInstanceState.getInt(KEY_CURRENT_STEP_ID);
            isFirstLaunch = false;
        } else if (getArguments() != null) {
            currentStepId = getArguments().getInt(KEY_CURRENT_STEP_ID);
            isFirstLaunch = getArguments().getBoolean(KEY_FIRST_LAUNCH);
        }
        adapter = new StepsAdapter(this, currentStepId);

        detailVM = ViewModelProviders.of(getActivity()).get(DetailVM.class);

        // Update recipe steps
        detailVM.getRecipe().observe(this, recipe -> {
            if (recipe != null && recipe.getSteps() != null) {
                adapter.setRecipeSteps(recipe.getSteps());

                List<Ingredient> ingredients = recipe.getIngredients();

                if (ingredients != null && ingredients.size() > 0) {
                    servings = recipe.getServings();
                    StringBuilder ingredientsAsString = new StringBuilder();
                    ingredientsAsString.append("\n--------------------\n");
                    for (Ingredient ingredient : ingredients) {
                        ingredientsAsString.append(ingredient.getIngredient());
                        ingredientsAsString.append(" - ");
                        ingredientsAsString.append(ingredient.getQuantity());
                        ingredientsAsString.append(" ");
                        ingredientsAsString.append(ingredient.getMeasure());
                        ingredientsAsString.append("\n--------------------\n");
                    }

                    strIngredients = ingredientsAsString.toString();
                }
                recipeName = recipe.getName();

                if (isFirstLaunch) {
                    detailVM.setCurrentStep(recipe.getSteps().get(0));
                }
            }
        });

        // Keep current step state synced
        detailVM.getStepStateHolder().observe(this, stepId -> {
            if (stepId != null) {
                currentStepId = stepId;
                adapter.setCurrentStepId(currentStepId);
                detailVM.setCurrentStep(adapter.getCurrentStep());
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe_steps, container, false);
        RecyclerView rvStepList = view.findViewById(R.id.recipe_step_list);
        rvStepList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvStepList.setAdapter(adapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            mListener = (Listener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement Listener");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_STEP_ID, currentStepId);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStepSelected(RecipeStep step) {
        currentStepId = step.getId();
        detailVM.setCurrentStep(step);
        detailVM.resetPLaybackPosition();
        mListener.onStepSelected(step);
    }

    /**
     * Displays ingredient list in an alert dialog
     */
    void displayIngredients() {

        new AlertDialog.Builder(getActivity())
                .setTitle(String.format(Locale.getDefault(), getString(R.string
                        .txt_ingredients_format), recipeName, servings))
                .setMessage(strIngredients)
                .setPositiveButton(R.string.txt_okay, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    public interface Listener {
        void onStepSelected(RecipeStep recipeStep);
    }
}
