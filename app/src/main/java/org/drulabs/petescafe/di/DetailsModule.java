package org.drulabs.petescafe.di;

import org.drulabs.petescafe.data.RecipeRepository;
import org.drulabs.petescafe.ui.details.DetailVMFactory;

import dagger.Module;
import dagger.Provides;

@Module
public class DetailsModule {

    private int recipeId;

    public DetailsModule(int recipeId) {
        this.recipeId = recipeId;
    }

//    @Provides
//    @RecipeQualifier
//    public int provideRecipeId() {
//        return recipeId;
//    }

    @Provides
    public DetailVMFactory providesDetailsVMFactory(RecipeRepository recipeRepository) {
        return new DetailVMFactory(recipeRepository, recipeId);
    }
}
