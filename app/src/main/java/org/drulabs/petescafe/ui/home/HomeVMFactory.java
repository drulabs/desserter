package org.drulabs.petescafe.ui.home;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import org.drulabs.petescafe.data.RecipeRepository;

import javax.inject.Inject;

public class HomeVMFactory extends ViewModelProvider.NewInstanceFactory {

    private RecipeRepository repository;

    @Inject
    HomeVMFactory(RecipeRepository recipeRepository) {
        this.repository = recipeRepository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        // noinspection unchecked
        return (T) new HomeVM(repository);
    }
}
