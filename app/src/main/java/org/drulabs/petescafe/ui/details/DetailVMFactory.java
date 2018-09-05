package org.drulabs.petescafe.ui.details;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import org.drulabs.petescafe.data.RecipeRepository;
import org.drulabs.petescafe.di.ActivityScope;
import org.drulabs.petescafe.di.RecipeQualifier;

import javax.inject.Inject;

public class DetailVMFactory extends ViewModelProvider.NewInstanceFactory {

    private final RecipeRepository repository;
    private final int recipeId;

    public DetailVMFactory(RecipeRepository repository, @RecipeQualifier int recipeId) {
        this.repository = repository;
        this.recipeId = recipeId;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        // noinspection unchecked
        return (T) new DetailVM(repository, recipeId);
    }
}
