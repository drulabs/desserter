package org.drulabs.petescafe.ui.details;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.drulabs.petescafe.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.drulabs.petescafe.ui.details.RecipeDetailsActivity.KEY_RECIPE_ID;

@RunWith(AndroidJUnit4.class)
public class DetailScreenTest {

    @Rule
    public ActivityTestRule<RecipeDetailsActivity> mActivityTestRule = new
            ActivityTestRule<RecipeDetailsActivity>(RecipeDetailsActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Intent intent = new Intent();
                    intent.putExtra(KEY_RECIPE_ID, 0);
                    return intent;
                }
            };

    @Test
    public void testIfOverflowMenuIsVisible() {
        onView(withId(R.id.menu_item_ingredients))
                .check(matches(isDisplayed()));
    }

}
