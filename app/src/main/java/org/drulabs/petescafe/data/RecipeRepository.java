package org.drulabs.petescafe.data;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.drulabs.petescafe.data.local.RecipeDAO;
import org.drulabs.petescafe.data.model.Recipe;
import org.drulabs.petescafe.data.remote.RecipeApi;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RecipeRepository {

    private static final long CACHE_EXPIRY_MILLIS = 60 * 1000; // 1 minute
    private static final String TAG = "Repository";
    private static final String PREFS_NAME = "prefs_repository";
    private static final String KEY_PREFS_RECIPE_ID = "current_recipe_id";
    private static final String KEY_PREFS_RECIPE_STEP_ID = "current_recipe_step_id";
    private static final String KEY_PREFS_RECIPE_NAME = "current_recipe_name";

    private long lastCacheFetched = 0;

    private RecipeApi recipeApi;
    private RecipeDAO recipeDAO;

    private LiveData<List<Recipe>> recipes;

    private SharedPreferences preferences;

    private CompositeDisposable disposables;

    @Inject
    public RecipeRepository(Context context, RecipeApi recipeApi, RecipeDAO recipeDAO) {
        this.recipeApi = recipeApi;
        this.recipeDAO = recipeDAO;

        this.recipes = recipeDAO.getRecipes();
        this.disposables = new CompositeDisposable();
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Gives a {@link LiveData} of {@link Recipe} list and refreshes cache if necessary
     *
     * @return {@link LiveData} of {@link Recipe} list
     */
    public LiveData<List<Recipe>> getRecipes() {

        // Check if cache is expired
        boolean isCacheExpired = (System.currentTimeMillis() - lastCacheFetched) >=
                CACHE_EXPIRY_MILLIS;

        // If expired, fetch from network again
        if (isCacheExpired) {
            Disposable disposable = recipeApi.fetchRecipes()
                    .subscribeOn(Schedulers.io())
                    .subscribe(recipesFromNetwork -> {
                        // update last cache fetched timestamp
                        lastCacheFetched = System.currentTimeMillis();
                        addRecipes(recipesFromNetwork);
                    }, Throwable::printStackTrace);
            disposables.add(disposable);
        }

        return recipes;
    }

    /**
     * Gets a single {@link Recipe} based on recipe id
     *
     * @param recipeId id of the {@link Recipe}
     * @return {@link Recipe} object
     */
    public LiveData<Recipe> getRecipe(int recipeId) {
        return recipeDAO.getRecipe(recipeId);
    }

    /**
     * Saves a recipe in preferences
     *
     * @param recipeId id of the {@link Recipe} to be saved
     */
    public void saveCurrentRecipeId(int recipeId, String recipeName) {
        preferences.edit()
                .putInt(KEY_PREFS_RECIPE_ID, recipeId)
                .putInt(KEY_PREFS_RECIPE_STEP_ID, 0)
                .putString(KEY_PREFS_RECIPE_NAME, recipeName)
                .apply();
    }

    public String getSavedRecipeName() {
        return preferences.getString(KEY_PREFS_RECIPE_NAME, null);
    }

    /**
     * Saves a recipe's step id in preferences
     *
     * @param recipeStepId id of the {@link Recipe} to be saved
     */
    public void saveRecipeStepId(int recipeStepId) {
        preferences.edit()
                .putInt(KEY_PREFS_RECIPE_STEP_ID, recipeStepId)
                .apply();
    }

    /**
     * Gets a recipe's step id from preferences
     *
     * @return stepId if found, else -1
     */
    public int getRecipeStepId() {
        return preferences.getInt(KEY_PREFS_RECIPE_STEP_ID, -1);
    }

    /**
     * Gets a recipe's id from preferences
     *
     * @return recipeId if found, else -1
     */
    public int getSavedRecipeId() {
        return preferences.getInt(KEY_PREFS_RECIPE_ID, -1);
    }

    /**
     * Retrieves the recipe saved via {@link RecipeRepository#saveCurrentRecipeId(int, String)}
     *
     * @return the saved {@link Recipe} or null if not found
     */
    public Recipe getSavedRecipe() {
        int recipeId = preferences.getInt(KEY_PREFS_RECIPE_ID, 0);
        return recipeDAO.getRecipeSync(recipeId);
    }

    /**
     * Adds {@link Recipe}s to local database for caching
     *
     * @param recipes list of {@link Recipe}s to be cached
     */
    private void addRecipes(List<Recipe> recipes) {
        Disposable disposable = Completable.fromAction(() -> recipeDAO.addRecipes(recipes))
                .subscribeOn(Schedulers.io())
                .subscribe(() -> Log.d(TAG, "run: new recipes inserted"),
                        Throwable::printStackTrace);
        disposables.add(disposable);
    }

    /**
     * Disposes any pending operation
     */
    public void destroy() {
        if (!disposables.isDisposed()) {
            disposables.dispose();
        }
    }

    // Clear preferences that stores app widget data
    public void clearWidgetData() {
        this.preferences.edit().clear().apply();
    }
}
