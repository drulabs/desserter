package org.drulabs.petescafe.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.drulabs.petescafe.R;
import org.drulabs.petescafe.data.model.Recipe;
import org.drulabs.petescafe.ui.details.RecipeDetailsActivity;
import org.drulabs.petescafe.utils.SimpleIdlingResource;

public class HomeScreen extends AppCompatActivity implements RecipeListFragment
        .OnFragmentInteractionListener {

    private static final String TAG = "HomeScreen";

    public static final String EXTRA_RECIPE_ID = "recipe_id";
    public static final String EXTRA_STEP_ID = "recipe_step_id";
    public static final String EXTRA_RECIPE_NAME = "recipe_name";

    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the idling instance
        getIdlingResource();

        // set idling to false as fragment will start fetching recipes as soon as created
        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(false);
        }

        setContentView(R.layout.activity_home_screen);

        Bundle extras = getIntent().getExtras();
        // This checks if the app was launched from continue button on widget
        if (extras != null) {
            int recipeId = extras.getInt(EXTRA_RECIPE_ID);
            int stepId = extras.getInt(EXTRA_STEP_ID);
            String recipeName = extras.getString(EXTRA_RECIPE_NAME);

            Intent detailsIntent = new Intent(this, RecipeDetailsActivity.class);
            detailsIntent.putExtra(RecipeDetailsActivity.KEY_RECIPE_ID, recipeId);
            detailsIntent.putExtra(RecipeDetailsActivity.KEY_RECIPE_NAME, recipeName);
            detailsIntent.putExtra(RecipeDetailsActivity.KEY_RECIPE_STEP_ID, stepId);
            startActivity(detailsIntent);
        }
    }

    @Override
    public void onRecipeSelected(Recipe recipe) {
        Log.d(TAG, "onRecipeSelected: " + recipe.getName());
        Intent detailsIntent = new Intent(this, RecipeDetailsActivity.class);
        detailsIntent.putExtra(RecipeDetailsActivity.KEY_RECIPE_ID, recipe.getId());
        detailsIntent.putExtra(RecipeDetailsActivity.KEY_RECIPE_NAME, recipe.getName());
        detailsIntent.putExtra(RecipeDetailsActivity.KEY_RECIPE_STEP_ID, -1);
        startActivity(detailsIntent);
    }

    @Override
    public void onRecipesFetched() {
        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(true);
        }
    }
}
