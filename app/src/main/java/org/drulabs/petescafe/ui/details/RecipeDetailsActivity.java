package org.drulabs.petescafe.ui.details;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import org.drulabs.petescafe.R;
import org.drulabs.petescafe.app.CafeApp;
import org.drulabs.petescafe.data.model.RecipeStep;
import org.drulabs.petescafe.di.DetailComponent;
import org.drulabs.petescafe.di.DetailsModule;

public class RecipeDetailsActivity extends AppCompatActivity implements
        RecipeStepsFragment.Listener {

    private static final String TAG_STEPS_FRAGMENT = "steps_fragment";
    private static final String TAG_DETAILS_FRAGMENT = "steps_details_fragment";

    public static final String KEY_RECIPE_ID = "recipe_id";
    public static final String KEY_RECIPE_NAME = "recipe_name";
    public static final String KEY_RECIPE_STEP_ID = "recipe_step_id";

    private static final String KEY_DETAILS_OPEN = "details_open";
    private static final float DETAILS_CLOSED = 1.0f;
    private static final float DETAILS_OPEN = 0.0f;
    private static final int INITIAL_STEP_ID = 0;

    // Class vars
    int recipeId = 0;
    String recipeName;
    int currentStepId = 0;
    boolean isDetailsOpen = false;
    boolean isTablet;
    boolean isLandscape;

    private DetailVM detailVM;

    // UI elements
    RecipeStepsFragment recipeStepsFragment;
    StepDescriptionFragment stepDescriptionFragment;
    Guideline guideline;
    ConstraintLayout.LayoutParams guidelineParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        isTablet = getResources().getBoolean(R.bool.is_tablet);
        isLandscape = getResources().getConfiguration().orientation == Configuration
                .ORIENTATION_LANDSCAPE;

        guideline = findViewById(R.id.guideline_vertical);
        guidelineParams = (ConstraintLayout.LayoutParams) guideline.getLayoutParams();

        Bundle extras = getIntent().getExtras();

        boolean isFirstLaunch = (savedInstanceState == null);

        if (savedInstanceState != null) {
            recipeId = savedInstanceState.getInt(KEY_RECIPE_ID);
            recipeName = savedInstanceState.getString(KEY_RECIPE_NAME);
            currentStepId = savedInstanceState.getInt(KEY_RECIPE_STEP_ID);
            isDetailsOpen = savedInstanceState.getBoolean(KEY_DETAILS_OPEN);
        } else if (extras != null && extras.containsKey(KEY_RECIPE_ID)) {
            recipeId = extras.getInt(KEY_RECIPE_ID);
            recipeName = extras.getString(KEY_RECIPE_NAME);
            currentStepId = extras.getInt(KEY_RECIPE_STEP_ID);
            if (currentStepId < 0) {
                currentStepId = INITIAL_STEP_ID;
                isDetailsOpen = false;
            } else {
                isDetailsOpen = true;
            }
        } else {
            throw new IllegalArgumentException("Required parameter not passed (recipe_id)");
        }

        if (isDetailsOpen && (!isLandscape || (isLandscape && !isTablet))) {
            guidelineParams.guidePercent = DETAILS_OPEN;
            guideline.setLayoutParams(guidelineParams);
        } else if (!isDetailsOpen && !isTablet) {
            guidelineParams.guidePercent = DETAILS_CLOSED;
            guideline.setLayoutParams(guidelineParams);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(recipeName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        handleLandscapeMode();

        DetailComponent component = ((CafeApp) getApplicationContext()).getAppComponent()
                .getDetailComponent(new DetailsModule(recipeId));
        DetailVMFactory detailVMFactory = component.getDetailVMFactory();
        detailVM = ViewModelProviders.of(this, detailVMFactory).get(DetailVM.class);

        FragmentManager fragmentManager = getSupportFragmentManager();
        recipeStepsFragment = RecipeStepsFragment.newInstance(currentStepId, isFirstLaunch);
        stepDescriptionFragment = StepDescriptionFragment.newInstance(currentStepId);
        fragmentManager.beginTransaction()
                .replace(R.id.fr_steps_frag_holder, recipeStepsFragment, TAG_STEPS_FRAGMENT)
                .replace(R.id.fr_step_details_frag_holder, stepDescriptionFragment,
                        TAG_DETAILS_FRAGMENT)
                .commit();
    }

    public void handleLandscapeMode() {
        // Make the video full screen on landscape mode for phones
        if (!isTablet && isDetailsOpen && isLandscape) {
            // remove title
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public void onStepSelected(RecipeStep recipeStep) {
        isDetailsOpen = true;
        currentStepId = recipeStep.getId();
        if (!isLandscape) {
            guidelineParams.guidePercent = DETAILS_OPEN;
            guideline.setLayoutParams(guidelineParams);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_ingredients:
                recipeStepsFragment.displayIngredients();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_RECIPE_STEP_ID, currentStepId);
        outState.putInt(KEY_RECIPE_ID, recipeId);
        outState.putString(KEY_RECIPE_NAME, recipeName);
        outState.putBoolean(KEY_DETAILS_OPEN, isDetailsOpen);
    }

    @Override
    public void onBackPressed() {
        if (!isTablet) {
            detailVM.setPlaybackPosition(0);
            detailVM.setPlaying(false);
            if (isDetailsOpen) {
                isDetailsOpen = false;
                guidelineParams.guidePercent = DETAILS_CLOSED;
                guideline.setLayoutParams(guidelineParams);
                stepDescriptionFragment.releasePlayer();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }
}
