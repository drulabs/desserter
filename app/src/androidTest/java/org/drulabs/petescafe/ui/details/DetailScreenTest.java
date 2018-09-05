package org.drulabs.petescafe.ui.details;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.drulabs.petescafe.R;
import org.drulabs.petescafe.data.local.RecipeDAO;
import org.drulabs.petescafe.data.local.RecipeDB;
import org.drulabs.petescafe.ui.utils.RecyclerViewMatcher;
import org.drulabs.petescafe.ui.utils.TestDataGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.drulabs.petescafe.ui.details.RecipeDetailsActivity.KEY_RECIPE_ID;

@RunWith(AndroidJUnit4.class)
public class DetailScreenTest {

    @Rule
    public ActivityTestRule<RecipeDetailsActivity> mActivityTestRule = new
            ActivityTestRule<RecipeDetailsActivity>(RecipeDetailsActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Intent intent = new Intent();
                    intent.putExtra(KEY_RECIPE_ID, 1);
                    return intent;
                }
            };

    private RecipeDAO recipeDAO;

    @Before
    public void setup() {
        recipeDAO = RecipeDB.getINSTANCE(InstrumentationRegistry.getTargetContext()).getRecipeDAO();
        recipeDAO.addRecipes(TestDataGenerator.getMockRecipeList());

        mActivityTestRule.getActivity()
                .getSupportFragmentManager().beginTransaction()
                .replace(R.id.fr_steps_frag_holder, RecipeStepsFragment.newInstance(0), "1")
                .replace(R.id.fr_step_details_frag_holder, StepDescriptionFragment.newInstance(0)
                        , "2")
                .commit();
    }

    @Test
    public void testIfOverflowMenuIsVisible() {
        onView(withId(R.id.menu_item_ingredients))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testIfFirstRecipeStepIsSelectedInitially() {
        // match recipe description
        onView(withRecyclerView(R.id.recipe_step_list).atPosition(0))
                .check(matches(hasDescendant(withText(TestDataGenerator.getMockRecipe().getSteps().get(0)
                        .getShortDescription()))));
    }

    @Test
    public void testRecipeStepSelection() {
        int index = 0;
        // Click on the second recipe step i.e. index = 1
        onView(withId(R.id.recipe_step_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(index, click()));

        // match recipe description
        onView(withId(R.id.tv_stepdec_description))
                .check(matches(withText(TestDataGenerator.getMockRecipe().getSteps().get(index)
                        .getDescription())));
    }

    @After
    public void tearDown() {
        recipeDAO.clearAll();
    }


    // Convenience helper
    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

}
