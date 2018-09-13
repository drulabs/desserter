package org.drulabs.petescafe.widget.stack;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.squareup.picasso.Picasso;

import org.drulabs.petescafe.R;
import org.drulabs.petescafe.app.CafeApp;
import org.drulabs.petescafe.data.RecipeRepository;
import org.drulabs.petescafe.data.model.Recipe;
import org.drulabs.petescafe.di.AppComponent;
import org.drulabs.petescafe.ui.home.HomeScreen;

import java.io.IOException;
import java.util.List;

public class RecipeStackService extends RemoteViewsService {

    static final String EXTRA_WIDGET_ID = "widget_id";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new IngredientFactory(this.getApplicationContext(), intent);
    }

    private class IngredientFactory implements RemoteViewsFactory {

        private Context context;
        private List<Recipe> recipes;
        private RecipeRepository recipeRepository;
        private int appWidgetId;

        IngredientFactory(Context applicationContext, Intent intent) {
            context = applicationContext;
            AppComponent appComponent = ((CafeApp) context.getApplicationContext())
                    .getAppComponent();
            appWidgetId = intent.getIntExtra(EXTRA_WIDGET_ID, -1);

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
                    .item_recipe_stack);
            rv.setTextViewText(R.id.step_recipe_name, recipe.getName());

            // Set fill in Intent
            Intent fillInIntent = new Intent();
            Bundle extras = new Bundle();
            extras.putString(HomeScreen.EXTRA_RECIPE_NAME, recipe.getName());
            extras.putInt(HomeScreen.EXTRA_RECIPE_ID, recipe.getId());
            extras.putInt(HomeScreen.EXTRA_STEP_ID, -1);
            fillInIntent.putExtras(extras);

            // Load the image from url into image view
            try {
                Bitmap recipeBitmap = Picasso.get().load(recipe.getImage()).get();
                rv.setImageViewBitmap(R.id.img_step_cake_resource, recipeBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            rv.setOnClickFillInIntent(R.id.rl_step_widget_holder, fillInIntent);

            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
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
