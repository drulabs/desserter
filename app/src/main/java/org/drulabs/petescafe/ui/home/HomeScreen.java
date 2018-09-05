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
    }

    @Override
    public void onRecipeSelected(Recipe recipe) {
        Log.d(TAG, "onRecipeSelected: " + recipe.getName());
        Intent detailsIntent = new Intent(this, RecipeDetailsActivity.class);
        detailsIntent.putExtra(RecipeDetailsActivity.KEY_RECIPE_ID, recipe.getId());
        detailsIntent.putExtra(RecipeDetailsActivity.KEY_RECIPE_NAME, recipe.getName());
        startActivity(detailsIntent);
    }

    @Override
    public void onRecipesFetched() {
        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(true);
        }
    }
}
