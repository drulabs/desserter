package org.drulabs.petescafe.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.drulabs.petescafe.R;
import org.drulabs.petescafe.app.CafeApp;
import org.drulabs.petescafe.data.RecipeRepository;
import org.drulabs.petescafe.data.model.Recipe;
import org.drulabs.petescafe.di.AppComponent;
import org.drulabs.petescafe.ui.home.HomeScreen;

import java.util.List;

public class StepWidgetService extends RemoteViewsService {

    static final String EXTRA_WIDGET_ID = "widget_id";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new IngredientFactory(this.getApplicationContext(), intent);
    }

    private class IngredientFactory implements RemoteViewsFactory {

        private Context context;
        private List<Recipe> recipes;
        private RecipeRepository recipeRepository;

        IngredientFactory(Context applicationContext, Intent intent) {
            context = applicationContext;
            AppComponent appComponent = ((CafeApp) context.getApplicationContext())
                    .getAppComponent();

            recipeRepository = appComponent.getRecipeRepository();
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            Log.d("WidgetService", "onDataSetChanged: called");
            recipes = recipeRepository.getRecipesSync();
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return (recipes == null ? 0 : recipes.size());
        }

        @Override
        public RemoteViews getViewAt(int position) {
            Recipe recipe = recipes.get(position);
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout
                    .step_widget);
            rv.setTextViewText(R.id.step_recipe_name, recipe.getName());
            rv.setImageViewResource(R.id.img_step_cake_resource, WidgetUtils
                    .getDrawableResId(recipe.getName()));

//            boolean isOddPosition = (position % 2 == 0);
//            rv.setInt(R.id.rl_step_widget_holder, "setBackgroundColor",
//                    isOddPosition ? Color.parseColor("#DDEC862E") : Color.parseColor
//                            ("#DD43A715"));

            // Set fill in Intent
            Intent fillInIntent = new Intent();
            Bundle extras = new Bundle();
            extras.putString(HomeScreen.EXTRA_RECIPE_NAME, recipe.getName());
            extras.putInt(HomeScreen.EXTRA_RECIPE_ID, recipe.getId());
            extras.putInt(HomeScreen.EXTRA_STEP_ID, -1);
            fillInIntent.putExtras(extras);

            rv.setOnClickFillInIntent(R.id.rl_step_widget_holder, fillInIntent);

            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            // TODO This is an intended comment by Kaushal (handle this later)
            // You can create a custom loading view (for instance when getViewAt() is slow.) If you
            // return null here, you will get the default loading view.
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
