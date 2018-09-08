package org.drulabs.petescafe.ui.home;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import org.drulabs.petescafe.data.RecipeRepository;
import org.drulabs.petescafe.data.model.Recipe;

import java.util.List;

public class HomeVM extends ViewModel {

    private final RecipeRepository repository;
    private final LiveData<List<Recipe>> recipeListLiveData;

    HomeVM(RecipeRepository repository) {
        this.repository = repository;
        recipeListLiveData = this.repository.getRecipes();
    }

    public LiveData<List<Recipe>> getRecipes() {
        return recipeListLiveData;
    }

    public void saveAsCurrentRecipe(Recipe recipe) {
        repository.saveCurrentRecipeId(recipe.getId(), recipe.getName());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.destroy();
    }
}
