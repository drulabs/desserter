package org.drulabs.petescafe.data;

import android.arch.lifecycle.LiveData;
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

    private long lastCacheFetched = 0;

    private RecipeApi recipeApi;
    private RecipeDAO recipeDAO;

    private LiveData<List<Recipe>> recipes;

    private CompositeDisposable disposables;

    @Inject
    RecipeRepository(RecipeApi recipeApi, RecipeDAO recipeDAO) {
        this.recipeApi = recipeApi;
        this.recipeDAO = recipeDAO;

        this.recipes = recipeDAO.getRecipes();
        this.disposables = new CompositeDisposable();
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
}
