package org.drulabs.petescafe.widget.status;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.drulabs.petescafe.R;
import org.drulabs.petescafe.app.CafeApp;
import org.drulabs.petescafe.data.RecipeRepository;
import org.drulabs.petescafe.data.model.Ingredient;
import org.drulabs.petescafe.data.model.Recipe;
import org.drulabs.petescafe.di.AppComponent;

import java.util.List;

public class RecipeStatusService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new IngredientFactory(this.getApplicationContext(), intent);
    }

    private class IngredientFactory implements RemoteViewsService.RemoteViewsFactory {

        private Context context;
        private List<Ingredient> ingredients;
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
            Recipe currentRecipe = recipeRepository.getSavedRecipe();
            if (currentRecipe != null) {
                ingredients = currentRecipe.getIngredients();
            }
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return (ingredients == null ? 0 : ingredients.size());
        }

        @Override
        public RemoteViews getViewAt(int position) {
            Ingredient ingredient = ingredients.get(position);
            String strIngredient = ingredient.getIngredient() + " - " + ingredient.getQuantity()
                    + " " + ingredient.getMeasure();

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout
                    .item_ingredient_remote);
            rv.setTextViewText(R.id.tv_recipe_ingredient, strIngredient);
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
